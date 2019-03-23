package com.cqbxzc.go.msg.dispatch.domain;

import com.cqbxzc.go.msg.dict.MessageEventEnum;
import com.cqbxzc.go.msg.mq.MessageTaskMQ;
import com.cqbxzc.go.msg.receive.MessageTask;
import live.jialing.core.data.LinkType;
import live.jialing.core.data.jpa.CustomJpaRepository;
import live.jialing.core.domain.service.GeneralService;
import live.jialing.core.domain.service.impl.SimpleGeneralService;
import live.jialing.data.util.SpecificationUtil;
import live.jialing.util.mapper.JsonMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MessageTaskRecordService extends SimpleGeneralService<MessageTaskRecord, Long> implements GeneralService<MessageTaskRecord, Long> {

    @Autowired
    private MessageTaskRecordRepository messageTaskRecordRepository;

    @Override
    public CustomJpaRepository<MessageTaskRecord, Long> getRepository() {
        return messageTaskRecordRepository;
    }

    @Override
    public Specification<MessageTaskRecord> buildSpecification(Class<MessageTaskRecord> classz, Map<String, Object> searchParams, LinkType linkType) {
        return SpecificationUtil.buildSpecification(MessageTaskRecord.class, searchParams, linkType);
    }


    /**
     * 保存消息任务记录
     *
     * @param messageTask 消息任务
     */
    @RabbitListener(queues = MessageTaskMQ.MESSAGE_TASK_PERSISTENCE_MQ_QUEUE)
    @RabbitHandler
    public void persistence(@Payload MessageTask messageTask) {

        log.info("监听到消息任务，进入消息任务持久化");

        if (messageTask == null) {
            return;
        }

        log.debug("开始持久化消息任务，{}", messageTask);

        MessageTaskRecord messageTaskRecord = MessageTaskRecord.builder()
                .code(messageTask.getCode())
                .eventCode(MessageEventEnum.valueOf(messageTask.getEventCode()))
                .level(messageTask.getLevel())
                .sourceCode(messageTask.getSourceCode())
                .receiver(JsonMapperUtil.toJ(messageTask.getReceiver()))
                .content(JsonMapperUtil.toJ(messageTask.getContent()))
                .build();

        this.create(messageTaskRecord);
    }

}
