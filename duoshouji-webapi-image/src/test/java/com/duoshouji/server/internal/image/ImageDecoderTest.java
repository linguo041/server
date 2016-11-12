package com.duoshouji.server.internal.image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.junit.Assert;
import org.junit.Test;

import com.aliyun.oss.common.utils.IOUtils;
import com.duoshouji.core.StoreImageException;

public class ImageDecoderTest {

	@Test
	public void recognizeStandardImageFormats() throws Exception {
		Assert.assertEquals("bmp", getImageFormat("/bmp-image.bmp").toLowerCase());
		Assert.assertEquals("gif", getImageFormat("/gif-image.gif").toLowerCase());
		Assert.assertEquals("jpeg", getImageFormat("/jpeg-image.jpg").toLowerCase());
		Assert.assertEquals("png", getImageFormat("/png-image.png").toLowerCase());
	}
	
	private String getImageFormat(String imageFile) throws Exception {
		return getImageReader(getImageInput(imageFile)).getFormatName();
	}
	
	private ImageReader getImageReader(InputStream uploadedImage) throws IOException, StoreImageException {
		final ImageInputStream imageInputStream = ImageIO.createImageInputStream(uploadedImage);
		ImageReader imageReader = ImageIO.getImageReaders(imageInputStream).next();
		imageReader.setInput(imageInputStream, true);
		return imageReader;
	}

	private InputStream getImageInput(String fileName) {
		return getClass().getResourceAsStream(fileName);
	}
	
	@Test
	public void recognizeBase64ImageFormat() throws Exception {
		final String imageData = IOUtils.readStreamAsString(getClass().getResourceAsStream("/png-base64-image"), StandardCharsets.US_ASCII.name());
		final String formatName = getImageReader(new ByteArrayInputStream(Base64.getDecoder().decode(imageData))).getFormatName();
		Assert.assertEquals("png", formatName.toLowerCase());
	}
}
