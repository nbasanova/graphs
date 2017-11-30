package roadgraph;

import geography.GeographicPoint;

public class MapEdge {
	private GeographicPoint start;
	private GeographicPoint end;
	private String name;
	private String roadtype;
	private double length;
	
	//constructor
	public MapEdge(GeographicPoint start, GeographicPoint end, String name, String roadtype, double length) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.roadtype = roadtype;
		this.length = length;
	}
	
	//getters for all member variables 
	public GeographicPoint getStart() {
		return this.start;
	}
	
	public GeographicPoint getEnd() {
		return this.end;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.roadtype;
	}
	
	public double getLength() {
		return this.length;
	}
	
	//for printing out the edges 
	public String toString() {
		return name+" from "+start.toString()+" to "+end.toString();
	}
}
