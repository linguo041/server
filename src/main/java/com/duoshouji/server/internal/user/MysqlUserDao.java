package com.duoshouji.server.internal.user;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.duoshouji.server.service.user.RegisteredUserDto;
import com.duoshouji.server.service.user.UserDao;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

public class MysqlUserDao implements UserDao {

	private JdbcTemplate mysqlDataSource;

	public MysqlUserDao(JdbcTemplate mysqlDataSource) {
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

}
