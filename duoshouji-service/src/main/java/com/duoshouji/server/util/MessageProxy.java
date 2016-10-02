package com.duoshouji.server.util;

import com.duoshouji.util.MobileNumber;

public interface MessageProxy {

	void sendVerificationCode(VerificationCode verificationCode);

	void sendInvitationMessage(MobileNumber inviterMobileNumber);

}
