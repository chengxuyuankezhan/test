package com.cqbxzc.go.msg.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cqbxzc.go.msg.constant.TemplateParamConstant;
import com.cqbxzc.go.msg.dict.LevelEnum;
import com.cqbxzc.go.msg.dict.MessageEventEnum;
import com.cqbxzc.go.msg.receive.MessageService;
import com.cqbxzc.go.msg.receive.MessageTask;
import com.cqbxzc.go.msg.receive.Receiver;
import com.cqbxzc.go.msg.util.dto.DispatcherMessageDTO;
import com.cqbxzc.go.msg.util.dto.LandCarMessageDTO;
import com.cqbxzc.go.msg.util.dto.OrderSuccessMessageDTO;
import com.cqbxzc.go.msg.util.dto.PaySuccessMessageDTO;
import com.cqbxzc.go.msg.util.dto.RefundSuccessMessageDTO;
import com.cqbxzc.go.msg.util.dto.TravelCancelMessageDTO;
import com.cqbxzc.go.msg.util.dto.TripReadyMessageDTO;
import com.cqbxzc.go.msg.util.dto.TripTravelMessageDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import live.jialing.core.context.SpringUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息任务的工具封装类
 */
@Slf4j
public class MessageUtil {

    final static MessageService messageService = SpringUtils.getBean(MessageService.class).get();

    /**
     * 发送消息
     *
     * @param messageTask 消息任务
     */
    public static void send(@NonNull MessageTask messageTask) {

        messageService.receiveMessageTask(messageTask);
    }


    /**
     * 完善会员资料时发送验证码
     * <p>
     * 短信
     *
     * @param mobile 手机号
     * @param code   验证码
     */
    public static void onPerfectMemberInfo(String mobile, String code) {

        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
            log.warn("目标手机号或验证码不正确：mobile={};code={}", mobile, code);
            return;
        }

        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.sms_verification_code, code);

        MessageTask task = MessageTask.builder()
                .receiver(Receiver.builder().mobile(Lists.newArrayList(mobile)).build())
                .level(LevelEnum.high)
                .eventCode(MessageEventEnum.verification_code_perfect.name())
                .content(content)
                .build();

        MessageUtil.send(task);
    }

    /**
     * 发送下单成功的消息
     * <p>
     * 微信消息
     *
     * @param orderSuccessMessageDTO 下单成功消息
     */
    public static void onOrderSuccess(OrderSuccessMessageDTO orderSuccessMessageDTO) {

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .eventCode(MessageEventEnum.order_success.name())
                .receiver(Receiver.builder().openId(Lists.newArrayList(orderSuccessMessageDTO.getOpenId())).build())
                .sourceCode(orderSuccessMessageDTO.getCode())
                .build();

        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.wx_formId, orderSuccessMessageDTO.getFormId());
        content.put(TemplateParamConstant.wx_page, orderSuccessMessageDTO.getPage());
        // 模版中的值
        content.put(TemplateParamConstant.wx_template_message_keyword1, orderSuccessMessageDTO.getSupplierName());
        content.put(TemplateParamConstant.wx_template_message_keyword2, orderSuccessMessageDTO.getGoods());
        content.put(TemplateParamConstant.wx_template_message_keyword3, orderSuccessMessageDTO.getPosition());
        content.put(TemplateParamConstant.wx_template_message_keyword4, orderSuccessMessageDTO.getStartTime());
        content.put(TemplateParamConstant.wx_template_message_keyword5, orderSuccessMessageDTO.getAmount());
        content.put(TemplateParamConstant.wx_template_message_keyword6, orderSuccessMessageDTO.getRemark());

        task.setContent(content);

        MessageUtil.send(task);

    }


    /**
     * 发送支付成功的消息
     * <p>
     * 微信消息
     *
     * @param paySuccessMessageDTO 支付成功消息
     */
    public static void onPaySuccess(PaySuccessMessageDTO paySuccessMessageDTO) {

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .receiver(Receiver.builder().openId(Lists.newArrayList(paySuccessMessageDTO.getOpenId())).build())
                .eventCode(MessageEventEnum.pay_success.name())
                .sourceCode(paySuccessMessageDTO.getCode())
                .build();

        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.wx_formId, paySuccessMessageDTO.getFormId());
        content.put(TemplateParamConstant.wx_page, paySuccessMessageDTO.getPage());
        // 模版中的值
        content.put(TemplateParamConstant.wx_template_message_keyword1, paySuccessMessageDTO.getSupplierName());
        content.put(TemplateParamConstant.wx_template_message_keyword2, paySuccessMessageDTO.getGoods());
        content.put(TemplateParamConstant.wx_template_message_keyword3, paySuccessMessageDTO.getPosition());
        content.put(TemplateParamConstant.wx_template_message_keyword4, paySuccessMessageDTO.getStartTime());
        content.put(TemplateParamConstant.wx_template_message_keyword5, paySuccessMessageDTO.getPayAmount());
        content.put(TemplateParamConstant.wx_template_message_keyword6, paySuccessMessageDTO.getPayResult());
        content.put(TemplateParamConstant.wx_template_message_keyword7, paySuccessMessageDTO.getPayTime());
        content.put(TemplateParamConstant.wx_template_message_keyword8, paySuccessMessageDTO.getRemark());

        task.setContent(content);

        MessageUtil.send(task);
    }

    /**
     * 发送取消行程成功的消息
     * <p>
     * 微信消息
     *
     * @param travelCancelMessageDTO 取消行程消息
     */
    public static void onTravelCancel(TravelCancelMessageDTO travelCancelMessageDTO) {

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .receiver(Receiver.builder().openId(Lists.newArrayList(travelCancelMessageDTO.getOpenId())).build())
                .eventCode(MessageEventEnum.travel_cancel.name())
                .sourceCode(travelCancelMessageDTO.getCode())
                .build();

        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.wx_formId, travelCancelMessageDTO.getFormId());
        content.put(TemplateParamConstant.wx_page, travelCancelMessageDTO.getPage());
        // 模版中的值
        content.put(TemplateParamConstant.wx_template_message_keyword1, travelCancelMessageDTO.getSupplierName());
        content.put(TemplateParamConstant.wx_template_message_keyword2, travelCancelMessageDTO.getGoods());
        content.put(TemplateParamConstant.wx_template_message_keyword3, travelCancelMessageDTO.getPosition());
        content.put(TemplateParamConstant.wx_template_message_keyword4, travelCancelMessageDTO.getStartTime());
        content.put(TemplateParamConstant.wx_template_message_keyword5, travelCancelMessageDTO.getStatus());
        content.put(TemplateParamConstant.wx_template_message_keyword6, travelCancelMessageDTO.getCancelTime());
        content.put(TemplateParamConstant.wx_template_message_keyword7, travelCancelMessageDTO.getRemark());

        task.setContent(content);

        MessageUtil.send(task);
    }

    /**
     * 发送退款成功的消息
     * <p>
     * 微信消息
     *
     * @param refundSuccessMessageDTO 退款成功消息
     */
    public static void onRefundSuccess(RefundSuccessMessageDTO refundSuccessMessageDTO) {

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .receiver(Receiver.builder().openId(Lists.newArrayList(refundSuccessMessageDTO.getOpenId())).build())
                .eventCode(MessageEventEnum.refund_success.name())
                .sourceCode(refundSuccessMessageDTO.getCode())
                .build();
        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.wx_formId, refundSuccessMessageDTO.getFormId());
        content.put(TemplateParamConstant.wx_page, refundSuccessMessageDTO.getPage());
        // 模版中的值
        content.put(TemplateParamConstant.wx_template_message_keyword1, refundSuccessMessageDTO.getGoods());
        content.put(TemplateParamConstant.wx_template_message_keyword2, refundSuccessMessageDTO.getStartTime());
        content.put(TemplateParamConstant.wx_template_message_keyword3, refundSuccessMessageDTO.getPosition());
        content.put(TemplateParamConstant.wx_template_message_keyword4, refundSuccessMessageDTO.getPayAmount());
        content.put(TemplateParamConstant.wx_template_message_keyword5, refundSuccessMessageDTO.getRefundAmount());
        content.put(TemplateParamConstant.wx_template_message_keyword6, refundSuccessMessageDTO.getRefundResult());
        content.put(TemplateParamConstant.wx_template_message_keyword7, refundSuccessMessageDTO.getRefundTime());
        content.put(TemplateParamConstant.wx_template_message_keyword8, refundSuccessMessageDTO.getRemark());

        task.setContent(content);

        MessageUtil.send(task);
    }


    /**
     * 发送调度和变更调度的消息
     * <p>
     * 短信
     *
     * @param dispatcherMessageDTO 调度信息
     */
    public static void onDispatcher(DispatcherMessageDTO dispatcherMessageDTO) {

        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.member_name, dispatcherMessageDTO.getName());
        content.put(TemplateParamConstant.start_time, dispatcherMessageDTO.getTime());
        content.put(TemplateParamConstant.sms_dispatcher_address, dispatcherMessageDTO.getAddress());

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .receiver(Receiver.builder().mobile(Lists.newArrayList(dispatcherMessageDTO.getMobile())).build())
                .eventCode(MessageEventEnum.dispatcher.name())
                .sourceCode(dispatcherMessageDTO.getCode())
                .content(content)
                .build();

        MessageUtil.send(task);
    }


    /**
     * 发送司机领车的消息
     * <p>
     * 短信，发送给所以乘客
     * </p>
     *
     * @param landCarMessageDTO 司机领车
     */
    public static void onLandCar(LandCarMessageDTO landCarMessageDTO) {
        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.member_name, landCarMessageDTO.getName());
        content.put(TemplateParamConstant.start_time, landCarMessageDTO.getTime());

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .receiver(Receiver.builder().mobile(Lists.newArrayList(landCarMessageDTO.getMobile())).build())
                .eventCode(MessageEventEnum.driver_land_car.name())
                .sourceCode(landCarMessageDTO.getCode())
                .content(content)
                .build();

        MessageUtil.send(task);
    }

    /**
     * 修改手机号时发送验证码
     *
     * @param mobile 手机号
     * @param code   验证码
     */
    public static void onChangeMobile(String mobile, String code) {

        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
            log.warn("目标手机号或验证码不正确：mobile={};code={}", mobile, code);
            return;
        }

        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.sms_verification_code, code);

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .receiver(Receiver.builder().mobile(Lists.newArrayList(mobile)).build())
                .eventCode(MessageEventEnum.verification_code_edit_mobile.name())
                .content(content)
                .build();

        MessageUtil.send(task);
    }


    /**
     * 班次准备
     * <p>
     * 短信
     *
     * @param tripReadyMessageDTO 班次准备
     */
    public static void tripReady(TripReadyMessageDTO tripReadyMessageDTO) {
        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.start_time, tripReadyMessageDTO.getTime());
        content.put(TemplateParamConstant.sms_trip_travel_start,tripReadyMessageDTO.getStart());
        content.put(TemplateParamConstant.sms_trip_travel_end,tripReadyMessageDTO.getEnd());
        content.put(TemplateParamConstant.sms_trip_travel_car_no,tripReadyMessageDTO.getCarNo());

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .receiver(Receiver.builder().mobile(Lists.newArrayList(tripReadyMessageDTO.getMobile())).build())
                .eventCode(MessageEventEnum.trip_ready.name())
                .sourceCode(tripReadyMessageDTO.getCode())
                .content(content)
                .build();

        MessageUtil.send(task);
    }

    /**
     * 班次出发
     * <p>
     * 短信
     *
     * @param tripTravelMessageDTO 班次出发
     */
    public static void tripTravel(TripTravelMessageDTO tripTravelMessageDTO) {
        Map<String, String> content = Maps.newHashMap();
        content.put(TemplateParamConstant.member_name, tripTravelMessageDTO.getName());
        content.put(TemplateParamConstant.start_time, tripTravelMessageDTO.getTime());
        content.put(TemplateParamConstant.sms_trip_travel_start,tripTravelMessageDTO.getStart());
        content.put(TemplateParamConstant.sms_trip_travel_end,tripTravelMessageDTO.getEnd());
        content.put(TemplateParamConstant.sms_trip_travel_trip_no,tripTravelMessageDTO.getTripNo());
        content.put(TemplateParamConstant.sms_trip_travel_car_no,tripTravelMessageDTO.getCarNo());
        content.put(TemplateParamConstant.supplier_name,tripTravelMessageDTO.getSupplierName());

        MessageTask task = MessageTask.builder()
                .level(LevelEnum.high)
                .receiver(Receiver.builder().mobile(Lists.newArrayList(tripTravelMessageDTO.getMobile())).build())
                .eventCode(MessageEventEnum.trip_travel.name())
                .sourceCode(tripTravelMessageDTO.getCode())
                .content(content)
                .build();

        MessageUtil.send(task);
    }

}
