package com.duoshouji.server.service.dao;

import java.util.List;

import com.duoshouji.util.Image;

public class NoteDetailDto extends BasicNoteDto {
	public String content;
	public long[] tagIds;
	public List<Image> images;
	public String productName;
}
