package com.duoshouji.server;

import java.util.LinkedList;
import java.util.List;

import com.duoshouji.server.internal.user.InMemoryRegisteredUserDto;
import com.duoshouji.server.service.user.RegisteredUserDto;
import com.duoshouji.server.service.user.UserDao;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

public class MockUserDao implements UserDao {
	
	public static final MockUserDao INSTANCE = new MockUserDao();
	
	private List<InMemoryRegisteredUserDto> userDtos;
	
	private MockUserDao() {
		userDtos = new LinkedList<InMemoryRegisteredUserDto>();
		loadExistingUsers();
	}
	
	@Override
	public RegisteredUserDto findUser(UserIdentifier userId) {
		for (InMemoryRegisteredUserDto u : userDtos) {
			if (u.getUserId().equals(userId))
				return u;
		}
		return null;
	}

	@Override
	public void addUser(UserIdentifier userId, MobileNumber mobileNumber) {
		userDtos.add(new InMemoryRegisteredUserDto(userId, mobileNumber));
	}

	private void loadExistingUsers() {
		InMemoryRegisteredUserDto userDto = new InMemoryRegisteredUserDto(
				MockConstants.MOCK_USER_IDENTIFIER
				, MockConstants.MOCK_MOBILE_NUMBER);
		userDto.setPasswordDigest(MockConstants.MOCK_PASSWORD.toString());
		userDtos.add(userDto);
	}
}
