package com.cqbxzc.go.msg.adapter.wxmsg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 模板消息任务
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeixinMessageTask implements Serializable {

    /**
     * 任务code
     */
    private String taskCode;

    /**
     * 接收者（用户）的openid
     */
    private String openId;

    /**
     * 点击模板查看详情跳转页面，不填则模板无跳转
     */
    private String page;

    /**
     * 表单提交场景下，为submit事件带上的formId
     */
    private String formId;

    /**
     * 支付场景下，为本次支付的prepay_id
     */
    private String prepayId;

    /**
     * 模板
     */
    private String template;

    /**
     * 模板参数
     */
    private Map<String, String> param;

    /**
     * 模板需要放大的关键词，不填则默认无放大
     */
    private String emphasisKeyword;

}
