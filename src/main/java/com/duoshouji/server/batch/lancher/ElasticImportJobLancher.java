package com.duoshouji.server.batch.lancher;

import javax.batch.operations.JobRestartException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.duoshouji.server.batch.configuration.ElasticImportJobConfiguration;

@Component
public class ElasticImportJobLancher {
	
	@Autowired
    JobLauncher jobLauncher;
 
    @Autowired
    Job elasticImportJob;
 
    public static void main(String... args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, org.springframework.batch.core.repository.JobRestartException {
 
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ElasticImportJobConfiguration.class);
 
        ElasticImportJobLancher main = context.getBean(ElasticImportJobLancher.class);
 
        JobExecution jobExecution = main.jobLauncher.run(main.elasticImportJob, new JobParameters());
 
        context.close();
 
    }
}
