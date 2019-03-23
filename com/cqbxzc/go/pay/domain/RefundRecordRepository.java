package com.cqbxzc.go.pay.domain;

import com.cqbxzc.go.pay.dict.RefundStatusEnum;
import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRecordRepository extends CustomJpaRepository<RefundRecord, Long> {

    /**
     * 根据退款单号查询退款记录
     *
     * @param code 退款单号
     * @return
     */
    Optional<RefundRecord> findTopByCode(String code);

    /**
     * 根据订单号和退款状态查询退款记录列表
     *
     * @param orderCode 订单号
     * @param status    退款状态列表
     * @return 退款记录
     */
    @Query("select r from RefundRecord r where r.orderCode = ?1 and r.status in ?2")
    List<RefundRecord> findRefundOrder(String orderCode, List<RefundStatusEnum> status);
}
