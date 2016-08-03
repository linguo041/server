package com.duoshouji.server.internal.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.duoshouji.server.internal.executor.DelegatedVerificationCodeLoginExecutor;
import com.duoshouji.server.internal.executor.SmsVerificationCodeAuthenticationExecutor;
import com.duoshouji.server.service.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.RegisteredUserDto;
import com.duoshouji.server.service.user.UserAlreadyExistsException;
import com.duoshouji.server.service.user.UserDao;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCodeGenerator;

public class UserOperationManager implements UserRepository {
	
	private VerificationCodeGenerator codeGenerator;
	private MessageProxyFactory messageProxyFactory;
	private UserDao userDao;

	@Required
	@Autowired
	public void setCodeGenerator(VerificationCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
	}
	
	@Required
	@Autowired
	public void setMessageProxyFactory(MessageProxyFactory messageProxyFactory) {
		this.messageProxyFactory = messageProxyFactory;
	}

	@Required
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	boolean verifyPassword(OperationDelegatingMobileUser user, Password password) {
		return user.getPasswordDigest().equals(password.toString());
	}

	VerificationCodeLoginExecutor newVerificationCodeLoginExecutor(
			OperationDelegatingMobileUser user) {
		return new DelegatedVerificationCodeLoginExecutor(
				new SmsVerificationCodeAuthenticationExecutor(messageProxyFactory.getMessageProxy(user), codeGenerator), user);
	}

	public void setPassword(OperationDelegatingMobileUser user, Password password) {
		user.setPasswordDigest(password.toString());
	}

	@Override
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		return findUser(new UserIdentifier(mobileNumber));
	}
	
	@Override
	public RegisteredUser createUser(MobileNumber mobileNumber) {
		final UserIdentifier userId = new UserIdentifier(mobileNumber);
		if (containsUser(userId)) {
			throw new UserAlreadyExistsException("User already exists in system, user id: " + userId);
		}
		OperationDelegatingMobileUser user = new OperationDelegatingMobileUser(
				new InMemoryRegisteredUserDto(userId, mobileNumber), this);
		userDao.addUser(userId, mobileNumber);
		return user;
	}
	
	private boolean containsUser(UserIdentifier userId) {
		return findUser(userId) != null;
	}
	
	public RegisteredUser findUser(UserIdentifier userId) {
		RegisteredUser user = null;
		RegisteredUserDto userDto = userDao.findUser(userId);
		if (userDto != null) {
			user = new OperationDelegatingMobileUser(userDto, this); 
		}
		return user;
	}
}
