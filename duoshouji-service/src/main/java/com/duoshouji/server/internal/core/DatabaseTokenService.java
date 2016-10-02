package com.duoshouji.server.internal.core;

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

import com.duoshouji.server.service.auth.UserTokenService;
import com.duoshouji.util.MobileNumber;

@Service
public class DatabaseTokenService implements UserTokenService {

	private JdbcTemplate connection;
	
	@Autowired
	private DatabaseTokenService(JdbcTemplate connection) {
		super();
		this.connection = connection;
	}

	@Override
	public boolean verify(final MobileNumber mobileNumber, final String token) {
		return connection.query(
				"select 1 from duoshouji.user_wechat_login where mobile = ? and token  = ? and expired = 0"
				, new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setLong(1, mobileNumber.toLong());
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
	public String newToken(final MobileNumber mobileNumber) {
		final String token = UUID.randomUUID().toString();
		connection.update(
				"insert into duoshouji.user_wechat_login (mobile, create_time, token, expired) values (?,?,?,0)"
				, new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setLong(1, mobileNumber.toLong());
						ps.setLong(2, System.currentTimeMillis());
						ps.setString(3, token);
					}
					
				});
		return token;
	}

	@Override
	public void logout(MobileNumber mobileNumber) {
		connection.update("update duoshouji.user_wechat_login set expired = 1 where mobile = " + mobileNumber);
	}

	@Override
	public MobileNumber fetchUserId(String token) {
		return connection.query("select mobile from duoshouji.user_wechat_login where token = '" + token + "'"
				, new ResultSetExtractor<MobileNumber>() {

			@Override
			public MobileNumber extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				if (rs.next()) {
					return MobileNumber.valueOf(rs.getLong("mobile"));
				}
				return null;
			}
			
		});
	}

}
