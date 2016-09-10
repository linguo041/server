package com.duoshouji.server.service.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageStore {

	String savePortrait(String accountId, BufferedImage swingImage) throws IOException;

	String saveNoteImage(long noteId, BufferedImage swingImage) throws IOException;

}
