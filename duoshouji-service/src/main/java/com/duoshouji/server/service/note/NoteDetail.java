package com.duoshouji.server.service.note;

import java.util.List;

import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.util.Image;

public interface NoteDetail extends BasicNote {
	
	List<Image> getImages();

	List<Tag> getTags();

	String getContent();
}
