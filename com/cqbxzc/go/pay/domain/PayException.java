package com.cqbxzc.go.pay.domain;

/**
 * 支付模块调用支付的异常
 *
 */
public class PayException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PayException() {
		super();
	}

	public PayException(String message) {
		super(message);
	}

	public PayException(Throwable throwable) {
		super(throwable);
	}

	public PayException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
