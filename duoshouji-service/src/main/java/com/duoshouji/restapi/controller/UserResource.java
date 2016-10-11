package com.duoshouji.restapi.controller;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.restapi.auth.UserTokenService;
import com.duoshouji.restapi.controller.model.LoginResult;
import com.duoshouji.restapi.controller.model.MobileNumberMappingUserIdResult;
import com.duoshouji.restapi.controller.model.UserProfileResult;
import com.duoshouji.restapi.controller.model.WrongPasswordException;
import com.duoshouji.restapi.controller.model.WrongVerificationCodeException;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.user.UserFacade.UserProfileSetter;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

@Controller
public class UserResource {

	private Logger logger = LogManager.getLogger();
	
	private UserFacade userFacade;
	private UserTokenService tokenService;
	
	@Autowired
	public UserResource(UserFacade userFacade, UserTokenService tokenService) {
		super();
		this.userFacade = userFacade;
		this.tokenService = tokenService;
	}

	@GetMapping("/users")
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

	@GetMapping("/users/{user-id}/profile")
	@ResponseBody
	public UserProfileResult getUserProfile(@PathVariable("user-id") long userId) {
		return new UserProfileResult(userFacade.getUserProfile(userId));
	}
	
	@GetMapping("/users/{user-id}/notes")
	public String getPublishedNotes(
			@PathVariable("user-id") long userId,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize,
			Model model
			) {
		model.addAttribute("authorId", userId);
		model.addAttribute("timestamp", timestamp);
		model.addAttribute("loadedSize", loadedSize);
		model.addAttribute("pageSize", pageSize);
		return "forward:/notes";
	}
	
	@PutMapping(path = "/mobiles/{mobile-number}/login-status", params = "code")
	@ResponseBody
	public LoginResult putVerificationCodeLogin(
		@PathVariable("mobile-number") MobileNumber mobileNumber,
		@RequestParam("code") VerificationCode verificationCode
			) {
		final long userId = userFacade.verificationCodeLogin(mobileNumber, verificationCode);
		if (userId < 0l) {
			throw new WrongVerificationCodeException();
		}
		return new LoginResult(userId, tokenService.newToken(userId));
	}
	
	@PutMapping(path = "/mobiles/{mobile-number}/login-status", params = "password")
	@ResponseBody
	public LoginResult putCredentialLogin(
		@PathVariable("mobile-number") MobileNumber mobileNumber,
		@RequestParam("password") String password
			) {
		final long userId = userFacade.passwordLogin(mobileNumber, Password.valueOf(password));
		if (userId < 0l) {
			throw new WrongPasswordException();
		}		
		return new LoginResult(userId, tokenService.newToken(userId));
	}
	
	@PutMapping("/users/{user-id}/logout")
	@ResponseBody
	public void putLogout(
			@PathVariable("user-id") long userId) {
		tokenService.logout(userId);
	}
	
	@PutMapping("/users/{user-id}/settings/security/password")
	@ResponseBody
	public void putPassword(
			@PathVariable("user-id") long userId,
			@RequestParam("code") VerificationCode verificationCode,
			@RequestParam("password") String password
			) {
		Password acceptedPassword = Password.valueOf(password);
		boolean success = userFacade.resetPassword(userId, verificationCode, acceptedPassword);
		if (!success) {
			throw new WrongVerificationCodeException();
		}
	}

	@PostMapping(path = "/users/{user-id}/settings/personal-information/protrait")
	@ResponseBody
	public void putUserPortrait(
			@PathVariable("user-id") long userId,
			@ModelAttribute("imageUrl") String imageUrl,
			@ModelAttribute("imageWidth") int imageWidth,
			@ModelAttribute("imageHeight") int imageHeight) throws IOException {
		logger.info("Processing call back from image service; user image properties - [width: {}, height: {}, url: {}]"
				, imageWidth, imageHeight, imageUrl);
		userFacade.setPortrait(userId, new Image(imageWidth, imageHeight, imageUrl));
	}
	
	@PostMapping("/users/{user-id}/settings/personal-information")
	@ResponseBody
	public void putPersonalInformation(
			@PathVariable("user-id") long userId,
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
	
	@PostMapping("/mobiles/{mobile-number}/message/verification-code/login")
	@ResponseBody
	public void postLoginVerificationCode(@PathVariable("mobile-number") MobileNumber mobileNumber) {
		userFacade.sendLoginVerificationCode(mobileNumber);
	}
	
	@PostMapping("/users/{user-id}/message/verification-code/reset-password")
	@ResponseBody
	public void postResetPasswordVerificationCode(
			@PathVariable("user-id") long userId
			) {
		userFacade.sendResetPasswordVerificationCode(userId);
	}
	
	@PostMapping("/users/{user-id}/notes")
	public String postNote(
			@PathVariable("user-id") long userId,
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
			@RequestParam("latitude") BigDecimal latitude,
			Model model
			) {
		model.addAttribute("authorId", userId);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("brandId", brandId);
		model.addAttribute("productName", productName);
		model.addAttribute("price", price.toString());
		model.addAttribute("districtId", districtId);
		model.addAttribute("tag", tags);
		model.addAttribute("title", title);
		model.addAttribute("content", content);
		model.addAttribute("rating", rating);
		model.addAttribute("longitude", longitude.toString());
		model.addAttribute("latitude", latitude.toString());
		return "forward:/notes";
	}
	
	@PostMapping("/users/{user-id}/likes/{note-id}")
	public String postLikeNote(
			@PathVariable("user-id") long userId,
			Model model
			) {
		model.addAttribute("userId", userId);
		return "forward:/notes/{note-id}/likes";
	}
	
	@PostMapping("/users/{user-id}/comments/{note-id}")
	public String postCommentNote(
			@PathVariable("user-id") long userId,
			@RequestParam("comment") String comment,
			@RequestParam("longitude") BigDecimal longitude,
			@RequestParam("latitude") BigDecimal latitude,
			@RequestParam("rating") int rating,
			Model model
			) {
		model.addAttribute("userId", userId);
		model.addAttribute("comment", comment);
		model.addAttribute("longitude", longitude.toString());
		model.addAttribute("latitude", latitude.toString());
		model.addAttribute("rating", rating);
		return "forward:/notes/{note-id}/comments";
	}
	
	@PostMapping("/users/{user-id}/connections/{followee-id}")
	@ResponseBody
	public void postFollowUser(
			@PathVariable("user-id") long followerId,
			@PathVariable("followee-id") long followeeId
			) {
		userFacade.buildFollowConnection(followerId, followeeId);
	}
	
	@PostMapping("/users/{user-id}/invitations")
	@ResponseBody
	public void postInviteFriends(
			@PathVariable("user-id") long userId,
			@RequestParam("mobile") MobileNumber[] mobileNumbers
			) {
		userFacade.inviteFriends(userId, mobileNumbers);
	}
}
