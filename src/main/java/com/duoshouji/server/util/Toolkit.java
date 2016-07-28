package com.duoshouji.server.util;

public interface Toolkit {

	UserMessageProxy getMessageProxy(MobileNumber mobileNumber);

	VerificationCodeGenerator getVerificationCodeGenerator();
}
