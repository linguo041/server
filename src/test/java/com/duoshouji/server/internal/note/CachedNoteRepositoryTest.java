package com.duoshouji.server.internal.note;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.internal.core.CachedNoteRepository;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteRepository;

public class CachedNoteRepositoryTest {

	private Mockery mockery;
	private NoteRepository delegator;
	private NoteCollection noteCollection;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		delegator = mockery.mock(NoteRepository.class);
		noteCollection = mockery.mock(NoteCollection.class);
	}
	
	@Test
	public void getNotesFromDelegator() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).findNotes(); will(returnValue(noteCollection));
		}});
		CachedNoteRepository testTarget = new CachedNoteRepository(delegator);
		Assert.assertEquals(noteCollection, testTarget.findNotes());
	}
	
	@Test
	public void getNotesFromCache() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).findNotes(); will(returnValue(noteCollection));
		}});
		CachedNoteRepository testTarget = new CachedNoteRepository(delegator);
		Assert.assertEquals(noteCollection, testTarget.findNotes());
		Assert.assertEquals(noteCollection, testTarget.findNotes());
	}
	
	@Test
	public void getNotesByTagFromDelegator() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).findNotes(MockConstants.MOCK_TAG_ID); will(returnValue(noteCollection));
		}});
		CachedNoteRepository testTarget = new CachedNoteRepository(delegator);
		Assert.assertEquals(noteCollection, testTarget.findNotes(MockConstants.MOCK_TAG_ID));
	}
	
	@Test
	public void getNotesByTagFromCache() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).findNotes(MockConstants.MOCK_TAG_ID); will(returnValue(noteCollection));
		}});
		CachedNoteRepository testTarget = new CachedNoteRepository(delegator);
		Assert.assertEquals(noteCollection, testTarget.findNotes(MockConstants.MOCK_TAG_ID));
		Assert.assertEquals(noteCollection, testTarget.findNotes(MockConstants.MOCK_TAG_ID));
	}
}
