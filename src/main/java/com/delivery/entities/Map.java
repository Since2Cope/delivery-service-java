package com.delivery.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "maps")
public class Map {
	
	@DatabaseField(generatedId = true)
	private int id;

	@NotEmpty(message = "name can't be empty")
	@DatabaseField(canBeNull = false, unique = true)
	private String name;
	
	@Valid
	@ForeignCollectionField
	private Collection<Route> routes;
	
	public Map() {
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Route> getRoutes() {
		return new ArrayList<Route>(routes);
	}
	
	public void setRoutes(Collection<Route> routes) {
		this.routes = routes;
	}
}
