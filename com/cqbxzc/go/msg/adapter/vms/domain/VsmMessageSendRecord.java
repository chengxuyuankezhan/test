package com.cqbxzc.go.msg.adapter.vms.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import java.time.LocalDateTime;

/**
 * 语音发送记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = VsmMessageSendRecord.class)
@Entity
@Table(name = "msg_vsm_message_send_record")
public class VsmMessageSendRecord extends JpaEntity<Long> {

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@NotNull(message = "语音发送记录id不能为空", groups = Second.class)
	private Long id;

	/**
	 * 唯一编码
	 */
	@Column(length = 32, nullable = false, updatable = false)
	@NotBlank(message = "唯一编码不能为空")
	@Length(message = "唯一编码的长度不能超过32", max = 32)
	private String code;

	/**
	 * 消息任务唯一标识
	 */
	@Column(length = 32, nullable = false, updatable = false, unique = true)
	@NotBlank(message = "消息任务唯一标识不能为空")
	@Length(message = "消息任务唯一标识的长度不能超过32", max = 32)
	private String taskCode;

	/**
	 * 接收的手机号
	 */
	@Column(length = 32, nullable = false)
	@NotBlank(message = "接收的手机号不能为空")
	@Length(message = "接收的手机号的长度不能超过32", max = 32)
	private String mobile;

	/**
	 * 语音数据
	 */
	@Column(columnDefinition = "text", nullable = false)
	@NotBlank(message = "语音数据不能为空")
	private String data;

	/**
	 * 语音模版
	 */
	@Column(length = 256, nullable = false)
	@NotBlank(message = "语音模板不能为空")
	@Length(message = "语音模板的长度不能超过32", max = 32)
	private String template;

	/**
	 * 发送时间
	 */
	@Column(columnDefinition = "timestamp(6) null")
	@DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
	private LocalDateTime sendTime;

	/**
	 * 返回结果时间
	 */
	@Column(columnDefinition = "timestamp(6) null")
	@DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
	private LocalDateTime backTime;

	/**
	 * 状态
	 */
	@Column(length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private VsmMessageSendStatusEnum status;

	/**
	 * 发送次数
	 */
	@Column(length = 20, nullable = false)
	@NotNull(message = "发送次数不能为空")
	private Integer sendCount;

	/**
	 * 发送失败的错误信息
	 */
	@Column(columnDefinition = "text")
	private String errMsg;

}
