package com.cqbxzc.go.msg.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 消息任务的消息队列
 */
@Configuration
public class MessageTaskMQ {

    /**
     * 消息任务的topic交换机
     */
    public static final String MESSAGE_TASK_MQ_FANOUT_EXCHANGE = "message_task_fanout_exchange";

    /**
     * 消息任务持久化的队列
     */
    public static final String MESSAGE_TASK_PERSISTENCE_MQ_QUEUE = "message_task_persistence_mq_queue";

    /**
     * 消息任分发调度的队列
     */
    public static final String MESSAGE_TASK_DISPATCH_MQ_QUEUE = "message_task_dispatch_mq_queue";


    /**
     * 定义交换机(广播)
     *
     * @return
     */
    @Bean(name = MessageTaskMQ.MESSAGE_TASK_MQ_FANOUT_EXCHANGE)
    public FanoutExchange exchange() {

        return (FanoutExchange) ExchangeBuilder
                .fanoutExchange(MessageTaskMQ.MESSAGE_TASK_MQ_FANOUT_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 消息任务持久化的队列
     *
     * @return
     */
    @Bean(name = MessageTaskMQ.MESSAGE_TASK_PERSISTENCE_MQ_QUEUE)
    public Queue messageTaskPersistenceQueue() {

        return QueueBuilder
                .durable(MessageTaskMQ.MESSAGE_TASK_PERSISTENCE_MQ_QUEUE)
                .build();
    }

    /**
     * 消息任务持久化的绑定
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding messageTaskPersistenceBinding(
            @Qualifier(MessageTaskMQ.MESSAGE_TASK_PERSISTENCE_MQ_QUEUE) Queue queue,
            @Qualifier(MessageTaskMQ.MESSAGE_TASK_MQ_FANOUT_EXCHANGE) FanoutExchange exchange) {

        return BindingBuilder.bind(queue).to(exchange);
    }

    /**
     * 消息任分发调度的队列
     *
     * @return
     */
    @Bean(name = MessageTaskMQ.MESSAGE_TASK_DISPATCH_MQ_QUEUE)
    public Queue messageTaskDispatchQueue() {

        return QueueBuilder
                .durable(MessageTaskMQ.MESSAGE_TASK_DISPATCH_MQ_QUEUE)
                .build();
    }

    /**
     * 消息任分发调度的绑定
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding messageTaskDispatchBinding(
            @Qualifier(MessageTaskMQ.MESSAGE_TASK_DISPATCH_MQ_QUEUE) Queue queue,
            @Qualifier(MessageTaskMQ.MESSAGE_TASK_MQ_FANOUT_EXCHANGE) FanoutExchange exchange) {

        return BindingBuilder.bind(queue).to(exchange);
    }

}
