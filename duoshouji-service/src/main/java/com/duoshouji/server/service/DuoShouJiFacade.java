package com.duoshouji.server.service;

import java.math.BigDecimal;
import java.util.List;

import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.service.user.UserProfile;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public interface DuoShouJiFacade {
	
	public interface SquareNoteRequester {

		void setTagId(long tagId);

		NoteCollection pushSquareNotes(boolean refresh);

	}
	
	public interface NoteBuilder {

		void setTitle(String title);

		void setContent(String content);

		long publishNote();

		void setTags(long[] tags);

		void setCategoryId(long categoryId);

		void setBrandId(long brandId);

		void setProductName(String productName);

		void setPrice(BigDecimal price);

		void setDistrictId(long districtId);

		void setRating(int rating);

		void setLocation(BigDecimal longitude, BigDecimal latitude);

	}

	void sendLoginVerificationCode(MobileNumber accountId);
	
	boolean verificationCodeLogin(MobileNumber accountId, VerificationCode verificationCode);
	
	UserProfile getUserProfile(MobileNumber mobileNumber);
	
	boolean passwordLogin(MobileNumber accountId, Password mockPassword);

	boolean resetPassword(MobileNumber accountId, VerificationCode code, Password password);

	void sendResetPasswordVerificationCode(MobileNumber accountId);

	void updateProfile(MobileNumber accountId, BasicUserAttributes attributes);

	NoteBuilder newNotePublisher(MobileNumber accountId);

	NoteCollection getUserPublishedNotes(MobileNumber accountId, boolean refresh);

	SquareNoteRequester newSquareNoteRequester(MobileNumber mobileNumber);

	List<Tag> getTags();
}
