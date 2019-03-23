package com.cqbxzc.go.msg.dict;

/**
 * 消息事件
 */
public enum MessageEventEnum {

    /**
     * 完善会员信息获取验证码
     */
    verification_code_perfect,

    /**
     * 下单成功
     */
    order_success,

    /**
     * 支付成功
     */
    pay_success,

    /**
     * 退款成功
     */
    refund_success,

    /**
     * 行程取消
     */
    travel_cancel,

    /**
     * 调度
     */
    dispatcher,

    /**
     * 司机领车
     */
    driver_land_car,

    /**
     * 修改手机号的验证码
     */
    verification_code_edit_mobile,

    /**
     * 班次准备
     */
    trip_ready,

    /**
     * 班次出发
     */
    trip_travel,

    /**
     * 系统异常
     */
    system_exception
}
