package com.duoshouji.service.note;

import java.math.BigDecimal;

import com.duoshouji.service.common.Brand;
import com.duoshouji.service.common.Category;
import com.duoshouji.service.common.District;

public interface NotePublishAttributes {

	public static final int MAX_TAG_COUNT = 9;
	
	public Category getCategory();
	
	public boolean isCategorySet();

	public Brand getBrand();
	
	public boolean isBrandSet();

	public String getProductName();
	
	public boolean isProductNameSet();	

	public District getDistrict();
	
	public boolean isDistrictSet();

	public BigDecimal getPrice();
	
	public boolean isPriceSet();

	public int getRating();

	public String getTitle();

	public String getContent();

}
