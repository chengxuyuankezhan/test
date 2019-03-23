package com.cqbxzc.go.msg.adapter.wxmsg.domain;

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
public class WeixinMessageSendRecordService extends SimpleGeneralService<WeixinMessageSendRecord,Long> implements GeneralService<WeixinMessageSendRecord,Long>{

    @Autowired
    private WeixinMessageSendRecordRepository weixinMessageSendRecordRepository;

    @Override
    public CustomJpaRepository<WeixinMessageSendRecord, Long> getRepository() {
        return weixinMessageSendRecordRepository;
    }

    @Override
    public Specification<WeixinMessageSendRecord> buildSpecification(Class<WeixinMessageSendRecord> classz, Map<String, Object> searchParams, LinkType linkType) {
        return SpecificationUtil.buildSpecification(WeixinMessageSendRecord.class,searchParams,linkType);
    }

    /**
     * 根据唯一编码查询发送记录
     * @param code
     * @return
     */
    public Optional<WeixinMessageSendRecord> findByCode(String code) {

        return weixinMessageSendRecordRepository.findTopByCode(code);
    }

}
