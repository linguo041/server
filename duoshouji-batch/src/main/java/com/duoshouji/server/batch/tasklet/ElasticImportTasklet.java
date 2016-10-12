package com.duoshouji.server.batch.tasklet;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.duoshouji.server.batch.service.ElasticSearchService;
import com.duoshouji.server.elasticsearch.ESUtil;
import com.duoshouji.server.elasticsearch.document.ESNote;
import com.duoshouji.server.jpa.entity.DBNote;
import com.google.common.collect.Lists;


public class ElasticImportTasklet implements Tasklet {
	private final static int SIZE = 100;

	@Autowired
	private ElasticSearchService elasticSearchService;
	
	@Autowired
	private ExecutorService executorService;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		System.out.println("begin to process.....");
		for (int pageIdx = 0; pageIdx >= 0; pageIdx++) {
			List<DBNote> notes = elasticSearchService.getStoredNotes(pageIdx, SIZE);
			
			if (notes == null || notes.size() <= 0) {
				break;
			}
			
			List<ESNote> esNotes = Lists.transform(notes, ESUtil.DBNOTE_TO_ES_FUNCTION);
			
			asyncImportNotesToES(esNotes);
		}
		return RepeatStatus.FINISHED;
	}
	
	private void asyncImportNotesToES (final List<ESNote> esNotes) {
		executorService.execute(new Runnable(){
			@Override
			public void run() {
				elasticSearchService.importToES(esNotes);
			}
		});
	}
}
