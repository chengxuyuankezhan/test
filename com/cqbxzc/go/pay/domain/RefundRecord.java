package com.cqbxzc.go.pay.domain;

import com.cqbxzc.go.pay.dict.RefundStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import live.jialing.core.beanvalidator.First;
import live.jialing.core.beanvalidator.Second;
import live.jialing.data.domain.JpaEntity;
import live.jialing.util.time.ClockUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = RefundRecord.class)
@Entity
@Table(name = "refund_record")
public class RefundRecord extends JpaEntity<Long> {

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@NotNull(message = "退款记录id不能为空", groups = Second.class)
	private Long id;

	/**
	 * 退款单号，唯一
	 */
	@Column(length = 32, unique = true, nullable = false, updatable = false)
	@NotBlank(message = "退款单号不能为空", groups = {First.class})
	@Length(max = 32, message = "退款单号长度不能超过32")
	private String code;
	/**
	 * 订单号
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotBlank(message = "订单号不能为空", groups = {First.class})
	@Length(max = 32, message = "订单号长度不能超过32")
	private String orderCode;
	/**
	 * 出行模式
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotBlank(message = "出行模式不能为空", groups = {First.class})
	@Length(max = 32, message = "出行模式长度不能超过32")
	private String travelModel;
	/**
	 * 退款金额
	 */
	@Column(length = 32, updatable = false)
	@NotNull(message = "退款金额不能为空", groups = {First.class})
	private BigDecimal amount;
	/**
	 * 退款记录状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 32, nullable = false)
	private RefundStatusEnum status;
	/**
	 * 退款成功时间
	 */
	@Column(columnDefinition = "timestamp(6) null")
	@DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
	private LocalDateTime successTime;
	/**
	 * 退款失败原因
	 */
	@Column(length = 256)
	@Length(max = 256, message = "退款失败原因长度不能超过256")
	private String errorRemark;
	/**
	 * 支付平台退款单号
	 */
	@Column(length = 32)
	@Length(max = 32, message = "支付平台退款单号长度不能超过32")
	private String remoteId;
	/**
	 * 商户号
	 */
	@Column(length = 32)
	@Length(max = 32, message = "商户号长度不能超过32")
	private String mchId;
	/**
	 * 外部调用支付传入的附加数据，会原样返回给外部
	 */
	@Column(length = 256)
	@Length(max = 256, message = "附加数据长度不能超过256")
	private String attach;
}
