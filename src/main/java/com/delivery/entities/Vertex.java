package com.delivery.entities;

import java.util.HashMap;
import java.util.Map;

public class Vertex implements Comparable<Vertex> {

    private final String name;    
    private int distance;
    private Vertex previous;
    private final Map<Vertex, Integer> neighbours;

    public Vertex(String name) {
       this.name = name;
       this.distance = Integer.MAX_VALUE;
       this.neighbours = new HashMap<>();
    }
    
    public String getName() {
		return name;
	}
    
    public int getDistance() {
		return distance;
	}
    
    public void setDistance(int distance) {
		this.distance = distance;
	}
    
    public Vertex getPrevious() {
		return previous;
	}
    
    public void setPrevious(Vertex previous) {
		this.previous = previous;
	}
    
    public Map<Vertex, Integer> getNeighbours() {
		return neighbours;
	}

    public int compareTo(Vertex other) {
       return Integer.compare(distance, other.distance);
    }
 }