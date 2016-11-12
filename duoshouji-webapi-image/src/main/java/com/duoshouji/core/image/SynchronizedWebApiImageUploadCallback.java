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
import org.springframework.stereotype.Service;

import com.duoshouji.core.ImageUploadCallback;
import com.duoshouji.core.StoreImageException;
import com.duoshouji.restapi.Constants;
import com.duoshouji.restapi.image.ImageJsonAdapter;
import com.duoshouji.restapi.image.UploadNoteImageCallbackData;
import com.duoshouji.service.util.Image;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SynchronizedWebApiImageUploadCallback implements ImageUploadCallback {

	private static final String CALLBACK_HOST = "restapi.share68.com";
	
	private CloseableHttpClient httpClient;
	
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
		makeCallbackAndEnsureSuccess(originalUploadRequest, new ImageJsonAdapter(imageInfo));
	}

	@Override
	public void fireNoteImageUpload(ServletRequest originalUploadRequest, UploadNoteImageCallbackData imageInfos) throws StoreImageException {
		makeCallbackAndEnsureSuccess(originalUploadRequest, imageInfos);
	}
	
	private void makeCallbackAndEnsureSuccess(ServletRequest originalUploadRequest, Object bodyContent) throws StoreImageException {
		try {
			HttpUriRequest request = RequestBuilder.post(buildURI(originalUploadRequest))
					.setEntity(new StringEntity(toJsonString(bodyContent)))
					.setCharset(StandardCharsets.UTF_8)
					.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
					.setHeader(Constants.APP_TOKEN_HTTP_HEADER_NAME, ((HttpServletRequest)originalUploadRequest).getHeader(Constants.APP_TOKEN_HTTP_HEADER_NAME))
					.build();
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
