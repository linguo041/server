package com.duoshouji.service.note;

import java.math.BigDecimal;

import com.duoshouji.service.common.Brand;
import com.duoshouji.service.common.Category;
import com.duoshouji.service.common.District;

public interface ReferredCommodity {

	Brand getBrand();
	
	Category getCategory();
	
	String getProductName();
	
	BigDecimal getPrice();
	
	District getDistrict();
}
