package com.duoshouji.server.service.image;

@SuppressWarnings("serial")
public class StoreImageException extends Exception {

	public StoreImageException(Throwable cause) {
		super(cause);
	}

	public StoreImageException(String message) {
		super(message);
	}
}
