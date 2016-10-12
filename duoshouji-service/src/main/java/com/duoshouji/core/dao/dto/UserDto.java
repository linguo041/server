package com.duoshouji.core.dao.dto;

import java.math.BigDecimal;

public class UserDto extends BasicUserDto {

	public String passwordDigest;
	public BigDecimal totalRevenue;
	public int publishedNoteCount;
	public int transactionCount;
	public int watchCount;
	public int fanCount;
}
