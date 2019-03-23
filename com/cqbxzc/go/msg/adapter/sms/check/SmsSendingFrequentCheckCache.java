package com.cqbxzc.go.msg.adapter.sms.check;

import com.cqbxzc.cache.redis.AbstractExpirationCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SmsSendingFrequentCheckCache  extends AbstractExpirationCache<Long> {

    public SmsSendingFrequentCheckCache(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public Long getExpiration() {
        return 60L;
    }

    @Override
    public String getCacheName() {
        return "smsSendingFrequentCheckCache";
    }
}
