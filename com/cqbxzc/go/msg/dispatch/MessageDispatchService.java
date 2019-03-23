package com.cqbxzc.go.msg.dispatch;

import com.cqbxzc.go.msg.adapter.email.EmailMessageTask;
import com.cqbxzc.go.msg.adapter.email.EmailMessageService;
import com.cqbxzc.go.msg.adapter.sms.ShortMessageService;
import com.cqbxzc.go.msg.adapter.sms.ShortMessageTask;
import com.cqbxzc.go.msg.adapter.vms.VsmMessageTask;
import com.cqbxzc.go.msg.adapter.vms.VsmMessageService;
import com.cqbxzc.go.msg.adapter.wxmsg.WeixinMessageService;
import com.cqbxzc.go.msg.adapter.wxmsg.WeixinMessageTask;
import com.cqbxzc.go.msg.config.domain.MessageEventService;
import com.cqbxzc.go.msg.config.domain.MessageTemplate;
import com.cqbxzc.go.msg.config.domain.MessageTemplateParam;
import com.cqbxzc.go.msg.config.domain.MessageTemplateService;
import com.cqbxzc.go.msg.constant.EmailMessageConstant;
import com.cqbxzc.go.msg.constant.WeixinMessageConstant;
import com.cqbxzc.go.msg.mq.MessageTaskMQ;
import com.cqbxzc.go.msg.receive.MessageTask;
import com.google.common.collect.Maps;
import live.jialing.util.general.Collections3;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 消息任务的调度
 */
@Slf4j
@Service
@RabbitListener(queues = MessageTaskMQ.MESSAGE_TASK_DISPATCH_MQ_QUEUE)
public class MessageDispatchService {

    private static final String param_template_key = "param_template_key";

    @Autowired
    private ShortMessageService shortMessageService;

    @Autowired
    private EmailMessageService emailMessageService;

    @Autowired
    private VsmMessageService vsmMessageService;

    @Autowired
    private WeixinMessageService weixinMessageService;

    @Autowired
    private MessageEventService messageEventService;

    @Autowired
    private MessageTemplateService messageTemplateService;

    /**
     * 解析任务
     *
     * @param messageTask 消息任务
     */
    @RabbitHandler
    public void parser(@Payload @NonNull MessageTask messageTask) {

        log.info("开始处理消息任务的解析及分发：{}", messageTask.getCode());

        // 1.通过事件找到对应的模板
        List<MessageTemplate> messageTemplates = messageEventService.findMessageTemplatesByCode(messageTask.getEventCode());

        messageTemplates.forEach(t -> {

            // 3.通过模板找到对应的模板参数
            List<MessageTemplateParam> messageTemplateParams = messageTemplateService.findByTemplateId(t.getId());

            Map content = messageTask.getContent();
            Map<String, String> param = Maps.newHashMap();

            for (MessageTemplateParam m : messageTemplateParams) {
                if (!content.containsKey(m.getName())) {
                    log.warn("当前消息任务(code={})缺少对应的参数({})，无法发送", messageTask.getCode(), m.getName());
                    return;
                }
                param.put(m.getName(), MapUtils.getString(content, m.getName()));
            }
            // 加入模板
            param.put(param_template_key, t.getCode());
            messageTask.setContent(param);

            switch (t.getChannel()) {
                case sms:
                    messageTask.setContent(param);
                    sms(messageTask);
                    break;
                case wxmsg:
                    param.put(WeixinMessageConstant.PARAM_PAGE, MapUtils.getString(content, WeixinMessageConstant.PARAM_PAGE));
                    param.put(WeixinMessageConstant.PARAM_FORM_ID, MapUtils.getString(content, WeixinMessageConstant.PARAM_FORM_ID));
                    messageTask.setContent(param);
                    wxmsg(messageTask);
                    break;
                case vms:
                    vms(messageTask);
                    break;
                case email:
                    param.put(EmailMessageConstant.SUBJECT, MapUtils.getString(content, EmailMessageConstant.SUBJECT));
                    messageTask.setContent(param);
                    email(messageTask);
                    break;
                default:
                    log.warn("消息任务{}使用了未知的消息方式", messageTask.getCode());
                    break;
            }
        });

        log.info("消息任务分发：{} 完成", messageTask.getCode());
    }


    /**
     * 短信调度
     *
     * @param messageTask 消息任务
     */
    private void sms(MessageTask messageTask) {

        if (Collections3.isEmpty(messageTask.getContent())
                || messageTask.getReceiver() == null
                || Collections3.isEmpty(messageTask.getReceiver().getMobile())) {
            return;
        }

        Map param = messageTask.getContent();

        ShortMessageTask task = ShortMessageTask.builder()
                .taskCode(messageTask.getCode())
                .template(MapUtils.getString(param, param_template_key))
                .mobile(messageTask.getReceiver().getMobile())
                .build();

        // 去掉map中模版项
        param.remove(param_template_key);
        task.setData(param);

        shortMessageService.send(task);
    }


    /**
     * 微信消息
     * <p>
     * 当前一次只能发送一个openId
     *
     * @param messageTask 消息任务
     */
    private void wxmsg(MessageTask messageTask) {

        Map content = messageTask.getContent();

        WeixinMessageTask weixinMessageTask = WeixinMessageTask.builder()
                .taskCode(messageTask.getCode())
                .openId(messageTask.getReceiver().getOpenId().get(0))
                .page(MapUtils.getString(content, WeixinMessageConstant.PARAM_PAGE))
                .formId(MapUtils.getString(content, WeixinMessageConstant.PARAM_FORM_ID))
                .template(MapUtils.getString(content, param_template_key))
                .emphasisKeyword(MapUtils.getString(content, WeixinMessageConstant.PARAM_EMPHASIS_KEYWORD))
                .build();

        content.remove(WeixinMessageConstant.PARAM_PAGE);
        content.remove(param_template_key);
        content.remove(WeixinMessageConstant.PARAM_FORM_ID);
        content.remove(WeixinMessageConstant.PARAM_EMPHASIS_KEYWORD);

        weixinMessageTask.setParam(content);

        weixinMessageService.send(weixinMessageTask);
    }

    /**
     * 语音消息
     *
     * @param messageTask 消息任务
     */
    private void vms(MessageTask messageTask) {

        Map content = messageTask.getContent();

        VsmMessageTask data = VsmMessageTask.builder()
                .code(messageTask.getCode())
                .mobile(messageTask.getReceiver().getMobile())
                .template(MapUtils.getString(content, param_template_key))
                .build();

        content.remove(param_template_key);

        data.setContent(content);

        vsmMessageService.send(data);
    }


    /**
     * 邮件任务
     *
     * @param messageTask 消息任务
     */
    private void email(MessageTask messageTask) {

        Map content = messageTask.getContent();

        EmailMessageTask task = EmailMessageTask.builder()
                .taskCode(messageTask.getCode())
                .to(messageTask.getReceiver().getEmail())
                .sub(MapUtils.getString(content, EmailMessageConstant.SUBJECT))
                .text(MapUtils.getString(content, EmailMessageConstant.TEXT))
                .template(MapUtils.getString(content, param_template_key))
                .build();

        content.remove(EmailMessageConstant.SUBJECT);
        content.remove(EmailMessageConstant.TEXT);
        content.remove(param_template_key);

        task.setContent(content);

        emailMessageService.send(task);
    }
}
