package com.duoshouji.server.batch.configuration;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.duoshouji.server.configuration.DuoShouJiConfiguration;

@Configuration
@EnableBatchProcessing
@Import(DuoShouJiConfiguration.class)
public class BaseBatchConfiguration {

	@Autowired
    private JobBuilderFactory jobs;
 
    @Autowired
    private StepBuilderFactory steps;
}
