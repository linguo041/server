package com.duoshouji.service.note;

import java.util.List;

import com.duoshouji.service.common.Tag;
import com.duoshouji.service.util.Image;

public interface NoteDetail extends BasicNote {
	
	List<Image> getImages();

	List<Tag> getTags();

	String getContent();
	
	String getProductName();

}
