package com.cqbxzc.go.pay.dict;

/**
 * 微信付款状态
 *
 */
public enum PaymentStatusEnum {

	/**
	 * 支付成功
	 */
	SUCCESS,

	/**
	 * 转入退款
	 */
	REFUND,

	/**
	 * 未支付
	 */
	NOTPAY,

	/**
	 * 已关闭
	 */
	CLOSED,

	/**
	 * 已撤销（刷卡支付）
	 */
	REVOKED,

	/**
	 * 用户支付中
	 */
	USERPAYING,

	/**
	 * 支付失败(其他原因，如银行返回失败)
	 */
	PAYERROR

}
