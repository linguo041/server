package com.duoshouji.server.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.duoshouji.server.elasticsearch.document.ESNote;

@Repository
public interface ESNoteRepository extends ElasticsearchRepository<ESNote, Long> {
	
}
