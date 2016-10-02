package com.duoshouji.server.service.dao;

import com.duoshouji.util.Image;


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
	public BasicUserDto owner;
}
