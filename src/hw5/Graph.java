package hw5;

import java.util.Arrays;
import java.util.LinkedList;

public class Graph {
	private int V;                         // number of vertices
	private int E;                         // number of edges
	private LinkedList<Integer>[] adjlist; // adjecency lists
	private boolean isBipartite; // is the graph bipartite?
	private boolean[] color; // color[v] gives vertices on one side of bipartition
	private boolean marked[]; // marked[v] = true if v has been visited in DFS
	private boolean cycle; 
	// creates a graph with V vertices and 0 edges 
	@SuppressWarnings("unchecked")
	public Graph(int V) {
		this.V = V;
		adjlist = (LinkedList<Integer>[])new LinkedList[V];
		for (int v = 0; v < V; v++) {
			adjlist[v] = new LinkedList<Integer>();
		}
	}
	
	// returns the number of vertices in this graph
	public int V() {
		return V;
	}
	
	// returns the number of edges in this graph
	public int E() {
		return E;
	}
	
	// adds an edge to this graph
	public void addEdge(int v, int w) {
		adjlist[v].add(w);
		adjlist[w].add(v);
		E++;
	}
	
	// returns the list of vertices adjacency to v
	public Iterable<Integer> adj(int v) {
		return adjlist[v];
	}
	
	// does this graph contain the edge (v,w)?
	public boolean containsEdge(int v, int w) {
		return adjlist[v].contains(w);
	}
	
	// adds a new vertex into the graph without any edges
	public void addVertex() {
		// TODO
		if(adjlist.length ==V){
			adjlist = Arrays.copyOf(adjlist, adjlist.length + 1); // resize by incrementing
			}
		adjlist[V] = new LinkedList<Integer>();
		V++;
	}
	
	// returns the adjacency matrix for this graph 
	public boolean[][] adjMatrix() {
		// TODO
		boolean[][] adjacencyMatrix = new boolean [V][V]; // new 
		for (int x = 0; x < V; x++){
			for (int y = 0; y < V; y++){
				adjacencyMatrix [x][y] = adjlist[x].contains(y);
				}
		}
			return adjacencyMatrix;
				
		//return null;
	}
	
	// returns true if and only if the graph contains a three cycle (triangle)
	public boolean containsTriangle() {
		// TODO
		for(int x = 0; x < V; x++){
			for(int y = 0; y < V; y++){
				for(int z = 0; z < V; z++){
					if(x != y && z != y && containsEdge(x,y) == true && containsEdge(y,z) == true && containsEdge (x,z)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	// returns true if and only if the graph contains a cycle (of arbitrary length)
	public boolean containsCycle() {
		// TODO
		marked = new boolean[V];
		cycle = false;
		for(int v=0; v < V; v++){
			if(!marked[v]){
				dfs(-1, v);
			}
		}
		return cycle;
	}
	
	public void dfs(int x, int v){
		marked[v] = true;
		for(int w: adjlist[v]){
			if(!marked[w]){
				dfs(v, w);
			} else if(w!=x){
				cycle = true;
			}
		}
	}
	
	// returns true if and only if the graph is bipartite
	// equivalently, returns true if and only if the graph contains an odd-length cycle
	public boolean isBipartite() {
		// TODO
		marked = new boolean[V];
		 
		color = new boolean[V];
		isBipartite =  true;
		for(int v=0; v < V; v++){
			if(!marked[v])  dfs(v);
		}
		return isBipartite;
	}
	private void dfs(int v){
		marked[v] = true;
		for(int w: adjlist[v]){
			if(!marked[w]){
				color[w]=! color[v];
				dfs(w);
			} else 
				if(color[w]== color[v]){
				isBipartite = false;
			}
		}
	}
}