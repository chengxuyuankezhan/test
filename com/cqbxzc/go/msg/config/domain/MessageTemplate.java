package com.cqbxzc.go.msg.config.domain;

import com.cqbxzc.go.msg.dict.ChannelEnum;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import live.jialing.core.beanvalidator.First;
import live.jialing.core.beanvalidator.Second;
import live.jialing.data.domain.JpaEntity;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 消息模板
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = MessageTemplate.class)
@Entity
@Table(name = "msg_config_template")
public class MessageTemplate extends JpaEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    @NotNull(message = "PK不能为NULL", groups = Second.class)
    private Long id;

    /**
     * 模板代码
     */
    @Column(length = 256,nullable = false)
    @NotBlank(message = "模板代码不能为空",groups = {First.class, Second.class})
    @Length(message = "模板代码的长度不能超过256",max = 256)
    private String code;

    /**
     * 模板描述
     */
    @Column(length = 256)
    @Length(max = 256,message = "模板描述的长度不能超过256")
    private String description;

    /**
     * 模板对应的消息渠道
     */
    @Column(length = 32,nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelEnum channel;

    @OneToMany
    @JoinColumn(name = "template_id",referencedColumnName = "id")
    private List<MessageTemplateParam> messageTemplateParams;
}
