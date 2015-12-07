package com.delivery.services;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.delivery.entities.DeliveryRoute;
import com.delivery.entities.DeliveryRoute.Path;
import com.delivery.entities.Route;

public class DeliveryRouteServiceTest {

	@Test
    public void testEconomicPath() {
		List<Route> routes = new ArrayList<>();
		routes.add(new Route("A", "B", 10));
		routes.add(new Route("B", "C", 5));
		routes.add(new Route("B", "D", 1));
		routes.add(new Route("D", "E", 2));
		routes.add(new Route("E", "C", 1));
		
		DeliveryRoute deliveryRoute = new DeliveryRoute("Mapa SP", "a", "c", 2.5d, 10d);
		
		DeliveryRouteService deliveryRouteService = new DeliveryRouteService(routes);
		Path economicPath = deliveryRouteService.economicPath(deliveryRoute);
		
        assertEquals(3.5d, economicPath.getCost(), 0);
        assertTrue(Arrays.equals(new String[]{"a", "b", "d", "e", "c"}, economicPath.getRoutes()));
	}
	
	@Test
    public void testEconomicPathWhenThereIsNoMap() {
		DeliveryRoute deliveryRoute = new DeliveryRoute("Mapa SP", "a", "c", 2.5d, 10d);
		
		DeliveryRouteService deliveryRouteService = new DeliveryRouteService(new ArrayList<Route>());
		Path economicPath = deliveryRouteService.economicPath(deliveryRoute);
		
        assertEquals(0.0d, economicPath.getCost(), 0);
        assertTrue(Arrays.equals(new String[]{}, economicPath.getRoutes()));
	}
    
	@Test
    public void testEconomicPathWhenRouteIsNotMapped() {
		List<Route> routes = new ArrayList<>();
		routes.add(new Route("A", "B", 10));
		routes.add(new Route("B", "C", 5));
		routes.add(new Route("B", "D", 1));
		routes.add(new Route("D", "E", 2));
		routes.add(new Route("E", "C", 1));
		
		DeliveryRoute deliveryRoute = new DeliveryRoute("Mapa SP", "z", "c", 2.5d, 10d);
		
		DeliveryRouteService deliveryRouteService = new DeliveryRouteService(routes);
		Path economicPath = deliveryRouteService.economicPath(deliveryRoute);
		
        assertEquals(0.0d, economicPath.getCost(), 0);
        assertTrue(Arrays.equals(new String[]{}, economicPath.getRoutes()));
	}
}
