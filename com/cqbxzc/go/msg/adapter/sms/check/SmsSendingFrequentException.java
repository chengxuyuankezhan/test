package com.cqbxzc.go.msg.adapter.sms.check;

/**
 * 短信发送频繁的异常
 */
public class SmsSendingFrequentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SmsSendingFrequentException() {
        super();
    }

    public SmsSendingFrequentException(String message) {
        super(message);
    }

    public SmsSendingFrequentException(Throwable cause) {
        super(cause);
    }

    public SmsSendingFrequentException(String message, Throwable cause) {
        super(message, cause);
    }

}
