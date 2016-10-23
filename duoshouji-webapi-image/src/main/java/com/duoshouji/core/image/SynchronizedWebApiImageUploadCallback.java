package com.duoshouji.core.image;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.duoshouji.core.ImageUploadCallback;
import com.duoshouji.core.StoreImageException;
import com.duoshouji.restapi.controller.model.response.ImageJsonAdapter;
import com.duoshouji.service.util.Image;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SynchronizedWebApiImageUploadCallback implements ImageUploadCallback {

	private static final String CALLBACK_HOST = "restapi.share68.com";
	
	private CloseableHttpClient httpClient;
	private Logger logger = LogManager.getLogger(ImageUploadCallback.class);
	
	@PostConstruct
	public void init() {
		httpClient = HttpClients.createDefault();
	}
	
	@PreDestroy
	public void destroy() throws IOException {
		httpClient.close();
	}
	
	@Override
	public void firePortraitUpload(ServletRequest originalUploadRequest, Image imageInfo) throws StoreImageException {
		logger.info("Start image upload callback for portrait image [width: {}, height: {}, url: {}]"
				, imageInfo.getWidth()
				, imageInfo.getHeight()
				, imageInfo.getUrl());
		makeCallbackAndEnsureSuccess(originalUploadRequest, new ImageJsonAdapter(imageInfo));
	}

	@Override
	public void fireNoteImageUpload(ServletRequest originalUploadRequest, Image[] imageInfos) throws StoreImageException {
		ImageJsonAdapter[] targetImages = new ImageJsonAdapter[imageInfos.length];
		for (int i = 0; i < imageInfos.length; ++i) {
			targetImages[i] = new ImageJsonAdapter(imageInfos[i]);
			logger.info("Start image upload callback for note image [width: {}, height: {}, url: {}]"
					, imageInfos[i].getWidth()
					, imageInfos[i].getHeight()
					, imageInfos[i].getUrl());
		}
		makeCallbackAndEnsureSuccess(originalUploadRequest, targetImages);
	}
	
	private void makeCallbackAndEnsureSuccess(ServletRequest originalUploadRequest, Object bodyContent) throws StoreImageException {
		try {
			HttpUriRequest request = RequestBuilder.post(buildURI(originalUploadRequest))
					.setEntity(new StringEntity(toJsonString(bodyContent)))
					.setCharset(StandardCharsets.UTF_8)
					.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()).build();
			final int statusCode = httpClient.execute(request).getStatusLine().getStatusCode();
			if (statusCode != 200) {
				throw new StoreImageException("Image url synchronize failed in service.");
			}
			LogManager.getLogger(ImageUploadCallback.class).info("Image upload callback success!");
		} catch (Exception e) {
			StoreImageException wrapped;
			if (e instanceof StoreImageException) {
				wrapped = (StoreImageException) e;
			} else {
				wrapped = new StoreImageException(e);
			}
			throw wrapped;
		}
	}
	
	private String toJsonString(Object object) throws Exception {
		return new ObjectMapper().writeValueAsString(object);
	}
	
	private URI buildURI(ServletRequest originalUploadRequest) throws URISyntaxException {
		final String originalUri = ((HttpServletRequest)originalUploadRequest).getRequestURI();
		return new URIBuilder().setScheme("http").setHost(CALLBACK_HOST).setPath(originalUri).build();
	}
}
