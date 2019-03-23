package com.cqbxzc.go.msg.util.dto;

import live.jialing.core.beanvalidator.First;
import live.jialing.core.beanvalidator.Second;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 下单成功的消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSuccessMessageDTO implements Serializable {

    /**
     * 业务系统标识
     */
    private String code;

    /**
     * 用户openid
     */
    @NotBlank(message = "用户openid不能为空",groups = {First.class,Second.class})
    private String openId;

    /**
     * 表单id/预支付id
     */
    @NotBlank(message = "表单id不能为空",groups = {First.class,Second.class})
    private String formId;

    /**
     * 跳转页面，可空
     */
    private String page;


    /**
     * 商家名称
     */
    private String supplierName;

    /**
     * 商品
     */
    private String goods;

    /**
     * 出发时间
     */
    private String startTime;

    /**
     * 座位
     */
    private String position;

    /**
     * 金额
     */
    private String amount;

    /**
     * 备注
     */
    private String remark;
}
