package com.cqbxzc.go.msg.dispatch.domain;

import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageTaskRecordRepository extends CustomJpaRepository<MessageTaskRecord,Long> {

}
