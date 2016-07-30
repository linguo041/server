package com.duoshouji.server;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.session.SessionManager;

import junit.framework.Assert;

public class ServiceEndToEndTest {
	
	private static final String TEST_ACCOUNT_ID = "13661863279";
	private static final String TEST_PASSWORD = "pwd";
	
	private HttpServer server;
	private WebTarget target;
	private MockLoginFacade loginFacade;
	private MockSessionManager sessionManager;
	
	@Before
	public void start() {
		loginFacade = MockLoginFacade.getInstance();
		sessionManager = MockSessionManager.getInstance();
		
		final ResourceConfig rc = new ResourceConfig().packages("com.duoshouji.server");
        manageCustomDependencyInjection(rc);
		server = Main.startServer(rc);
		target = ClientBuilder.newClient().target(Main.BASE_URI);
	}
	
    private static void manageCustomDependencyInjection(final ResourceConfig rc) {
//    	rc.register(new AbstractBinder(){
//			@Override
//			protected void configure() {
//				bindFactory(LoginFacadeFactory.class)
//					.to(LoginFacade.class)
//					.proxy(true)
//					.proxyForSameScope(false)
//					.in(RequestScoped.class);
//				bindFactory(MockSessionManagerFactory.class)
//				.to(SessionManager.class)
//				.proxy(true)
//				.proxyForSameScope(false)
//				.in(RequestScoped.class);
//			}
//    	});
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
		final String returnedToken = response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME);
		Assert.assertEquals(sessionManager.findToken(TEST_ACCOUNT_ID), returnedToken);
	}
	
	@Test
	public void loginByVerificationCode() {
		Form form;
		form = new Form().param("account", TEST_ACCOUNT_ID).param("purpose", "login");
		Response response = target.path("message/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		
		form = new Form().param("account", TEST_ACCOUNT_ID).param("code", loginFacade.findVerificationCode(TEST_ACCOUNT_ID));
		response = target.path("/login/authenticate/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		final String returnedToken = response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME);
		Assert.assertEquals(sessionManager.findToken(TEST_ACCOUNT_ID), returnedToken);
	}
	
	@Test
	public void loginWithSecondVerificationCode() {
		Form form;
		form = new Form()
				.param("account", TEST_ACCOUNT_ID)
				.param("purpose", "login");
		WebTarget t = target.path("message/verification-code");
		t.request().post(Entity.form(form)).getStatus();
		final String code1 = loginFacade.findVerificationCode(TEST_ACCOUNT_ID);
		t.request().post(Entity.form(form)).getStatus();
		final String code2 = loginFacade.findVerificationCode(TEST_ACCOUNT_ID);
		Assert.assertFalse(code1.equals(code2));
		
		form = new Form().param("account", TEST_ACCOUNT_ID).param("code", code2);
		Response response = target.path("/login/authenticate/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		final String returnedToken = response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME);
		Assert.assertEquals(sessionManager.findToken(TEST_ACCOUNT_ID), returnedToken);
	}

}
