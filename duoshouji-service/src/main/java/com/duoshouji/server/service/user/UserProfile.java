package com.duoshouji.server.service.user;

import java.math.BigDecimal;

public interface UserProfile extends BasicUser {

	BigDecimal getTotalRevenue();
	
	int getPublishedNoteCount();
	
	int getTransactionCount();
	
	int getWatchCount();
	
	int getFanCount();
}