package com.cqbxzc.go.msg.dispatch.domain;

import com.cqbxzc.go.msg.dict.LevelEnum;
import com.cqbxzc.go.msg.dict.MessageEventEnum;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import live.jialing.core.beanvalidator.Second;
import live.jialing.data.domain.JpaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 任务记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = MessageTaskRecord.class)
@Entity
@Table(name = "msg_task_record")
public class MessageTaskRecord extends JpaEntity<Long> {

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@NotNull(message = "消息任务记录id不能为空", groups = Second.class)
	private Long id;

	/**
	 * 唯一编码，业务系统不需要管
	 */
	@Column(length = 32, nullable = false, unique = true)
	@NotBlank(message = "唯一编码不能为空")
	@Length(max = 32, message = "唯一编码长度不能超过32")
	private String code;

	/**
	 * 消息事件代码
	 */
	@Column(length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private MessageEventEnum eventCode;

	/**
	 * 来源唯一标识，对应到业务模块具体的id或者code
	 */
	@Column(length = 32)
	private String sourceCode;

	/**
	 * 优先级
	 */
	@Column(length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private LevelEnum level;

	/**
	 * 接收者
	 */
	@Column(columnDefinition = "text")
	@NotBlank(message = "接收者不能为空")
	private String receiver;

	/**
	 * 发送内容
	 */
	@Column(columnDefinition = "text")
	@NotBlank(message = "发送内容不能为空")
	private String content;

}
