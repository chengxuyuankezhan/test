package com.cqbxzc.go.pay.dict;

/**
 * 退款状态
 *
 */
public enum RefundStatusEnum {

	/**
	 * 退款成功
	 */
	SUCCESS,

	/**
	 * 退款关闭
	 */
	REFUNDCLOSE,

	/**
	 * 退款处理中,定义为本系统初始退款状态
	 */
	PROCESSING,

	/**
	 * 退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败
	 */
	CHANGE

}
