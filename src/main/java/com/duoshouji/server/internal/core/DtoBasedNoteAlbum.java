package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.dao.NoteAlbumDto;
import com.duoshouji.server.service.note.NoteAlbum;
import com.duoshouji.server.util.Image;

public class DtoBasedNoteAlbum implements NoteAlbum {

	private NoteAlbumDto dto;
	public DtoBasedNoteAlbum(NoteAlbumDto dto) {
		super();
		this.dto = dto;
	}
	@Override
	public Image getMainImage() {
		return dto.getMainImage();
	}

}
