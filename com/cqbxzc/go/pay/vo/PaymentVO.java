package com.cqbxzc.go.pay.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.cqbxzc.go.pay.dict.PayTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 付款需要的订单信息
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVO implements Serializable {

	/**
	 * 订单号
	 */
	@NotBlank(message = "订单号不能为空")
	private String orderCode;
	/**
	 * 出行模式
	 */
	@NotBlank(message = "出行模式不能为空")
	private String travelModel;
	/**
	 * 订单金额
	 */
	@NotNull(message = "订单金额不能为NULL")
	private BigDecimal amount;
	/**
	 * 订单内容，格式：商家名称-销售商品类目，如：腾讯-游戏
	 */
	@NotBlank(message = "订单内容不能为空")
	private String body;
	/**
	 * 支付方式
	 */
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private PayTypeEnum type = PayTypeEnum.weixin;
	/**
	 * 支付类型（支付方式下的支付类型，如微信支付下面的小程序支付）
	 */
	@Builder.Default
	private String category = "JSAPI";

	/**
	 * 支付用户
	 */
	@NotBlank(message = "支付用户不能为空")
	private String openid;

	/**
	 * 支付IP
	 */
	@Builder.Default
	private String ip = "127.0.0.1";

	/**
	 * 附加数据，会原样返回
	 */
	private String attach;
}
