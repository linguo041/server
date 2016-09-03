package com.duoshouji.server.service.note;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public final class TagRepository {

	private final List<Tag> tags;
	
	@Autowired
	public TagRepository(JdbcTemplate jdbcTemplate) {
		tags = Collections.unmodifiableList(queryTags(jdbcTemplate));
	}

	private List<Tag> queryTags(JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.query(
				"select id, name from duoshouji.tag order by sort_order"
				, new RowMapper<Tag>() {

					@Override
					public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new Tag(rs.getLong("id"), rs.getString("name"));
					}
					
				});
	}
	
	public List<Tag> listTags() {
		return tags;
	}
	
	public Tag findTag(long tagId) {
		for (Tag tag : tags) {
			if (tag.getTagId() == tagId) {
				return tag;
			}
		}
		throw new IllegalArgumentException("Tag not found for id: " + tagId);
	}
}
