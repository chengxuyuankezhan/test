package com.cqbxzc.go.msg.adapter.email;

import com.cqbxzc.email.mq.EmailMessageQueueConstant;
import com.cqbxzc.email.mq.EmailMessageQueueData;
import com.cqbxzc.go.msg.adapter.email.domain.EmailMessageSendRecord;
import com.cqbxzc.go.msg.adapter.email.domain.EmailMessageSendRecordService;
import com.cqbxzc.mq.RabbitProducer;
import live.jialing.util.general.IdGenerator;
import live.jialing.util.mapper.JsonMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailMessageService {

    @Autowired
    private EmailMessageSendRecordService emailMessageSendRecordService;

    /**
     * 发送邮件
     *
     * @param task 邮件任务
     */
    public void send(EmailMessageTask task) {

        String template = task.getTemplate();
        if (StringUtils.isBlank(template)) {
            log.warn("邮件任务模板为空，任务：{}", task.getTaskCode());
            return;
        }

        if (StringUtils.isBlank(task.getText())) {
            final String[] text = {getTemplateString(template)};
            task.getContent().forEach((k, v) -> {
                text[0] = text[0].replaceAll(MessageFormat.format("#{0}", k), v);
            });
            task.setText(text[0]);
        }
        String code = IdGenerator.uuid2();

        //持久化
        emailMessageSendRecordService.batchCreate(task.getTo().stream().map(t -> EmailMessageSendRecord.builder()
                .code(code)
                .taskCode(task.getTaskCode())
                .subject(task.getSub())
                .receive(t)
                .text(task.getText())
                .sendTime(LocalDateTime.now())
                .build()).collect(Collectors.toList()));

        EmailMessageQueueData data = EmailMessageQueueData.builder()
                .code(code)
                .to(task.getTo())
                .sub(task.getSub())
                .text(task.getText())
                .build();

        send(data);

    }

    /**
     * 发送邮件消息
     *
     * @param data 邮件消息
     */
    private void send(EmailMessageQueueData data) {
        CorrelationData correlationData = new CorrelationData(data.getCode());
        RabbitProducer.send(EmailMessageQueueConstant.EMAIL_MQ_DIRECT_EXCHANGE
                , EmailMessageQueueConstant.EMAIL_SEND_MQ_ROUTING_KEY
                , data, correlationData);

        log.info("添加到邮件消息的发送队列中。{}", JsonMapperUtil.toJ(data));
    }

    /**
     * 获取模板消息
     *
     * @param code 模板代码
     * @return 模板
     */
    private String getTemplateString(String code) {
        return "系统发生异常，异常信息：#info，异常时间：#time";
    }
}
