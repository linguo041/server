package com.duoshouji.server.internal.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.duoshouji.server.service.image.StoreImageException;
import com.duoshouji.util.MobileNumber;

public class AliyunOssImageStoreTest {

	@Test
	public void testConnection() throws IOException, StoreImageException {
		AliyunOssImageStore imageStore = new AliyunOssImageStore();
		imageStore.init();
		InputStream inputStream = new URL("https://oss-example.oss-cn-hangzhou.aliyuncs.com/aliyun-logo.png").openStream();
		URL imageUrl = imageStore.saveUserPortrait(MobileNumber.valueOf(13661863279l), inputStream);
		inputStream.close();
		imageStore.destory();
		inputStream = imageUrl.openStream();
		Assert.assertTrue(IOUtils.contentEquals(inputStream, new URL("https://oss-example.oss-cn-hangzhou.aliyuncs.com/aliyun-logo.png").openStream()));
		inputStream.close();
	}
}
