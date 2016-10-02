package com.duoshouji.server.service;

import java.math.BigDecimal;
import java.util.List;

import com.duoshouji.server.service.interaction.BasicNoteAndOwner;
import com.duoshouji.server.service.interaction.NoteCommentAndAuthor;
import com.duoshouji.server.service.interaction.NoteDetailAndOwner;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.recommand.EcommerceItem;
import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.service.user.UserProfile;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.util.MobileNumber;

public interface DuoShouJiFacade {
	
	public interface SquareNoteRequester {

		void setTagId(long tagId);
		
		void setWatchedOnly(MobileNumber userId);
		
		void setUserLocation(BigDecimal longitude, BigDecimal latitude);

		List<BasicNoteAndOwner> getSquareNotes(long timestamp, int loadedSize, int pageSize);

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

	public interface CommentPublisher {
		
		void setComment(String comment);
		
		void setRating(int rating);
		
		void setLocation(BigDecimal longitude, BigDecimal latitude);
		
		void publishComment();
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

	SquareNoteRequester newSquareNoteRequester();

	NoteDetailAndOwner getNote(long noteId);

	CommentPublisher newCommentPublisher(long noteId, MobileNumber userId);
	
	void likeNote(long noteId, MobileNumber userId);
	
	void buildFollowConnection(MobileNumber followerId, MobileNumber followedId);

	List<EcommerceItem> getNoteRecommendations(long noteId);

	void inviteFriends(MobileNumber userId, MobileNumber[] mobileNumbers);

	List<NoteCommentAndAuthor> getNoteComments(long noteId);
}
