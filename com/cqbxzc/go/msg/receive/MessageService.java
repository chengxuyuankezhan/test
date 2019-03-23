package com.cqbxzc.go.msg.receive;

import com.cqbxzc.go.msg.mq.MessageTaskMQ;
import com.cqbxzc.mq.RabbitProducer;
import live.jialing.core.beanvalidator.BeanValidators;
import live.jialing.util.general.IdGenerator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

/**
 * 消息服务
 */
@Slf4j
@Service
public class MessageService {

    @Autowired
    private Validator validator;

    /**
     * 接收消息任务
     *
     * @param messageTask 消息任务
     */
    public void receiveMessageTask(@NonNull MessageTask messageTask) {

        BeanValidators.validateWithException(validator, messageTask);

        messageTask.setCode(IdGenerator.uuid2());
        log.info("接收到消息任务：{}", messageTask);

        // 异步MQ处理
        CorrelationData correlationData = new CorrelationData(messageTask.getCode());
        RabbitProducer.send(MessageTaskMQ.MESSAGE_TASK_MQ_FANOUT_EXCHANGE, messageTask, correlationData);
        log.info("消息任务：{} 已加入到队列", messageTask.getCode());

        // 监听1：持久化任务
        // 监听2：解析并分发任务
    }


}
