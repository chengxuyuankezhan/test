package com.cqbxzc.go.rtc.mq;

import com.cqbxzc.middleware.rtc.mq.RtcQueueConstant;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义即时通信相关的消息队列
 */
@Configuration
public class RtcRabbitConfig {


    /**
     * 即时通信 MQ 交换机
     *
     * @return
     */
    @Bean(name = RtcQueueConstant.RTC_MQ_TOPIC_EXCHANGE)
    public TopicExchange topicExchange() {

        return (TopicExchange) ExchangeBuilder
                .topicExchange(RtcQueueConstant.RTC_MQ_TOPIC_EXCHANGE)
                .durable(true).build();
    }


    /**
     * 定义即时通信的队列
     *
     * @return
     */
    @Bean(name = RtcQueueConstant.RTC_MQ_QUEUE)
    public Queue queue() {

        return QueueBuilder.durable(RtcQueueConstant.RTC_MQ_QUEUE).build();
    }


    /**
     * 定义绑定
     *
     * @param queue
     * @param topicExchange
     * @return
     */
    @Bean(name = RtcQueueConstant.RTC_MQ_ROUTING_KEY)
    public Binding binding(@Qualifier(RtcQueueConstant.RTC_MQ_QUEUE) Queue queue
            , @Qualifier(RtcQueueConstant.RTC_MQ_TOPIC_EXCHANGE) TopicExchange topicExchange) {

        return BindingBuilder.bind(queue).to(topicExchange).with(RtcQueueConstant.RTC_MQ_ROUTING_KEY);
    }

    /**
     * 定义绑定结果的队列
     *
     * @return
     */
    @Bean(name = RtcQueueConstant.RTC_RESULT_MQ_QUEUE)
    public Queue resultQueue() {

        return QueueBuilder.durable(RtcQueueConstant.RTC_RESULT_MQ_QUEUE).build();
    }

    /**
     * 定义结果的绑定
     *
     * @param queue
     * @param topicExchange
     * @return
     */
    @Bean(name = RtcQueueConstant.RTC_RESULT_MQ_ROUTING_KEY)
    public Binding binding2(@Qualifier(RtcQueueConstant.RTC_RESULT_MQ_QUEUE) Queue queue
            , @Qualifier(RtcQueueConstant.RTC_MQ_TOPIC_EXCHANGE) TopicExchange topicExchange) {

        return BindingBuilder.bind(queue).to(topicExchange).with(RtcQueueConstant.RTC_RESULT_MQ_ROUTING_KEY);
    }

}
