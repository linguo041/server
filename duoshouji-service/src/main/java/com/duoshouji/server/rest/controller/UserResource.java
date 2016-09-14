package com.duoshouji.server.rest.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.DuoShouJiFacade.NoteBuilder;
import com.duoshouji.server.service.DuoShouJiFacade.SquareNoteRequester;
import com.duoshouji.server.service.auth.UnauthenticatedUserException;
import com.duoshouji.server.service.auth.UserTokenService;
import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.BasicNoteAndOwner;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.service.user.UserProfile;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.server.util.VerificationCodeNotMatchException;

@RestController
@RequestMapping(path = "/accounts/{account-id}")
public class UserResource {

	private DuoShouJiFacade duoShouJiFacade;
	private UserTokenService anthentication;
	
	@Autowired
	public UserResource(DuoShouJiFacade userFacade, UserTokenService anthentication) {
		super();
		this.duoShouJiFacade = userFacade;
		this.anthentication = anthentication;
	}

	@ModelAttribute
	public void checkToken(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token,
			@PathVariable("account-id") MobileNumber mobileNumber) throws UnauthenticatedUserException {
		final boolean isAuthenticated = anthentication.verify(mobileNumber, token);
		if (!isAuthenticated) {
			throw new UnauthenticatedUserException("Token is expired or not exists.");
		}
	}
	
	@RequestMapping(path = "/logout", method = RequestMethod.POST)
	public void logout(
			@PathVariable("account-id") MobileNumber mobileNumber) {
		anthentication.logout(mobileNumber);
	}
	
	@RequestMapping(path = "/message/verification-code/reset-password", method = RequestMethod.POST)
	public void sendResetPasswordVerificationCode(
			@PathVariable("account-id") MobileNumber mobileNumber
			) {
		duoShouJiFacade.sendResetPasswordVerificationCode(mobileNumber);
	}
	
	@RequestMapping(path = "/settings/security/password", method = RequestMethod.POST)
	public void resetPassword(
			@PathVariable("account-id") MobileNumber mobileNumber,
			@RequestParam("code") String code,
			@RequestParam("password") String password
			) {
		Password acceptedPassword = Password.valueOf(password);
		boolean success = duoShouJiFacade.resetPassword(mobileNumber, VerificationCode.valueOf(code), acceptedPassword);
		if (!success) {
			throw new VerificationCodeNotMatchException();
		}
	}
	
	@RequestMapping(path = "/settings/profile", method = RequestMethod.POST)
	public void updateProfile(
			@PathVariable("account-id") MobileNumber accountId,
			@RequestParam(value="nickname", required=false) final String nickname,
			@RequestParam(value="gender", required=false) final Gender gender
			) {
		duoShouJiFacade.updateProfile(accountId, new BasicUserAttributes() {

			@Override
			public String getNickname() {
				return nickname;
			}

			@Override
			public Gender getGender() {
				return gender;
			}
			
		});
	}
	
	@RequestMapping(path = "/notes", method = RequestMethod.POST)
	public PublishNoteResult publishNote(
			@PathVariable("account-id") MobileNumber accountId,
			@RequestParam("categoryId") long categoryId,
			@RequestParam("brandId") long brandId,
			@RequestParam("productName") String productName,
			@RequestParam("price") BigDecimal price,
			@RequestParam("districtId") long districtId, 
			@RequestParam("tag") long[] tags,
			@RequestParam("title") String title,
			@RequestParam("content") String content,
			@RequestParam("rating") int rating,
			@RequestParam("longitude") BigDecimal longitude,
			@RequestParam("latitude") BigDecimal latitude
			) {
		NoteBuilder publisher = duoShouJiFacade.newNotePublisher(accountId);
		publisher.setCategoryId(categoryId);
		publisher.setBrandId(brandId);
		publisher.setProductName(productName);
		publisher.setPrice(price);
		publisher.setDistrictId(districtId);
		publisher.setTitle(title);
		publisher.setContent(content);
		publisher.setTags(tags);
		publisher.setRating(rating);
		publisher.setLocation(longitude, latitude);
		return new PublishNoteResult(publisher.publishNote());
	}
	
	public static class PublishNoteResult {
		private long noteId;

		private PublishNoteResult(long noteId) {
			this.noteId = noteId;
		}

		public long getNoteId() {
			return noteId;
		}
	}
		
	@RequestMapping(path = "/notes", method = RequestMethod.GET)
	public List<PublishedNote> getPublishedNotes(
			@PathVariable("account-id") MobileNumber mobileNumber,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		boolean refresh = false;
		if (loadedSize < 0) {
			loadedSize = 0;
			refresh = true;
		}
		NoteCollection notes = duoShouJiFacade.getUserPublishedNotes(mobileNumber, refresh)
				.subCollection(loadedSize, loadedSize + pageSize);
		List<PublishedNote> returnValue = new LinkedList<PublishedNote>();
		for (BasicNote note : notes) {
			PublishedNote pn = new PublishedNote();
			pn.noteId = note.getNoteId();
			pn.publishTime = note.getPublishedTime();
			pn.image = note.getMainImage().getUrl();
			pn.imageWidth = note.getMainImage().getWidth();
			pn.imageHeight = note.getMainImage().getHeight();
			pn.noteTitle = note.getTitle();
			pn.likeCount = note.getLikeCount();
			pn.commentCount = note.getCommentCount();
			pn.transactionCount = note.getTransactionCount();
			returnValue.add(pn);
		}
		return returnValue;
	}
	
	public static class PublishedNote {
		long noteId;
		long publishTime;
		String image;
		int imageHeight;
		int imageWidth;
		String noteTitle;
		int likeCount;
		int commentCount;
		int transactionCount;
		
		public long getNoteId() {
			return noteId;
		}
		public long getPublishTime() {
			return publishTime;
		}
		public String getImage() {
			return image;
		}
		public int getImageHeight() {
			return imageHeight;
		}
		public int getImageWidth() {
			return imageWidth;
		}
		public String getNoteTitle() {
			return noteTitle;
		}
		public int getLikeCount() {
			return likeCount;
		}
		public int getCommentCount() {
			return commentCount;
		}
		public int getTransactionCount() {
			return transactionCount;
		}
	}
	
	@RequestMapping(path = "/pushed/notes", method = RequestMethod.GET)
	public List<NoteJson> getPushedNotes(
			@PathVariable("account-id") MobileNumber mobileNumber,
			@RequestParam(value="tagId", required=false) Long tagId,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		SquareNoteRequester requester = duoShouJiFacade.newSquareNoteRequester(mobileNumber);
		if (tagId != null) {
			requester.setTagId(tagId.longValue());
		}
		boolean refresh = false;
		if (loadedSize < 0) {
			loadedSize = 0;
			refresh = true;
		}
		List<NoteJson> returnValue = new ArrayList<NoteJson>();
		for (BasicNoteAndOwner noteAndOwner : requester.pushSquareNotes(refresh, loadedSize, pageSize)) {
			returnValue.add(convert((BasicNoteAndOwner)noteAndOwner));
		}
		return returnValue;
	}
	
	private NoteJson convert(BasicNoteAndOwner noteAndOwner) {
		BasicNote note = noteAndOwner.getNote();
		NoteJson noteJson = new NoteJson();
		noteJson.setNoteId(note.getNoteId());
		noteJson.setTitle(note.getTitle());
		noteJson.setImage(note.getMainImage().getUrl());
		noteJson.setImageWidth(note.getMainImage().getWidth());
		noteJson.setImageHeight(note.getMainImage().getHeight());
		noteJson.setPortrait(noteAndOwner.getOwner().getPortrait().getUrl());
		noteJson.setRank(note.getRating());
		noteJson.setLikeCount(note.getLikeCount());
		noteJson.setCommentCount(note.getCommentCount());
		return noteJson;
	}

	public class NoteJson {

		private long noteId;
		private String title;
		private String image;
		private int imageWidth;
		private int imageHeight;
		private String portrait;
		private int rank;
		private int likeCount;
		private int commentCount;

		public long getNoteId() {
			return noteId;
		}

		public String getTitle() {
			return title;
		}

		public String getImage() {
			return image;
		}

		public int getImageWidth() {
			return imageWidth;
		}

		public int getImageHeight() {
			return imageHeight;
		}

		public String getPortrait() {
			return portrait;
		}

		public int getRank() {
			return rank;
		}

		public int getLikeCount() {
			return likeCount;
		}

		public int getCommentCount() {
			return commentCount;
		}

		void setNoteId(long noteId) {
			this.noteId = noteId;
		}

		void setTitle(String title) {
			this.title = title;
		}

		void setImage(String image) {
			this.image = image;
		}

		void setImageWidth(int imageWidth) {
			this.imageWidth = imageWidth;
		}

		void setImageHeight(int imageHeight) {
			this.imageHeight = imageHeight;
		}

		void setPortrait(String portrait) {
			this.portrait = portrait;
		}

		void setRank(int rank) {
			this.rank = rank;
		}

		void setLikeCount(int likeCount) {
			this.likeCount = likeCount;
		}

		void setCommentCount(int commentCount) {
			this.commentCount = commentCount;
		}

	}

	
	@RequestMapping(path = "/profile", method = RequestMethod.GET)
	public UserProfileView getUserProfile(@PathVariable("account-id") MobileNumber mobileNumber) {
		return new UserProfileView(duoShouJiFacade.getUserProfile(mobileNumber));
	}
	
	public static class UserProfileView {
		private UserProfile profile;
		
		private UserProfileView(UserProfile profile) {
			super();
			this.profile = profile;
		}

		public long getUserId() {
			return profile.getMobileNumber().toLong();
		}
		
		public String getPortraitUrl() {
			return profile.getPortrait().getUrl();
		}
		
		public int getPortraitHeight() {
			return profile.getPortrait().getHeight();
		}
		
		public int getPortraitWidth() {
			return profile.getPortrait().getWidth();
		}
		
		public String getNickname() {
			return profile.getNickname();
		}
		
		public Gender getGender() {
			return profile.getGender();
		}
		
		public BigDecimal getTotalRevenue() {
			return profile.getTotalRevenue();
		}
		
		public int getPublishedNoteCount() {
			return profile.getPublishedNoteCount();
		}
		
		public int getTransactionCount() {
			return profile.getTransactionCount();
		}
		
		public int getWatchCount() {
			return profile.getWatchCount();
		}
		
		public int getFanCount() {
			return profile.getFanCount();
		}
	}
	
	@RequestMapping(path = "/follow", method = RequestMethod.POST)
	public void watchUser(
			@PathVariable("account-id") MobileNumber watcherId,
			@RequestParam("userId") MobileNumber watchedId
			) {
		duoShouJiFacade.watchUser(watcherId, watchedId);
	}
}
