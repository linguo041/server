package com.duoshouji.server.service.note;

import com.duoshouji.server.annotation.Unique;
import com.duoshouji.server.util.Image;

@Unique
public interface Note extends NoteDetail {

	void setMainImage(Image mainImage);
}
