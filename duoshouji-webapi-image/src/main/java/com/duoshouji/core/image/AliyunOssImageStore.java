package com.duoshouji.core.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.duoshouji.core.ImageStore;
import com.duoshouji.core.StoreImageException;
import com.duoshouji.service.util.Image;

@Service
public class AliyunOssImageStore implements ImageStore {
	private static final String ACCESS_KEY_ID = "REQ4WWCYXaNWqTBZ";
	private static final String ACCESS_KEY_SECRET = "aC1RtnoiQ4xj96R7U4nFfJPxO9tSeT";
	private static final String ALIYUN_END_POINT = "oss-cn-shanghai.aliyuncs.com";
	private static final String BUCKET_NAME = "duoshouji-test";
	
	private static final String DUOSHOUJI_END_POINT = "images.share68.com";
	
	private OSSClient client;
	
	@PostConstruct
	public void init() {
		client = new OSSClient(ALIYUN_END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
	}
	
	@PreDestroy
	public void destory() {
		client.shutdown();
	}
	
	@Override
	public Image saveUserPortrait(long userId, byte[] uploadedImage) throws StoreImageException {
		final String keyWithoutImageFormat = "images/users/" + userId + "/portrait/" + System.currentTimeMillis();
		return saveImageToAliyun(uploadedImage, keyWithoutImageFormat);
	}

	@Override
	public Image[] saveNoteImage(long noteId, byte[][] uploadedImages) throws StoreImageException {
		final String noteKeyPrefix = "images/notes/" + noteId + "/" + System.currentTimeMillis() + "/";
		final Image[] results = new Image[uploadedImages.length];
		for (int i = 0; i < uploadedImages.length; ++i) {
			final String keyWithoutImageFormat = noteKeyPrefix + String.format("%02d", i);
			results[i] = saveImageToAliyun(uploadedImages[i], keyWithoutImageFormat);
		}
		return results;
	}
	
	private Image saveImageToAliyun(byte[] imageBytes, String keyWithoutImageFormat) throws StoreImageException {
		try {
			ImageReader imageReader = getImageReader(new ByteArrayInputStream(imageBytes));
			final int width = imageReader.getWidth(imageReader.getMinIndex());
			final int height = imageReader.getHeight(imageReader.getMinIndex());
			final String formatName = imageReader.getFormatName();
			final String keyWithImageFormat = keyWithoutImageFormat + "." + formatName;
			client.putObject(BUCKET_NAME, keyWithImageFormat, new ByteArrayInputStream(imageBytes));
			return new Image(width, height, buildObjectURL(keyWithImageFormat));
		} catch (Exception e) {
			throw new StoreImageException(e);
		} 
	}
	
	private ImageReader getImageReader(InputStream uploadedImage) throws IOException, StoreImageException {
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(uploadedImage);
		Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
		if (!imageReaders.hasNext()) {
			throw new StoreImageException("Unrecognizable image format!");
		}
		ImageReader imageReader = imageReaders.next();
		imageReader.setInput(imageInputStream, true);
		return imageReader;
	}
	
	private String buildObjectURL(String key) throws MalformedURLException {
		StringBuilder urlBuilder = new StringBuilder("http://");
		urlBuilder.append(DUOSHOUJI_END_POINT);
		urlBuilder.append('/');
		urlBuilder.append(key);
		return urlBuilder.toString();
	}
}
