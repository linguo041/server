package com.duoshouji.server.service.note.recommand;

import java.math.BigDecimal;

import com.duoshouji.util.Image;

public interface EcommerceItem {

	String getProvider();
	
	String getProviderItemId();
	
	String getTitle();
	
	BigDecimal getPrice();
	
	Image getImage();
}
