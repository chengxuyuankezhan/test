package com.cqbxzc.go.msg.adapter.vms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 语音通知数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VsmMessageTask implements Serializable {
    /**
     * 任务唯一标识
     */
    private String code;

    /**
     * 手机号
     */
    private List<String> mobile;

    /**
     * 模板code
     */
    private String template;

    /**
     * 语音数据
     */
    private Map content;


}
