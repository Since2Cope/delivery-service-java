package com.delivery.entities;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class DeliveryRoute {

	@NotEmpty(message = "name can't be empty")
	private String name;
	
	@NotEmpty(message = "origin can't be empty")
	private String origin;
	
	@NotEmpty(message = "destination can't be empty")
	private String destination;
	
	@NotNull(message = "liter price can't be empty")
	private Double literPrice;
	
	@NotNull(message = "autonomy can't be empty")
	private Double autonomy;
	
	public DeliveryRoute() {
	}
	
	public DeliveryRoute(String name, String origin, String destination, Double literPrice, Double autonomy) {
		this.name = name;
		this.origin = origin;
		this.destination = destination;
		this.literPrice = literPrice;
		this.autonomy = autonomy;
	}
	
	public String getName() {
		return name;
	}
	
	public String getOrigin() {
		return origin == null ? origin : origin.toLowerCase();
	}
	
	public String getDestination() {
		return destination == null ? destination : destination.toLowerCase();
	}
	
	public Double getLiterPrice() {
		return literPrice;
	}
	
	public Double getAutonomy() {
		return autonomy;
	}
	
	public class Path {
		
		private String[] routes = {};
		private double cost = 0.0d;
		
		public Path() {
		}
	
		public double getCost() {
			return cost;
		}
		
		public String[] getRoutes() {
			return routes;
		}
		
		public void setCost(double cost) {
			this.cost = cost;
		}
		
		public void setRoutes(String[] routes) {
			this.routes = routes;
		}
	}
}
