package com.duoshouji.server;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.duoshouji.server.internal.core.InMemoryRegisteredUserDto;
import com.duoshouji.server.internal.dao.UserNoteDao;
import com.duoshouji.server.internal.user.RegisteredUserDto;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

@Service
public class MockMysqlDao implements UserNoteDao {
		
	private List<InMemoryRegisteredUserDto> userDtos;
	
	public MockMysqlDao() {
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
