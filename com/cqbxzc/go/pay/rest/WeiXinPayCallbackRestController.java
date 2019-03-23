package com.cqbxzc.go.pay.rest;

import com.cqbxzc.go.pay.domain.PaymentService;
import com.cqbxzc.go.pay.domain.RefundService;
import com.cqbxzc.go.pay.rest.dto.ResponseDTO;
import com.cqbxzc.wxpay.PaymentResult;
import com.cqbxzc.wxpay.RefundResult;
import com.cqbxzc.wxpay.WeixinPayComponent;
import live.jialing.core.web.Servlets;
import live.jialing.core.web.controller.SimpleRestController;
import live.jialing.util.mapper.XmlMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * 微信支付的回调API
 */
@Slf4j
@RestController
@RequestMapping("/pay/weixin")
public class WeiXinPayCallbackRestController extends SimpleRestController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private WeixinPayComponent weixinPayComponent;

    /**
     * 付款的回调
     *
     * @param request
     * @return
     */
    @PostMapping(consumes = MediaType.TEXT_XML_VALUE, value = "/payment")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) {

        log.info("微信支付回调进入");
        ResponseDTO resp = ResponseDTO.builder().build();
        try {
            String xml = Servlets.getRequestPostStr(request);
            log.info(xml);

            // 验证参数
            PaymentResult result = weixinPayComponent.payNotify(xml);
            if (result == null) {
                resp.setCode("FAIL");
                resp.setMsg("参数校验失败");
            } else {
                // 处理逻辑
                paymentService.payNotify(result);

                resp.setCode("SUCCESS");
                resp.setMsg("成功");
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        String returnStr = new String(XmlMapperUtil.toNormalXml(resp), Charset.defaultCharset());
        log.info(returnStr);

        try (PrintWriter writer = response.getWriter()) {
            writer.print(returnStr);
        } catch (IOException e) {
            log.info("微信付款回调返回失败");
        }
    }

    /**
     * 退款的回调
     *
     * @param request
     * @return
     */
    @PostMapping(consumes = MediaType.TEXT_XML_VALUE, value = "/refund")
    public void refundNotify(HttpServletRequest request, HttpServletResponse response) {

        log.info("微信退款回调进入");
        ResponseDTO resp = ResponseDTO.builder().build();
        try {
            String xml = Servlets.getRequestPostStr(request);
            log.info(xml);

            // 验证参数
            RefundResult result = weixinPayComponent.refundNotity(xml);
            if (result == null) {
                resp.setCode("FAIL");
                resp.setMsg("参数校验失败");
            } else {
                // 处理逻辑
                refundService.refundNotify(result);

                // 返回
                resp.setCode("SUCCESS");
                resp.setMsg("成功");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        String returnStr = new String(XmlMapperUtil.toNormalXml(resp), Charset.defaultCharset());
        log.info(returnStr);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(returnStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
