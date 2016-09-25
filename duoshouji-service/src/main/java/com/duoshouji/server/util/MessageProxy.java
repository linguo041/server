package com.duoshouji.server.util;

public interface MessageProxy {

	void sendVerificationCode(VerificationCode verificationCode);

	void sendInvitationMessage(MobileNumber inviterMobileNumber);

}
