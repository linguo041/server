package com.duoshouji.server.internal.user;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;

@Service
public class UserFacadeImpl implements UserFacade {

	private UserRepository userRepository;
	
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

}
