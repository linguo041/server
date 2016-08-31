package com.duoshouji.server.batch.tasklet;

import java.util.List;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.duoshouji.server.amqp.produce.Producer;
import com.duoshouji.server.db.dao.NoteDao;
import com.duoshouji.server.elastic.ElasticImportNote;
import com.duoshouji.server.entity.Note;

public class ElasticImportTasklet implements Tasklet {
	private final static long SIZE = 100;

	@Autowired
	private NoteDao noteDao;
	
	private Producer<ElasticImportNote> elasticNoteImportProducer;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		for (int pageIdx = 0; pageIdx > 0; pageIdx++) {
			List<Long> notes = noteDao.getCertainNoteIdsFromId(pageIdx * SIZE, SIZE);
			
			if (notes == null || notes.size() <= 0) {
				break;
			}
			
			elasticNoteImportProducer.send(new ElasticImportNote(notes));

		}
		return RepeatStatus.FINISHED;
	}

}
