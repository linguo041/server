package com.duoshouji.core.dao.dto;

import com.duoshouji.service.note.NoteImage;


public class BasicNoteDto {
	public long noteId;
	public String title;
	public int commentRatingSum;
	public int ownerRating;
	public long publishedTime;
	public NoteImage mainImage;
	public int likeCount;
	public int commentCount;
	public int transactionCount;
	public long authorId;
}
