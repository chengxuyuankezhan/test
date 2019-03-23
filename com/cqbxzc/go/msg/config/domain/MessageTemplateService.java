package com.cqbxzc.go.msg.config.domain;

import com.google.common.collect.Lists;
import live.jialing.core.beanvalidator.BeanValidators;
import live.jialing.core.beanvalidator.First;
import live.jialing.core.data.LinkType;
import live.jialing.core.data.jpa.CustomJpaRepository;
import live.jialing.core.domain.service.GeneralService;
import live.jialing.core.domain.service.impl.SimpleGeneralService;
import live.jialing.data.util.SpecificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MessageTemplateService extends SimpleGeneralService<MessageTemplate,Long> implements GeneralService<MessageTemplate,Long> {

    @Autowired
    private MessageTemplateRepository messageTemplateRepository;

    @Autowired
    private MessageTemplateParamRepository messageTemplateParamRepository;

    @Override
    public CustomJpaRepository<MessageTemplate, Long> getRepository() {
        return messageTemplateRepository;
    }

    @Override
    public Specification<MessageTemplate> buildSpecification(Class<MessageTemplate> classz, Map<String, Object> searchParams, LinkType linkType) {
        return SpecificationUtil.buildSpecification(MessageTemplate.class,searchParams,linkType);
    }


    /**
     * 通过模板id查询模板参数列表
     * @param templateId
     * @return
     */
    public List<MessageTemplateParam> findByTemplateId(Long templateId){

        Optional<MessageTemplate> messageTemplateOptional = messageTemplateRepository.findWholeById(templateId);

        if(messageTemplateOptional.isPresent()){

            MessageTemplate messageTemplate = messageTemplateOptional.get();

            return messageTemplate.getMessageTemplateParams();

        }

        return Lists.newArrayList();

    }

    /**
     * 新增模板参数
     * @param messageTemplateParam
     * @return
     */
    public MessageTemplateParam createMessageTemplateParam(MessageTemplateParam messageTemplateParam){

        BeanValidators.validateWithException(validator,messageTemplateParam, First.class);

        messageTemplateParam = messageTemplateParamRepository.saveAndFlush(StringUtils.EMPTY,messageTemplateParam);

        messageTemplateParam.setAlias(messageTemplateParam.getName()+String.valueOf(messageTemplateParam.getId()));

        messageTemplateParamRepository.saveAndFlush(StringUtils.EMPTY,messageTemplateParam);

        return messageTemplateParam;
    }
}
