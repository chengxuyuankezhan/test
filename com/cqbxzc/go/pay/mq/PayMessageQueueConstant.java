package com.cqbxzc.go.pay.mq;

/**
 * 支付/退款MQ常量
 */
public class PayMessageQueueConstant {

    /**
     * topic交换机
     */
    public static final String PAY_MQ_TOPIC_EXCHANGE = "pay_mq_topic_exchange";

    /**
     * 合租车付款队列
     */
    public static final String DOLMUS_PAYMENT_MQ_QUEUE = "dolmus_payment_mq_queue";

    /**
     * 合租车付款队列的key
     */
    public static final String DOLMUS_PAYMENT_MQ_ROUTING_KEY = "dolmus_payment_mq_routing_key";

    /**
     * 合租车退款队列
     */
    public static final String DOLMUS_REFUND_MQ_QUEUE = "dolmus_refund_mq_queue";

    /**
     * 合租车退款队列的key
     */
    public static final String DOLMUS_REFUND_MQ_ROUTING_KEY = "dolmus_refund_mq_routing_key";
    
    /**
     * 班线付款队列
     */
    public static final String RTR_PAYMENT_MQ_QUEUE = "rtr_payment_mq_queue";

    /**
     * 班线付款队列的key
     */
    public static final String RTR_PAYMENT_MQ_ROUTING_KEY = "rtr_payment_mq_routing_key";

    /**
     * 班线退款队列
     */
    public static final String RTR_REFUND_MQ_QUEUE = "rtr_refund_mq_queue";

    /**
     * 班线退款队列的key
     */
    public static final String RTR_REFUND_MQ_ROUTING_KEY = "rtr_refund_mq_routing_key";


}
