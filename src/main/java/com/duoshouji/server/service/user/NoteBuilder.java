package com.duoshouji.server.service.user;

public interface NoteBuilder {

	void setTitle(String title);

	void setContent(String content);

	long publishNote();

}
