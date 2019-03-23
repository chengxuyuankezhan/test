package com.cqbxzc.go.msg.adapter.sms.domain;

import live.jialing.core.data.LinkType;
import live.jialing.core.data.jpa.CustomJpaRepository;
import live.jialing.core.domain.service.GeneralService;
import live.jialing.core.domain.service.impl.SimpleGeneralService;
import live.jialing.data.util.SpecificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ShortMessageSendRecordService extends SimpleGeneralService<ShortMessageSendRecord,Long> implements GeneralService<ShortMessageSendRecord,Long>{

    @Autowired
    private ShortMessageSendRecordReposity shortMessageSendRecordReposity;

    @Override
    public CustomJpaRepository<ShortMessageSendRecord, Long> getRepository() {
        return shortMessageSendRecordReposity;
    }

    @Override
    public Specification<ShortMessageSendRecord> buildSpecification(Class<ShortMessageSendRecord> classz, Map<String, Object> searchParams, LinkType linkType) {

        return SpecificationUtil.buildSpecification(ShortMessageSendRecord.class,searchParams,linkType);
    }


    /**
     * 通过唯一标识查询记录
     * @param code 唯一标识
     * @return
     */
    public Optional<ShortMessageSendRecord> findByCode(String code) {

        return shortMessageSendRecordReposity.findTopByCode(code);
    }

}
