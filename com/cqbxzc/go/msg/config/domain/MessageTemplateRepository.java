package com.cqbxzc.go.msg.config.domain;

import live.jialing.core.data.jpa.CustomJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageTemplateRepository extends CustomJpaRepository<MessageTemplate,Long>{


    /**
     * 通过id查询模板的信息和模板参数信息
     * @param id
     * @return
     */
    @Query("select t from MessageTemplate t left join fetch t.messageTemplateParams where t.id = ?1")
    Optional<MessageTemplate> findWholeById(Long id);

}
