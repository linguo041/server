package com.duoshouji.restapi.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.restapi.controller.model.response.BasicNoteResult;
import com.duoshouji.restapi.controller.model.response.DetailNoteResponseData;
import com.duoshouji.restapi.controller.model.response.NoteCommentResult;
import com.duoshouji.service.common.TagRepository;
import com.duoshouji.service.note.Note;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteFacade;
import com.duoshouji.service.note.NoteFacade.SquareNoteRequester;
import com.duoshouji.service.note.recommand.EcommerceItem;
import com.duoshouji.service.note.recommand.NoteRecommendService;

@Controller
public class PublicResource {
		
	private NoteFacade noteFacade;
	private TagRepository tagRepository;
	private NoteRecommendService recommendService;
	
	@Autowired
	@Required
	public void setNoteFacade(NoteFacade noteFacade) {
		this.noteFacade = noteFacade;
	}

	@Autowired
	@Required
	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Autowired
	@Required
	public void setRecommendService(NoteRecommendService recommendService) {
		this.recommendService = recommendService;
	}

	@GetMapping("/notes/{note-id}")
	@ResponseBody
	public DetailNoteResponseData getNote(@PathVariable("note-id") long noteId) {
		Note note = noteFacade.getNote(noteId);
		return new DetailNoteResponseData(note, tagRepository.findTags(note));
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
			@RequestParam(value="tagId", required=false) Long tagId,
			@RequestParam("timestamp") long timestamp,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		final SquareNoteRequester requester = noteFacade.newSquareNoteRequester();
		if (tagId != null) {
			requester.setChannelId(tagId.longValue());
		}
		List<BasicNoteResult> results = new ArrayList<BasicNoteResult>();
		for (Note note : requester.getSquareNotes(timestamp).subCollection(loadedSize, loadedSize + pageSize)) {
			results.add(new BasicNoteResult(note));
		}
		return results;
	}
}
