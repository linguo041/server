package com.duoshouji.server.db.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import com.duoshouji.server.entity.Note;
import com.google.common.collect.Maps;

@Component
public class NoteDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<Note> getNoteByNoteId (Long noteId) {
		String sql = "select * from note where id = :noteId";
		
		Map <String, Long> params = Maps.newHashMapWithExpectedSize(1);
		params.put("noteId", noteId);
		
		return namedParameterJdbcTemplate.query(sql, params,
				BeanPropertyRowMapper.newInstance(Note.class));
	}
}
