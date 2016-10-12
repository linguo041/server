package com.duoshouji.server.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.duoshouji.server.elasticsearch.Definition;

import lombok.Data;

@Data
@Document(indexName=Definition.ESNote.INDEX_NAME, type=Definition.ESNote.TYPE_NAME)
public class ESNote {

	@Id  
	private Long noteId;
	private String title;
	private String content;
	private Long userId;
	private String productName;
}
