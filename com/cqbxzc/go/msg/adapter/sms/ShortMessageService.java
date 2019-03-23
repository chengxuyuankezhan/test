package com.cqbxzc.go.msg.adapter.sms;

import com.cqbxzc.go.msg.adapter.sms.check.SmsSendingFrequentCheckUtil;
import com.cqbxzc.go.msg.adapter.sms.domain.ShortMessageSendRecord;
import com.cqbxzc.go.msg.adapter.sms.domain.ShortMessageSendRecordErrorMsg;
import com.cqbxzc.go.msg.adapter.sms.domain.ShortMessageSendRecordService;
import com.cqbxzc.go.msg.adapter.sms.domain.ShortMessageSendStatusEnum;
import com.cqbxzc.mq.RabbitProducer;
import com.cqbxzc.sms.mq.ShortMessageQueueConstant;
import com.cqbxzc.sms.mq.ShortMessageQueueData;
import com.google.common.collect.Lists;
import live.jialing.util.general.IdGenerator;
import live.jialing.util.mapper.JsonMapperUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 短信服务
 */
@Slf4j
@Service
@RabbitListener(queues = ShortMessageQueueConstant.SMS_RESULT_MQ_QUEUE)
public class ShortMessageService {

    /**
     * 失败重试次数
     */
    private static final int TRY_COUNT = 3;

    private final ShortMessageSendRecordService shortMessageSendRecordService;

	@Autowired
	public ShortMessageService(ShortMessageSendRecordService shortMessageSendRecordService) {
		this.shortMessageSendRecordService = shortMessageSendRecordService;
	}

	/**
     * 发送短信
     *
     * @param msg 短信消息任务
     */
    @Transactional
    public void send(@NonNull ShortMessageTask msg) {

        msg.getMobile().forEach(m -> {
            String code = IdGenerator.uuid2();

            // 持久化
            ShortMessageSendRecord shortMessageSendRecord = ShortMessageSendRecord.builder()
                    .code(code)
                    .taskCode(msg.getTaskCode())
                    .mobile(m)
                    .sendTime(LocalDateTime.now())
                    .template(msg.getTemplate())
                    .sendCount(1)
                    .data(JsonMapperUtil.toJ(msg.getData()))
                    .status(ShortMessageSendStatusEnum.commit)
                    .build();

            shortMessageSendRecordService.create(shortMessageSendRecord);


            send(msg.getTemplate(), m, msg.getData(), code);
        });
    }

    /**
     * 默认发送
     * <p>
     * 发送频繁校验
     *
     * @param template 模板
     * @param mobile 手机号
     * @param data 参数
     * @param code 发送记录code，全局唯一
     */
    private void send(String template, String mobile, Map data, String code) {

        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(template)) {
            log.warn("发短信数据不正确：template={};mobile={}", template, mobile);
            return;
        }

        // 异步发送（消息队列）
        send(ShortMessageQueueData.builder()
                .code(code)
                .template(template)
                .mobile(mobile)
                .param(data)
                .build());
    }

    /**
     * 发送短信
     * <p>
     * 发送频繁校验
     *
     * @param data 短信发送数据
     */
    private void send(@NonNull ShortMessageQueueData data) {

        // MQ异步发送短信
        // 监听1：发送短信
        // 监听2：持久化短信

        // 发送频繁校验
        if (SmsSendingFrequentCheckUtil.checkAndNext(data.getMobile(), data.getTemplate())) {
            log.warn("短信发送太频繁了.{}", JsonMapperUtil.toJ(data));
        } else {
            sendShortMessage(data);
        }
    }

    /**
     * 发送短信
     *
     * @param data  短信发送数据
     */
    private void sendShortMessage(@NonNull ShortMessageQueueData data) {

        // 添加到队列
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        RabbitProducer.send(ShortMessageQueueConstant.SMS_MQ_DIRECT_EXCHANGE, ShortMessageQueueConstant.SMS_SEND_MQ_ROUTING_KEY, data, correlationData);
        log.info("已经添加到短信队列中。{}", JsonMapperUtil.toJ(data));
    }


    /**
     * 监听结果并处理
     *
     * @param data 短信返回的结果
     */
    @RabbitHandler
    @Transactional
    public void resultHandler(@Payload ShortMessageQueueData data) {

        log.info("监听到短信发送结果，进行处理");

        if (data == null) {
            return;
        }

        log.debug("开始进行短信结果的处理，{}", data);

        //根据唯一标识查询短信发送记录
        Optional<ShortMessageSendRecord> shortMessageSendRecordOptional = shortMessageSendRecordService.findByCode(data.getCode());

        if (shortMessageSendRecordOptional.isPresent()) {

            ShortMessageSendRecord shortMessageSendRecord = shortMessageSendRecordOptional.get();
            int sendCount = shortMessageSendRecord.getSendCount();
            if (data.getIsSuccess() == null) {
                return;
            }

            //处理结果
            if (data.getIsSuccess()) {
                shortMessageSendRecord.setStatus(ShortMessageSendStatusEnum.success);
            } else {
                LocalDateTime sendTime = shortMessageSendRecord.getSendTime();
                //失败重发
                if (sendCount <= TRY_COUNT) {
                    shortMessageSendRecord.setStatus(ShortMessageSendStatusEnum.retry);
                    shortMessageSendRecord.setSendTime(LocalDateTime.now());
                    shortMessageSendRecord.setSendCount(sendCount + 1);
                    sendShortMessage(data);
                } else {
                    shortMessageSendRecord.setStatus(ShortMessageSendStatusEnum.fail);
                }

                List errorMsgs = Lists.newArrayList();
                if (StringUtils.isNotBlank(shortMessageSendRecord.getErrMsg())) {
                    errorMsgs = JsonMapperUtil.fromJ(shortMessageSendRecord.getErrMsg(), List.class);
                }
                // 记录错误消息
                ShortMessageSendRecordErrorMsg errorMsg = ShortMessageSendRecordErrorMsg.builder()
                        .sendTime(sendTime)
                        .count(sendCount)
                        .errorMsg(data.getErrorMsg())
                        .build();
                errorMsgs.add(errorMsg);
                shortMessageSendRecord.setErrMsg(JsonMapperUtil.toJ(errorMsgs));
            }
            shortMessageSendRecord.setBackTime(LocalDateTime.now());
            shortMessageSendRecordService.update(shortMessageSendRecord);
        }

    }

}


