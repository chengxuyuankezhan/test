package com.cqbxzc.go.msg.adapter.wxmsg.domain;

public enum WeixinMessageSendStatusEnum {

    /**
     * 提交发送
     */
    commit,

    /**
     * 发送成功
     */
    success,

    /**
     * 重试
     */
    retry,

    /**
     * 发送失败
     */
    fail
}
