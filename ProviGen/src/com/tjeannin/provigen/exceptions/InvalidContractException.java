package com.tjeannin.provigen.exceptions;

public class InvalidContractException extends BaseException {
	public InvalidContractException(final String string) {
		super(string);
	}

	public InvalidContractException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
