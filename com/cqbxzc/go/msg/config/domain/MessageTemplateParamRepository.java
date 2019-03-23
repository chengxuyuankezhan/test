package com.cqbxzc.go.msg.config.domain;

import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageTemplateParamRepository extends CustomJpaRepository<MessageTemplateParam,Long> {

}
