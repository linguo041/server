package com.duoshouji.server.internal.user;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserDao;

public class MysqlUserDao implements UserDao {

	private JdbcTemplate mysqlDataSource;
	@Override
	public RegisteredUser findUser(String accountId) {
		final List<Map<String, Object>> result = mysqlDataSource.queryForList(
				"select password, password_salt from user where id = " + accountId);
		InMemoryUser user = null;
		if (!result.isEmpty()) {
			final Map<String, Object> row = result.get(0);
			user = new InMemoryUser(accountId);
			user.setPasswordDigest((String)row.get("password"));
			user.setPasswordSalt((String)row.get("password_salt"));
		}
		return user;
	}

}
