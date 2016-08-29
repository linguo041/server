package com.duoshouji.server.rest.user;

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
import com.duoshouji.server.service.auth.UnauthenticatedUserException;
import com.duoshouji.server.service.auth.UserTokenService;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.user.NoteBuilder;
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
		duoShouJiFacade.logout(mobileNumber);
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
			@PathVariable("account-id") MobileNumber mobileNumber,
			@RequestParam("nickname") String nickname
			) {
		duoShouJiFacade.updateNickname(mobileNumber, nickname);
	}
	
	@RequestMapping(path = "/notes", method = RequestMethod.POST)
	public PublishNoteResult publishNote(
			@PathVariable("account-id") MobileNumber mobileNumber,
			@RequestParam("title") String title,
			@RequestParam("content") String content
			) {
		NoteBuilder publisher = duoShouJiFacade.newNotePublisher(mobileNumber);
		publisher.setTitle(title);
		publisher.setContent(content);
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
			@PathVariable("account-id") MobileNumber mobileNumber
			) {
		NoteCollection notes = duoShouJiFacade.getUserPublishedNotes(mobileNumber);
		List<PublishedNote> returnValue = new LinkedList<PublishedNote>();
		for (Note note : notes) {
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
	public List<NoteJson> getNotes(
			@PathVariable("account-id") MobileNumber mobileNumber,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		NoteCollection notes;
		if (loadedSize < 0) {
			notes = duoShouJiFacade.pushSquareNotes(mobileNumber);
			loadedSize = 0;
		} else {
			notes = duoShouJiFacade.getPushedSquareNotes(mobileNumber);
		}
		notes = notes.subCollection(loadedSize, loadedSize + pageSize);
		List<NoteJson> returnValue = new ArrayList<NoteJson>();
		for (Note note : notes) {
			returnValue.add(convert(note));
		}
		return returnValue;
	}
	
	private NoteJson convert(Note note) {
		NoteJson noteJson = new NoteJson();
		noteJson.setNoteId(note.getNoteId());
		noteJson.setTitle(note.getTitle());
		noteJson.setImage(note.getMainImage().getUrl());
		noteJson.setImageWidth(note.getMainImage().getWidth());
		noteJson.setImageHeight(note.getMainImage().getHeight());
		noteJson.setPortrait(note.getOwner().getPortrait().getUrl());
		noteJson.setRank(note.getRank());
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

}
