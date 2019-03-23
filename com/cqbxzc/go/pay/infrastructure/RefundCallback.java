package com.cqbxzc.go.pay.infrastructure;

import com.cqbxzc.go.pay.constant.Constants;
import com.cqbxzc.go.pay.domain.RefundRecord;
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
 * 退款回调订单
 */
@Slf4j
@Component
public class RefundCallback {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 退款结果回调订单
	 *
	 * @param refundRecord 退款记录
	 */
	public void callback(RefundRecord refundRecord) {

		if (StringUtils.isNotBlank(refundRecord.getTravelModel()) && StringUtils.isNotBlank(refundRecord.getStatus().name())) {

			if (refundRecord.getTravelModel().equals(Constants.DOLMUS_TRAVEL_MODE)) {
				dolmusCallback(refundRecord.getCode(), refundRecord.getStatus().name(), refundRecord.getAttach(), refundRecord.getErrorRemark());
			} else if (refundRecord.getTravelModel().equals(Constants.LINE_TRAVEL_MODE)) {
				lineCallback(refundRecord.getCode(), refundRecord.getStatus().name(), refundRecord.getAttach(), refundRecord.getErrorRemark());
			}
		}
	}

	/**
	 * 合租车退款结果回调
	 *
	 * @param status
	 * @param attach
	 */
	private void dolmusCallback(String code, String status, String attach, String message) {

		PayMessageQueueData data = new PayMessageQueueData();
		data.setCode(code).setAttach(attach).setStatus(status).setMessage(message);

		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE,
				PayMessageQueueConstant.DOLMUS_REFUND_MQ_ROUTING_KEY, data, correlationData);

		log.info("退款结果添加到合租车退款款回调队列中。{}", JsonMapperUtil.toJ(data));
	}
	
	/**
	 * 班线退款结果回调
	 *
	 * @param status
	 * @param attach
	 */
	private void lineCallback(String code, String status, String attach, String message) {

		PayMessageQueueData data = new PayMessageQueueData();
		data.setCode(code).setAttach(attach).setStatus(status).setMessage(message);

		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(PayMessageQueueConstant.PAY_MQ_TOPIC_EXCHANGE,
				PayMessageQueueConstant.RTR_REFUND_MQ_ROUTING_KEY, data, correlationData);

		log.info("退款结果添加到班线退款款回调队列中。{}", JsonMapperUtil.toJ(data));
	}

}
