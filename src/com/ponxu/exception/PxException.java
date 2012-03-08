package com.ponxu.exception;

public class PxException extends RuntimeException {
	private static final long serialVersionUID = 264174247411329727L;

	public PxException(String message) {
		super(message);
	}

	public PxException(Throwable cause) {
		super(cause);
	}

}
