package com.cqbxzc.go.pnp.domain;

import com.cqbxzc.go.pnp.dict.PnpStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import live.jialing.core.beanvalidator.First;
import live.jialing.core.beanvalidator.Second;
import live.jialing.data.domain.JpaEntity;
import live.jialing.util.time.ClockUtil;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 电话绑定记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = PnpBindRecord.class)
@Entity
@Table(name = "pnp_bind_record")
public class PnpBindRecord extends JpaEntity<Long> {

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@NotNull(message = "电话绑定记录id不能为空", groups = Second.class)
	private Long id;

	/**
	 * 绑定A-B所需要的号码池
	 */
	@Column(length = 32)
	@Length(max = 32, message = "号码池的长度不能超过32")
	private String poolKey;

	/**
	 * A-真实电话号码
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotBlank(message = "电话号码A不能为空", groups = First.class)
	@Length(message = "电话号码A的长度不能超过32", max = 32)
	private String phoneA;

	/**
	 * B-真实电话号码
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotBlank(message = "电话号码B不能为空", groups = First.class)
	@Length(message = "电话号码B的长度不能超过32", max = 32)
	private String phoneB;

	/**
	 * X-中间号（非真实）
	 */
	@Column(length = 32)
	@Length(message = "中间号X的长度不能超过32", max = 32)
	private String phoneX;

	/**
	 * 绑定时间
	 */
	@Column(columnDefinition = "timestamp(6) null")
	@DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
	private LocalDateTime bindTime;

	/**
	 * 绑定关系id，解绑时需要
	 */
	@Column(length = 32)
	@Length(message = "绑定关系id的长度不能超过32", max = 32)
	private String subsId;

	/**
	 * 解绑时间
	 */
	@Column(columnDefinition = "timestamp(6) null")
	@DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
	private LocalDateTime unbindTime;

	/**
	 * 状态
	 */
	@Column(length = 32)
	@Enumerated(EnumType.STRING)
	private PnpStatusEnum status;

	/**
	 * 错误消息
	 */
	@Column(length = 256)
	private String errormsg;
}
