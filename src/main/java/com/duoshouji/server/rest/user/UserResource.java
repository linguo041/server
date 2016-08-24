package com.duoshouji.server.rest.user;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.rest.StandardJsonResponse;
import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.user.NoteBuilder;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.server.util.WrongPasswordFormatException;

@RestController
public class UserResource {

	private DuoShouJiFacade userFacade;

	@Autowired
	private UserResource(DuoShouJiFacade userFacade) {
		this.userFacade = userFacade;
	}

	@RequestMapping(path = "/login/authenticate/verification-code", method = RequestMethod.POST)
	public StandardJsonResponse authenticateVerificationCode(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("code") String verificationCode
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		final String token = userFacade.verificationCodeLogin(mobile, VerificationCode.valueOf(verificationCode));
		VerificationCodeLoginResult result;
		if (token != null) {
			result = new VerificationCodeLoginResult(token, true);
		} else {
			result = new VerificationCodeLoginResult(false);
		}
		return StandardJsonResponse.wrapResponse(result);
	}
	
	@RequestMapping(path = "/login/authenticate/credential", method = RequestMethod.POST)
	public StandardJsonResponse authenticateCredential(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("password") String password
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		CredentialLoginResult result;
		final String token = userFacade.passwordLogin(mobile, Password.valueOf(password));
		try {
			if (token != null) {
				result = new CredentialLoginResult(token, 0);
			} else {
				result = new CredentialLoginResult(2);
			}
		} catch (PasswordNotSetException ex) {
			result = new CredentialLoginResult(1);
		}
		return StandardJsonResponse.wrapResponse(result);
	}
	
	private static abstract class LoginResult {

		private String token;

		public LoginResult(String token) {
			super();
			this.token = token;
		}

		public String getToken() {
			return token;
		}
		
		void setToken(String token) {
			this.token = token;
		}
	}
	
	public static class VerificationCodeLoginResult extends LoginResult {

		private boolean loginSuccess;

		public VerificationCodeLoginResult(boolean loginSuccess) {
			this(null, loginSuccess);
		}

		public VerificationCodeLoginResult(String token, boolean loginSuccess) {
			super(token);
			this.loginSuccess = loginSuccess;
		}

		public boolean isLoginSuccess() {
			return loginSuccess;
		}
	}
	
	public static class CredentialLoginResult extends LoginResult {

		private int loginResultCode;

		public CredentialLoginResult(int loginResultCode) {
			this(null, loginResultCode);
		}

		public CredentialLoginResult(String token, int loginResultCode) {
			super(token);
			this.loginResultCode = loginResultCode;
		}

		public int getLoginResultCode() {
			return loginResultCode;
		}

	}
	@RequestMapping(path = "/accounts/${account-id}/logout", method = RequestMethod.POST)
	public StandardJsonResponse logout(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token) {
		userFacade.logout(token);
		return StandardJsonResponse.emptyResponse();
	}
	
	@RequestMapping(path = "/accounts/${account-id}/settings/security/password", method = RequestMethod.POST)
	public StandardJsonResponse resetPassword(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token,
			@RequestParam("code") String code,
			@RequestParam("password") String password
			) {
		StandardJsonResponse response;
		try {
			Password acceptedPassword = Password.valueOf(password);
			boolean success = userFacade.resetPassword(token, VerificationCode.valueOf(code), acceptedPassword);
			if (success) {
				response = StandardJsonResponse.wrapResponse(new PasswordResetResult(0));
			} else {
				response = StandardJsonResponse.wrapResponse(new PasswordResetResult(1));
			}
		} catch (WrongPasswordFormatException e) {
			response = StandardJsonResponse.wrapResponse(e, new PasswordResetResult(2));
		}
		return response;
	}
	
	public static class PasswordResetResult {
		int passwordUpdateResultCode;

		private PasswordResetResult(int passwordUpdateResultCode) {
			super();
			this.passwordUpdateResultCode = passwordUpdateResultCode;
		}

		public int getPasswordUpdateResultCode() {
			return passwordUpdateResultCode;
		}
	}
	
	@RequestMapping(path = "/accounts/${account-id}/settings/profile", method = RequestMethod.POST)
	public StandardJsonResponse updateProfile(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token,
			@RequestParam("nickname") String nickname
			) {
		userFacade.updateNickname(token, nickname);
		return StandardJsonResponse.emptyResponse();
	}
	
	@RequestMapping(path = "/accounts/${account-id}/notes/note", method = RequestMethod.POST)
	public StandardJsonResponse publishNote(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token,
			@RequestParam("title") String title,
			@RequestParam("content") String content
			) {
		NoteBuilder publisher = userFacade.newNotePublisher(token);
		publisher.setTitle(title);
		publisher.setContent(content);
		return StandardJsonResponse.wrapResponse(new PublishNoteResult(publisher.publishNote()));
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
	
	@RequestMapping(path = "/accounts/${account-id}/notes", method = RequestMethod.POST)
	public StandardJsonResponse getUserPublishedNotes(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token
			) {
		NoteCollection notes = userFacade.getUserPublishedNotes(token);
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
		return StandardJsonResponse.wrapResponse(returnValue);
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
}
