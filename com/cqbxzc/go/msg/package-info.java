/**
 * 消息中心：发送站内信、短信、微信消息、邮件、语音、推送
 * <p>
 * 接收 -> mq -> 调度 -> 消息适配 -> mq
 * <p>
 * 入口：receive/MessageUtil
 */
package com.cqbxzc.go.msg;