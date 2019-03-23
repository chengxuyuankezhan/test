package com.cqbxzc.go.msg.adapter.email.domain;

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

@Slf4j
@Service
public class EmailMessageSendRecordService extends SimpleGeneralService<EmailMessageSendRecord, Long> implements GeneralService<EmailMessageSendRecord, Long> {

    @Autowired
    private EmailMessageSendRecordRepository emailMessageSendRecordRepository;

    /**
     * 实体对应的仓储
     *
     * @return 仓储
     */
    @Override
    public CustomJpaRepository<EmailMessageSendRecord, Long> getRepository() {
        return emailMessageSendRecordRepository;
    }

    /**
     * 分页查询：查询条件
     *
     * @param classz       领域对象
     * @param searchParams 查询条件及值
     * @param linkType     sql where连接类型
     * @return 查询结果
     * @author baizt E-mail:baizt@03199.com
     * @version 创建时间：2016年4月27日 下午2:54:49
     */
    @Override
    public Specification<EmailMessageSendRecord> buildSpecification(Class<EmailMessageSendRecord> classz, Map<String, Object> searchParams, LinkType linkType) {
        return SpecificationUtil.buildSpecification(EmailMessageSendRecord.class, searchParams, linkType);
    }


}
