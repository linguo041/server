package com.duoshouji.restapi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.restapi.AuthenticationAdvice;
import com.duoshouji.restapi.controller.model.request.CommentNoteRequestData;
import com.duoshouji.restapi.controller.model.request.FollowUserRequestData;
import com.duoshouji.restapi.controller.model.request.InviteFriendsRequestData;
import com.duoshouji.restapi.controller.model.request.PublishNoteRequestData;
import com.duoshouji.restapi.controller.model.request.SetPasswordRequestData;
import com.duoshouji.restapi.controller.model.request.SetPersonalInformationRequestData;
import com.duoshouji.restapi.controller.model.response.BasicNoteResult;
import com.duoshouji.restapi.controller.model.response.MobileNumberMappingUserIdResult;
import com.duoshouji.restapi.controller.model.response.NotePublishingResult;
import com.duoshouji.restapi.controller.model.response.UserProfileResult;
import com.duoshouji.restapi.controller.model.response.WrongVerificationCodeException;
import com.duoshouji.restapi.image.ImageJsonAdapter;
import com.duoshouji.restapi.image.UploadNoteImageCallbackData;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.Note;
import com.duoshouji.service.note.NoteFacade;
import com.duoshouji.service.note.NoteFacade.NotePublisher;
import com.duoshouji.service.note.NoteFacade.SquareNoteRequester;
import com.duoshouji.service.note.NoteImage;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.user.UserFacade.UserProfileSetter;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AuthorizedResource extends AuthenticationAdvice {
	
	private Logger logger = LogManager.getLogger();
	
	private UserFacade userFacade;
	private NoteFacade noteFacade;
	
	@Required
	@Autowired
	public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}
	
	@Required
	@Autowired
	public void setNoteFacade(NoteFacade noteFacade) {
		this.noteFacade = noteFacade;
	}
	
	@GetMapping("/user/mobile-contacts/status")
	@ResponseBody
	public MobileNumberMappingUserIdResult[] getRegisteredUserIds(
			@RequestParam("mobiles") MobileNumber[] mobileNumbers) {
		MobileNumberMappingUserIdResult[] results = new MobileNumberMappingUserIdResult[mobileNumbers.length];
		for (int i = 0; i < mobileNumbers.length; ++i) {
			final MobileNumber mobileNumber = mobileNumbers[i];
			results[i] = new MobileNumberMappingUserIdResult(mobileNumber, userFacade.getUserId(mobileNumber));
		}
		return results;
	}
	
	@GetMapping("/user/profile")
	@ResponseBody
	public UserProfileResult getUserProfile(
			@RequestParam(required=false) Long userId,
			Model model) {
		Long visitorId = (Long) model.asMap().get("userId");
		long actualUserId;
		if (userId != null) {
			actualUserId = userId.longValue();
		} else {
			actualUserId = visitorId.longValue();
		}
		return new UserProfileResult(userFacade.getUserProfile(actualUserId), visitorId);
	}
	
	@GetMapping("/user/notes")
	@ResponseBody
	public List<BasicNoteResult> getPublishedNotes(
			@RequestParam(required=false) Long userId,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize,
			Model model
			) {
		final Long visitorId = (Long) model.asMap().get("userId");
		long actualUserId;
		if (userId != null) {
			actualUserId = userId.longValue();
		} else {
			actualUserId = visitorId.longValue();
		}
		List<BasicNoteResult> results = new ArrayList<BasicNoteResult>();
		for (Note note : noteFacade.listPublishedNotes(actualUserId, timestamp).subCollection(loadedSize, loadedSize + pageSize)) {
			results.add(new BasicNoteResult(note, visitorId));
		}
		return results;
	}
	
	@GetMapping(path = "/notes", params = "myFollowOnly")
	@ResponseBody
	public List<BasicNoteResult> getSquareNotes(
			@ModelAttribute("userId") long userId,
			@RequestParam(value="tagId", required=false) Long tagId,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		final SquareNoteRequester requester = noteFacade.newSquareNoteRequester();
		requester.setFollowedNoteOnly(userId);
		if (tagId != null) {
			requester.setChannelId(tagId.longValue());
		}
		List<BasicNoteResult> results = new ArrayList<BasicNoteResult>();
		for (Note note : requester.getSquareNotes(timestamp).subCollection(loadedSize, loadedSize + pageSize)) {
			results.add(new BasicNoteResult(note));
		}
		return results;
	}	
	
	@PostMapping("/user/logout")
	@ResponseBody
	public void logout(@ModelAttribute("userId") long userId) {
		tokenService.logout(userId);
	}
	
	@PostMapping("/user/settings/security/password")
	@ResponseBody
	public void setPassword(
			@ModelAttribute("userId") long userId,
			@RequestBody SetPasswordRequestData requestData
			) {
		Password acceptedPassword = Password.valueOf(requestData.password);
		boolean success = userFacade.resetPassword(userId, VerificationCode.valueOf(requestData.code), acceptedPassword);
		if (!success) {
			throw new WrongVerificationCodeException();
		}
	}

	@PostMapping(path = "/user/settings/personal-information/portrait")
	@ResponseBody
	public void setUserPortrait(
			@ModelAttribute("userId") long userId,
			@RequestBody ImageJsonAdapter requestData) throws IOException {
		logger.info("Processing call back from image service; user image properties - [width: {}, height: {}, url: {}]"
				, requestData.width, requestData.height, requestData.url);
		userFacade.setPortrait(userId, new Image(requestData.width, requestData.height, requestData.url));
	}
	
	@PostMapping("/user/settings/personal-information")
	@ResponseBody
	public void setPersonalInformation(
			@ModelAttribute("userId") long userId,
			@RequestBody SetPersonalInformationRequestData requestData
			) {
		UserProfileSetter userProfileSetter = userFacade.newUserProfileSetter(userId);
		if (requestData.nickname != null) {
			userProfileSetter.setNickname(requestData.nickname);
		}
		if (requestData.gender != null) {
			userProfileSetter.setGender(Gender.valueOf(requestData.gender));
		}
		userProfileSetter.commitProfileChanges();
	}
	
	@PostMapping("/user/message/verification-code")
	@ResponseBody
	public void sendResetPasswordVerificationCode(@ModelAttribute("userId") long userId) {
		userFacade.sendResetPasswordVerificationCode(userId);
	}
	
	@PostMapping("/user/notes")
	@ResponseBody
	public NotePublishingResult postNote(
			@ModelAttribute("userId") long userId,
			@RequestBody PublishNoteRequestData requestData
			) {
		NotePublisher publisher = noteFacade.newNotePublisher(userId);
		publisher.setTitle(requestData.title);
		publisher.setContent(requestData.content);
		publisher.setRating(1);
		if (requestData.longitude != null && requestData.latitude != null) {
			publisher.setLocation(requestData.longitude, requestData.latitude);
		}
		if (requestData.address != null) {
			publisher.setAddress(requestData.address);
		}
		return new NotePublishingResult(publisher.publishNote());
	}
		
	@PostMapping("/notes/{note-id}/likes")
	@ResponseBody
	public void likeNote(
			@ModelAttribute("userId") long userId,
			@PathVariable("note-id") long noteId
			) {
		noteFacade.likeNote(userId, noteId);
	}
	
	@PostMapping("/notes/{note-id}/comments")
	@ResponseBody
	public void commentNote(
			@ModelAttribute("userId") long userId,
			@PathVariable("note-id") long noteId,
			@RequestBody final CommentNoteRequestData requestData
			) {
		noteFacade.publishComment(userId, noteId, new CommentPublishAttributes() {

			@Override
			public String getComment() {
				return requestData.comment;
			}

			@Override
			public int getRating() {
				return requestData.rating;
			}
		});
	}

	@PostMapping("/user/follows")
	@ResponseBody
	public void followUser(
			@ModelAttribute("userId") long userId,
			@RequestBody FollowUserRequestData requestData
			) {
		userFacade.buildFollowConnection(userId, requestData.followeeId);
	}
	
	@PostMapping("/user/invitations")
	@ResponseBody
	public void inviteFriends(
			@ModelAttribute("userId") long userId,
			@RequestBody InviteFriendsRequestData requestData
			) {
		MobileNumber[] mobiles = new MobileNumber[requestData.mobiles.length];
		for (int i = 0; i < mobiles.length; ++i) {
			mobiles[i] = MobileNumber.valueOf(requestData.mobiles[i]);
		}
		userFacade.inviteFriends(userId, mobiles);
	}

	@PostMapping("/notes/{note-id}/images")
	@ResponseBody
	public void setNoteImages(
			@PathVariable("note-id") long noteId,
			@RequestBody UploadNoteImageCallbackData requestData) throws IOException {
		NoteImage[] images = new NoteImage[requestData.images.length];
		for (int i = 0; i < images.length; ++i) {
			logger.info("Processing call back from image service");
			UploadNoteImageCallbackData.ImageInfo imageData = requestData.images[i];
			String marks = null;
			if (imageData.imageMarks != null && imageData.imageMarks.length > 0) {
				marks = new ObjectMapper().writeValueAsString(imageData.imageMarks);
			}
			images[i] = new NoteImage(imageData.imageInfo.width, imageData.imageInfo.height, imageData.imageInfo.url, marks);
		}
		noteFacade.setNoteImages(noteId, images);
	}


}
