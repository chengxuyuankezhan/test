package com.cqbxzc.go.rtc;

import com.cqbxzc.middleware.rtc.mq.RtcQueueConstant;
import com.cqbxzc.middleware.rtc.mq.RtcQueueData;
import com.cqbxzc.mq.RabbitProducer;
import com.cqbxzc.rtc.leancloud.LeanStorageProxy;
import live.jialing.util.mapper.JsonMapperUtil;
import live.jialing.util.time.ClockUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 实时通信服务
 */
@Slf4j
@Service
@RabbitListener(queues = RtcQueueConstant.RTC_RESULT_MQ_QUEUE)
public class RtcService {

    private static final String cache_key_object = "objectId";
    private static final String cache_key_timestamp = "timestamp";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LeanStorageProxy leanStorageProxy;

    /**
     * 发送信息
     * <p>
     * 1.通过实体和唯一标识到缓存中查询对应的objectId
     * 2.将对应的objectId设置到参数中
     * 3.异步发送
     *
     * @param domain 领域唯一
     * @param code   数据唯一
     * @param param  参数
     */
    public void send(String domain, String code, Map param) {

        RtcQueueData rtcQueueData = RtcQueueData.builder()
                .className(domain)
                .code(code)
                .param(param)
                .build();

        String cacheName = getCacheName(domain, code);

        if (redisTemplate.hasKey(cacheName)) {
//            String objectId = (String) redisTemplate.opsForValue().get(cacheName);
            String objectId = (String) redisTemplate.opsForHash().get(cacheName, cache_key_object);
            rtcQueueData.setObjectId(objectId);
        }

        // 添加到队列
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        RabbitProducer.send(RtcQueueConstant.RTC_MQ_TOPIC_EXCHANGE, RtcQueueConstant.RTC_MQ_ROUTING_KEY, rtcQueueData, correlationData);
        log.info("已经添加到实时通信队列中。{}", JsonMapperUtil.toJ(rtcQueueData));
    }

    /**
     * 第三方系统处理结果的处理
     * <p>
     * 1.监听结果
     * 2.如果成功，保存对应关系；如果失败，记录失败原因
     */
    @RabbitHandler
    public void resultHandler(@Payload RtcQueueData data) {

        log.info("监听到实时通信结果");

        if (data == null) {
            return;
        }

        log.debug("开始处理实时通信结果：{}", data);

        if (data.isSuccess()) {
            String cacheName = getCacheName(data.getClassName(), data.getCode());
            if (!redisTemplate.hasKey(cacheName)) {
                //864000000
                //redisTemplate.opsForValue().set(cacheName, data.getObjectId(),20, TimeUnit.SECONDS);
                redisTemplate.opsForHash().putAll(cacheName, Map.of(cache_key_object, data.getObjectId(), cache_key_timestamp, System.currentTimeMillis()));
            }
            log.info("实时通信成功");
        } else {
            log.error("实时通信失败：{}；参数：{}；时间：{}", data.getErrorMsg(), data.getParam(), LocalDateTime.now().format(ClockUtil.LOCAL_DATE_TIME_FORMATTER));
        }
    }

    /**
     * 删除过期数据
     * <p>
     * 10天
     * </p>
     */
    public void delete() {

        // xxx 直接依赖了leanStorageProxy
        redisTemplate.keys("rtc:*").forEach(k -> {
            Long timestamp = (Long) redisTemplate.opsForHash().get(k, cache_key_timestamp);
            Duration duration = Duration.ofMillis(System.currentTimeMillis() - timestamp);
            if (duration.toDays() >= 10) {
                String objectId = (String) redisTemplate.opsForHash().get(k, cache_key_object);
                String className = StringUtils.split((String) k, ":")[1];
                leanStorageProxy.delete(className, objectId);
                redisTemplate.delete(k);
            }
        });
    }

    /**
     * 获取缓存名称
     *
     * @param className 领域名称
     * @param code      数据唯一标识
     * @return 缓存名称
     */
    private String getCacheName(String className, String code) {

        return MessageFormat.format("rtc:{0}:{1}", className, code);
    }

}
