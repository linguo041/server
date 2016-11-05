package com.duoshouji.restapi.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.restapi.controller.model.response.BasicNoteResult;
import com.duoshouji.restapi.controller.model.response.DetailNoteResult;
import com.duoshouji.restapi.controller.model.response.NoteCommentResult;
import com.duoshouji.service.note.BasicNote;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteFacade;
import com.duoshouji.service.note.NoteFacade.SquareNoteRequester;
import com.duoshouji.service.note.recommand.EcommerceItem;
import com.duoshouji.service.note.recommand.NoteRecommendService;

@Controller
public class PublicResource {
		
	private NoteFacade noteFacade;
	private NoteRecommendService recommendService;
	
	@Autowired
	public PublicResource(NoteFacade noteFacade, NoteRecommendService recommendService) {
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
	
	@GetMapping(path = "/notes", params = "!myFollowOnly")
	@ResponseBody
	public List<BasicNoteResult> getSqureNotes(
			@RequestParam(value="longitude", required=false) BigDecimal longitude,
			@RequestParam(value="latitude", required=false) BigDecimal latitude,
			@RequestParam(value="tagId", required=false) Long tagId,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		final SquareNoteRequester requester = noteFacade.newSquareNoteRequester();
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
}
