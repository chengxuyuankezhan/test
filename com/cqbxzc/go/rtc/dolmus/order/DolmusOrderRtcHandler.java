package com.cqbxzc.go.rtc.dolmus.order;

import com.cqbxzc.go.rtc.RtcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 合租车订单状态变更、调度行程变更
 * <br>
 * {
 *     orderCode:'hzc15247173079961',//订单编码，唯一
 *     change:{
 *         status:'UNPAID',//订单状态，
 *         dispatchTimestamp:1524573850336//如果产生调度变更，则更新调度的时间戳
 *     },
 *     updateTimestamp:1524573850336,//数据更新的当前时间戳
 * }
 */
@Slf4j
@Validated
@Component
public class DolmusOrderRtcHandler {

    private RtcService rtcService;

    public DolmusOrderRtcHandler(RtcService rtcService) {
        this.rtcService = rtcService;
    }

    /**
     * 订单状态变更
     *
     * @param orderCode
     * @param status
     */
    public void orderStatusChange(@NotBlank String orderCode, @NotBlank String status) {

        Map data = Map.of(DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_CODE, orderCode
                , DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_UPDATE_TIMESTAMP, System.currentTimeMillis()
                , DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_CHANGE, Map.of(DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_CHANGE_STATUS, status)
        );

        rtcService.send(DolmusOrderRtcConstant.DOLMUS_TRAVEL_DOMAIN, orderCode, data);
    }

    /**
     * 行程变更
     *
     * @param orderCode
     */
    public void travelChange(@NotBlank String orderCode,Map<String,Object> param) {

        Map data = Map.of(DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_CODE, orderCode
                , DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_UPDATE_TIMESTAMP, System.currentTimeMillis()
                , DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_CHANGE, Map.of(DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_CHANGE_DISPATCH, System.currentTimeMillis()
                		,DolmusOrderRtcConstant.DOLMUS_TRAVEL_PARAM_CHANGE_DATA,param)
        );

        rtcService.send(DolmusOrderRtcConstant.DOLMUS_TRAVEL_DOMAIN, orderCode, data);

    }

}
