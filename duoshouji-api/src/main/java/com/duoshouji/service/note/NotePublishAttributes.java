package com.duoshouji.service.note;

import java.math.BigDecimal;

import com.duoshouji.service.common.Brand;
import com.duoshouji.service.common.Category;
import com.duoshouji.service.common.District;

public interface NotePublishAttributes {

	public static final int MAX_TAG_COUNT = 9;
	
	public Category getCategory();

	public Brand getBrand();

	public String getProductName();

	public District getDistrict();

	public BigDecimal getPrice();

	public int getRating();

	public String getTitle();

	public String getContent();

}
