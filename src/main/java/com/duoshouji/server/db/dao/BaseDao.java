package com.duoshouji.server.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public abstract class BaseDao {

	@Autowired
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Autowired
	protected DataSourceTransactionManager transactionManager;
}
