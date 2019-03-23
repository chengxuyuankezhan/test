package com.cqbxzc.go.msg.config.domain;

import com.cqbxzc.go.msg.dict.MessageEventEnum;
import com.google.common.collect.Lists;
import live.jialing.core.data.LinkType;
import live.jialing.core.data.jpa.CustomJpaRepository;
import live.jialing.core.domain.service.GeneralService;
import live.jialing.core.domain.service.impl.SimpleGeneralService;
import live.jialing.data.util.SpecificationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MessageEventService extends SimpleGeneralService<MessageEvent,Long> implements GeneralService<MessageEvent,Long> {

    @Autowired
    private MessageEventRepository messageEventRepository;

    @Override
    public CustomJpaRepository<MessageEvent, Long> getRepository() {
        return messageEventRepository;
    }

    @Override
    public Specification<MessageEvent> buildSpecification(Class<MessageEvent> classz, Map<String, Object> searchParams, LinkType linkType) {
        return SpecificationUtil.buildSpecification(MessageEvent.class,searchParams,linkType);
    }

    /**
     * 通过事件代码查询模板列表
     * @param code 事件代码
     * @return
     */
    public List<MessageTemplate> findMessageTemplatesByCode(String code){

        Optional<MessageEvent> messageEventOptional = messageEventRepository.findWholeByCode(MessageEventEnum.valueOf(code));
        List<MessageTemplate> messageTemplates = Lists.newArrayList();
        if(messageEventOptional.isPresent()){
            MessageEvent messageEvent = messageEventOptional.get();
            messageTemplates = messageEvent.getMessageTemplates();
        }
        return messageTemplates;
    }



}
