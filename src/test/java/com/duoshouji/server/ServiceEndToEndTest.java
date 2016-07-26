package com.duoshouji.server;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServiceEndToEndTest {
	
	private static final String TEST_ACCOUNT_ID = "13661863279";
	private static final String TEST_PASSWORD = "pwd";
	
	private HttpServer server;
	private WebTarget target;
	@Before
	public void start() {
		server = Main.startServer();
		target = ClientBuilder.newClient().target(Main.BASE_URI);
	}
	
	@SuppressWarnings("deprecation")
	@After
	public void stop() {
		server.stop();
	}
	
	@Test
	public void loginByUserNameAndPassword() {
		final Form form = new Form().param("account", TEST_ACCOUNT_ID).param("password", TEST_PASSWORD);
		Response response = target.path("login/authenticate/credential").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertNotNull(response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME));
	}
	
	@Test
	public void loginByVerificationCode() {
		Form form;
		form = new Form().param("account", TEST_ACCOUNT_ID).param("purpose", "login");
		Response response = target.path("message/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		
		form = new Form().param("account", TEST_ACCOUNT_ID).param("code", "");
		response = target.path("/login/authenticate/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertNotNull(response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME));		
	}
	
	@Test
	public void createPasswordWhenFirstLoginByVerificationCode() {
		Form form;
		form = new Form().param("account", TEST_ACCOUNT_ID).param("purpose", "login");
		Response response = target.path("message/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		
		form = new Form().param("account", TEST_ACCOUNT_ID).param("code", "");
		response = target.path("/login/authenticate/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(203, response.getStatus());
		Assert.assertNotNull(response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME));
		
		form = new Form().param("account", TEST_ACCOUNT_ID).param("password", TEST_PASSWORD);
		response = target.path("login/authenticate/credential").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertNull(response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME));
	}
}
