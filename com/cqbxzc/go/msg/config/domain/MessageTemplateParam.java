package com.cqbxzc.go.msg.config.domain;

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

/**
 * 模板参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = MessageTemplateParam.class)
@Entity
@Table(name = "msg_config_template_param")
public class MessageTemplateParam extends JpaEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    @NotNull(message = "PK不能为NULL", groups = Second.class)
    private Long id;

    /**
     * 参数名称
     */
    @Column(length = 32,nullable = false)
    @NotBlank(message = "参数名称不能为空",groups = {First.class,Second.class})
    @Length(max = 32,message = "参数名称长度不能超过32")
    private String name;

    /**
     * 参数别名，全局唯一
     */
    @Column(length = 32)
    @Length(max = 32,message = "参数别名长度不能超过32")
    private String alias;

    /**
     * 描述
     */
    @Column(length = 256)
    @Length(max = 256,message = "参数描述长度不能超过256")
    private String description;

    /**
     * 参数顺序
     */
    @Column(length = 20)
    private Integer sort;

}
