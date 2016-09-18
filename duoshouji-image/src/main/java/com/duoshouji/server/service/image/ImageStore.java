package com.duoshouji.server.service.image;

import java.io.InputStream;
import java.net.URL;

import com.duoshouji.server.util.MobileNumber;

public interface ImageStore {

	URL saveUserPortrait(MobileNumber userId, InputStream swingImage) throws StoreImageException;

	URL saveNoteImage(long noteId, int index, InputStream swingImage) throws StoreImageException;

}
