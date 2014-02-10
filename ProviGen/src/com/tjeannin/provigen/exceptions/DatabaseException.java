package com.tjeannin.provigen.exceptions;

public class DatabaseException extends BaseException {
	public DatabaseException(final String message) {
		super(message);
	}

	public DatabaseException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
