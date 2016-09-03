package com.duoshouji.server.service;

import java.util.List;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.Tag;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public interface DuoShouJiFacade {
	
	public interface SquareNoteRequester {

		void setTagId(long tagId);

		NoteCollection pushSquareNotes();

		NoteCollection getPushedSquareNotes();

	}
	
	public interface NoteBuilder {

		void setTitle(String title);

		void setContent(String content);

		long publishNote();

		void setTags(long[] tags);

	}

	void sendLoginVerificationCode(MobileNumber accountId);
	
	boolean verificationCodeLogin(MobileNumber accountId, VerificationCode verificationCode);
	
	boolean passwordLogin(MobileNumber accountId, Password mockPassword);

	boolean resetPassword(MobileNumber accountId, VerificationCode code, Password password);

	void sendResetPasswordVerificationCode(MobileNumber accountId);

	void updateNickname(MobileNumber accountId, String nickname);

	NoteBuilder newNotePublisher(MobileNumber accountId);

	NoteCollection getUserPublishedNotes(MobileNumber accountId);

	void logout(MobileNumber accountId);

	List<Tag> getTags();

	SquareNoteRequester newSquareNoteRequester(MobileNumber mobileNumber);

}
