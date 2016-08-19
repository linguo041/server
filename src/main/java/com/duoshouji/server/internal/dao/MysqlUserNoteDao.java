package com.duoshouji.server.internal.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.duoshouji.server.service.dao.CommentDtoCollection;
import com.duoshouji.server.service.dao.LikeDtoCollection;
import com.duoshouji.server.service.dao.NoteAlbumDto;
import com.duoshouji.server.service.dao.NoteDto;
import com.duoshouji.server.service.dao.NoteDtoCollection;
import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

public class MysqlUserNoteDao implements UserNoteDao {

	private JdbcTemplate mysqlDataSource;

	public MysqlUserNoteDao(JdbcTemplate mysqlDataSource) {
		this.mysqlDataSource = mysqlDataSource;
	}
	
	@Override
	public RegisteredUserDto findUser(UserIdentifier userId) {
		final List<Map<String, Object>> result = mysqlDataSource.queryForList(
				"select id, mobile, password, password_salt from user where id = " + userId);
		InMemoryRegisteredUserDto user = null;
		if (!result.isEmpty()) {
			final Map<String, Object> row = result.get(0);
			user = new InMemoryRegisteredUserDto(userId, new MobileNumber(row.get("mobile").toString()));
			user.setPasswordDigest((String) row.get("password"));
			user.setPasswordSalt((String) row.get("password_salt"));
		}
		return user;
	}

	@Override
	public void addUser(UserIdentifier userId, MobileNumber mobileNumber) {
		mysqlDataSource.update("insert into user (id, mobile) values (?, ?)"
				, Long.valueOf(userId.toString())
				, Long.valueOf(mobileNumber.toString()));
	}

	@Override
	public NoteAlbumDto findNoteAlbum(NoteDto noteDto) {
		return null;
	}

	@Override
	public RegisteredUserDto findOwner(NoteDto noteDto) {
		return null;
	}

	@Override
	public LikeDtoCollection findLikes(NoteDto noteDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommentDtoCollection findComments(NoteDto noteDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NoteDtoCollection findNotes(long cutoff) {
		List<Map<String, Object>> notes = mysqlDataSource.queryForList(
				"select ");
		return null;
	}

	@Override
	public NoteDtoCollection findNotes(long cutoff, int startIndex, int endIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
