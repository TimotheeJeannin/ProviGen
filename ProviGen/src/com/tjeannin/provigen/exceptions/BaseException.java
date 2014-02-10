package com.tjeannin.provigen.exceptions;

public abstract class BaseException extends Exception {
	protected BaseException(final String message) {
		super(message);
	}

	protected BaseException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
