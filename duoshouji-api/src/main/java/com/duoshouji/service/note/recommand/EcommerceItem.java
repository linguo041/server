package com.duoshouji.service.note.recommand;

import java.math.BigDecimal;

import com.duoshouji.service.util.Image;

public interface EcommerceItem {

	String getProvider();
	
	String getProviderItemId();
	
	String getTitle();
	
	BigDecimal getPrice();
	
	Image getImage();
}
