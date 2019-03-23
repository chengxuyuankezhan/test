package com.cqbxzc.go.msg.dict;

/**
 * 消息来源
 */
public enum MsgSourceEnum {

    /**
     * 下单成功
     */
    order_success,

    /**
     * 付款成功
     */
    payment_success,

    /**
     * 付款失败
     */
    payment_fail,

    /**
     * 退款成功
     */
    refund_success,

    /**
     * 退款失败
     */
    refund_fail,

    /**
     * 订单取消
     */
    order_cancel,

    /**
     * 验证码
     */
    verify_code
}
