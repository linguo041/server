package com.duoshouji.server.internal.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFacade;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.RegisteredUser;

@Service
public class NoteFacadeImpl implements NoteFacade {

	private NoteRepository noteRepository;
	private NoteCollectionSnapshots snapshots;
	
	@Autowired
	public NoteFacadeImpl(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
		snapshots = new NoteCollectionSnapshots();
	}

	@Override
	public NoteCollection pushSquareNotes(RegisteredUser user) {
		NoteCollection notes = noteRepository.findNotes();
		snapshots.putSnapshot(user, notes);
		return notes;
	}

	@Override
	public NoteCollection getPushedSquareNotes(RegisteredUser user) {
		NoteCollection notes = snapshots.getSnapshot(user);
		if (notes == null) {
			notes = pushSquareNotes(user);
		}
		return notes;
	}

}
