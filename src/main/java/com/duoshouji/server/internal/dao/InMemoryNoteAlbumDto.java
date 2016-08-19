package com.duoshouji.server.internal.dao;

import com.duoshouji.server.service.dao.NoteAlbumDto;
import com.duoshouji.server.util.Image;

public class InMemoryNoteAlbumDto implements NoteAlbumDto {

	private Image mainImage;
	
	public InMemoryNoteAlbumDto(Image mainImage) {
		super();
		this.mainImage = mainImage;
	}

	@Override
	public Image getMainImage() {
		return mainImage;
	}

}
