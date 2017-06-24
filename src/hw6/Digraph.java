package hw6;

import java.util.Iterator;
import java.util.LinkedList;

public class Digraph {
	private int V;                         // number of vertices
	private int E;                         // number of edges
	private LinkedList<Integer>[] adjlist; // adjecency lists
	private boolean[] marked; // mark
	private Stack<Integer>path; //path

	// creates a graph with V vertices and 0 edges 
	@SuppressWarnings("unchecked")
	public Digraph(int V) {
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
	
	// returns the reverse of this graph which is:
	// the graph on the same vertices with the same edges except that each edge is reversed in direction  
	public Digraph reverse() {
		// TODO
		 Digraph reverseinDirection = new Digraph(V);
	        for (int v = 0; v < V; v++) {
	            for (int w : adj(v)) {
	            	reverseinDirection.addEdge(w, v);
	            }
	        }
	        return reverseinDirection;
	}
	
	// returns the complement of this graph which is:
	// the graph on the same vertices containing all of the edges that are not in this graph   
	public Digraph complement() {
		// TODO
		Digraph complement = new Digraph(V);
		for(int v = 0; v < V; v++){
			for(int i = 0; i < V; i++){
				if(i != v && !adjlist[v].contains(i)){
					complement.addEdge(v, i);
				}
			}
		}
		return complement;
	}
	
	// determines whether the given iterable list of vertices is a topological sort or not
	public boolean isTopological(Iterable<Integer> sort) {
		// TODO
		LinkedList<Integer> listSort = new LinkedList<Integer>();
		for(Iterator <Integer> i = sort.iterator(); i.hasNext();){
			listSort.add(i.next());
		}
		if(listSort.size()!= V)return false;
		for(int v = 0; v < V; v++){
			for(int w: adj(listSort.get(v))){
				for(int x = 0; x < v; x++){
					if(listSort.get(x) == w) return false;
				}
			}
		}
		return true;
	}
	
	// finds and returns a path from v to w, if there exists one, or returns null otherwise
	public Iterable<Integer> pathTo(int v, int w) {
		// TODO
		//return null;
		marked = new boolean[V];
		return dfs(v,w);
	}
	private Stack<Integer>dfs(int v, int x){
		Stack<Integer>pathTo;
		marked[v] = true;
		for(int w: adj(v)){
			if(!marked[w]){
				if (w==x){
					pathTo = new Stack<Integer>();
					pathTo.push(x);
					pathTo.push(v);
					return pathTo;
			}
				pathTo = dfs(w,x);
				if(pathTo !=null){
					pathTo.push(v);
					return pathTo;
				}
			}	
		}
		return null;
	}
	// finds a directed cycle that contains the given two vertices v and w (if there exists one)
	// otherwise (if there are no cycles between v and w), this method returns null 
	public Iterable<Integer> cycleBetween(int v, int w) {
		// TODO
		Stack<Integer> firstdirectedCycle = (Stack<Integer>) pathTo(v,w);
		Stack<Integer> seconddirectedCycle = (Stack <Integer>) pathTo(w,v);
		
		if (firstdirectedCycle != null && seconddirectedCycle != null){
			LinkedList<Integer> path = new LinkedList<Integer>();
			for (int i: firstdirectedCycle)
				path.add(i);
			for (int x: seconddirectedCycle)
				if(x!= w && x!= v )
					path.add(x);
			return path;
			
		}
		return null;
	}
	// returns a topological sort if this graph is a DAG, and returns null if this graph is not a DAG
	public Iterable<Integer> topologicalSort() {
	    // TODO
		marked = new boolean[V];
		path = new Stack<Integer>();
		for(int v = 0; v < V; v++){
			if(!marked[v]) 
				dfs(v);
		}
		if(isTopological(path)) {
			return path;
		}
			//return path;
	    return null;
	}
	
	public void dfs(int v){
		marked[v] = true;
		for(int w: adjlist[v])
			if(!marked[w]) 
				dfs(w);
		path.push(v);
	}
}