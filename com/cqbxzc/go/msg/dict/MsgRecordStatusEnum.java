package com.cqbxzc.go.msg.dict;

public enum MsgRecordStatusEnum {
    /**
     * 发起，初始状态
     */
    commit,

    /**
     * 成功
     */
    success,

    /**
     * 重试
     */
    retry,

    /**
     * 失败
     */
    fail

}
