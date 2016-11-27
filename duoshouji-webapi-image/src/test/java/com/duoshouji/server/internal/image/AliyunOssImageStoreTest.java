package com.duoshouji.server.internal.image;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.duoshouji.core.image.AliyunOssImageStore;
import com.duoshouji.service.util.Image;

public class AliyunOssImageStoreTest {

	private static final String MOCK_PORTRAIT_FILE = "/unknown-portrait.jpg";
	private static final String MOCK_NOTE_IMAGE_FILE0 = "/note-image0.jpg";
	private static final String MOCK_NOTE_IMAGE_FILE1 = "/note-image1.jpg";
	
	private InputStream openStream(String imageUrl) throws Exception {
		if (imageUrl.startsWith("/")) {
			return getClass().getResourceAsStream(imageUrl);
		} else {
			return new URL(imageUrl).openStream();
		}
	}
	
	@Test
	public void uploadUserPortrait() throws Exception {
		AliyunOssImageStore imageStore = new AliyunOssImageStore();
		imageStore.init();
		Image image = imageStore.saveUserPortrait(0l, IOUtils.toByteArray(openStream(MOCK_PORTRAIT_FILE)));
		imageStore.destory();
		Assert.assertTrue(IOUtils.contentEquals(
				openStream(image.getUrl())
				, openStream(MOCK_PORTRAIT_FILE)));
		Assert.assertTrue(image.getUrl().startsWith("http://images.share68.com/images/users/0/portrait"));
	}
	
	@Test
	public void uploadNoteImages() throws Exception {
		AliyunOssImageStore imageStore = new AliyunOssImageStore();
		imageStore.init();
		Image[] images = imageStore.saveNoteImage(0l , new byte[][] {
				IOUtils.toByteArray(openStream(MOCK_NOTE_IMAGE_FILE0)),
				IOUtils.toByteArray(openStream(MOCK_NOTE_IMAGE_FILE1))
				});
		imageStore.destory();
		
		Assert.assertEquals(2, images.length);
		Assert.assertTrue(IOUtils.contentEquals(openStream(images[0].getUrl()) , openStream(MOCK_NOTE_IMAGE_FILE0)));
		Assert.assertTrue(IOUtils.contentEquals(openStream(images[1].getUrl()) , openStream(MOCK_NOTE_IMAGE_FILE1)));
		Assert.assertTrue(images[0].getUrl().startsWith("http://images.share68.com/images/notes/0/00"));
		Assert.assertTrue(images[1].getUrl().startsWith("http://images.share68.com/images/notes/0/01"));
	}
}
