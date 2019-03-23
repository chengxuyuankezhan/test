package com.cqbxzc.go.msg.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
/**
 * 行程开始的消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispatcherMessageDTO {

    /**
     * 业务系统标识
     */
    @NotBlank(message = "消息来源唯一标识不能为空")
    private String code;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;


    /**
     * 会员姓名
     */
    private String name;

    /**
     * 计算的所有乘客的最早时间 - 最晚时间
     */
    private String time;

    /**
     * 取车地点
     */
    private String address;
}
