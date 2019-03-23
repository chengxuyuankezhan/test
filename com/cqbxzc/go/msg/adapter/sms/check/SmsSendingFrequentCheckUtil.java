package com.cqbxzc.go.msg.adapter.sms.check;

import live.jialing.core.context.SpringUtils;

import java.text.MessageFormat;

/**
 * 短信发送频繁检查工具
 */
public class SmsSendingFrequentCheckUtil {

    /**
     * 检查时候发送频繁
     * <p>
     * 如果不频繁，则缓存起为下一次检查做准备
     *
     * @param mobile 手机号
     * @param mark   标示，一般是短信模版编码
     * @return true：频繁；false：不频繁
     */
    public static boolean checkAndNext(String mobile, String mark) {

        //1分钟
        SmsSendingFrequentCheckCache checkCache = SpringUtils.getApplicationContext().getBean(SmsSendingFrequentCheckCache.class);

        String key = MessageFormat.format("{0}:{1}", mobile, mark);

        if (checkCache.get(key).isPresent()) {
            return true;
        } else {
            checkCache.put(key, System.currentTimeMillis());
            return false;
        }
    }
}
