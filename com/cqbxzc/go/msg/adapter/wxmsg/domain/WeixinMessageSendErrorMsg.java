package com.cqbxzc.go.msg.adapter.wxmsg.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import live.jialing.util.time.ClockUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 微信模板通知错误消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeixinMessageSendErrorMsg implements Serializable {

    /**
     * 发送时间
     */
    @DateTimeFormat(pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ClockUtil.LOCAL_DATE_TIME_FORMATTER_PATTERN, timezone = ClockUtil.DEFAULT_TIME_ZONE)
    private LocalDateTime sendTime;

    /**
     * 发送次数
     */
    private Integer count;

    /**
     * 错误消息
     */
    private String errorMsg;

}
