package com.duoshouji.server.service.dao;

import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

public class BasicUserDto {
	public MobileNumber mobileNumber;
	public String nickname;
	public Image portrait;
	public Gender gender;
}
