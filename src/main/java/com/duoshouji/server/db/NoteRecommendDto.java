package com.duoshouji.server.db;

import lombok.Data;

@Data
public class NoteRecommendDto {

	private Long noteId;

	private String title;

	private String image;

	private Float listPrice;

	private Float discountPrice;
}
