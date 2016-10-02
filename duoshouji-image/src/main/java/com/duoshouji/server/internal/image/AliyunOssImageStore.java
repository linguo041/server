package com.duoshouji.server.internal.image;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.duoshouji.server.service.image.ImageStore;
import com.duoshouji.server.service.image.StoreImageException;
import com.duoshouji.util.MobileNumber;

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
	public URL saveUserPortrait(MobileNumber userId, InputStream swingImage) throws StoreImageException {
		final String key = userId.toString() + ".portrait";
		try {
			client.putObject(BUCKET_NAME, key, swingImage);
			return buildObjectURL(key);
		} catch (Exception e) {
			throw new StoreImageException(e);
		}
	}

	@Override
	public URL saveNoteImage(long noteId, int index, InputStream swingImage) throws StoreImageException {
		final String key = "note" + noteId + ".image" + index;
		try {
			client.putObject(BUCKET_NAME, key, swingImage);
			return buildObjectURL(key);
		} catch (Exception e) {
			throw new StoreImageException(e);
		}
	}
	
	private URL buildObjectURL(String key) throws MalformedURLException {
		StringBuilder urlBuilder = new StringBuilder("http://");
		urlBuilder.append(DUOSHOUJI_END_POINT);
		urlBuilder.append('/');
		urlBuilder.append(key);
		return new URL(urlBuilder.toString());
	}
}
