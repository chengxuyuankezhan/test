package com.cqbxzc.go.pay.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PayCodeUtil {

	/**
	 * 根据付款记录id得到商户订单号
	 *
	 * @param paymentRecordId
	 *            付款记录id
	 * @return
	 */
	public static String getPaymentCode(Long paymentRecordId) {
		String prefix = "pts"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		return prefix + paymentRecordId;
	}

	/**
	 * 根据付款单号得到付款记录id
	 * 
	 * @param paymentCode
	 * @return
	 */
	public static Long getPaymentRecordId(String paymentCode) {
		paymentCode = paymentCode.substring(20);
		return Long.parseLong(paymentCode);
	}

}
