package com.duoshouji.restapi;

public final class StandardJsonResponse {
	
	private static final StandardJsonResponse EMPTY_RESPONSE = new StandardJsonResponse();
	
	private int resultCode;
	private String resultErrorMessage;
	private Object resultValues;
	
	private StandardJsonResponse(Object resultValues) {
		resultCode = ResultCode.SUCCESS;
		this.resultValues = resultValues;
	}
	
	private StandardJsonResponse(Exception e) {
		this.resultCode = ResultCode.ERROR;
		this.resultErrorMessage = e.getMessage();
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

	public static StandardJsonResponse wrapResponse(Exception ex) {
		return new StandardJsonResponse(ex);
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
