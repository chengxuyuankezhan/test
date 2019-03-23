package com.cqbxzc.go.msg.adapter.sms.domain;


import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ShortMessageSendRecordReposity extends CustomJpaRepository<ShortMessageSendRecord,Long> {

    /**
     * 通过唯一标识查询记录
     * @param code 唯一标识
     * @return
     */
    Optional<ShortMessageSendRecord> findTopByCode(String code);
}
