/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {
	//adjacency list for nodes of the graph
	HashMap<GeographicPoint, List<GeographicPoint>> adjList;
	
	//adjacency list for keeping nodes with all adjacent to them edges
	HashMap<GeographicPoint, List<MapEdge>> edgeAdjList;
	
	//variables to keep track of numbers of vertices and edges of the graph
	int numVertices;
	int numEdges;
	
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph()
	{
		// initialize all member variables
		adjList = new HashMap<GeographicPoint, List<GeographicPoint>>();
		edgeAdjList = new HashMap<GeographicPoint, List<MapEdge>>();
		numVertices = 0;
		numEdges = 0;
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		return numVertices;
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		return adjList.keySet();
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges()
	{
		return numEdges;
	}

	
	
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{
		if(adjList.keySet().contains(location)) {
			return false;
		}
		adjList.put(location, new LinkedList<GeographicPoint>());
		edgeAdjList.put(location, new LinkedList<MapEdge>());
		numVertices++;
		return true;
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {
		// perform all checks before adding an edge
		if((length<0)||(from == null)||(to == null)||(roadName == null)||(roadType == null)) {
			throw new IllegalArgumentException("Arguments for adding an edge can't be null or <0");
		}
		if((!adjList.keySet().contains(from))||((!adjList.keySet().contains(to)))) {
			throw new IllegalArgumentException("Vertices should be added to the graph first before adding an edge between them");
		}
		
		//add the edge to both adjacency lists
		addVertexToAdjList(from,to);
		addEdgeToAdjList(from, to, roadName, roadType, length);
		numEdges++;
	}
	
	//add a vertex to nodes adjacency list
	private void addVertexToAdjList(GeographicPoint from, GeographicPoint to) {
		List<GeographicPoint> temp = adjList.get(from);
		temp.add(to);
		adjList.put(from, temp);
	}
	
	//create and add an edge to nodes and edges adjacency list
	private void addEdgeToAdjList(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) {
		
		MapEdge me = new MapEdge(from, to, roadName, roadType, length);
		
		List<MapEdge> temp = edgeAdjList.get(from);
		temp.add(me);
		edgeAdjList.put(from, temp);
	}

	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		//initialize all structures 
		boolean found = false;
		Queue<GeographicPoint> toExplore = new LinkedList<GeographicPoint>();
		HashSet<GeographicPoint> visited = new HashSet<GeographicPoint>();
		HashMap<GeographicPoint, GeographicPoint> parentMap = new HashMap<GeographicPoint, GeographicPoint>();
		
		//BFS
		toExplore.add(start);
		while(!toExplore.isEmpty()) {
			GeographicPoint curr =  toExplore.poll();
			if(curr.equals(goal)) {
				found = true;
				break;
			}
			for(GeographicPoint gp: adjList.get(curr)) {
				if(!visited.contains(gp)) {
					visited.add(gp);
					parentMap.put(gp, curr);
					toExplore.add(gp);
					//nodeSearched.accept(gp.getLocation());
				}
			}
		}
		
		//if there's no path from start to goal,
		//return empty list
		if(!found) {
			System.out.println("Path not found");
			return new LinkedList<GeographicPoint>();
		}
		
		return reconstructPath(start, goal, parentMap);
	}
	
	//reconstructing path from the parents map
	private List<GeographicPoint> reconstructPath(GeographicPoint start, GeographicPoint goal, HashMap<GeographicPoint, GeographicPoint> parentMap){
		GeographicPoint curr = goal;
		LinkedList<GeographicPoint> path = new LinkedList<GeographicPoint>();
		while(curr!=start) {
			path.addFirst(curr);
			curr = parentMap.get(curr);
		}
		path.addFirst(start);
		
		return path;
	}
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// TODO: Implement this method in WEEK 4

		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		return null;
	}

	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// TODO: Implement this method in WEEK 4
		
		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		return null;
	}

	// for printing out graph adjacency list  
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("--------------- NODES ---------------\n");
		for(GeographicPoint gp: adjList.keySet()) {
			s.append(gp.toString()+" -> ");
			for(GeographicPoint adjgp: adjList.get(gp)) {
				s.append(adjgp.toString()+ "; ");
			}
			s.append("\n");
		}
		
		s.append("--------------- EDGES ---------------\n");
		for(GeographicPoint gp: edgeAdjList.keySet()) {
			s.append(gp.toString()+" -> ");
			for(MapEdge edge: edgeAdjList.get(gp)) {
				s.append(edge.toString()+ "; ");
			}
			s.append("\n");
		}
		s.append("numVertices = "+ numVertices+" numEdges = "+numEdges);
		return s.toString();
	}
	
	
	public static void main(String[] args)
	{
		System.out.print("Making a new map...");
		MapGraph firstMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", firstMap);
		System.out.println("DONE.");
		System.out.println(firstMap);
		// You can use this method for testing.  
		
		GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
		GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);
		
		System.out.println("Test 1 using bfs: ");
		List<GeographicPoint> testroute = firstMap.bfs(testStart,testEnd);
		System.out.println(testroute);
		
		/* Here are some test cases you should try before you attempt 
		 * the Week 3 End of Week Quiz, EVEN IF you score 100% on the 
		 * programming assignment.
		 */
		/*
		MapGraph simpleTestMap = new MapGraph();
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);
		
		GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
		GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);
		
		System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
		List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart,testEnd);
		List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart,testEnd);
		
		
		MapGraph testMap = new MapGraph();
		GraphLoader.loadRoadMap("data/maps/utc.map", testMap);
		
		// A very simple test using real data
		testStart = new GeographicPoint(32.869423, -117.220917);
		testEnd = new GeographicPoint(32.869255, -117.216927);
		System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		
		
		// A slightly more complex test using real data
		testStart = new GeographicPoint(32.8674388, -117.2190213);
		testEnd = new GeographicPoint(32.8697828, -117.2244506);
		System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		*/
		
		
		/* Use this code in Week 3 End of Week Quiz */
		/*MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		
		List<GeographicPoint> route = theMap.dijkstra(start,end);
		List<GeographicPoint> route2 = theMap.aStarSearch(start,end);

		*/
		
	}
	
}
