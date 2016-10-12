package com.duoshouji.server.batch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.duoshouji.server.elasticsearch.document.ESNote;
import com.duoshouji.server.elasticsearch.repository.ESNoteRepository;
import com.duoshouji.server.jpa.entity.DBNote;
import com.duoshouji.server.jpa.repository.DBNoteRepository;

@Service
public class ElasticSearchService {
	@Autowired
	public ESNoteRepository esNoteRepository;
	
	@Autowired
	public DBNoteRepository dbNoteRepository;
	
	public void importToES (ESNote esNote) {
		esNoteRepository.save(esNote);
	}
	
	public void importToES (List<ESNote> esNote) {
		esNoteRepository.save(esNote);
	}
	
	public DBNote getStoredNoteByNoteId (Long noteId) {
		return dbNoteRepository.findOne(noteId);
	}
	
	public List<DBNote> getStoredNoteByUserId (Long userId) {
		return dbNoteRepository.findByUserId(userId);
	}
	
	public List<DBNote> getStoredNotes (int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		Page<DBNote> notes = dbNoteRepository.findAll(pageable);
		return notes.getContent();
	}
}
