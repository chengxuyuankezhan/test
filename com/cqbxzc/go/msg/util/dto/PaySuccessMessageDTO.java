package com.cqbxzc.go.msg.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 支付成功的消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaySuccessMessageDTO implements Serializable{

    /**
     * 业务系统标识
     */
    private String code;

    /**
     * 用户openid
     */
    @NotBlank(message = "用户openid不能为空")
    private String openId;

    /**
     * 表单id/预支付id
     */
    @NotBlank(message = "表单id不能为空")
    private String formId;

    /**
     * 跳转页面，可空
     */
    private String page;

    /**
     * 商家
     */
    private String supplierName;

    /**
     * 商品
     */
    private String goods;

    /**
     * 位置
     */
    private String position;

    /**
     * 出发时间
     */
    private String startTime;

    /**
     * 付款金额
     */
    private String payAmount;

    /**
     * 付款结果
     */
    private String payResult;

    /**
     * 付款时间
     */
    private String payTime;

    /**
     * 备注
     */
    private String remark;
}
