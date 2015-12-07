package com.delivery.entities;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "routes")
public class Route {

	@DatabaseField(generatedId = true)
	private int id;
	
	@NotEmpty(message = "origin can't be empty")
	@DatabaseField(canBeNull = false, uniqueCombo = true)
	private String origin;
	
	@NotEmpty(message = "destination can't be empty")
	@DatabaseField(canBeNull = false, uniqueCombo = true)
	private String destination;
	
	@NotNull(message = "distance can't be empty")
	@DatabaseField(canBeNull = false)
	private Integer distance;

	@DatabaseField(canBeNull = false, foreign = true, uniqueCombo = true)
	private Map map;
	
	public Route() {
	}
	
    public Route(String origin, String destination, int distance) {
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
    }
    
    public Route(String origin, String destination, int distance, Map map) {
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.map = map;
    }

	public int getId() {
		return id;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	
	public void setMap(Map map) {
		this.map = map;
	}
	
	@JsonIgnore
	public Map getMap() {
		return map;
	}
}
