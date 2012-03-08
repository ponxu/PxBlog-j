package com.ponxu.dbutils.exception;

public class DBException extends RuntimeException {
	private static final long serialVersionUID = -6935880875950016270L;

	public DBException(String message) {
		super(message);
	}

	public DBException(Throwable cause) {
		super(cause);
	}

}
