package com.cqbxzc.go.msg.adapter.vms.domain;


import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface VsmMessageSendRecordReposity extends CustomJpaRepository<VsmMessageSendRecord,Long> {

    /**
     * 通过唯一标识查询记录
     * @param code 唯一标识
     * @return
     */
    Optional<VsmMessageSendRecord> findTopByCode(String code);
}
