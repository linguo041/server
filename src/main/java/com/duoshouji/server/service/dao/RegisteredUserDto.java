package com.duoshouji.server.service.dao;

import java.math.BigDecimal;

public class RegisteredUserDto extends BasicUserDto {

	public String passwordDigest;
	public BigDecimal totalRevenue;
	public int publishedNoteCount;
	public int transactionCount;
	public int watchCount;
	public int fanCount;
}
