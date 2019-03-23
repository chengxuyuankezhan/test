package com.cqbxzc.go.msg.adapter.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 邮件消息任务
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailMessageTask implements Serializable {

    /**
     * 任务标识
     */
    private String taskCode;

    /**
     * 收件人邮箱
     */
    private List<String> to;

    /**
     * 邮件主题
     */
    private String sub;

    /**
     * 邮件内容
     */
    private String text;

    /**
     * 模板编码
     */
    private String template;

    /**
     * 邮件参数内容
     * <p>
     * 当使用模板时使用，不使用模板，可以直接传入邮件内容
     */
    private Map<String, String> content;
}
