package hu.elte.szakdolgozat.rest.server;

import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RestServer {

    public static final String BASE_URI = "http://localhost:8080/tutkoim/";

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("hu.elte");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.in.read();
        server.stop();
    }
}
