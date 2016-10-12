package com.duoshouji.restapi.controller.model;

public class NotePublishingResult {
	private long noteId;

	public NotePublishingResult(long noteId) {
		this.noteId = noteId;
	}

	public long getNoteId() {
		return noteId;
	}
}