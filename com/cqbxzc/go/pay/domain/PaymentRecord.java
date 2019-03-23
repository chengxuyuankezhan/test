package com.cqbxzc.go.pay.domain;

import com.cqbxzc.go.pay.dict.PayTypeEnum;
import com.cqbxzc.go.pay.dict.PaymentStatusEnum;
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
 * 付款记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = PaymentRecord.class)
@Entity
@Table(name = "payment_record")
public class PaymentRecord extends JpaEntity<Long> {
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@NotNull(message = "付款记录id不能为空", groups = Second.class)
	private Long id;

	/**
	 * 付款单号
	 */
	@Column(length = 32)
	@NotBlank(message = "付款单号不能为空",groups = {First.class})
	@Length(max = 32,message = "付款单号长度不能超过32")
	private String code;

	/**
	 * 商户订单号
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotBlank(message = "订单号不能为空", groups = { First.class })
	@Length(max = 32, message = "订单号长度不能超过32")
	private String orderCode;

	/**
	 * 出行模式
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotBlank(message = "出行模式不能为空", groups = { First.class })
	@Length(max = 32, message = "出行模式长度不能超过32")
	private String travelModel;
	/**
	 * 付款方式
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 32, nullable = false, updatable = false)
	private PayTypeEnum type;

	/**
	 * 具体付款类型
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotBlank(message = "付款类型不能为空", groups = { First.class })
	@Length(max = 32, message = "付款类型不能超过32")
	private String category;

	/**
	 * 付款金额
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotNull(message = "付款金额不能为空", groups = { First.class })
	private BigDecimal amount;

	/**
	 * 付款状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 32, nullable = false)
	private PaymentStatusEnum status;

	/**
	 * 付款完成时间
	 */
	@Column(columnDefinition = "timestamp(6) null")
	@DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
	private LocalDateTime timeEnd;

	/**
	 * 当付款失败时，记录失败原因
	 */
	@Column(length = 256)
	@Length(max = 256, message = "错误描述的长度不能超过256")
	private String errorRemark;

	/**
	 * 支付平台生成的订单号
	 */
	@Column(length = 32)
	@Length(max = 32, message = "支付平台方的订单id长度不能超过32")
	private String remoteId;

	/**
	 * 商户号
	 */
	@Column(length = 32)
	@Length(max = 32, message = "商户号长度不能超过32")
	private String mchId;

	/**
	 * 是否已经处理
	 */
	@Column(nullable = false)
	@NotNull(message = "[是否处理]不能为NULL")
	private Boolean isHandle;

	/**
	 * 外部调用付款传入的附加数据，会原样返回
	 */
	@Column(length = 256)
	@Length(max = 256, message = "附加数据长度不能超过256")
	private String attach;

	/**
	 * 付款人信息
	 */
	@Column(length = 256)
	@Length(max = 256, message = "付款人信息长度不能超过256")
	private String payer;

	/**
	 * 预支付id
	 */
	@Column(length = 256)
	@Length(max = 256, message = "预支付id长度不能超过256")
	private String prePayId;
}
