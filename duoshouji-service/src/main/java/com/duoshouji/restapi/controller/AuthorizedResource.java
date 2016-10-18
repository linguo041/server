package com.duoshouji.restapi.controller;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.restapi.Constants;
import com.duoshouji.restapi.auth.UnauthenticatedUserException;
import com.duoshouji.restapi.auth.UserTokenService;
import com.duoshouji.restapi.controller.model.BasicNoteResult;
import com.duoshouji.restapi.controller.model.MobileNumberMappingUserIdResult;
import com.duoshouji.restapi.controller.model.NotePublishingResult;
import com.duoshouji.restapi.controller.model.UserProfileResult;
import com.duoshouji.restapi.controller.model.WrongVerificationCodeException;
import com.duoshouji.service.note.BasicNote;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteFacade;
import com.duoshouji.service.note.NoteFacade.NotePublisher;
import com.duoshouji.service.note.NoteFacade.SquareNoteRequester;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.user.UserFacade.UserProfileSetter;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.Location;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

@Controller
public class AuthorizedResource {
	
	private Logger logger = LogManager.getLogger();
	
	private UserFacade userFacade;
	private NoteFacade noteFacade;
	private UserTokenService tokenService;
	
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
	
	@Required
	@Autowired
	public void setTokenService(UserTokenService tokenService) {
		this.tokenService = tokenService;
	}

	@ModelAttribute
	public void checkToken(Model model,
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token) throws UnauthenticatedUserException {
		final long userId = tokenService.getUserId(token);
		if (userId == UserFacade.NULL_USER_ID) {
			throw new UnauthenticatedUserException("Token is expired or not exists. Please login first.");
		}
		model.addAttribute("userId", Long.valueOf(userId));
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
	public UserProfileResult getUserProfile(@ModelAttribute("userId") long userId) {
		return new UserProfileResult(userFacade.getUserProfile(userId));
	}
	
	@GetMapping("/user/notes")
	@ResponseBody
	public List<BasicNoteResult> getPublishedNotes(
			@ModelAttribute("userId") long userId,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		List<BasicNoteResult> results = new ArrayList<BasicNoteResult>();
		for (BasicNote note : noteFacade.listPublishedNotes(userId, timestamp).subCollection(loadedSize, loadedSize + pageSize)) {
			results.add(new BasicNoteResult(note));
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
			requester.setTagId(tagId.longValue());
		}
		List<BasicNoteResult> results = new ArrayList<BasicNoteResult>();
		for (BasicNote note : requester.getSquareNotes(timestamp).subCollection(loadedSize, loadedSize + pageSize)) {
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
			@RequestParam("code") VerificationCode verificationCode,
			@RequestParam("password") String password
			) {
		Password acceptedPassword = Password.valueOf(password);
		boolean success = userFacade.resetPassword(userId, verificationCode, acceptedPassword);
		if (!success) {
			throw new WrongVerificationCodeException();
		}
	}

	@PostMapping(path = "/user/settings/personal-information/protrait")
	@ResponseBody
	public void setUserPortrait(
			@ModelAttribute("userId") long userId,
			@RequestParam("imageUrl") String imageUrl,
			@RequestParam("imageWidth") int imageWidth,
			@RequestParam("imageHeight") int imageHeight) throws IOException {
		logger.info("Processing call back from image service; user image properties - [width: {}, height: {}, url: {}]"
				, imageWidth, imageHeight, imageUrl);
		userFacade.setPortrait(userId, new Image(imageWidth, imageHeight, imageUrl));
	}
	
	@PostMapping("/user/settings/personal-information")
	@ResponseBody
	public void setPersonalInformation(
			@ModelAttribute("userId") long userId,
			@RequestParam(value="nickname", required=false) final String nickname,
			@RequestParam(value="gender", required=false) final Gender gender
			) {
		UserProfileSetter userProfileSetter = userFacade.newUserProfileSetter(userId);
		if (nickname != null) {
			userProfileSetter.setNickname(nickname);
		}
		if (gender != null) {
			userProfileSetter.setGender(gender);
		}
		userProfileSetter.commitProfileChanges();
	}
	
	@PostMapping(path="/message/verification-code", params="purpose=setpassword")
	@ResponseBody
	public void sendResetPasswordVerificationCode(@ModelAttribute("userId") long userId) {
		userFacade.sendResetPasswordVerificationCode(userId);
	}
	
	@PostMapping("/user/notes")
	@ResponseBody
	public NotePublishingResult postNote(
			@ModelAttribute("userId") long userId,
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
		NotePublisher publisher = noteFacade.newNotePublisher(userId);
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
			@RequestParam("comment") final String comment,
			@RequestParam("longitude") final BigDecimal longitude,
			@RequestParam("latitude") final BigDecimal latitude,
			@RequestParam("rating") final int rating
			) {
		noteFacade.publishComment(userId, noteId, new CommentPublishAttributes() {

			@Override
			public String getComment() {
				return comment;
			}

			@Override
			public int getRating() {
				return rating;
			}

			@Override
			public Location getLocation() {
				return new Location(longitude, latitude);
			}
		});
	}

	@PostMapping("/user/follows")
	@ResponseBody
	public void followUser(
			@ModelAttribute("userId") long userId,
			@RequestParam("followeeId") long followeeId
			) {
		userFacade.buildFollowConnection(userId, followeeId);
	}
	
	@PostMapping("/user/invitations")
	@ResponseBody
	public void inviteFriends(
			@ModelAttribute("userId") long userId,
			@RequestParam("mobile") MobileNumber[] mobileNumbers
			) {
		userFacade.inviteFriends(userId, mobileNumbers);
	}

	@PostMapping("/notes/{note-id}/images")
	@ResponseBody
	public void setNoteImages(
			@PathVariable("note-id") long noteId,
			@RequestParam("imageCount") int imageCount,
			@RequestParam("imageUrl") String[] imageUrls,
			@RequestParam("imageWidth") int[] imageWidths,
			@RequestParam("imageHeight") int[] imageHeights) throws IOException {
		Image[] images = new Image[imageCount];
		for (int i = 0; i < imageCount; ++i) {
			logger.info("Processing call back from image service; note image properties - [width: {}, height: {}, url: {}]"
					, imageWidths[i], imageHeights[i], imageUrls[i]);
			images[i] = new Image(imageWidths[i], imageHeights[i], imageUrls[i]);
		}
		noteFacade.setNoteImages(noteId, images);
	}


}
