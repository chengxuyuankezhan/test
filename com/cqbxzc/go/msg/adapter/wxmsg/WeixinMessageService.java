package com.cqbxzc.go.msg.adapter.wxmsg;

import com.cqbxzc.go.msg.adapter.wxmsg.domain.WeixinMessageSendErrorMsg;
import com.cqbxzc.go.msg.adapter.wxmsg.domain.WeixinMessageSendRecord;
import com.cqbxzc.go.msg.adapter.wxmsg.domain.WeixinMessageSendRecordService;
import com.cqbxzc.go.msg.adapter.wxmsg.domain.WeixinMessageSendStatusEnum;
import com.cqbxzc.mq.RabbitProducer;
import com.cqbxzc.wxmsg.mq.WeixinMessageQueueConstant;
import com.cqbxzc.wxmsg.mq.WeixinMessageQueueData;
import com.google.common.collect.Lists;
import io.micrometer.core.lang.NonNull;
import live.jialing.util.general.Collections3;
import live.jialing.util.general.IdGenerator;
import live.jialing.util.mapper.BeanMapperUtil;
import live.jialing.util.mapper.JsonMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RabbitListener(queues = WeixinMessageQueueConstant.WEIXIN_MSG_RESULT_MQ_QUEUE)
public class WeixinMessageService {
    /**
     * 发送失败最大重试次数
     */
    private static final int TRY_COUNT = 3;

    private final WeixinMessageSendRecordService weixinMessageSendRecordService;

    @Autowired
    public WeixinMessageService(WeixinMessageSendRecordService weixinMessageSendRecordService) {
        this.weixinMessageSendRecordService = weixinMessageSendRecordService;
    }


    /**
     * 发送微信模板消息
     *
     * @param task 微信模板消息任务
     */
    @Transactional
    public void send(@NonNull WeixinMessageTask task) {

        if (StringUtils.isBlank(task.getOpenId())
                || (StringUtils.isBlank(task.getFormId()))
                || StringUtils.isBlank(task.getTemplate())
                || Collections3.isEmpty(task.getParam())) {
            return;
        }

        WeixinMessageQueueData data = BeanMapperUtil.map(task
                , BeanMapperUtil.getType(WeixinMessageTask.class)
                , BeanMapperUtil.getType(WeixinMessageQueueData.class));
        data.setCode(IdGenerator.uuid2());

        // 异步发送微信消息
        // 监听1：发送微信消息
        // 监听2：持久化

        // 持久化
        weixinMessageSendRecordService.create(WeixinMessageSendRecord.builder()
                .code(data.getCode())
                .taskCode(task.getTaskCode())
                .sendTime(LocalDateTime.now())
                .openid(data.getOpenId())
                .templateId(data.getTemplate())
                .status(WeixinMessageSendStatusEnum.commit)
                .content(JsonMapperUtil.toJ(data.getParam()))
                .sendCount(1)
                .formId(data.getFormId())
                .build());

        send(data);
    }


    /**
     * 监听微信模板消息发送的结果，并进行处理
     *
     * @param data 微信模板消息数据
     */
    @RabbitHandler
    @Transactional
    public void handleResult(WeixinMessageQueueData data) {

        log.info("监听到微信模板消息发送的结果");

         if (data == null) {
             return;
         }

         log.debug("开始进行微信模板消息发送结果的处理：{}", data);

        // 通过唯一标识查询对应记录
         Optional<WeixinMessageSendRecord> optional = weixinMessageSendRecordService.findByCode(data.getCode());
         if (!optional.isPresent()) {
             return;
         }

         WeixinMessageSendRecord weixinMessageSendRecord = optional.get();
         if (data.getIsSuccess()) {
             weixinMessageSendRecord.setStatus(WeixinMessageSendStatusEnum.success);

             //保存修改
             weixinMessageSendRecordService.update(weixinMessageSendRecord);
         } else {
             int count = weixinMessageSendRecord.getSendCount();
             LocalDateTime sendTime = weixinMessageSendRecord.getSendTime();
             if (count <= TRY_COUNT) {
                 weixinMessageSendRecord.setStatus(WeixinMessageSendStatusEnum.retry);
                 weixinMessageSendRecord.setSendCount(count + 1);
                 weixinMessageSendRecord.setSendTime(LocalDateTime.now());

                 //保存修改
                 weixinMessageSendRecordService.update(weixinMessageSendRecord);

                 //重新发送
             } else {
                 weixinMessageSendRecord.setStatus(WeixinMessageSendStatusEnum.fail);
             }
             List<WeixinMessageSendErrorMsg> errorMsgs = Lists.newArrayList();
             if(StringUtils.isNotBlank(weixinMessageSendRecord.getErrorMsg())){
                 errorMsgs = JsonMapperUtil.fromJ(weixinMessageSendRecord.getErrorMsg(),List.class);
             }
             errorMsgs.add(WeixinMessageSendErrorMsg.builder()
                     .sendTime(sendTime)
                     .count(count)
                     .errorMsg(data.getErrMsg())
                     .build());
             weixinMessageSendRecord.setErrorMsg(JsonMapperUtil.toJ(errorMsgs));

             //保存修改
             weixinMessageSendRecordService.update(weixinMessageSendRecord);
         }

    }


    /**
     * 发送微信模板消息
     * @param data 微信模板消息数据
     */
    private void send(@NonNull WeixinMessageQueueData data){
        CorrelationData correlationData = new CorrelationData(data.getCode());
        RabbitProducer.send(WeixinMessageQueueConstant.WEIXIN_MSG_MQ_TOPIC_EXCHANGE
                , WeixinMessageQueueConstant.WEIXIN_MSG_SEND_MQ_ROUTING_KEY
                , data, correlationData);

        log.info("添加到微信消息的发送队列中。{}", JsonMapperUtil.toJ(data));
    }

}
