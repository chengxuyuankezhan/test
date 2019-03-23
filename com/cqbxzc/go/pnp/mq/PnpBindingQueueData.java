package com.cqbxzc.go.pnp.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 绑定电话时传入消息队列的内容
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PnpBindingQueueData implements Serializable {

    /**
     * 唯一编码
     */
    protected String code;

    /**
     * 号码A
     */
    protected String phoneA;

    /**
     * 号码B
     */
    protected String phoneB;

    /**
     * 中间号（可以不传）
     */
    protected String phoneX;

    /**
     * 过期时间，格式：yyyy-MM-dd HH:mm:ss
     */
    protected LocalDateTime expiration;

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 订购关系ID
     */
    private String subsId;

    /**
     * 使用的号码池key
     */
    private String poolKey;

    /**
     * 创建时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String gmtCreate;

    /**
     * 失效时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String expireDate;

    /**
     * 错误消息
     */
    private String errorMsg;
}
