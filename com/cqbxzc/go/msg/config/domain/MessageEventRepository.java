package com.cqbxzc.go.msg.config.domain;

import com.cqbxzc.go.msg.dict.MessageEventEnum;
import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageEventRepository extends CustomJpaRepository<MessageEvent,Long>{

    /**
     * 通过事件代码查询详细事件
     * @param code 事件代码
     * @return
     */
    @Query("select e from MessageEvent e left join fetch e.messageTemplates where e.code=?1")
    Optional<MessageEvent> findWholeByCode(MessageEventEnum code);

}
