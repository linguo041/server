package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteDetail;

class NoteProxyWithBasicNote extends AbstractNoteProxy {
	
	private UserNoteOperationManager operationManager;
	private BasicNote delegator;
	
	NoteProxyWithBasicNote(UserNoteOperationManager operationManager, BasicNote delegator) {
		this.operationManager = operationManager;
		this.delegator = delegator;
	}

	@Override
	public long getNoteId() {
		return delegator.getNoteId();
	}

	@Override
	protected BasicNote getBasicNote() {
		return delegator;
	}

	@Override
	protected NoteDetail getNoteDetail() {
		return getDelegator();
	}

	@Override
	protected Note getDelegator() {
		if (!(delegator instanceof Note)) {
			delegator = operationManager.getNote(getNoteId());
		}
		return (Note)delegator;

	}

	@Override
	public void fireAddComment(int rating) {
		if (delegator instanceof UserNoteInteractionAware) {
			((UserNoteInteractionAware)delegator).fireAddComment(rating);
		}
	}

	@Override
	public void fireAddLike() {
		if (delegator instanceof UserNoteInteractionAware) {
			((UserNoteInteractionAware)delegator).fireAddLike();
		}		
	}

}
