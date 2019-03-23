package com.cqbxzc.go.pay.vo;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 退款时传入的参数
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundVO {

	/**
	 * 订单号
	 */
	@NotBlank(message = "订单号不能为空")
	private String orderCode;
	/**
	 * 退款单号
	 */
	@NotBlank(message = "退款单号不能为空")
	private String code;
	/**
	 * 出行模式
	 */
	@NotBlank(message = "出现模式不能为空")
	private String travelModel;
	/**
	 * 订单金额
	 */
	@NotNull(message = "订单金额不能为空")
	private BigDecimal totalAmount;
	/**
	 * 退款金额
	 */
	@NotNull(message = "退款金额不能为空")
	private BigDecimal amount;
	/**
	 * 附加数据，原样返回
	 */
	private String attach;
}
