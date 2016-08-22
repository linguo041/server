package com.duoshouji.server.service;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.user.NoteBuilder;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public interface DuoShouJiFacade {
	
	void sendLoginVerificationCode(MobileNumber accountId);
	
	String verificationCodeLogin(MobileNumber accountId, VerificationCode verificationCode);
	
	String passwordLogin(MobileNumber accountId, Password mockPassword);

	boolean resetPassword(String userToken, VerificationCode code, Password password);

	void sendResetPasswordVerificationCode(String userToken);

	void updateNickname(String userToken, String nickname);

	NoteBuilder newNotePublisher(String userToken);

	NoteCollection getUserPublishedNotes(String userToken);

	void logout(String userToken);
	
	NoteCollection pushSquareNotes(String userToken);

	NoteCollection getPushedSquareNotes(String userToken);

}
