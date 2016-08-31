package com.duoshouji.server.elastic;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ElasticImportNote {
	private List<Long> noteIds;
}
