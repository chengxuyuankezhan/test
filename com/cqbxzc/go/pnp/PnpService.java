package com.cqbxzc.go.pnp;

import com.cqbxzc.go.pnp.dict.PnpStatusEnum;
import com.cqbxzc.go.pnp.domain.PnpBindRecord;
import com.cqbxzc.go.pnp.domain.PnpBindRecordService;
import com.cqbxzc.go.pnp.infrastructure.PnpCacheComponent;
import com.cqbxzc.pnp.PnpBindResult;
import com.cqbxzc.pnp.PnpProxy;
import com.cqbxzc.pnp.PnpUnbindResult;
import live.jialing.util.general.Collections3;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PnpService {

    /**
     * 默认的过期时间（秒）
     */
    @Value("${ali.pnp.defaultExpiration}")
    private Long defaultExpiration;


    private final PnpProxy pnpProxy;

    private final PnpCacheComponent pnpCacheComponent;

    private final PnpBindRecordService pnpBindRecordService;


    @Autowired
    public PnpService(PnpProxy pnpProxy, PnpCacheComponent pnpCacheComponent, PnpBindRecordService pnpBindRecordService) {
        this.pnpProxy = pnpProxy;
        this.pnpCacheComponent = pnpCacheComponent;
        this.pnpBindRecordService = pnpBindRecordService;
    }

    // 一个司机、多个乘客
    // 绑定了，行程就需要这些数据，同步
    // 缓存所有的中间号码，缓存绑定情况，获取可以中间号码，获取绑定情况

    /**
     * 绑定
     * <p>
     * 同步
     *
     * @param driverMobile 司机手机号
     * @param guestMobile  乘客手机号集合
     * @return 结果列表
     */
    @Transactional
    public List<PnpDTO> binding(String driverMobile, List<String> guestMobile) {

        return guestMobile.stream().distinct().map(g -> {
            Optional<PnpDTO> optional = binding(driverMobile, g);
            return optional.orElse(null);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 绑定
     * <p>
     * 同步
     *
     * @param phoneA 电话A
     * @param phoneB 电话A
     * @return 中间号码
     */
    @Transactional
    public Optional<PnpDTO> binding(String phoneA, String phoneB) {

        return binding(phoneA, phoneB, defaultExpiration);
    }

    /**
     * 即时绑定 - 司机绑定乘客
     *
     * @param driverMobile 驾驶员电话
     * @param memberMobile 乘客电话
     * @return 绑定结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Optional<PnpDTO> instantBindByDriver(String driverMobile, String memberMobile) {
        return instantBindByMember(driverMobile, memberMobile, defaultExpiration);
    }

    /**
     * 即时绑定 - 乘客绑定司机
     *
     * @param driverMobile 驾驶员电话
     * @param memberMobile 乘客电话
     * @return 绑定结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Optional<PnpDTO> instantBindByMember(String driverMobile, String memberMobile) {
        return instantBindByDriver(driverMobile, memberMobile, defaultExpiration);
    }

    /**
     * 即时绑定 - 司机绑定乘客
     *
     * @param driverMobile 司机电话
     * @param memberMobile 乘客电话
     * @param expir        超时时间
     * @return 绑定结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Optional<PnpDTO> instantBindByDriver(String driverMobile, String memberMobile, long expir) {
        //1、查询是否存在绑定关系
        Optional<PnpDTO> cacheOpt = pnpCacheComponent.getData(driverMobile, memberMobile);
        if (cacheOpt.isPresent()) {
            return cacheOpt.map(f -> PnpDTO.builder()
                    .phoneA(f.getPhoneA())
                    .phoneB(f.getPhoneB())
                    .phoneX(f.getPhoneX())
                    .isSuccess(true)
                    .build());
        }

        //2、查询司机绑定信息
        List<PnpDTO> pnpDTOS = pnpCacheComponent.getDriverCacheData(driverMobile);
        if (Collections3.isNotEmpty(pnpDTOS)) {
            // 解除对应的关系
            pnpDTOS.forEach(pnpDTO -> {
                Future<Boolean> booleanFuture = unBind(pnpDTO.getPhoneA(), pnpDTO.getPhoneB());
                if (booleanFuture.isDone()) {
                    pnpCacheComponent.removeData(pnpDTO.getPhoneA(), pnpDTO.getPhoneB());
                }
            });
        }

        return binding(driverMobile, memberMobile, expir);
    }

    /**
     * 即时绑定 - 乘客绑定司机
     *
     * @param driverMobile 司机电话
     * @param memberMobile 乘客电话
     * @param expir        超时时间
     * @return 绑定结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Optional<PnpDTO> instantBindByMember(String driverMobile, String memberMobile, long expir) {
        //1、查询是否存在绑定关系
        Optional<PnpDTO> cacheOpt = pnpCacheComponent.getData(driverMobile, memberMobile);
        if (cacheOpt.isPresent()) {
            return cacheOpt.map(f -> PnpDTO.builder()
                    .phoneA(f.getPhoneA())
                    .phoneB(f.getPhoneB())
                    .phoneX(f.getPhoneX())
                    .isSuccess(true)
                    .build());
        }

        //2、查询司机绑定信息
        List<PnpDTO> pnpDTOS = pnpCacheComponent.getDriverCacheData(driverMobile);
        if (Collections3.isNotEmpty(pnpDTOS) && pnpDTOS.size() > 3) {
            //表示存在对应关系
            long minExpir = pnpCacheComponent.getMinExpirByDriver(driverMobile);
            if (expir - minExpir < 180) {
                log.info("同一驾驶员绑定太频繁了，驾驶员手机号：{}", driverMobile);
                return Optional.empty();
            }
            // 解除对应的关系
            PnpDTO pnpDTO = pnpDTOS.get(0);
            Future<Boolean> booleanFuture = unBind(pnpDTO.getPhoneA(), pnpDTO.getPhoneB());
            if (booleanFuture.isDone()) {
                pnpCacheComponent.removeData(pnpDTO.getPhoneA(), pnpDTO.getPhoneB());
            }
        }

        return binding(driverMobile, memberMobile, expir);
    }

    /**
     * 绑定
     * <p>
     * 同步
     *
     * @param phoneA     电话A
     * @param phoneB     电话B
     * @param expiration 过期时间(秒)
     * @return 中间号码
     */
    private Optional<PnpDTO> binding(String phoneA, String phoneB, Long expiration) {

        if (StringUtils.isBlank(phoneA) || StringUtils.isBlank(phoneB)
                || expiration == null || expiration < 1L) {
            return Optional.empty();
        }

        Optional<PnpDTO> optional = pnpCacheComponent.getData(phoneA, phoneB);
        if (optional.isPresent()) {
            //表示已经绑定
            return Optional.ofNullable(PnpDTO.builder()
                    .phoneA(phoneA)
                    .phoneB(phoneB)
                    .phoneX(getZHPhoneX(optional.get().getPhoneX()))
                    .isSuccess(true)
                    .build());
        }

        PnpBindResult pnpBindResult = pnpProxy.bind(phoneA, phoneB, LocalDateTime.now().plusSeconds(expiration));

        PnpBindRecord pnpBindRecord = PnpBindRecord.builder()
                .phoneA(phoneA)
                .phoneB(phoneB)
                .phoneX(pnpBindResult.getPhoneX())
                .bindTime(LocalDateTime.now())
                .poolKey(pnpBindResult.getPoolKey())
                .subsId(pnpBindResult.getSubsId())
                .status(PnpStatusEnum.bind)
                .errormsg(pnpBindResult.getErrormsg())
                .build();

        if (!pnpBindResult.isSuccess()) {
            pnpBindRecord.setStatus(PnpStatusEnum.unbind);
        }

        pnpBindRecord = pnpBindRecordService.create(pnpBindRecord);

        //缓存数据
        if (pnpBindResult.isSuccess()) {
            pnpCacheComponent.cacheData(PnpDTO.builder()
                    .phoneA(phoneA)
                    .phoneB(phoneB)
                    .phoneX(pnpBindRecord.getPhoneX())
                    .poolKey(pnpBindRecord.getPoolKey())
                    .subsId(pnpBindRecord.getSubsId())
                    .recordId(pnpBindRecord.getId())
                    .isSuccess(true)
                    .build(), expiration);

        }

        return Optional.of(PnpDTO.builder()
                .phoneA(phoneA)
                .phoneB(phoneB)
                .phoneX(pnpBindResult.getPhoneX())
                .isSuccess(pnpBindResult.isSuccess())
                .build());
    }

    /**
     * 解绑
     * <p>
     * 异步
     *
     * @param phoneA 司机手机号
     * @param phoneB 乘客手机号
     */
    @Async
    @Transactional
    public Future<Boolean> unBind(String phoneA, String phoneB) {

        if (StringUtils.isBlank(phoneA) || StringUtils.isBlank(phoneB)) {
            return new AsyncResult<>(true);
        }

        boolean isSuccess = true;
        Optional<PnpDTO> optional = pnpCacheComponent.getData(phoneA, phoneB);
        if (optional.isPresent()) {
            //缓存中存在
            PnpDTO pnpDTO = optional.get();
            PnpUnbindResult result = pnpProxy.unBind(pnpDTO.getSubsId(), pnpDTO.getPhoneX(), pnpDTO.getPoolKey());

            Optional<PnpBindRecord> recordOpt = pnpBindRecordService.findByPK(optional.get().getRecordId());
            if (recordOpt.isPresent()) {
                PnpBindRecord pnpBindRecord = recordOpt.get();
                if (result.isSuccess()) {
                    //更新接驳时间
                    pnpBindRecord.setUnbindTime(LocalDateTime.now());
                    pnpBindRecord.setStatus(PnpStatusEnum.unbind);
                } else {
                    isSuccess = false;
                    if (StringUtils.isNotBlank(pnpBindRecord.getErrormsg())) {
                        pnpBindRecord.setErrormsg(MessageFormat.format("{0};{1}", result.getErrorMsg()));
                    } else {
                        pnpBindRecord.setErrormsg(result.getErrorMsg());
                    }
                }
                pnpBindRecordService.update(pnpBindRecord);
            }
            //清除缓存中的数据
            pnpCacheComponent.removeData(phoneA, phoneB);
        }

        return new AsyncResult<>(isSuccess);
    }

    /**
     * 获取中国地区的中间号
     *
     * @param phoneX 中间号
     * @return 带中国区号的中间号
     */
    private String getZHPhoneX(String phoneX) {
        return MessageFormat.format("+86{0}", phoneX);
    }
}
