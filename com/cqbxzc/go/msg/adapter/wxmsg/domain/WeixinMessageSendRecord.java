package com.cqbxzc.go.msg.adapter.wxmsg.domain;

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
 * 微信模板消息发送记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = WeixinMessageSendRecord.class)
@Entity
@Table(name = "msg_wxmsg_send_record")
public class WeixinMessageSendRecord extends JpaEntity<Long>{
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    @NotNull(message = "微信消息发送记录id不能为空", groups = Second.class)
    private Long id;

    /**
     * 微信模板消息唯一标识
     */
    @Column(length = 32,nullable = false,unique = true)
    @NotBlank(message = "微信模板消息唯一标识不能为空",groups = First.class)
    @Length(max = 32,message = "微信模板消息唯一标识长度不能超过32")
    private String code;

    /**
     * 消息任务唯一标识
     */
    @Column(length = 32,nullable = false,unique = true)
    @NotBlank(message = "消息任务唯一标识不能为空",groups = First.class)
    @Length(max = 32,message = "消息任务唯一标识长度不能超过32")
    private String taskCode;

    @Column(length = 32,nullable = false)
    @NotBlank(message = "用户openid不能为空",groups = First.class)
    @Length(max = 32,message = "用户openid长度不能超过32")
    private String openid;

    @Column(length = 256,nullable = false)
    @NotBlank(message = "表单id不能为空",groups = First.class)
    @Length(max = 256,message = "表单id长度不能超过256")
    private String formId;

    @Column(length = 256,nullable = false)
    @NotBlank(message = "模板id不能为空",groups = First.class)
    @Length(max = 256,message = "模板id长度不能超过256")
    private String templateId;

    @Column(columnDefinition = "text",nullable = false)
    @NotBlank(message = "内容不能为空",groups = First.class)
    private String content;

    @Column(columnDefinition = "timestamp(6) null")
    @DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
    private LocalDateTime sendTime;

    @Column(length = 20)
    @NotNull(message = "发送次数不能为NULL",groups = {First.class, Second.class})
    private Integer sendCount;

    @Column(length = 32,nullable = false)
    @Enumerated(EnumType.STRING)
    private WeixinMessageSendStatusEnum status;

    @Column(columnDefinition = "text")
    private String errorMsg;

}
