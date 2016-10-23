package com.duoshouji.core.dao.dto;

import java.util.List;

import com.duoshouji.service.util.Image;

public class NoteDetailDto extends BasicNoteDto {
	public String content;
	public long[] tagIds;
	public List<Image> images;
	public String productName;
}
