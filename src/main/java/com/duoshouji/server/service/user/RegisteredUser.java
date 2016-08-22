package com.duoshouji.server.service.user;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public interface RegisteredUser extends BasicUserAttributes {

	UserMessageProxy getMessageProxy();

	boolean verifyPassword(Password password);

	boolean hasPassword();

	void logout();

	void setPassword(Password password);

	String login();

	void setNickname(String nickname);

	NoteCollection getPublishedNotes();

	long publishNote(NotePublishAttributes notePublishAttributes);
}
