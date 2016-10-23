package com.duoshouji.core;

import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

public interface MessageProxy {

	void sendVerificationCode(VerificationCode verificationCode);

	void sendInvitationMessage(MobileNumber inviterMobileNumber);

}
