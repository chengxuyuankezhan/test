package com.cqbxzc.go.msg.adapter.vms;

import com.cqbxzc.go.msg.adapter.sms.domain.ShortMessageSendRecordErrorMsg;
import com.cqbxzc.go.msg.adapter.vms.domain.VsmMessageSendRecord;
import com.cqbxzc.go.msg.adapter.vms.domain.VsmMessageSendRecordService;
import com.cqbxzc.go.msg.adapter.vms.domain.VsmMessageSendStatusEnum;
import com.cqbxzc.mq.RabbitProducer;
import com.cqbxzc.vsm.mq.VsmMessageQueueConstant;
import com.cqbxzc.vsm.mq.VsmMessageQueueData;
import com.google.common.collect.Lists;
import live.jialing.util.general.Collections3;
import live.jialing.util.general.IdGenerator;
import live.jialing.util.mapper.JsonMapperUtil;
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
import java.util.Optional;

/**
 * 语音通知服务
 */
@Slf4j
@Service
@RabbitListener(queues = VsmMessageQueueConstant.VSM_RESULT_MQ_QUEUE)
public class VsmMessageService {

    /**
     * 失败重试次数
     */
    private static final int TRY_COUNT = 3;

    @Autowired
    private VsmMessageSendRecordService vsmMessageSendRecordService;

    /**
     * 发送语音通知
     *
     * @param task 语音信息
     */
    public void send(VsmMessageTask task) {

        if (Collections3.isEmpty(task.getMobile())) {
            log.warn("发送语音消息，手机号为空");
            return;
        }

        task.getMobile().parallelStream().forEach(mobile -> {
            String code = IdGenerator.uuid2();
            VsmMessageQueueData data = VsmMessageQueueData.builder()
                    .code(code)
                    .mobile(mobile)
                    .template(task.getTemplate())
                    .param(task.getContent())
                    .build();

            //持久化
            vsmMessageSendRecordService.create(VsmMessageSendRecord.builder()
                    .code(code)
                    .mobile(mobile)
                    .template(task.getTemplate())
                    .data(JsonMapperUtil.toJ(task.getContent()))
                    .sendTime(LocalDateTime.now())
                    .taskCode(task.getCode())
                    .status(VsmMessageSendStatusEnum.commit)
                    .sendCount(1)
                    .build());

            //异步发送
            send(data);
        });

    }

    /**
     * 发送语音消息
     *
     * @param data 语音消息数据
     */
    private void send(VsmMessageQueueData data) {
        CorrelationData correlationData = new CorrelationData(data.getCode());
        RabbitProducer.send(VsmMessageQueueConstant.VSM_MQ_DIRECT_EXCHANGE
                , VsmMessageQueueConstant.VSM_SEND_MQ_ROUTING_KEY
                , data, correlationData);

        log.info("添加到语音消息的发送队列中。{}", JsonMapperUtil.toJ(data));
    }


    /**
     * 监听结果并处理
     *
     * @param data 语音返回的结果
     */
    @RabbitHandler
    @Transactional
    public void resultHandler(@Payload VsmMessageQueueData data) {

        log.info("监听到语音发送结果，进行处理");

        if (data == null) {
            return;
        }

        log.debug("开始进行语音结果的处理，{}", data);

        //根据唯一标识查询短信发送记录
        Optional<VsmMessageSendRecord> recordOptional = vsmMessageSendRecordService.findByCode(data.getCode());

        if (recordOptional.isPresent()) {

            VsmMessageSendRecord vsmMessageSendRecord = recordOptional.get();
            int sendCount = vsmMessageSendRecord.getSendCount();
            if (data.getIsSuccess() == null) {
                return;
            }

            //处理结果
            if (data.getIsSuccess()) {
                vsmMessageSendRecord.setStatus(VsmMessageSendStatusEnum.success);
            } else {
                LocalDateTime sendTime = vsmMessageSendRecord.getSendTime();
                //失败重发
                if (sendCount <= TRY_COUNT) {
                    vsmMessageSendRecord.setStatus(VsmMessageSendStatusEnum.retry);
                    vsmMessageSendRecord.setSendTime(LocalDateTime.now());
                    vsmMessageSendRecord.setSendCount(sendCount + 1);
                    send(data);
                } else {
                    vsmMessageSendRecord.setStatus(VsmMessageSendStatusEnum.fail);
                }

                List errorMsgs = Lists.newArrayList();
                if (StringUtils.isNotBlank(vsmMessageSendRecord.getErrMsg())) {
                    errorMsgs = JsonMapperUtil.fromJ(vsmMessageSendRecord.getErrMsg(), List.class);
                }
                // 记录错误消息
                ShortMessageSendRecordErrorMsg errorMsg = ShortMessageSendRecordErrorMsg.builder()
                        .sendTime(sendTime)
                        .count(sendCount)
                        .errorMsg(data.getErrorMsg())
                        .build();
                errorMsgs.add(errorMsg);
                vsmMessageSendRecord.setErrMsg(JsonMapperUtil.toJ(errorMsgs));
            }
            vsmMessageSendRecord.setBackTime(LocalDateTime.now());
            vsmMessageSendRecordService.update(vsmMessageSendRecord);
        }

    }

}
