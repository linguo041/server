package com.duoshouji.server.batch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.duoshouji.server.batch.BatchPackageScanned;
import com.duoshouji.server.batch.tasklet.ElasticImportTasklet;
import com.duoshouji.server.elasticsearch.configuration.ElasticSearchConfiguration;
import com.duoshouji.server.jpa.configuration.JpaConfiguration;

@Configuration
@Import({ElasticSearchConfiguration.class, JpaConfiguration.class, BaseBatchConfiguration.class})
@ComponentScan(basePackageClasses = { BatchPackageScanned.class})
public class ElasticImportJobConfiguration {
	
	@Bean
	public Tasklet elasticImportTasklet () {
		return new ElasticImportTasklet();
	}

	@Bean
    public Job elasticImportJob(JobBuilderFactory jobs, Step s1) {
		return jobs.get("elasticImportJob")
				.incrementer(new RunIdIncrementer())
				.flow(s1)
				.end()
				.build();
	}
	
	@Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, Tasklet elasticImportTasklet) {
		return stepBuilderFactory.get("step1")
				.tasklet(elasticImportTasklet)
				.build();
	}
}
