package com.duoshouji.server.internal.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;

@Service
public class UserFacadeImpl implements UserFacade {

	private UserRepository userRepository;
	
	@Autowired
	public UserFacadeImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public RegisteredUser getUser(MobileNumber mobileNumber) {
		RegisteredUser user = userRepository.findUser(mobileNumber);
		if (user == null) {
			user = userRepository.createUser(mobileNumber);
		}
		return user;
	}

	@Override
	public RegisteredUser getUser(UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
