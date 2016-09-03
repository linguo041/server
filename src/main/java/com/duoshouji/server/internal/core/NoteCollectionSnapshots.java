package com.duoshouji.server.internal.core;

import java.util.HashMap;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.user.RegisteredUser;

public class NoteCollectionSnapshots {

	private HashMap<RegisteredUser, NoteCollection> snapshotMapping;
	
	NoteCollectionSnapshots() {
		snapshotMapping = new HashMap<RegisteredUser, NoteCollection>();
	}
	
	void putSnapshot(RegisteredUser user, NoteCollection notes) {
		snapshotMapping.put(user, notes);
	}

	NoteCollection getSnapshot(RegisteredUser user) {
		return snapshotMapping.get(user);
	}

}
