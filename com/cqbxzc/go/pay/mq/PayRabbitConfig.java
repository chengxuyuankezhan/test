package com.cqbxzc.go.pay.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayRabbitConfig {

	/**
	 * 定义交换机
	 *
	 * @return
	 */
	@Bean(name = PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE)
	public TopicExchange exchange() {

		return (TopicExchange) ExchangeBuilder.topicExchange(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE).durable(true).build();
	}

	/**
	 * 定义合租车付款队列
	 *
	 * @return
	 */
	@Bean(name = PayMessageQueueConstant.DOLMUS_PAYMENT_MQ_QUEUE)
	public Queue dolmusPaymentQueue() {

		return QueueBuilder.durable(PayMessageQueueConstant.DOLMUS_PAYMENT_MQ_QUEUE).build();
	}

	/**
	 * 合租车付款绑定
	 *
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean(PayMessageQueueConstant.DOLMUS_PAYMENT_MQ_ROUTING_KEY)
	public Binding dolmusPaymentBinding(@Qualifier(PayMessageQueueConstant.DOLMUS_PAYMENT_MQ_QUEUE) Queue queue,
			@Qualifier(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE) TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(PayMessageQueueConstant.DOLMUS_PAYMENT_MQ_ROUTING_KEY);
	}

	/**
	 * 定义合租车退款队列
	 *
	 * @return
	 */
	@Bean(name = PayMessageQueueConstant.DOLMUS_REFUND_MQ_QUEUE)
	public Queue dolmusRefundQueue() {

		return QueueBuilder.durable(PayMessageQueueConstant.DOLMUS_REFUND_MQ_QUEUE).build();
	}

	/**
	 * 合租车退款绑定
	 *
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean(PayMessageQueueConstant.DOLMUS_REFUND_MQ_ROUTING_KEY)
	public Binding dolmusRefundBinding(@Qualifier(PayMessageQueueConstant.DOLMUS_REFUND_MQ_QUEUE) Queue queue,
			@Qualifier(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE) TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(PayMessageQueueConstant.DOLMUS_REFUND_MQ_ROUTING_KEY);
	}
	
	/**
	 * 定义合租车付款队列
	 *
	 * @return
	 */
	@Bean(name = PayMessageQueueConstant.RTR_PAYMENT_MQ_QUEUE)
	public Queue rtrPaymentQueue() {

		return QueueBuilder.durable(PayMessageQueueConstant.RTR_PAYMENT_MQ_QUEUE).build();
	}

	/**
	 * 合租车付款绑定
	 *
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean(PayMessageQueueConstant.RTR_PAYMENT_MQ_ROUTING_KEY)
	public Binding rtrPaymentBinding(@Qualifier(PayMessageQueueConstant.RTR_PAYMENT_MQ_QUEUE) Queue queue,
			@Qualifier(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE) TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(PayMessageQueueConstant.RTR_PAYMENT_MQ_ROUTING_KEY);
	}

	/**
	 * 定义合租车退款队列
	 *
	 * @return
	 */
	@Bean(name = PayMessageQueueConstant.RTR_REFUND_MQ_QUEUE)
	public Queue rtrRefundQueue() {

		return QueueBuilder.durable(PayMessageQueueConstant.RTR_REFUND_MQ_QUEUE).build();
	}

	/**
	 * 合租车退款绑定
	 *
	 * @param queue
	 * @param exchange
	 * @return
	 */
	@Bean(PayMessageQueueConstant.RTR_REFUND_MQ_ROUTING_KEY)
	public Binding rtrRefundBinding(@Qualifier(PayMessageQueueConstant.RTR_REFUND_MQ_QUEUE) Queue queue,
			@Qualifier(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE) TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(PayMessageQueueConstant.RTR_REFUND_MQ_ROUTING_KEY);
	}
}
