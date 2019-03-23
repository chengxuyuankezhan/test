package com.cqbxzc.go.rtc;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RtcPullJobConfig {

    @Bean
    public JobDetail rtcPullDetail() {

        return JobBuilder.newJob(RtcPullJob.class)
                .withIdentity("rtcPullJob")
                .withDescription("定时删除实时通信过期数据")
                .storeDurably()
                .build();
    }

    /**
     * 触发器
     *
     * @return
     */
    @Bean
    public Trigger rtcPullTrigger() {

        String cronExpression = "0 0 1 * * ? ";
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

//         SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
//                 .withIntervalInHours(24).repeatForever();

        JobDetail jobDetail = rtcPullDetail();

        Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity("rtcPullTrigger").withSchedule(scheduleBuilder).build();

        log.info("【 {} - {} 】定时任务已注册", jobDetail.getDescription(),cronExpression);

        return trigger;
    }
}
