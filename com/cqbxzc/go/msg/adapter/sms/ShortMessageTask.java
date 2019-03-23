package com.cqbxzc.go.msg.adapter.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 短信消息的发送任务
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortMessageTask implements Serializable {

    /**
     * 任务code
     */
    private String taskCode;
    /**
     * 短信模版
     */
    private String template;

    /**
     * 接收者手机号，一个或多个
     */
    private List<String> mobile;

    /**
     * 模板参数
     */
    private Map<String, String> data;


}
