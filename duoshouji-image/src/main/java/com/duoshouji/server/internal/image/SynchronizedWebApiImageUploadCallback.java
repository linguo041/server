package com.duoshouji.server.internal.image;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.image.ImageUploadCallback;
import com.duoshouji.server.service.image.StoreImageException;
import com.duoshouji.server.util.Image;

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
	public void fireImageUpload(ServletRequest originalUploadRequest, Image imageInfo) throws StoreImageException {
		try {
			HttpUriRequest request = RequestBuilder.post(buildURI(originalUploadRequest))
					.addParameter("imageUrl", imageInfo.getUrl())
					.addParameter("imageWidth", Integer.toString(imageInfo.getWidth()))
					.addParameter("imageHeight", Integer.toString(imageInfo.getHeight())).build();
			makeCallbackAndEnsureSuccess(request);
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

	@Override
	public void fireImageUpload(ServletRequest originalUploadRequest, Image[] imageInfos) throws StoreImageException {
		try {
			RequestBuilder requestBuilder = RequestBuilder.post(buildURI(originalUploadRequest));
			requestBuilder.addParameter("imageCount", Integer.toString(imageInfos.length));		
			for (Image imageInfo : imageInfos) {
				requestBuilder.addParameter("imageUrl", imageInfo.getUrl())
					.addParameter("imageWidth", Integer.toString(imageInfo.getWidth()))
					.addParameter("imageHeight", Integer.toString(imageInfo.getHeight())).build();
			}
			makeCallbackAndEnsureSuccess(requestBuilder.build());
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
	
	private URI buildURI(ServletRequest originalUploadRequest) throws URISyntaxException {
		final String originalUri = ((HttpServletRequest)originalUploadRequest).getRequestURI();
		return new URIBuilder().setScheme("http").setHost(CALLBACK_HOST).setPath("/callback" + originalUri).build();
	}
	
	private void makeCallbackAndEnsureSuccess(HttpUriRequest request) throws ClientProtocolException, IOException, StoreImageException {
		final int statusCode = httpClient.execute(request).getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new StoreImageException("Image url synchronize failed in service.");
		}
	}
}
