package com.cqbxzc.go.pnp.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义PNP相关的消息队列
 */
@Configuration
public class PnpRabbitConfig {

    /**
     * PNP MQ 交换机
     *
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_MQ_TOPIC_EXCHANGE)
    public TopicExchange topicExchange() {

        return (TopicExchange) ExchangeBuilder
                .topicExchange(PnpQueueConstant.PNP_MQ_TOPIC_EXCHANGE)
                .durable(true).build();
    }


    /**
     * 定义电话绑定的队列
     *
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_BINDING_MQ_QUEUE)
    public Queue queue() {

        return QueueBuilder.durable(PnpQueueConstant.PNP_BINDING_MQ_QUEUE).build();
    }


    /**
     * 定义绑定
     *
     * @param queue
     * @param topicExchange
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_BINDING_MQ_ROUTING_KEY)
    public Binding binding(@Qualifier(PnpQueueConstant.PNP_BINDING_MQ_QUEUE) Queue queue
            , @Qualifier(PnpQueueConstant.PNP_MQ_TOPIC_EXCHANGE) TopicExchange topicExchange) {

        return BindingBuilder.bind(queue).to(topicExchange).with(PnpQueueConstant.PNP_BINDING_MQ_ROUTING_KEY);
    }

    /**
     * 定义绑定结果的队列
     *
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_BINDING_RESULT_MQ_QUEUE)
    public Queue resultQueue() {

        return QueueBuilder.durable(PnpQueueConstant.PNP_BINDING_RESULT_MQ_QUEUE).build();
    }

    /**
     * 定义结果的绑定
     *
     * @param queue
     * @param topicExchange
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_BINDING_RESULT_MQ_ROUTING_KEY)
    public Binding binding2(@Qualifier(PnpQueueConstant.PNP_BINDING_RESULT_MQ_QUEUE) Queue queue
            , @Qualifier(PnpQueueConstant.PNP_MQ_TOPIC_EXCHANGE) TopicExchange topicExchange) {

        return BindingBuilder.bind(queue).to(topicExchange).with(PnpQueueConstant.PNP_BINDING_RESULT_MQ_ROUTING_KEY);
    }

    /**
     * 定义解绑绑定的队列
     *
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_UNTYING_MQ_QUEUE)
    public Queue untyingQueue() {

        return QueueBuilder.durable(PnpQueueConstant.PNP_UNTYING_MQ_QUEUE).build();
    }

    /**
     * 定义解绑电话队列和交换机的绑定
     *
     * @param queue         解绑电话的队列
     * @param topicExchange 解绑电话的交换机
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_UNTYING_MQ_ROUTING_KEY)
    public Binding untyingBinding(@Qualifier(PnpQueueConstant.PNP_UNTYING_MQ_QUEUE) Queue queue
            , @Qualifier(PnpQueueConstant.PNP_MQ_TOPIC_EXCHANGE) TopicExchange topicExchange) {

        return BindingBuilder.bind(queue).to(topicExchange).with(PnpQueueConstant.PNP_UNTYING_MQ_ROUTING_KEY);
    }

    /**
     * 定义解绑结果的队列
     *
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_UNTYING_RESULT_MQ_QUEUE)
    public Queue untyingResultQueue() {

        return QueueBuilder.durable(PnpQueueConstant.PNP_UNTYING_RESULT_MQ_QUEUE).build();
    }


    /**
     * 定义解绑结果消息队列和交换机的绑定
     *
     * @param queue         解绑结果消息队列
     * @param topicExchange 解绑结果交换机
     * @return
     */
    @Bean(name = PnpQueueConstant.PNP_UNTYING_RESULT_MQ_ROUTING_KEY)
    public Binding untyingResultBinding(@Qualifier(PnpQueueConstant.PNP_UNTYING_RESULT_MQ_QUEUE) Queue queue
            , @Qualifier(PnpQueueConstant.PNP_MQ_TOPIC_EXCHANGE) TopicExchange topicExchange) {

        return BindingBuilder.bind(queue).to(topicExchange).with(PnpQueueConstant.PNP_UNTYING_RESULT_MQ_ROUTING_KEY);
    }


}
