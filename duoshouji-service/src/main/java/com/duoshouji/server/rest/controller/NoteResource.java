package com.duoshouji.server.rest.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.DuoShouJiFacade.CommentPublisher;
import com.duoshouji.server.service.auth.UnauthenticatedUserException;
import com.duoshouji.server.service.auth.UserTokenService;
import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.note.NoteDetail;
import com.duoshouji.server.service.note.NoteDetailAndOwner;
import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

@RestController
public class NoteResource {
	
	private DuoShouJiFacade duoShouJiFacade;
	private UserTokenService anthentication;
	
	@ModelAttribute
	public void setupUserInModel(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token, Model model)
					throws UnauthenticatedUserException {
		final MobileNumber userId = anthentication.fetchUserId(token);
		model.addAttribute("userId", userId);
	}
	
	@RequestMapping(path = "/notes/{note-id}", method = RequestMethod.GET)
	public NoteView getNote(@PathVariable("note-id") long noteId) {
		return new NoteView(duoShouJiFacade.getNote(noteId));
	}
	
	public class NoteView {
		private NoteDetail note;
		private BasicUser owner;
		
		public NoteView(NoteDetailAndOwner noteAndOwner) {
			this.note = noteAndOwner.getNote();
			this.owner = noteAndOwner.getOwner();
		}
		
		public long getUserId() {
			return owner.getMobileNumber().toLong();
		}
		
		public String getNickname() {
			return owner.getNickname();
		}
		
		public String getPortrait() {
			return owner.getPortrait().getUrl();
		}
		
		public long getNoteId() {
			return note.getNoteId();
		}
		
		public long getPublishTime() {
			return note.getPublishedTime();
		}
		
		public List<Image> getImages() {
			return note.getImages();
		}
		
		public String getContent() {
			return note.getContent();
		}
		
		public List<Tag> getTags() {
			return note.getTags();
		}
		
		public int getLikeCount() {
			return note.getLikeCount();
		}
		
		public int getCommentCount() {
			return note.getCommentCount();
		}
		
		public int getTransactionCount() {
			return note.getTransactionCount();
		}
		
		public int getRating() {
			return note.getRating();
		}
	}
	
	@RequestMapping(path = "/notes/{note-id}/comment", method = RequestMethod.POST)
	public void addComment(
			@PathVariable("note-id") long noteId,
			@RequestParam("comment") String comment,
			@RequestParam("longitude") BigDecimal longitude,
			@RequestParam("latitude") BigDecimal latitude,
			@RequestParam("rating") int rating,
			Model model
			) {
		CommentPublisher publisher = duoShouJiFacade.newCommentPublisher(noteId, getUserId(model));
		publisher.setComment(comment);
		publisher.setLocation(longitude, latitude);
		publisher.setRating(rating);
		publisher.publishComment();
	}
	
	private MobileNumber getUserId(Model model) {
		return (MobileNumber) model.asMap().get("userId");
	}
}
