package com.cqbxzc.go.pay.infrastructure;

import com.cqbxzc.go.pay.constant.Constants;
import com.cqbxzc.go.pay.domain.PaymentRecord;
import com.cqbxzc.go.pay.mq.PayMessageQueueConstant;
import com.cqbxzc.go.pay.mq.PayMessageQueueData;
import live.jialing.util.mapper.JsonMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 付款回调订单
 */
@Slf4j
@Component
public class PaymentCallback {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 付款结果回调订单
	 *
	 */
	public void callback(PaymentRecord paymentRecord) {

		if (StringUtils.isNotBlank(paymentRecord.getTravelModel()) && StringUtils.isNotBlank(paymentRecord.getStatus().name())) {

			if (paymentRecord.getTravelModel().equals(Constants.DOLMUS_TRAVEL_MODE)) {
				dolmusCallback(paymentRecord.getOrderCode(), paymentRecord.getStatus().name(), paymentRecord.getAttach(), paymentRecord.getErrorRemark());
			} else if (paymentRecord.getTravelModel().equals(Constants.LINE_TRAVEL_MODE)) {
				rtrCallback(paymentRecord.getOrderCode(), paymentRecord.getStatus().name(), paymentRecord.getAttach(), paymentRecord.getErrorRemark());
			}
		}
	}

	/**
	 * 合租车付款结果回调
	 *
	 * @param status
	 * @param attach
	 */
	private void dolmusCallback(String code, String status, String attach, String message) {

		PayMessageQueueData data = new PayMessageQueueData();
		data.setCode(code).setAttach(attach).setStatus(status).setMessage(message);

		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE,
				PayMessageQueueConstant.DOLMUS_PAYMENT_MQ_ROUTING_KEY, data, correlationData);

		log.info("付款结果添加到合租车付款回调队列中。{}", JsonMapperUtil.toJ(data));
	}
	
	/**
	 * 班线付款结果回调
	 *
	 * @param status
	 * @param attach
	 */
	private void rtrCallback(String code, String status, String attach, String message) {

		PayMessageQueueData data = new PayMessageQueueData();
		data.setCode(code).setAttach(attach).setStatus(status).setMessage(message);

		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE,
				PayMessageQueueConstant.RTR_PAYMENT_MQ_ROUTING_KEY, data, correlationData);

		log.info("付款结果添加到班线付款回调队列中。{}", JsonMapperUtil.toJ(data));
	}


}
