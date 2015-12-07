package com.delivery.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.delivery.Main;
import com.delivery.entities.DeliveryRoute;
import com.delivery.entities.Map;
import com.delivery.entities.Route;
import com.delivery.services.DeliveryRouteService;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;

@Path("maps")
@Consumes({MediaType.APPLICATION_JSON, "application/vnd.delivery.v1"})
@Produces({MediaType.APPLICATION_JSON, "application/vnd.delivery.v1"})
public class MapResource {
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	@POST
	public Response create(@Valid final Map map) {
		try {
			createMapAndRoutes(map);
		} catch (SQLException sqlException) {
			return Response.status(422).entity(sqlException.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		
		UriBuilder fromResource = UriBuilder.fromResource(MapResource.class);
		return Response.created(fromResource.path(String.valueOf(map.getId())).build()).entity(map).build();
	}

	/**
	 * 
	 * @param deliveryRoute
	 * @return
	 */
	@POST
	@Path("/estimate_delivery")
	public Response estimateDelivery(@Valid DeliveryRoute deliveryRoute) {

		List<Route> routes = new ArrayList<Route>();
		
		try {
			Dao<Map, String> mapDao = getMapDao(getConnectionSource());
			PreparedQuery<Map> query = mapDao.queryBuilder().where().like("name", deliveryRoute.getName()).prepare();
			
			Map map = mapDao.queryForFirst(query);

			if (map != null) {
				routes = map.getRoutes();	
			}
		} catch (SQLException sqlException) {
			return Response.status(500).entity(sqlException.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		
		DeliveryRouteService deliveryRouteService = new DeliveryRouteService(routes);
		com.delivery.entities.DeliveryRoute.Path economicPath = deliveryRouteService.economicPath(deliveryRoute);
		
		return Response.ok(economicPath).build();
	}

	/**
	 * 
	 * @return
	 */
    @GET
    public Response getMap() {
    	List<Map> maps = new ArrayList<Map>();

    	try {
			maps = getMapDao(getConnectionSource()).queryForAll();
		} catch (SQLException sqlException) {
			return Response.status(500).entity(sqlException.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
    	
    	return Response.ok().entity(maps).build();
    }
    
    private JdbcConnectionSource getConnectionSource() throws SQLException {
		JdbcConnectionSource connectionSource = new JdbcConnectionSource(Main.DATABASEURL);
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
    
    private void createMapAndRoutes(final Map map) throws SQLException {
    	final JdbcConnectionSource connectionSource = getConnectionSource();
		TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {

			@Override
			public Void call() throws Exception {
		        getMapDao(connectionSource).create(map);

	        	Dao<Route, String> routeDao = getRouteDao(connectionSource);
				for (Route route : map.getRoutes()) {
					route.setMap(map);
					routeDao.create(route);
				}
				return null;
	        }
		 });
    }
}
