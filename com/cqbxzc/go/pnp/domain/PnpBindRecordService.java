package com.cqbxzc.go.pnp.domain;

import com.cqbxzc.go.pnp.mq.PnpQueueConstant;
import live.jialing.core.data.LinkType;
import live.jialing.core.data.jpa.CustomJpaRepository;
import live.jialing.core.domain.service.GeneralService;
import live.jialing.core.domain.service.impl.SimpleGeneralService;
import live.jialing.data.util.SpecificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RabbitListener(queues = PnpQueueConstant.PNP_BINDING_RESULT_MQ_QUEUE)
public class PnpBindRecordService extends SimpleGeneralService<PnpBindRecord, Long> implements GeneralService<PnpBindRecord, Long> {

    /**
     * 绑定失败重试次数
     */
    private static final int retryCount = 3;

    @Autowired
    private PnpBindRecordRepository pnpBindRecordRepository;


    @Override
    public CustomJpaRepository<PnpBindRecord, Long> getRepository() {
        return pnpBindRecordRepository;
    }

    @Override
    public Specification<PnpBindRecord> buildSpecification(Class<PnpBindRecord> classz, Map<String, Object> searchParams, LinkType linkType) {
        return SpecificationUtil.buildSpecification(PnpBindRecord.class, searchParams, linkType);
    }

}
