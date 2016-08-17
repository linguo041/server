package com.duoshouji.server.internal.dao;

public class InMemoryNoteDto implements NoteDto {

	private final long noteId;
	private String title;
	private int rank;
	
	public InMemoryNoteDto(long noteId, String title, int rank) {
		super();
		this.noteId = noteId;
		this.title = title;
		this.rank = rank;
	}

	@Override
	public long getNoteId() {
		return noteId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int getRank() {
		return rank;
	}

}
