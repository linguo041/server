package com.duoshouji.core;

import com.duoshouji.service.util.Image;

public interface ImageStore {

	Image saveUserPortrait(long userId, byte[] uploadedImage) throws StoreImageException;

	Image[] saveNoteImage(long noteId, byte[][] uploadedImages) throws StoreImageException;

}
