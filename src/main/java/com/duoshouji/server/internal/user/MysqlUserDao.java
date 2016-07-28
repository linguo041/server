package com.duoshouji.server.internal.user;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserDao;
import com.duoshouji.server.user.UserIdentifier;
import com.duoshouji.server.util.Password;

public class MysqlUserDao implements UserDao {

	private JdbcTemplate mysqlDataSource;

	@Override
	public RegisteredUser findUser(UserIdentifier accountId) {
		final List<Map<String, Object>> result = mysqlDataSource.queryForList(
				"select password, password_salt from user where id = " + accountId);
		InMemoryUser user = null;
		if (!result.isEmpty()) {
			final Map<String, Object> row = result.get(0);
			user = new InMemoryUser(accountId, null);
			user.setPassword(Password.valueOf((String)row.get("password")));
		}
		return user;
	}
	@Override
	public void saveUser(RegisteredUser user) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addUser(RegisteredUser user) {
		// TODO Auto-generated method stub
		
	}

}
