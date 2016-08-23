package com.duoshouji.server.internal.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import com.duoshouji.server.service.image.ImageStore;

@Service
public class ServletPathImageStore implements ImageStore, ServletContextAware {

	private ServletContext servletContext;
	
	@Override
	public String savePortrait(String accountId, BufferedImage swingImage) throws IOException {
		final String servletPath = "/images/users/" + accountId + "/portrait.jpeg";
		saveToFile(swingImage, createFile(servletPath));
		return servletPath;
	}

	@Override
	public String saveNoteImage(long noteId, BufferedImage swingImage) throws IOException {
		final String servletPath = "/images/notes/" + noteId + "/main.jpeg";
		saveToFile(swingImage, createFile(servletPath));
		return servletPath;
	}
	
	private void saveToFile(BufferedImage swingImage, File imageFile) throws IOException {
		if (imageFile.exists()) {
			imageFile.delete();
		}
		ImageIO.write(swingImage, "jpeg", imageFile);
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
