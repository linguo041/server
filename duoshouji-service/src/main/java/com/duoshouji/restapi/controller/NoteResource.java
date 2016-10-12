package com.duoshouji.restapi.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.restapi.controller.model.BasicNoteResult;
import com.duoshouji.restapi.controller.model.DetailNoteResult;
import com.duoshouji.restapi.controller.model.NoteCommentResult;
import com.duoshouji.restapi.controller.model.NotePublishingResult;
import com.duoshouji.service.note.BasicNote;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteFacade;
import com.duoshouji.service.note.NoteFacade.NotePublisher;
import com.duoshouji.service.note.NoteFacade.SquareNoteRequester;
import com.duoshouji.service.note.recommand.EcommerceItem;
import com.duoshouji.service.note.recommand.NoteRecommendService;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.Location;

@Controller
public class NoteResource {
	
	private Logger logger = LogManager.getLogger();
	
	private NoteFacade noteFacade;
	private NoteRecommendService recommendService;
	
	@Autowired
	public NoteResource(NoteFacade noteFacade, NoteRecommendService recommendService) {
		this.noteFacade = noteFacade;
		this.recommendService = recommendService;
	}

	@GetMapping("/notes/{note-id}")
	@ResponseBody
	public DetailNoteResult getNote(@PathVariable("note-id") long noteId) {
		return new DetailNoteResult(noteFacade.getNote(noteId));
	}
	
	@GetMapping("/notes/{note-id}/recommendations")
	@ResponseBody
	public List<EcommerceItem> getNoteRecommandations(@PathVariable("note-id") long noteId) {
		return recommendService.recommendEcommerceItems(noteFacade.getNote(noteId));
	}

	@GetMapping("/notes/{note-id}/comments")
	@ResponseBody
	public List<NoteCommentResult> getNoteComments(@PathVariable("note-id") long noteId) {
		List<NoteCommentResult> results = new LinkedList<NoteCommentResult>();
		for (NoteComment comment : noteFacade.getNoteComments(noteId)) {
			results.add(new NoteCommentResult(comment));
		}
		return results;
	}
	
	@GetMapping(path = "/notes", params = "!authorId")
	@ResponseBody
	public List<BasicNoteResult> getNotes(
			@RequestParam(value="followerId", required=false) Long followerId,
			@RequestParam(value="longitude", required=false) BigDecimal longitude,
			@RequestParam(value="latitude", required=false) BigDecimal latitude,
			@RequestParam(value="tagId", required=false) Long tagId,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		final SquareNoteRequester requester = noteFacade.newSquareNoteRequester();
		if (followerId != null) {
			requester.setFollowedNoteOnly(followerId);
		}
		if (tagId != null) {
			requester.setTagId(tagId.longValue());
		}
		if (longitude != null && latitude != null) {
			requester.setLocation(longitude, latitude);
		}
		List<BasicNoteResult> results = new ArrayList<BasicNoteResult>();
		for (BasicNote note : requester.getSquareNotes(timestamp).subCollection(loadedSize, loadedSize + pageSize)) {
			results.add(new BasicNoteResult(note));
		}
		return results;
	}

	@GetMapping(path = "/notes", params = "authorId")
	@ResponseBody
	public List<BasicNoteResult> getNotes(
			@RequestParam("authorId") long authorId,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		List<BasicNoteResult> results = new ArrayList<BasicNoteResult>();
		for (BasicNote note : noteFacade.listPublishedNotes(authorId, timestamp).subCollection(loadedSize, loadedSize + pageSize)) {
			results.add(new BasicNoteResult(note));
		}
		return results;
	}
	
	
	@PutMapping("/notes/{note-id}/images")
	@ResponseBody
	public void putNoteImages(
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

	@PostMapping("/notes")
	@ResponseBody
	public NotePublishingResult postNote(
			@RequestParam("authorId") long authorId,
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
		NotePublisher publisher = noteFacade.newNotePublisher(authorId);
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
	public void postLikeNote(
			@RequestParam("userId") long userId,
			@PathVariable("note-id") long noteId
			) {
		noteFacade.likeNote(userId, noteId);
	}
	
	@PostMapping("/notes/{note-id}/comments")
	@ResponseBody
	public void postCommentNote(
			@PathVariable("note-id") long noteId,
			@RequestParam("userId") long userId,
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
}
