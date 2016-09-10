package com.duoshouji.server.db;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class NoteDto {

	private Long noteId;

	private Long userId;

	private String portrait;

	private String publishTime;

	private String image;

	private String description;

	private List<String> tags;

	private Long likeCount;

	private Long transactionCount;

	private Map<String, Long> ranking;

	private List<NoteRecommendDto> authorRecommanded;

	private List<NoteRecommendDto> systemRecommanded;
}
