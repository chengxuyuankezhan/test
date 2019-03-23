package com.cqbxzc.go.pay.mq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 支付MQ数据
 */
@Setter
@Getter
@ToString
@Accessors(chain = true)
public class PayMessageQueueData implements Serializable {

    /**
     * 订单号/退款单号
     */
    private String code;

    /**
     * 传入的数据，原样返回
     */
    private String attach;

    /**
     * 付款/退款状态
     */
    private String status;

    /**
     * 第三方支付返回的信息
     */
    private String message;
}
