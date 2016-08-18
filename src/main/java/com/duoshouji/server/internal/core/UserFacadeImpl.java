package com.duoshouji.server.internal.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.executor.MultiStepProcessSupport;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.service.user.UserNotExistsException;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Service
public class UserFacadeImpl implements UserFacade {

	private UserRepository userRepository;
	private MultiStepProcessSupport processSupport;
	
	@Autowired
	public UserFacadeImpl(UserRepository userRepository, MultiStepProcessSupport processSupport) {
		super();
		this.userRepository = userRepository;
		this.processSupport = processSupport;
	}

	@Override
	public void sendLoginVerificationCode(MobileNumber mobileNumber) {
		RegisteredUser user = userRepository.findUser(mobileNumber);
		if (user == null) {
			user = userRepository.createUser(mobileNumber);
		}
		processSupport
			.getVerificationCodeLoginExecutor(user)
			.sendVerificationCode();
	}

	@Override
	public RegisteredUser checkLoginVerificationCode(MobileNumber mobileNumber, VerificationCode verificationCode) {
		final RegisteredUser user = getUser(mobileNumber);
		if (processSupport
			.getVerificationCodeLoginExecutor(user)
			.authenticate(verificationCode)) {
			return user;
		} else {
			return null;
		}
	}

	@Override
	public RegisteredUser checkLoginPassword(MobileNumber mobileNumber, Password password) {
		RegisteredUser user = getUser(mobileNumber);
		if (user.verifyPassword(password)) {
			return user;
		} else {
			return null;
		}

	}
	
	private RegisteredUser getUser(MobileNumber mobileNumber) {
		RegisteredUser user = userRepository.findUser(mobileNumber);
		if (user == null) {
			throw new UserNotExistsException("Mobile: " + mobileNumber);
		}
		return user;
	}
}
