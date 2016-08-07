package com.duoshouji.server.internal.note;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.service.note.FilteredNoteCollection;
import com.duoshouji.server.service.note.NoteFacade.PushedNoteRequestor;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.Location;

public class NoteFacadeImplTest {

	private Mockery mockery;
	private NoteRepository noteRepository;
	private RegisteredUser user;
	private FilteredNoteCollection noteCollection;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		noteRepository = mockery.mock(NoteRepository.class);
		noteCollection = mockery.mock(FilteredNoteCollection.class);
	}
	
	@Test
	public void getAllPushedRepository() {
		mockery.checking(new Expectations(){{
			oneOf(noteRepository).findNotes(); will(returnValue(noteCollection));
		}});
		
		NoteFacadeImpl target = new NoteFacadeImpl(noteRepository);
		Assert.assertEquals(noteCollection, target.getPushedNoteRequestor().getPushedNotes());
	}
	
	@Test
	public void getPushedRepositoryByUserMarks() {
		mockery.checking(new Expectations(){{
			oneOf(noteRepository).findNotes(); will(returnValue(noteCollection));
			oneOf(noteCollection).filter(user); will(returnValue(noteCollection));
		}});
		
		NoteFacadeImpl target = new NoteFacadeImpl(noteRepository);
		PushedNoteRequestor requestor = target.getPushedNoteRequestor();
		requestor.setUserMarked(user);
		Assert.assertEquals(noteCollection, requestor.getPushedNotes());
	}

	@Test
	public void getPushedRepositoryByLocation() {
		final Location mockLocation = Location.parse("(12.21,34.567)");
		mockery.checking(new Expectations(){{
			oneOf(noteRepository).findNotes(); will(returnValue(noteCollection));
			oneOf(noteCollection).filter(mockLocation); will(returnValue(noteCollection));
		}});
		
		NoteFacadeImpl target = new NoteFacadeImpl(noteRepository);
		PushedNoteRequestor requestor = target.getPushedNoteRequestor();
		requestor.setLocation(mockLocation);
		Assert.assertEquals(noteCollection, requestor.getPushedNotes());
	}

	@Test
	public void getPushedRepositoryByUserMarksAndCategory() {
		final int mockCategoryId = 1;
		mockery.checking(new Expectations(){{
			oneOf(noteRepository).findNotes(); will(returnValue(noteCollection));
			oneOf(noteCollection).filter(user); will(returnValue(noteCollection));
			oneOf(noteCollection).filter(mockCategoryId); will(returnValue(noteCollection));
		}});
		
		NoteFacadeImpl target = new NoteFacadeImpl(noteRepository);
		PushedNoteRequestor requestor = target.getPushedNoteRequestor();
		requestor.setUserMarked(user);
		requestor.setCategory(mockCategoryId);
		Assert.assertEquals(noteCollection, requestor.getPushedNotes());
	}

	@Test
	public void getPushedRepositoryByLocationAndCategory() {
		final Location mockLocation = Location.parse("(12.21,34.567)");
		final int mockCategoryId = 1;
		mockery.checking(new Expectations(){{
			oneOf(noteRepository).findNotes(); will(returnValue(noteCollection));
			oneOf(noteCollection).filter(mockLocation); will(returnValue(noteCollection));
			oneOf(noteCollection).filter(mockCategoryId); will(returnValue(noteCollection));
		}});
		
		NoteFacadeImpl target = new NoteFacadeImpl(noteRepository);
		PushedNoteRequestor requestor = target.getPushedNoteRequestor();
		requestor.setLocation(mockLocation);
		requestor.setCategory(mockCategoryId);
		Assert.assertEquals(noteCollection, requestor.getPushedNotes());
	}

}
