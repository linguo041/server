package com.duoshouji.restapi.end2endtest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
public class TestDataSourceConfig {

	@Bean
	public JdbcTemplate getJdbcTempalte() {
		EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
			.setName("duoshouji")
		    .setType(EmbeddedDatabaseType.H2)
		    .setScriptEncoding("UTF-8")
		    .ignoreFailedDrops(true)
		    .addScripts("schema.sql", "data.sql")
		    .build();
		return new JdbcTemplate(db);
	}
}
