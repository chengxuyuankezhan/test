package com.cqbxzc.go.pay.domain;

import com.cqbxzc.wxpay.*;
import com.cqbxzc.go.pay.dict.PaymentStatusEnum;
import com.cqbxzc.go.pay.infrastructure.PaymentCallback;
import com.cqbxzc.go.pay.vo.PaymentVO;
import live.jialing.core.beanvalidator.BeanValidators;
import live.jialing.core.exception.ServiceException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PaymentService {

	private final String success = "SUCCESS";
	private final String payment_error_message = "付款过程，内部错误";

	@Autowired
	private PaymentRecordRepository paymentRecordRepository;

	@Autowired
	private WeixinPayComponent weixinPayComponent;

	@Autowired
	private PaymentCallback paymentCallback;

	@Autowired
	protected Validator validator;

	/**
	 * 付款回调的处理
	 * 
	 * @param result
	 *            付款结果
	 */
	public void payNotify(@NonNull PaymentResult result) {
		// 定义错误消息
		String errorMsg = null;

		// 处理Return不成功的时候
		if (!success.equals(result.getReturnCode())) {
			errorMsg = MessageFormat.format("returnCode={0},returnMsg={1}", result.getReturnCode(), result.getReturnMsg());
		}

		// 处理result不成功的时候
		if (!success.equals(result.getResultCode())) {
			errorMsg = result.getErrCodeDes();
		}

		String orderCode = result.getOutTradeNo();

		Long paymentRecordId = PayCodeUtil.getPaymentRecordId(orderCode);

		Optional<PaymentRecord> paymentRecordOpt = paymentRecordRepository.findById(paymentRecordId);
		if (paymentRecordOpt.isPresent()) {

			PaymentRecord paymentRecord = paymentRecordOpt.get();

			// 判断该记录是否已经处理过了
			if (paymentRecord.getIsHandle()) {
				return;
			}

			// 判断金额是否一致
			if (paymentRecord.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(result.getTotalFee())) != 0) {
				// 支付金额与申请支付金额不一致
				errorMsg = MessageFormat.format("回调的订单金额{0}与支付金额{1}不一致", paymentRecord.getAmount().multiply(new BigDecimal(100)), new BigDecimal(result.getTotalFee()));
			}

			paymentRecord.setIsHandle(true);
			if (errorMsg != null) {
				paymentRecord.setStatus(PaymentStatusEnum.PAYERROR);
				paymentRecord.setErrorRemark(errorMsg);
				paymentRecordRepository.saveAndFlush(StringUtils.EMPTY, paymentRecord);
			} else {
				paymentRecord.setStatus(PaymentStatusEnum.SUCCESS);
				paymentRecord.setTimeEnd(LocalDateTime.parse(result.getTimeEnd(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
				paymentRecord.setRemoteId(result.getTransactionId());
				paymentRecord.setMchId(result.getMchId());
			}

			// 保存支付记录
			paymentRecord = paymentRecordRepository.saveAndFlush(StringUtils.EMPTY, paymentRecord);
			// 将支付结果发送到mq
			paymentCallback.callback(paymentRecord);
		}

	}

	/**
	 * 获取预付款信息，保存支付记录
	 *
	 * @param payment
	 * @return
	 */
	public RoutinePay prePay(@NonNull PaymentVO payment) {

		BeanValidators.validateWithException(validator, payment);

		Optional<PaymentRecord> paymentRecordOpt = findPaymentResultByOrder(payment.getOrderCode());
		if (paymentRecordOpt.isPresent()) {
			// 查询状态，如果已经支付成功，提示
			PaymentRecord paymentRecord = paymentRecordOpt.get();
			if (paymentRecord.getStatus().name().equals(PaymentStatusEnum.SUCCESS.name())) {
				throw new ServiceException(MessageFormat.format("订单{0}已经成功支付，不能重复支付", payment.getOrderCode()));
			} else if (paymentRecord.getStatus().name().equals(PaymentStatusEnum.USERPAYING.name())) {
				throw new ServiceException(MessageFormat.format("订单{0}正在支付中，请等待支付结果", payment.getOrderCode()));
			} else if (paymentRecord.getStatus().name().equals(PaymentStatusEnum.NOTPAY.name())) {
				// 判断时间是否超过2个小时（这里考虑数据传输时间，设置为100分钟）
				Duration duration = Duration.between(paymentRecord.getCreateDate(), LocalDateTime.now());
				if (StringUtils.isNotBlank(paymentRecord.getPrePayId()) && duration.toMinutes() > 100) {
					try {
						return weixinPayComponent.routinePay(paymentRecord.getPrePayId());
					} catch (Exception e) {
						throw new PayException(payment_error_message, e);
					}
				}
			} else {
				// 修改支付记录，关闭
				paymentRecord.setIsHandle(true);
				paymentRecordRepository.saveAndFlush(StringUtils.EMPTY, paymentRecord);
			}
		}
		// 保存支付记录，得到支付记录id
		PaymentRecord paymentRecord = PaymentRecord.builder()
				.type(payment.getType())
				.category(payment.getCategory())
				.travelModel(payment.getTravelModel())
				.amount(payment.getAmount())
				.orderCode(payment.getOrderCode())
				.status(PaymentStatusEnum.NOTPAY)
				.attach(payment.getAttach())
				.payer(payment.getOpenid())
				.isHandle(false)
				.build();
		paymentRecord = paymentRecordRepository.saveAndFlush(StringUtils.EMPTY, paymentRecord);

		String code = PayCodeUtil.getPaymentCode(paymentRecord.getId());
		// 2.1构建预支付请求参数
		PaymentOrderRequest request = PaymentOrderRequest.builder()
				.outTradeNo(code)
				.body(payment.getBody())
				.tradeType(payment.getCategory())
				.totalFee(String.valueOf(payment.getAmount().multiply(new BigDecimal(100)).intValue()))
				.openid(payment.getOpenid())
				.spbillCreateIp(payment.getIp())
				.build();
		PaymentOrderResponse response = null;
		try {
			response = weixinPayComponent.unifiedOrder(request);
		} catch (Exception e) {
			throw new PayException(payment_error_message, e);
		}
		// 2.2对预支付请求结果进行验证
		if (!success.equals(response.getReturnCode())) {
			throw new ServiceException(response.getReturnMsg());
		}
		if (!success.equals(response.getResultCode())) {
			throw new ServiceException(response.getErrCodeDes());
		}

		// 2.3将预支付信息存入付款记录
		paymentRecord.setCode(code);
		paymentRecord.setPrePayId(response.getPrepayId());
		paymentRecordRepository.saveAndFlush(StringUtils.EMPTY, paymentRecord);

		// 2.4获取预支付信息
		try {
			return weixinPayComponent.routinePay(response.getPrepayId());
		} catch (Exception e) {
			throw new PayException(payment_error_message, e);
		}
	}

	/**
	 * 根据订单号查询付款结果
	 * <p>
	 * 1、查询本系统的付款记录及付款结果 <br>
	 * 2、如果有付款结果，返回付款结果 <br>
	 * 3、如果无付款结果，则从微信支付系统中查询 <br>
	 * 4、保存从微信支付系统查询的结果<br>
	 * 5、返回付款结果
	 *
	 * @param orderCode
	 *            订单号
	 * @return
	 */
	@Transactional
	public Optional<PaymentRecord> findPaymentResultByOrder(String orderCode) {

		if (StringUtils.isBlank(orderCode)) {
			return Optional.empty();
		}

		// 查询本地记录
		Optional<PaymentRecord> paymentRecordOpt = paymentRecordRepository.findFirstByOrderCodeOrderByIdDesc(orderCode);
		if (!paymentRecordOpt.isPresent()) {
			return Optional.empty();
		}
		PaymentRecord paymentRecord = paymentRecordOpt.get();
		// 如果记录的状态为NOTPAY或者USERPAYING状态，就从支付系统中查询
		if (org.apache.commons.lang3.StringUtils.equalsAny(paymentRecord.getStatus().name(), PaymentStatusEnum.NOTPAY.name(), PaymentStatusEnum.USERPAYING.name())) {
			PaymentResult result = null;
			try {
				result = weixinPayComponent.queryByOutTraderNo(paymentRecord.getCode());
			} catch (Exception e) {
				throw new PayException(payment_error_message, e);
			}
			// a.判断返回状态码
			if (!result.getReturnCode().equals(success)) {
				log.error("通过微信查询付款结果返回状态码错误", result.getReturnMsg());
				paymentRecord.setErrorRemark(result.getReturnMsg());
				return Optional.of(paymentRecord);
			}
			// b.判断返回的业务代码
			if (!result.getResultCode().equals(success)) {
				log.error("通过微信查询付款结果返回业务码错误", result.getErrCodeDes());
				paymentRecord.setErrorRemark(result.getErrCodeDes());
				return Optional.of(paymentRecord);
			}
			// c.判断交易状态
			String status = result.getTradeState();
			// 如果状态不是未支付状态，需要同步付款状态
			if (!status.equals(PaymentStatusEnum.NOTPAY.name())) {
				if (status.equals(PaymentStatusEnum.SUCCESS.name())) {
					paymentRecord.setTimeEnd(LocalDateTime.parse(result.getTimeEnd(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
					paymentRecord.setRemoteId(result.getTransactionId());
					paymentRecord.setIsHandle(true);
					paymentRecord.setMchId(result.getMchId());
				} else if (status.equals(PaymentStatusEnum.USERPAYING.name())) {
					paymentRecord.setIsHandle(false);
				} else {
					paymentRecord.setIsHandle(true);
					paymentRecord.setErrorRemark(result.getTradeStateDesc());
				}
				paymentRecord.setStatus(PaymentStatusEnum.valueOf(result.getTradeState()));
				// 修改支付记录
				paymentRecord = paymentRecordRepository.saveAndFlush(StringUtils.EMPTY, paymentRecord);

				// 如果是已经处理的状态，需要告诉将支付结果发送到mq
				/*if (paymentRecord.getIsHandle()) {
					paymentCallback.callback(paymentRecord);
				}*/
			}
		}
		return Optional.of(paymentRecord);
	}

}