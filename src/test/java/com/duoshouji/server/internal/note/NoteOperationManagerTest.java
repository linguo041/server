package com.duoshouji.server.internal.note;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class NoteOperationManagerTest {

	private Mockery mockery; 
	private NoteDao noteDao;
	private NoteDtoCollection noteDtoCollection;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		noteDao = mockery.mock(NoteDao.class);
		noteDtoCollection = mockery.mock(NoteDtoCollection.class);
	}
	
	@Test
	public void getAllNotes() {
		mockery.checking(new Expectations(){{
			oneOf(noteDao).findNotes(); will(returnValue(noteDtoCollection));
		}});
		
	}
	
	@Test
	public void getNoteByTagId() {
		
	}
}
