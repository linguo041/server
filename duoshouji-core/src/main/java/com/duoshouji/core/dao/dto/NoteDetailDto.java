package com.duoshouji.core.dao.dto;

import java.math.BigDecimal;
import java.util.List;

import com.duoshouji.service.note.NoteImage;

public class NoteDetailDto extends BasicNoteDto {
	public String content;
	public List<NoteImage> images;
	public String address;
	public BigDecimal longitude;
	public BigDecimal latitude;
}
