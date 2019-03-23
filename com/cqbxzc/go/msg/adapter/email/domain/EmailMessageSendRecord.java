package com.cqbxzc.go.msg.adapter.email.domain;


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
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = EmailMessageSendRecord.class)
@Entity
@Table(name = "msg_email_message_send_record")
public class EmailMessageSendRecord extends JpaEntity<Long> {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    @NotNull(message = "id不能为空", groups = {Second.class})
    private Long id;

    /**
     * 消息唯一标识
     */
    @Column(nullable = false, updatable = false)
    @NotBlank(message = "唯一标识不能为空", groups = First.class)
    @Length(max = 32, message = "唯一标识的长度不能为空")
    private String code;

    /**
     * 任务唯一标识
     */
    @Column(nullable = false, updatable = false)
    @NotBlank(message = "任务标识不能为空", groups = {First.class})
    @Length(max = 32, message = "任务标识的长度不能超过32")
    private String taskCode;

    /**
     * 接收人邮件
     */
    @Column(nullable = false)
    @NotBlank(message = "接收人邮件不能为空", groups = {First.class, Second.class})
    @Length(max = 50, message = "接收人邮件的长度不能超过50")
    private String receive;

    /**
     * 邮件主题
     */
    @Column
    private String subject;

    /**
     * 邮件内容
     */
    @Column(columnDefinition = "text")
    private String text;

    /**
     * 发送时间
     */
    @Column(columnDefinition = "timestamp")
    @JsonFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, shape = JsonFormat.Shape.STRING, timezone = ClockUtil.DEFAULT_TIME_ZONE)
    @DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
    private LocalDateTime sendTime;

    /**
     * 错误消息
     */
    @Column(columnDefinition = "text")
    private String errorMsg;
}
