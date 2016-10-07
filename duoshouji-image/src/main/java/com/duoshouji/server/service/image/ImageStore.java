package com.duoshouji.server.service.image;

import java.io.InputStream;

import com.duoshouji.service.util.Image;

public interface ImageStore {

	Image saveUserPortrait(long userId, InputStream uploadedImage) throws StoreImageException;

	Image[] saveNoteImage(long noteId, InputStream[] uploadedImages) throws StoreImageException;

}
