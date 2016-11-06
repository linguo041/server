package com.duoshouji.restapi.controller.model.request;

import java.math.BigDecimal;

public class PublishNoteRequestData {
	
	public long categoryId;
	public long brandId;
	public String productName;
	public BigDecimal price;
	public long districtId; 
	public String title;
	public String content;
	public int rating;
}
