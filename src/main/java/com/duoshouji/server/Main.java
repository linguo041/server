package com.duoshouji.server;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import com.duoshouji.server.rest.di.UserFacadeFactory;
import com.duoshouji.server.user.UserFacade;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:80/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.duoshouji.server package
        final ResourceConfig rc = new ResourceConfig().packages("com.duoshouji.server");
        manageCustomDependencyInjection(rc);
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    private static void manageCustomDependencyInjection(final ResourceConfig rc) {
    	rc.register(new AbstractBinder(){
			@Override
			protected void configure() {
				bindFactory(UserFacadeFactory.class)
					.to(UserFacade.class)
					.proxy(true)
					.proxyForSameScope(false)
					.in(RequestScoped.class);
			}
    	});
    }

    
    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

