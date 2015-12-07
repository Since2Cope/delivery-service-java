package com.delivery.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.delivery.entities.DeliveryRoute;
import com.delivery.entities.Route;
import com.delivery.entities.Vertex;
 
public class DeliveryRouteService {

   private final Map<String, Vertex> vertexesMap;
 
   public DeliveryRouteService(List<Route> routes) {
      vertexesMap = new HashMap<>(routes.size());
 
      for (Route route : routes) {
    	  String origin = route.getOrigin().toLowerCase();
    	  String destination = route.getDestination().toLowerCase();
    	  
         if (!vertexesMap.containsKey(origin)) {
        	 vertexesMap.put(origin, new Vertex(origin));
         }
         
         if (!vertexesMap.containsKey(destination)) { 
        	 vertexesMap.put(destination, new Vertex(destination));
         }
      }
 
      for (Route route : routes) {
    	  String origin = route.getOrigin().toLowerCase();
    	  String destination = route.getDestination().toLowerCase();
    	  
         vertexesMap.get(origin).getNeighbours().put(vertexesMap.get(destination), route.getDistance());
      }
   }
   
   
   public DeliveryRoute.Path economicPath(DeliveryRoute deliveryRoute) {
	   DeliveryRoute.Path path = deliveryRoute.new Path();
	   
	   String origin = deliveryRoute.getOrigin();
	   String destination = deliveryRoute.getDestination();
	   
	   if (vertexesMap.get(origin) != null && vertexesMap.get(destination) != null) {
		   estimateVertexesDistance(origin);

		   LinkedList<String> routes = new LinkedList<String>();
		   vertexesMap.get(destination);
		   
		   while (!destination.equals(origin)) {
			   routes.addFirst(destination);
			   destination = vertexesMap.get(destination).getPrevious().getName();
		   }

		   if (!routes.isEmpty()) {
			   routes.addFirst(origin);
			   path.setRoutes(routes.toArray(new String[routes.size()]));
		   }

		   path.setCost((vertexesMap.get(deliveryRoute.getDestination()).getDistance() * deliveryRoute.getLiterPrice()) / deliveryRoute.getAutonomy());
	   }
	   
	   return path;
   }
  
   private void estimateVertexesDistance(String origin) {
      vertexesMap.get(origin).setPrevious(vertexesMap.get(origin));
      vertexesMap.get(origin).setDistance(0);
      
      NavigableSet<Vertex> vertexes = new TreeSet<>(vertexesMap.values());
      Vertex vertex, neighbourVertex;

      while (!vertexes.isEmpty()) {
         vertex = vertexes.pollFirst();

         if (vertex.getDistance() == Integer.MAX_VALUE) {
        	 break;
         }
 
         for (Map.Entry<Vertex, Integer> neighbour : vertex.getNeighbours().entrySet()) {
            neighbourVertex = neighbour.getKey();
 
            final int originDestinationDistance = vertex.getDistance() + neighbour.getValue();

            if (originDestinationDistance < neighbourVertex.getDistance()) {
               vertexes.remove(neighbourVertex);
               neighbourVertex.setDistance(originDestinationDistance);
               neighbourVertex.setPrevious(vertex);
               vertexes.add(neighbourVertex);
            }
         }
      }
   }
}