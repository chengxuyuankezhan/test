package com.cqbxzc.go.pnp.infrastructure;

import com.cqbxzc.go.pnp.PnpDTO;
import com.google.common.collect.Lists;
import live.jialing.util.general.Collections3;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class PnpCacheComponent {

    @Value("${ali.pnp.defaultExpiration}")
    private Long default_expiration;

    @Autowired
    private RedisTemplate<String, PnpDTO> redisTemplate;

    /**
     * 缓存数据
     *
     * @param pnpDTO 数据
     */
    public void cacheData(PnpDTO pnpDTO) {
        cacheData(pnpDTO, default_expiration);
    }

    /**
     * 缓存数据
     *
     * @param pnpDTO 数据
     * @param expir  过期时间
     */
    public void cacheData(PnpDTO pnpDTO, long expir) {
        if (StringUtils.isBlank(pnpDTO.getPhoneA()) || StringUtils.isBlank(pnpDTO.getPhoneB())) {
            return;
        }
        String cacheKeyA = getCacheKey(pnpDTO.getPhoneA(), pnpDTO.getPhoneB());
        if (!redisTemplate.hasKey(cacheKeyA)) {
            redisTemplate.opsForValue().set(cacheKeyA, pnpDTO, expir, TimeUnit.SECONDS);
        }
        String cacheKeyB = getCacheKey(pnpDTO.getPhoneB(), pnpDTO.getPhoneA());
        if (!redisTemplate.hasKey(cacheKeyB)) {
            redisTemplate.opsForValue().set(cacheKeyB, pnpDTO, expir, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取司机相关的缓存数据
     *
     * @param phoneA 驾驶员电话
     * @return 第一个驾驶员绑定关系
     */
    public List<PnpDTO> getDriverCacheData(String phoneA) {

        Set<String> keys = redisTemplate.keys(getCacheKey(phoneA, "*"));
        if (Collections3.isNotEmpty(keys)) {
            return redisTemplate.opsForValue().multiGet(keys);
        }
        return Lists.newArrayList();
    }

    /**
     * 获取驾驶员绑定的最短解除时间
     *
     * @param driverMobile 驾驶员电话
     * @return 驾驶员绑定最早解除的时间
     */
    public long getMinExpirByDriver(String driverMobile) {
        Set<String> keys = redisTemplate.keys(getCacheKey(driverMobile, "*"));
        if (Collections3.isNotEmpty(keys)) {
            Optional<Long> optional = keys.stream().map(f -> {
                Long expire = redisTemplate.getExpire(f, TimeUnit.SECONDS);
                if (expire == null) {
                    expire = 0L;
                }
                return expire;
            }).min(Comparator.naturalOrder());
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return 0;
    }

    /**
     * 获取数据
     *
     * @param phoneA A号码
     * @param phoneB B号码
     * @return 缓存数据
     */
    public Optional<PnpDTO> getData(String phoneA, String phoneB) {
        if (StringUtils.isBlank(phoneA) || StringUtils.isBlank(phoneB)) {
            return Optional.empty();
        }
        String cacheKeyA = getCacheKey(phoneA, phoneB);
        if (redisTemplate.hasKey(cacheKeyA)) {
            return Optional.ofNullable(redisTemplate.opsForValue().get(cacheKeyA));
        }
        String cacheKeyB = getCacheKey(phoneB, phoneA);
        if (redisTemplate.hasKey(cacheKeyB)) {
            return Optional.ofNullable(redisTemplate.opsForValue().get(cacheKeyB));
        }
        return Optional.empty();
    }

    /**
     * 删除缓存数据
     *
     * @param phoneA 电话A
     * @param phoneB 电话B
     */
    public void removeData(String phoneA, String phoneB) {
        if (StringUtils.isBlank(phoneA) || StringUtils.isBlank(phoneB)) {
            return;
        }
        String cacheKeyA = getCacheKey(phoneA, phoneB);
        if (redisTemplate.hasKey(cacheKeyA)) {
            redisTemplate.delete(cacheKeyA);
        }
        String cacheKeyB = getCacheKey(phoneA, phoneB);
        if (redisTemplate.hasKey(cacheKeyB)) {
            redisTemplate.delete(cacheKeyB);
        }
    }

    /**
     * 获取缓存键
     *
     * @param phoneA 电话A
     * @param phoneB 电话B
     * @return 缓存键
     */
    private String getCacheKey(String phoneA, String phoneB) {
        return MessageFormat.format("pnp:bind:{0}-{1}", phoneA, phoneB);
    }

}
