package com.cqbxzc.go.msg.adapter.vms.domain;

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
public class VsmMessageSendRecordService extends SimpleGeneralService<VsmMessageSendRecord,Long> implements GeneralService<VsmMessageSendRecord,Long>{

    @Autowired
    private VsmMessageSendRecordReposity vsmMessageSendRecordReposity;

    @Override
    public CustomJpaRepository<VsmMessageSendRecord, Long> getRepository() {
        return vsmMessageSendRecordReposity;
    }

    @Override
    public Specification<VsmMessageSendRecord> buildSpecification(Class<VsmMessageSendRecord> classz, Map<String, Object> searchParams, LinkType linkType) {

        return SpecificationUtil.buildSpecification(VsmMessageSendRecord.class,searchParams,linkType);
    }


    /**
     * 通过唯一标识查询记录
     * @param code 唯一标识
     * @return
     */
    public Optional<VsmMessageSendRecord> findByCode(String code) {

        return vsmMessageSendRecordReposity.findTopByCode(code);
    }

}
