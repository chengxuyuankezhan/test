package com.cqbxzc.go.msg.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 退款成功消息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundSuccessMessageDTO implements Serializable{


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
     * 商品名称
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
     * 付款金额
     */
    private String payAmount;

    /**
     * 退款金额
     */
    private String refundAmount;

    /**
     * 退款结果
     */
    private String refundResult;

    /**
     * 退款时间
     */
    private String refundTime;

    /**
     * 备注
     */
    private String remark;

}
