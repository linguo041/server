package com.duoshouji.server.rest;

public final class StandardJsonResponse {
	
	private static final StandardJsonResponse EMPTY_RESPONSE = new StandardJsonResponse();
	
	private int resultCode;
	private String resultErrorMessage;
	private Object resultValues;
	
	private StandardJsonResponse(Object resultValues) {
		resultCode = ResultCode.SUCCESS;
		this.resultValues = resultValues;
	}
	
	private StandardJsonResponse(String resultErrorMessage, Object resultValues) {
		this.resultCode = ResultCode.ERROR;
		this.resultErrorMessage = resultErrorMessage;
		this.resultValues = resultValues;
	}	
	
	public StandardJsonResponse() {
		resultCode = ResultCode.SUCCESS;
	}

	public static StandardJsonResponse emptyResponse() {
		return EMPTY_RESPONSE;
	}
	
	public static StandardJsonResponse wrapResponse(Object resultValues) {
		return new StandardJsonResponse(resultValues);
	}

	public static StandardJsonResponse wrapResponse(Exception ex, Object resultValues) {
		return new StandardJsonResponse(ex.getMessage(), resultValues);
	}	
	
	public int getResultCode() {
		return resultCode;
	}

	public String getResultErrorMessage() {
		return resultErrorMessage;
	}

	public Object getResultValues() {
		return resultValues;
	}
}
