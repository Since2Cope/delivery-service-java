package com.delivery.resources;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.validation.ValidationError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.delivery.Main;
import com.delivery.entities.DeliveryRoute;
import com.delivery.entities.Map;
import com.delivery.entities.Route;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MapResourceTest {

    private HttpServer server;
    private WebTarget target;
    
    private static final String DATABASEURL = "jdbc:sqlite:delivey_test.db";
    
    @Before
    public void setUp() throws Exception {
    	Main.DATABASEURL = DATABASEURL;

    	server = Main.startServer();

        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
    	dropTables();
        server.stop();
    }

    @Test
    public void testGetMaps() throws SQLException {
    	Map map = createMapWithRoutes();
    	
         List<Map> maps = target.path("maps").request()
        		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
        		 .get(new GenericType<List<Map>>(){});
                 
        Map resultMap = maps.get(0);

        assertEquals(map.getName(), resultMap.getName());
        assertEquals(map.getRoutes().size(), resultMap.getRoutes().size());
    }
    
    @Test
    public void testGetMapsWhenThereIsNoMap() throws SQLException {
        List<Map> maps = target.path("maps").request()
        		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
        		 .get(new GenericType<List<Map>>(){});
                 
        assertEquals(maps.isEmpty(), true);
    }
    
    
    @Test
    public void testPostMaps() {
    	Map map = buildMapWithRoutes();
    	
        Response response = target.path("maps").request()
       		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
       		 .post(Entity.entity(map, MediaType.APPLICATION_JSON));
        
        Map responseEntity = response.readEntity(new GenericType<Map>(){});
        
        assertEquals(201, response.getStatus());
        assertEquals(map.getName(), responseEntity.getName());
        assertEquals(map.getRoutes().size(), responseEntity.getRoutes().size());
    }
    
    @Test
    public void testPostMapsWhenAttributesAreEmpty() {
    	Map map = buildMapWithRoutes();
    	map.setName("");
    	map.getRoutes().get(0).setOrigin("");
    	
        Response response = target.path("maps").request()
       		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
       		 .post(Entity.entity(map, MediaType.APPLICATION_JSON));
        
        List<ValidationError> errors = response.readEntity(new GenericType<List<ValidationError>>(){});

        assertEquals(400, response.getStatus());
        assertEquals(2, errors.size());
    }

    @Test
    public void testPostMapsWhenMapAlreadyExists() {
    	Map map = buildMapWithRoutes();
    	
        target.path("maps").request()
       		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
       		 .post(Entity.entity(map, MediaType.APPLICATION_JSON));
        
        Response response = target.path("maps").request()
          		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
          		 .post(Entity.entity(map, MediaType.APPLICATION_JSON));
        
        assertEquals(422, response.getStatus());
    }
    
    @Test
    public void testEstimateDelivery() throws SQLException {
    	Map map = createMapWithRoutes();
    	
    	DeliveryRoute deliveryRoute = new DeliveryRoute(map.getName(), "a", "c", 2.5d, 10d);
    	
        Response response = target.path("maps").path("estimate_delivery").request()
  		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
  		 .post(Entity.entity(deliveryRoute, MediaType.APPLICATION_JSON));
        
        assertEquals(200, response.getStatus());
        assertEquals("{\"routes\":[\"a\",\"b\",\"d\",\"e\",\"c\"],\"cost\":3.5}", response.readEntity(String.class));
    }
    
    @Test
    public void testEstimateDeliveryWhenAttributesAreEmpty() throws SQLException {    	
    	DeliveryRoute deliveryRoute = new DeliveryRoute();
    	
        Response response = target.path("maps").path("estimate_delivery").request()
  		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
  		 .post(Entity.entity(deliveryRoute, MediaType.APPLICATION_JSON));
                
        List<ValidationError> errors = response.readEntity(new GenericType<List<ValidationError>>(){});

        assertEquals(400, response.getStatus());
        assertEquals(5, errors.size());
    }
    
    @Test
    public void testEstimateDeliveryWhenThereIsNoRoute() throws SQLException {
    	Map map = createMapWithRoutes();
    	
    	DeliveryRoute deliveryRoute = new DeliveryRoute(map.getName(), "a", "Z", 2.5d, 10d);
    	
        Response response = target.path("maps").path("estimate_delivery").request()
  		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
  		 .post(Entity.entity(deliveryRoute, MediaType.APPLICATION_JSON));
        
        assertEquals(200, response.getStatus());
        assertEquals("{\"routes\":[],\"cost\":0.0}", response.readEntity(String.class));
    }
    
    @Test
    public void testEstimateDeliveryWhenThereIsNoMap() {
    	DeliveryRoute deliveryRoute = new DeliveryRoute("No Map", "a", "c", 2.5d, 10d);
    	
        Response response = target.path("maps").path("estimate_delivery").request()
  		 .accept(MediaType.APPLICATION_JSON, "application/vnd.delivery.v1")
  		 .post(Entity.entity(deliveryRoute, MediaType.APPLICATION_JSON));
        
        assertEquals(200, response.getStatus());
        assertEquals("{\"routes\":[],\"cost\":0.0}", response.readEntity(String.class));
    }
    
    private void dropTables() throws SQLException {
    	JdbcConnectionSource connectionSource = getConnectionSource();
    	TableUtils.dropTable(connectionSource, Map.class, true);
    	TableUtils.dropTable(connectionSource, Route.class, true);
    	connectionSource.close();
    }
    
    
    private Map createMapWithRoutes() throws SQLException {
    	Map map = buildMapWithRoutes();
    	getMapDao(getConnectionSource()).create(map);
		
		for (Route route : map.getRoutes()) {
	    	getRouteDao(getConnectionSource()).create(route);
		}
		
		return map;
    }
    
    private Map buildMapWithRoutes() {
    	Map map = new Map();
    	map.setName("Mapa Teste");
    	    	
		List<Route> routes = new ArrayList<>();
		routes.add(new Route("A", "B", 10, map));
		routes.add(new Route("B", "C", 5, map));
		routes.add(new Route("B", "D", 1, map));
		routes.add(new Route("D", "E", 2, map));
		routes.add(new Route("E", "C", 1, map));
		
		map.setRoutes(routes);
		
		return map;
    }
    
    private JdbcConnectionSource getConnectionSource() throws SQLException {
		JdbcConnectionSource connectionSource = new JdbcConnectionSource(DATABASEURL);
		return connectionSource;
    }
    
    private Dao<Map, String> getMapDao(JdbcConnectionSource connectionSource) throws SQLException {    	
		Dao<Map, String> mapDao = DaoManager.createDao(connectionSource, Map.class);
		return mapDao;
    }
    
    private Dao<Route, String> getRouteDao(JdbcConnectionSource connectionSource) throws SQLException {    	
		Dao<Route, String> routeDao = DaoManager.createDao(connectionSource, Route.class);
		return routeDao;
    }
}
