package com.duoshouji.server.internal.note;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteRepository;

public class NoteFacadeImplTest {

	private Mockery mockery;
	private NoteRepository noteRepository;
	private NoteCollection noteCollection;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		noteRepository = mockery.mock(NoteRepository.class);
		noteCollection = mockery.mock(NoteCollection.class);
	}
	
	@Test
	public void getAllPushedNotes() {
		mockery.checking(new Expectations(){{
			oneOf(noteRepository).findNotes(); will(returnValue(noteCollection));
		}});
		
		NoteFacadeImpl target = new NoteFacadeImpl(noteRepository);
		Assert.assertEquals(noteCollection, target.getPushedNotes());
	}
}
