package com.cqbxzc.go.pay.domain;

import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRecordRepository extends CustomJpaRepository<PaymentRecord, Long> {

    /**
     * 根据订单号查询创建时间最晚的一条记录
     *
     * @param orderCode 订单号
     * @return
     */
    Optional<PaymentRecord> findFirstByOrderCodeOrderByIdDesc(String orderCode);

}
