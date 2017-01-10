package com.duoshouji.core.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.duoshouji.service.common.Tag;
import com.duoshouji.service.common.TagRepository;
import com.duoshouji.service.note.NoteTextProperties;

@Service
public class DatabaseTagRepository extends TagRepository implements RowMapper<Tag> {

	private List<Tag> tags;
	
	public DatabaseTagRepository(JdbcTemplate jdbcTemplate) {
		tags = jdbcTemplate.query("select tag_id, tag_name, is_channel from duoshouji.tag order by tag_id", this);
	}
	
	@Override
	public List<Tag> getChannels() {
		return Collections.unmodifiableList(tags);
	}

	@Override
	public List<Tag> findTags(NoteTextProperties noteProperties) {
		return Collections.unmodifiableList(tags);
	}

	@Override
	public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
		return createTag(rs.getLong("tag_id"), rs.getString("tag_name"));
	}

	@Override
	public Tag findChannel(long tagId) {
		for (Tag tag : tags) {
			if (tag.getId() ==  tagId) {
				return tag;
			}
		}
		throw new IllegalArgumentException("Channel Id: " + tagId + " not found!");
	}

}
