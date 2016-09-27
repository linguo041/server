package com.duoshouji.server.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.duoshouji.server.internal.core.DatabaseCatalogRepository;
import com.duoshouji.server.internal.core.DatabaseDistrictRepository;
import com.duoshouji.server.service.common.DistrictRepository;

@Configuration
@Import(DuoShouJiConfiguration.class)
public class DuoShouJiApiConfiguration {

	@Bean
    public DistrictRepository districtRepository (JdbcTemplate jdbcTemplate) {
		return new DatabaseDistrictRepository(jdbcTemplate);
	}
	
	@Bean
	public DatabaseCatalogRepository databaseCatalogRepository(JdbcTemplate jdbcTemplate) {
		return new DatabaseCatalogRepository(jdbcTemplate);
	}
}
