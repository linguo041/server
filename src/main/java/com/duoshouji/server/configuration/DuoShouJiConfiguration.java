package com.duoshouji.server.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DuoShouJiConfiguration {

	@Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://139.196.151.131:3306/duoshouji");
        dataSource.setUsername("root");
        dataSource.setPassword("Duo@2016");

        return dataSource;
    }
	
	@Bean
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate () {
		return new NamedParameterJdbcTemplate(mysqlDataSource());
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate () {
		return new JdbcTemplate(mysqlDataSource());
	}
	
	@Bean
	public DataSourceTransactionManager transactionManager () {
		return new DataSourceTransactionManager(mysqlDataSource());
	}
}
