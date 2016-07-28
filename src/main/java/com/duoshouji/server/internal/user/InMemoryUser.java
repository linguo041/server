package com.duoshouji.server.internal.user;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.internal.executor.DelegatedVerificationCodeLoginExecutor;
import com.duoshouji.server.internal.executor.ExecutorHolder;
import com.duoshouji.server.internal.executor.SmsVerificationCodeAuthenticationExecutor;
import com.duoshouji.server.user.AccountSecurity;
import com.duoshouji.server.user.PasswordNotSetException;
import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.Toolkit;

@Service
public class InMemoryUser implements RegisteredUser, AccountSecurity, ExecutorHolder {

	private final UserIdentifier userId;
	private final MobileNumber mobileNumber;
	private String passwordDigest;
	private String passwordSalt;
	private final Toolkit toolkit;
	
	private VerificationCodeLoginExecutor executor;

	public InMemoryUser(UserIdentifier userId, Toolkit toolkit) {
		super();
		this.userId = userId;
		mobileNumber = new MobileNumber(userId.toString());
		this.toolkit = toolkit;
	}
	@Override
	public boolean verifyPassword(Password password) {
		if (passwordDigest == null) {
			throw new PasswordNotSetException();
		}
		return passwordDigest.equals(password.toString());
	}
	@Override
	public boolean hasPassword() {
		return passwordDigest != null;
	}
	@Override
	public UserIdentifier getIdentifier() {
		return userId;
	}
	
	private MobileNumber getMobileNumber() {
		return mobileNumber;
	}
	
	@Override
	public AccountSecurity getAccountSecurity() {
		return this;
	}
	
	@Override
	public VerificationCodeLoginExecutor processVerificationCodeLogin() {
		if (executor == null) {
			executor = new DelegatedVerificationCodeLoginExecutor(
					new SmsVerificationCodeAuthenticationExecutor(
							toolkit.getMessageProxy(getMobileNumber()), toolkit.getVerificationCodeGenerator()), this);
		}
		return executor;
	}
	
	@Override
	public void detachExecutor(Object executor) {
		executor = null;
	}
	
	@Override
	public void setPassword(Password password) {
		passwordDigest = password.toString();
	}
}
