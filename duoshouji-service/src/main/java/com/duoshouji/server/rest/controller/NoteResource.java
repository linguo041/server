package com.duoshouji.server.rest.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.DuoShouJiFacade.SquareNoteRequester;
import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.interaction.BasicNoteAndOwner;
import com.duoshouji.server.service.interaction.NoteCommentAndAuthor;
import com.duoshouji.server.service.interaction.NoteDetailAndOwner;
import com.duoshouji.server.service.note.recommand.EcommerceItem;
import com.duoshouji.util.Image;
import com.duoshouji.util.MobileNumber;

@RestController
public class NoteResource {
	
	private DuoShouJiFacade duoShouJiFacade;
	
	@Autowired
	public NoteResource(DuoShouJiFacade duoShouJiFacade) {
		this.duoShouJiFacade = duoShouJiFacade;
	}

	@RequestMapping(path = "/notes", method = RequestMethod.GET)
	public List<ResultBasicNote> getNotes(
			@RequestParam(value="timestamp") long timestamp,
			@RequestParam(value="accountId", required=false) MobileNumber accountId,
			@RequestParam(value="longitude", required=false) BigDecimal longitude,
			@RequestParam(value="latitude", required=false) BigDecimal latitude,
			@RequestParam(value="tagId", required=false) Long tagId,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		SquareNoteRequester requester = duoShouJiFacade.newSquareNoteRequester();
		if (accountId != null) {
			requester.setWatchedOnly(accountId);
		}
		if (tagId != null) {
			requester.setTagId(tagId.longValue());
		}
		if (longitude != null && latitude != null) {
			requester.setUserLocation(longitude, latitude);
		}
		List<ResultBasicNote> resultBasicNotes = new ArrayList<ResultBasicNote>();
		for (BasicNoteAndOwner noteAndOwner : requester.getSquareNotes(timestamp, loadedSize, pageSize)) {
			resultBasicNotes.add(new ResultBasicNote(noteAndOwner));
		}
		return resultBasicNotes;
	}

	public class ResultBasicNote {
		private BasicNoteAndOwner delegator;

		private ResultBasicNote(BasicNoteAndOwner delegator) {
			this.delegator = delegator;
		}

		public long getNoteId() {
			return delegator.getNote().getNoteId();
		}

		public String getTitle() {
			return delegator.getNote().getTitle();
		}

		public String getImage() {
			return delegator.getNote().getMainImage().getUrl();
		}

		public int getImageWidth() {
			return delegator.getNote().getMainImage().getWidth();
		}

		public int getImageHeight() {
			return delegator.getNote().getMainImage().getHeight();
		}

		public String getPortrait() {
			return delegator.getOwner().getPortrait().getUrl();
		}

		public int getRank() {
			return delegator.getNote().getRating();
		}

		public int getLikeCount() {
			return delegator.getNote().getLikeCount();
		}

		public int getCommentCount() {
			return delegator.getNote().getCommentCount();
		}
	}

	
	@RequestMapping(path = "/notes/{note-id}", method = RequestMethod.GET)
	public ResultNoteDetial getNote(@PathVariable("note-id") long noteId) {
		return new ResultNoteDetial(duoShouJiFacade.getNote(noteId));
	}
	
	public class ResultNoteDetial {
		private NoteDetailAndOwner delegator;
		
		private ResultNoteDetial(NoteDetailAndOwner delegator) {
			this.delegator = delegator;
		}
		
		public long getUserId() {
			return delegator.getOwner().getMobileNumber().toLong();
		}
		
		public String getNickname() {
			return delegator.getOwner().getNickname();
		}
		
		public String getPortrait() {
			return delegator.getOwner().getPortrait().getUrl();
		}
		
		public long getNoteId() {
			return delegator.getNote().getNoteId();
		}
		
		public long getPublishTime() {
			return delegator.getNote().getPublishedTime();
		}
		
		public List<Image> getImages() {
			return delegator.getNote().getImages();
		}
		
		public String getContent() {
			return delegator.getNote().getContent();
		}
		
		public List<Tag> getTags() {
			return delegator.getNote().getTags();
		}
		
		public int getLikeCount() {
			return delegator.getNote().getLikeCount();
		}
		
		public int getCommentCount() {
			return delegator.getNote().getCommentCount();
		}
		
		public int getTransactionCount() {
			return delegator.getNote().getTransactionCount();
		}
		
		public int getRating() {
			return delegator.getNote().getRating();
		}
	}
	
	@RequestMapping(path = "/notes/{note-id}/recommendations", method = RequestMethod.GET)
	public List<EcommerceItem> getNoteRecommandations(@PathVariable("note-id") long noteId) {
		return duoShouJiFacade.getNoteRecommendations(noteId);
	}

	@RequestMapping(path = "/notes/{note-id}/comments", method = RequestMethod.GET)
	public List<ResultNoteComment> getNoteComments(@PathVariable("note-id") long noteId) {
		List<ResultNoteComment> results = new LinkedList<ResultNoteComment>();
		for (NoteCommentAndAuthor comment : duoShouJiFacade.getNoteComments(noteId)) {
			results.add(new ResultNoteComment(comment));
		}
		return results;
	}
	
	public class ResultNoteComment {
		private NoteCommentAndAuthor delegator;

		private ResultNoteComment(NoteCommentAndAuthor delegator) {
			super();
			this.delegator = delegator;
		}
		
		public String getComment() {
			return delegator.getNoteComment().getComment();
		}
		
		public String getAuthorNickname() {
			return delegator.getAuthor().getNickname();
		}
	}
}
