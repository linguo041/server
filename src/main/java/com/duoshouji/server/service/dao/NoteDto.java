package com.duoshouji.server.service.dao;

import com.duoshouji.server.util.Image;

public class NoteDto {
	public long noteId;
	public String title;
	public int rank;
	public Image mainImage;
	public BasicUserDto owner;
	public long publishedTime;
	public int likeCount;
	public int commentCount;
	public int transactionCount;
}
