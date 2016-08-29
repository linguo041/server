package com.duoshouji.server.service;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.user.NoteBuilder;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public interface DuoShouJiFacade {
	
	void sendLoginVerificationCode(MobileNumber accountId);
	
	boolean verificationCodeLogin(MobileNumber accountId, VerificationCode verificationCode);
	
	boolean passwordLogin(MobileNumber accountId, Password mockPassword);

	boolean resetPassword(MobileNumber accountId, VerificationCode code, Password password);

	void sendResetPasswordVerificationCode(MobileNumber accountId);

	void updateNickname(MobileNumber accountId, String nickname);

	NoteBuilder newNotePublisher(MobileNumber accountId);

	NoteCollection getUserPublishedNotes(MobileNumber accountId);

	void logout(MobileNumber accountId);
	
	NoteCollection pushSquareNotes(MobileNumber accountId);

	NoteCollection getPushedSquareNotes(MobileNumber accountId);

}
