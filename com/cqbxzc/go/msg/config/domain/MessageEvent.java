package com.cqbxzc.go.msg.config.domain;

import com.cqbxzc.go.msg.dict.MessageEventEnum;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import live.jialing.core.beanvalidator.Second;
import live.jialing.data.domain.JpaEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 消息事件
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = MessageEvent.class)
@Entity
@Table(name = "msg_config_event")
public class MessageEvent extends JpaEntity<Long> {
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@NotNull(message = "消息事件id不能为空", groups = Second.class)
	private Long id;

	/**
	 * 事件代码
	 */
	@Column(length = 32, nullable = false)
	@Enumerated(EnumType.STRING)
	private MessageEventEnum code;
	/**
	 * 事件名称
	 */
	@Column(length = 32)
	private String name;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "msg_event_template_relation", // 被关联表
			joinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "id")}, // 维护端外键
			inverseJoinColumns = {@JoinColumn(name = "template_id", referencedColumnName = "id")}) // 被维护端外键
	private List<MessageTemplate> messageTemplates;
}
