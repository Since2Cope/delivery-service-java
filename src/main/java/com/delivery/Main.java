package com.delivery;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import com.delivery.entities.Map;
import com.delivery.entities.Route;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class Main {

	public static final String BASE_URI = "http://0.0.0.0:8080/api/";
    public static String DATABASEURL = "jdbc:sqlite:delivey.db";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * Creates the base datasets to persist data.
     * 
     * @return Grizzly HTTP server.
     * @throws SQLException 
     */
    public static HttpServer startServer() throws SQLException {
    	JdbcConnectionSource connectionSource = new JdbcConnectionSource(DATABASEURL);

    	// creates the 'maps' and 'routes' tables
    	TableUtils.createTableIfNotExists(connectionSource, Map.class);
    	TableUtils.createTableIfNotExists(connectionSource, Route.class);

    	connectionSource.close();

        // create a resource config that scans for JAX-RS resources and providers
        // in com.delivery package
        final ResourceConfig rc = new ResourceConfig().packages("com.delivery");
        rc.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        rc.property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
        rc.register(JacksonFeature.class);
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException, SQLException {
        startServer();
    }
}

