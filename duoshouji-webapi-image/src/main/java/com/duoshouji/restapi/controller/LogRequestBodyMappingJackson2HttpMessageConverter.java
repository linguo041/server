package com.duoshouji.restapi.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.duoshouji.restapi.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LogRequestBodyMappingJackson2HttpMessageConverter extends
		MappingJackson2HttpMessageConverter {

	public LogRequestBodyMappingJackson2HttpMessageConverter() {
		super();
	}

	public LogRequestBodyMappingJackson2HttpMessageConverter(
			ObjectMapper objectMapper) {
		super(objectMapper);
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz
			, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		return super.readInternal(clazz, this.wrapMessage(inputMessage));
	}

	@Override
	public Object read(Type type, Class<?> contextClass,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		return super.read(type, contextClass, wrapMessage(inputMessage));
	}

	private HttpInputMessage wrapMessage(HttpInputMessage inputMessage) throws IOException {
		System.out.println("Receiving new message body:");
		System.out.println("X-App-Token: " + inputMessage.getHeaders().getFirst(Constants.APP_TOKEN_HTTP_HEADER_NAME));
		return new InnerHttpInputMessage(inputMessage);
	}
	
	
	private static class InnerHttpInputMessage extends InputStream implements HttpInputMessage {
		OutputStream output;
		HttpInputMessage delegator;
		InputStream in;
		
		InnerHttpInputMessage(HttpInputMessage delegator) throws IOException {
			File file = new File("duoshoujioutput");
			if (!file.exists()) {
				file.mkdirs();
			}
			output = new BufferedOutputStream(new FileOutputStream("duoshoujioutput/" + System.currentTimeMillis()));
			this.delegator = delegator;
			this.in = delegator.getBody();
		}
		
		@Override
		public HttpHeaders getHeaders() {
			return delegator.getHeaders();
		}

		@Override
		public InputStream getBody() throws IOException {
			return this;
		}
		
		@Override
		public int read() throws IOException {
			int ch = in.read();
			output.write(ch);
			output.flush();
			return ch;
		}

		@Override
		public int read(byte[] b) throws IOException {
			int read = in.read(b);
			output.write(b, 0, read);
			output.flush();
			return read;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int read = in.read(b, off, len);
			output.write(b, off, read);
			output.flush();
			return read;
		}

		@Override
		public long skip(long n) throws IOException {
			return in.skip(n);
		}

		@Override
		public int available() throws IOException {
			return in.available();
		}

		@Override
		public void close() throws IOException {
			in.close();
			output.close();
		}

		@Override
		public synchronized void mark(int readlimit) {
			in.mark(readlimit);
		}

		@Override
		public synchronized void reset() throws IOException {
			in.reset();
		}

		@Override
		public boolean markSupported() {
			return in.markSupported();
		}
		
	}
	
}
