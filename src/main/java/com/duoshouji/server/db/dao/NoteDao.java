package com.duoshouji.server.db.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import com.duoshouji.server.entity.Note;
import com.google.common.collect.Maps;

@Component
public class NoteDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSourceTransactionManager transactionManager;
	
	public List<Note> getNoteByNoteId (Long noteId) {
		String sql = "select * from note where id = :noteId";
		
		Map <String, Long> params = Maps.newHashMapWithExpectedSize(1);
		params.put("noteId", noteId);
		
		return namedParameterJdbcTemplate.query(sql, params,
				BeanPropertyRowMapper.newInstance(Note.class));
	}
	
	public List<Long> getCertainNoteIdsFromId (long id, long size) {
		String sql = "select id from note where id >= :noteId limit :size";
		
		Map <String, Long> params = Maps.newHashMapWithExpectedSize(1);
		params.put("noteId", id);
		params.put("size", size);
		
		return namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
	}
}
