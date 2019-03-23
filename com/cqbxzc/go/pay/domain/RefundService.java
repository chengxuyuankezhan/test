package com.cqbxzc.go.pay.domain;

import com.cqbxzc.go.pay.dict.PaymentStatusEnum;
import com.cqbxzc.go.pay.dict.RefundStatusEnum;
import com.cqbxzc.go.pay.infrastructure.RefundCallback;
import com.cqbxzc.go.pay.vo.RefundVO;
import com.cqbxzc.wxpay.RefundOrderRequest;
import com.cqbxzc.wxpay.RefundOrderResponse;
import com.cqbxzc.wxpay.RefundResult;
import com.cqbxzc.wxpay.WeixinPayComponent;
import live.jialing.core.beanvalidator.BeanValidators;
import live.jialing.core.exception.ServiceException;
import live.jialing.util.general.Collections3;
import live.jialing.util.time.ClockUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 退款部分的服务
 */

@Slf4j
@Service
public class RefundService {

    private final String success = "SUCCESS";

    private final String refund_error_message = "退款过程中，内部异常";

    @Autowired
    private RefundRecordRepository refundRecordRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private WeixinPayComponent weixinPayComponent;

    @Autowired
    private RefundCallback refundCallback;

    @Autowired
    protected Validator validator;

    /**
     * 保存退款记录
     *
     * @param refund 退款记录结果
     * @return
     */
    public RefundRecord refund(@NonNull RefundVO refund) {

        BeanValidators.validateWithException(validator, refund);

        // 1.判断退款是否合理
        // 1.1查询本地记录，所有退款成功的该订单记录
        List<RefundRecord> refundRecords = refundRecordRepository.findRefundOrder(refund.getOrderCode(), List.of(RefundStatusEnum.SUCCESS, RefundStatusEnum.PROCESSING));
        if (Collections3.isNotEmpty(refundRecords)) {
            final BigDecimal[] refundAmount = {BigDecimal.ZERO};
            refundRecords.forEach(r -> {
                refundAmount[0] = refundAmount[0].add(r.getAmount());
            });
            // 1.2判断已经退款的金额和订单总金额是否满足这次的退款
            if (refund.getTotalAmount().subtract(refundAmount[0]).compareTo(refund.getAmount()) < 0) {
                throw new ServiceException("订单可退款金额不足");
            }
        }

        // 得到付款时的商户订单号
        Optional<PaymentRecord> paymentRecordOptional = paymentRecordRepository.findFirstByOrderCodeOrderByIdDesc(refund.getOrderCode());
        if (!paymentRecordOptional.isPresent()) {
            throw new ServiceException(MessageFormat.format("订单号{0}无付款记录", refund.getOrderCode()));
        }
        PaymentRecord paymentRecord = paymentRecordOptional.get();
        if (!paymentRecord.getStatus().name().equals(PaymentStatusEnum.SUCCESS.name())) {
            throw new ServiceException(MessageFormat.format("订单号{0}付款记录不是成功状态，不能退款", refund.getOrderCode()));
        }

        // 2.组装退款参数
        RefundOrderRequest request = RefundOrderRequest.builder()
                .outRefundNo(refund.getCode())
                .outTradeNo(paymentRecord.getCode())
                .totalFee(String.valueOf(refund.getTotalAmount().multiply(new BigDecimal(100)).intValue()))
                .refundFee(String.valueOf(refund.getAmount().multiply(new BigDecimal(100)).intValue()))
                .build();

        // 3.调用退款
        RefundOrderResponse response = null;
        try {
            response = weixinPayComponent.refund(request);
        } catch (Exception e) {
            throw new PayException(refund_error_message, e);
        }

        // 4.判断退款请求结果
        if (!success.equals(response.getReturnCode())) {
            throw new ServiceException(response.getReturnMsg());
        }
        if (!success.equals(response.getResultCode())) {
            throw new ServiceException(response.getErrCodeDes());
        }

        // 5.保存退款数据
        RefundRecord refundRecord = RefundRecord.builder()
                .orderCode(refund.getOrderCode())
                .travelModel(refund.getTravelModel())
                .code(refund.getCode())
                .attach(refund.getAttach())
                .status(RefundStatusEnum.PROCESSING)
                .amount(refund.getAmount())
                .remoteId(response.getRefundId())
                .build();
        return refundRecordRepository.saveAndFlush(StringUtils.EMPTY, refundRecord);
    }

    /**
     * 根据订单号查询退款结果
     * <p>
     * 1、查询本系统的退款记录及退款结果 <br>
     * 2、如果有退款结果，返回退款结果 <br>
     * 3、如果无退款结果，则从微信支付系统中查询 <br>
     * 4、保存从微信支付系统查询的结果 <br>
     * 5、返回退款结果
     *
     * @param code 退款单号
     * @return
     */
    public Optional<RefundRecord> findRefundResultByOrder(String code) {

        if (StringUtils.isBlank(code)) {
            return Optional.empty();
        }

        Optional<RefundRecord> refundRecordOpt = refundRecordRepository.findTopByCode(code);
        if (!refundRecordOpt.isPresent()) {
            return Optional.empty();
        }

        RefundRecord refundRecord = refundRecordOpt.get();
        // 判断状态，如果是初始状态，就从微信支付系统中查询结果
        if (refundRecord.getStatus().equals(RefundStatusEnum.PROCESSING)) {
            RefundResult result = null;
            try {
                result = weixinPayComponent.queryRefundByOutRefundNo(code);
            } catch (Exception e) {
                throw new PayException(refund_error_message, e);
            }

            if (!result.getReturnCode().equals(success)) {
                refundRecord.setErrorRemark(result.getReturnMsg());
                return Optional.of(refundRecord);
            }

            if (!result.getResultCode().equals(success)) {
                refundRecord.setErrorRemark(result.getErrCodeDes());
                return Optional.of(refundRecord);
            }

            String status = result.getDetails().get(0).getRefundStatus();
            if (status.equals(RefundStatusEnum.PROCESSING.name())) {
                return Optional.of(refundRecord);
            } else if (status.equals(RefundStatusEnum.SUCCESS.name())) {
                refundRecord.setSuccessTime(LocalDateTime.parse(result.getDetails().get(0).getRefundSuccessTime(), ClockUtil.LOCAL_DATE_TIME_FORMATTER));
                // 将退款结果发送给mq
                refundCallback.callback(refundRecord);
            } else {
                refundRecord.setErrorRemark(result.getErrCodeDes());
            }
            refundRecord.setStatus(RefundStatusEnum.valueOf(result.getDetails().get(0).getRefundStatus()));
            refundRecordRepository.saveAndFlush(StringUtils.EMPTY, refundRecord);
        }
        return Optional.of(refundRecord);
    }

    /**
     * 退款成功后的处理
     *
     * @param result 退款结果
     */
    public void refundNotify(@NonNull RefundResult result) {
        // 根据退款单号查询本地记录
        String code = result.getOutRefundNo();
        Optional<RefundRecord> refundRecordOpt = refundRecordRepository.findTopByCode(code);
        if (!refundRecordOpt.isPresent()) {
            log.error(MessageFormat.format("系统无退款单号为{0}的记录", code));
            return;
        }
        RefundRecord refundRecord = refundRecordOpt.get();
        // 判断记录状态，如果不是提交状态表示已经处理过了，不再处理
        if (!refundRecord.getStatus().name().equals(RefundStatusEnum.PROCESSING.name())) {
            return;
        }
        if (success.equals(result.getRefundStatus())) {
            refundRecord.setStatus(RefundStatusEnum.SUCCESS);
            refundRecord.setSuccessTime(LocalDateTime.parse(result.getSuccessTime(), ClockUtil.LOCAL_DATE_TIME_FORMATTER));
            refundRecord.setCode(result.getOutRefundNo());
            refundRecord.setRemoteId(result.getRefundId());
            refundRecord.setMchId(result.getMchId());
        } else {
            refundRecord.setStatus(RefundStatusEnum.CHANGE);
            refundRecord.setErrorRemark(result.getReturnMsg());
        }
        refundRecord = refundRecordRepository.saveAndFlush(StringUtils.EMPTY, refundRecord);

        // 将退款结果发送给mq
        refundCallback.callback(refundRecord);
    }

}
