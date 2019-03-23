package com.cqbxzc.go.pnp.mq;

/**
 * 私密电话的相关mq常量
 */
public class PnpQueueConstant {

    /**
     * PNP MQ的交换机
     */
    public static final String PNP_MQ_TOPIC_EXCHANGE = "PNP_MQ_TOPIC_EXCHANGE";

    /**
     * 绑定电话的队列
     */
    public static final String PNP_BINDING_MQ_QUEUE = "pnp_binding_mq_queue";
    /**
     * 绑定电话的key
     */
    public static final String PNP_BINDING_MQ_ROUTING_KEY = "pnp_binding_mq_routing_key";

    /**
     * 绑定结果的队列
     */
    public static final String PNP_BINDING_RESULT_MQ_QUEUE = "pnp_binding_result_mq_queue";

    /**
     * 绑定结果的key
     */
    public static final String PNP_BINDING_RESULT_MQ_ROUTING_KEY = "pnp_binding_result_mq_routing_key";

    /**
     * 解绑电话的队列
     */
    public static final String PNP_UNTYING_MQ_QUEUE = "pnp_untying_mq_queue";


    /**
     * 解绑电话的key
     */
    public static final String PNP_UNTYING_MQ_ROUTING_KEY = "pnp_untying_mq_routing_key";


    /**
     * 解绑结果的队列
     */
    public static final String PNP_UNTYING_RESULT_MQ_QUEUE = "pnp_untying_result_mq_queue";


    /**
     * 解绑结果的key
     */
    public static final String PNP_UNTYING_RESULT_MQ_ROUTING_KEY = "pnp_untying_result_mq_routing_key";


}
