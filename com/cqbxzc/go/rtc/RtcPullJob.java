package com.cqbxzc.go.rtc;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 定时删除实时通信过期数据
 */
@Slf4j
public class RtcPullJob extends QuartzJobBean {

    @Autowired
    private RtcService rtcService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("启动删除实时通信过期数据");

        rtcService.delete();

        log.info("完成删除实时通信过期数据");
    }
}
