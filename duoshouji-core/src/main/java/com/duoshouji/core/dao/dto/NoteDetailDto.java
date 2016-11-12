package com.duoshouji.core.dao.dto;

import java.math.BigDecimal;
import java.util.List;

import com.duoshouji.service.note.NoteImage;

public class NoteDetailDto extends BasicNoteDto {
	public String content;
	public List<NoteImage> images;
	public String productName;
	public BigDecimal price;
	public long districtId;
	public long brandId;
	public long categoryId;
}
