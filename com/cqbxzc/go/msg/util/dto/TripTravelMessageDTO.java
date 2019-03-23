package com.cqbxzc.go.msg.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 班次出发
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripTravelMessageDTO implements Serializable {

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
     * 会员名称
     */
    private String name;

    /**
     * 出发时间
     * <p>
     * 格式：yyyy-MM-dd HH:mm
     */
    private String time;

    /**
     * 起点
     */
    private String start;

    /**
     * 终点
     */
    private String end;

    /**
     * 班次号
     */
    private String tripNo;

    /**
     * 车牌号
     */
    private String carNo;

    /**
     * 商家名称
     */
    private String supplierName;

}
