package com.duoshouji.server;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.session.SessionManager;

public class ServiceEndToEndTest {
	
	private HttpServer server;
	private WebTarget target;
	
	@Before
	public void start() {
		
		final ResourceConfig rc = new ResourceConfig().packages("com.duoshouji.server");
        manageCustomDependencyInjection(rc);
		server = Main.startServer(rc);
		target = ClientBuilder.newClient().target(Main.BASE_URI);
	}
	
    private static void manageCustomDependencyInjection(final ResourceConfig rc) {
    	rc.register(new AbstractBinder(){
			@Override
			protected void configure() {
				bindFactory(MockLoginFacadeFactory.class)
					.to(LoginFacade.class);
				bindFactory(MockSessionManagerFactory.class)
					.to(SessionManager.class);
				
			}
    	});
    }

	@SuppressWarnings("deprecation")
	@After
	public void stop() {
		server.stop();
	}
	
	@Test
	public void loginByUserNameAndPassword() {
		final Form form = new Form()
			.param("account", MockConstants.MOCK_USER_IDENTIFIER.toString())
			.param("password", MockConstants.MOCK_PASSWORD.toString());
		Response response = target.path("login/authenticate/credential").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		final String returnedToken = response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME);
		Assert.assertEquals(MockSessionManager.getInstance().findToken(MockConstants.MOCK_USER_IDENTIFIER.toString()), returnedToken);
	}
	
	@Test
	public void loginByVerificationCode() {
		Form form;
		form = new Form()
			.param("account", MockConstants.MOCK_USER_IDENTIFIER.toString())
			.param("purpose", "LOGIN");
		Response response = target.path("message/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		
		form = new Form()
			.param("account", MockConstants.MOCK_USER_IDENTIFIER.toString())
			.param("code", MockMessageSender.INSTANCE.findHistory(MockConstants.MOCK_USER_IDENTIFIER).toString());
		response = target.path("/login/authenticate/verification-code").request().post(Entity.form(form));
		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		final String returnedToken = response.getHeaderString(Contants.APP_TOKEN_HTTP_HEADER_NAME);
		Assert.assertEquals(MockSessionManager.getInstance().findToken(MockConstants.MOCK_USER_IDENTIFIER.toString()), returnedToken);
	}
}
