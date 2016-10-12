package com.duoshouji.restapi.auth;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

@Service
public class DatabaseTokenService implements UserTokenService {

	private JdbcTemplate connection;
	
	@Autowired
	private DatabaseTokenService(JdbcTemplate connection) {
		super();
		this.connection = connection;
	}

	@Override
	public boolean verify(final long userId, final String token) {
		return connection.query(
				"select 1 from duoshouji.user_wechat_login where mobile = ? and token  = ? and expired = 0"
				, new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setLong(1, userId);
						ps.setString(2, token);
					}
				}
				, new ResultSetExtractor<Boolean>() {
					@Override
					public Boolean extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						return Boolean.valueOf(rs.next());
				}
		});
	}

	@Override
	public String newToken(final long userId) {
		final String token = UUID.randomUUID().toString();
		connection.update(
				"insert into duoshouji.user_wechat_login (mobile, create_time, token, expired) values (?,?,?,0)"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setLong(1, userId);
						ps.setLong(2, System.currentTimeMillis());
						ps.setString(3, token);
					}
					
				});
		return token;
	}

	@Override
	public void logout(long userId) {
		connection.update("update duoshouji.user_wechat_login set expired = 1 where mobile = " + userId);
	}
}