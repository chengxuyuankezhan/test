package com.cqbxzc.go.msg.adapter.email.domain;

import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailMessageSendRecordRepository extends CustomJpaRepository<EmailMessageSendRecord, Long> {

}
