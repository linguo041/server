package com.duoshouji.core.dao.dto;

import com.duoshouji.service.util.Image;


public class BasicNoteDto {
	public long noteId;
	public String title;
	public int commentRatingSum;
	public int ownerRating;
	public long publishedTime;
	public Image mainImage;
	public int likeCount;
	public int commentCount;
	public int transactionCount;
	public long authorId;
}
