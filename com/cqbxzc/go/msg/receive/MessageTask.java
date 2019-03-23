package com.cqbxzc.go.msg.receive;

import com.cqbxzc.go.msg.dict.LevelEnum;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 消息的发送任务
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MessageTask implements Serializable {

    /**
     * 唯一编码，业务系统不需要管
     */
    private String code;

    /**
     * 事件代码
     */
    private String eventCode;

    /**
     * 来源唯一标识，对应到业务模块具体的id或者code
     */
    private String sourceCode;

    /**
     * 优先级
     */
    private LevelEnum level;

    /**
     * 接收者
     */
    @NotNull(message = "接收者不能为空")
    private Receiver receiver;

    /**
     * 发送内容
     */
    @NotNull(message = "内容不能为空")
    private Map content;

    /**
     * 预留参数
     */
    private Map extra;
}
