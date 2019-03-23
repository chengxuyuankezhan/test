package com.cqbxzc.go.msg.adapter.wxmsg.domain;

import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeixinMessageSendRecordRepository extends CustomJpaRepository<WeixinMessageSendRecord,Long> {

    /**
     * 根据唯一编码查询发送记录
     * @param code 唯一编码
     * @return
     */
    public Optional<WeixinMessageSendRecord> findTopByCode(String code);

}
