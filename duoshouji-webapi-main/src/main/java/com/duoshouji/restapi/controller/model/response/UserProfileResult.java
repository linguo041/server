package com.duoshouji.restapi.controller.model.response;

import java.math.BigDecimal;

import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.UserProfile;

public class UserProfileResult {
	
	private UserProfile profile;
	private Long visitorId;
	
	public UserProfileResult(UserProfile profile) {
		this(profile, null);
	}	
	
	public UserProfileResult(UserProfile profile, Long visitorId) {
		this.profile = profile;
		this.visitorId = visitorId;
	}

	public long getUserId() {
		return profile.getUserId();
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
		return profile.getFollowCount();
	}
	
	public int getFanCount() {
		return profile.getFanCount();
	}
	
	public boolean getIsFollowedByVisitor() {
		if (visitorId == null) {
			return false;
		}
		return profile.isFollowedBy(visitorId.longValue());
	}
}