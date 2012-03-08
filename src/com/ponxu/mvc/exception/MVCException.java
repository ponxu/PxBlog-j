package com.ponxu.mvc.exception;

public class MVCException extends RuntimeException {
	private static final long serialVersionUID = 264174247411329727L;

	public MVCException(String message) {
		super(message);
	}

	public MVCException(Throwable cause) {
		super(cause);
	}

}
