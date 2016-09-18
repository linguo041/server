package com.duoshouji.server.internal.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.springframework.web.context.ServletContextAware;

import com.duoshouji.server.service.image.ImageStore;
import com.duoshouji.server.service.image.StoreImageException;
import com.duoshouji.server.util.MobileNumber;

public class ServletPathImageStore implements ImageStore, ServletContextAware {

	private ServletContext servletContext;
	
	@Override
	public URL saveUserPortrait(MobileNumber accountId, InputStream swingImage) throws StoreImageException {
		final String servletPath = "/images/users/" + accountId + "/portrait.jpeg";
		try {
			saveToFile(swingImage, createFile(servletPath));
			return new URL(servletPath);
		} catch (Exception e) {
			throw new StoreImageException(e);
		} 
	}

	@Override
	public URL saveNoteImage(long noteId, int index, InputStream swingImage) throws StoreImageException {
		final String servletPath = "/images/notes/" + noteId + "/"+index+".jpeg";
		try {
			saveToFile(swingImage, createFile(servletPath));
			return new URL(servletPath);
		} catch (Exception e) {
			throw new StoreImageException(e);
		}
	}
	
	private void saveToFile(InputStream swingImage, File imageFile) throws IOException {
		if (imageFile.exists()) {
			imageFile.delete();
		}
		FileUtils.copyInputStreamToFile(swingImage, imageFile);
	}

	private File createFile(String servletPath) {
		Path filePath = Paths.get(servletContext.getRealPath(servletPath));
		File dirFile = filePath.getParent().toFile();
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		return filePath.toFile();
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	
}
