package hw6;

import org.junit.Test;

import hw5.Queue;

import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

public class HW6Test {
	String border = "*******************************************\n";
	String passed = "* Passed!                                 *\n";
	String failed = "* Failed!                                 *\n";
	String test;
	
	AssertionError ae;
	Exception e;
	
	int p, seed, prob;
	Digraph gr, lastgr, comp, retgr;
	boolean[] marked;
	int[] id;
	int count;
	Stack<Integer> revPost;
	Iterable<Integer> ret;
	LinkedList<Integer> topo;
	
	public static class Edge implements Comparable<Edge> {
		public static final Comparator<Edge> DEF = Comparator.naturalOrder();
		public final int v, w; // Vertices of this undirected edge
		
		// Constructor for an edge (no self-loops allowed)
		public Edge(int v, int w) {
			if (v < 0 || w < 0 || v == w) {
				throw new IllegalArgumentException("Vertices of an edge must be different nonnegative integers, v = " + v + ", w = " + w);
			} else {
				this.v = v;
				this.w = w;
			}
		}
		
		@Override
		// Returns true iff the given object describes this edge
		public boolean equals(Object o) {
			if (!(o instanceof Edge)) return false;
			Edge e = (Edge) o;
			return v == e.v && w == e.w;
		}

		@Override
		// Compares the given object against this object according to lexicographic order
		public int compareTo(Edge e) {
			return (e.v == v) ? ((Integer)w).compareTo(e.w) : ((Integer)v).compareTo(e.v);
		}
		
		// Returns true iff this edge can be an edge in a graph with V vertices
		public boolean valid(int V) {
			return (v < V && w < V);
		}
		
		// Returns a string representing this edge
		public String toString() {
			return "(" + v + "," + w + ")";
		}
	}
	
	public HW6Test () {
	}

	int V, lastV, v, w, lastw, d, lastd;
	LinkedList<LinkedList<Edge>> cycles, lastcyc;
	Iterable<Edge> remEdges, mins;
	Edge rem;
	LinkedList<Edge> g, lastg;
	Iterable<Integer> dlist, lastdlist;
	String str, laststr;
	
	public static int randomPerm(int i, int V, int v) {
		int[] mult = {1, 277, 281, 283};
		return (v * mult[i % 4] + i) % V;
	}
	
	private int r(int v) {
		return randomPerm(p, V, v);
	}

	// generates a random set of edges of the form (v, w), v0 <= v < v1, w0 <= w < w1 
	// where each edge is independently included in the set with prob/128 probability
	public LinkedList<Edge> randomEdges(int seed, int v0, int v1, int prob, boolean topo) {
		LinkedList<Edge> edges = new LinkedList<Edge>();
		Random rand = new Random(seed);
		
		for (int v = v0; v < v1; v++) {
			for (int w = (topo ? v + 1 : v0); w < v1; w++) {
				if (v == w) continue;
				int pick = rand.nextInt() & ((1 << 7) - 1);
				if (prob > pick) {
					edges.add(new Edge(r(v), r(w)));
				}
			}
		}
		
		return edges;
	}
	
	private String graphToString(int V, LinkedList<Edge> edges) {
		LinkedList<Edge> sorted = new LinkedList<Edge>(edges);
		sorted.sort(Edge.DEF);
		StringBuilder s = new StringBuilder();
		for (Edge e : sorted)
			if (e.valid(V)) s.append(e);
		return "V = " + V + ", edges = " + ((s.length() == 0) ? "-" : s.toString());
	}

	private void assertIsTopoDAG(Edge e) {
		assertTrue(gr.isTopological(topo));
		if (e != null) {
			LinkedList<Integer> newtopo = new LinkedList<Integer>();
			for (int v : topo) {
				if (v == e.v) newtopo.add(e.w);
				else if (v == e.w) newtopo.add(e.v);
				else newtopo.add(v);
			}
			assertFalse(gr.isTopological(newtopo));
		}
	}
	
	private Digraph createGraph(int V, Iterable<Edge> edges) {
		Digraph graph = new Digraph(V);
		for (Edge e : edges) {
			graph.addEdge(e.v, e.w);
		}
		return graph;
	}
	
	private void testIsTopo() {
		int i;
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (p = 0; p < 4; p++) {
					for (prob = 1; prob <= 128; prob*=2) {
						topo = new LinkedList<Integer>();
						g = this.randomEdges(seed, 0, V, prob, true);
						gr = createGraph(V, g);
						for (i = 0; i < V; i++) topo.add(r(i));
						assertIsTopoDAG(g.poll());
						lastV = V;
						lastg = g;
						
						topo = new LinkedList<Integer>();
						g = this.randomEdges(seed, 0, V, prob, false);
						gr = createGraph(V, g);
						SCC();
						i = 0;
						for (int v : revPost) topo.add(v);
						if (count == V) {
							assertIsTopoDAG(g.poll());
						} else {
							assertFalse(gr.isTopological(topo));
						}
						lastV = V;
						lastg = g;
					}
				}
			}
		}
	}
	
	private void assertTopological(LinkedList<Edge> edges) {
		assertNotEquals(null, ret);
		marked = new boolean[V];
		for (int v : ret) {
			for (int w = 0; w < V; w++) {
				if (marked[w]) assertFalse(edges.contains(new Edge(v, w)));
			}
			marked[v] = true;
		}
		for (int v = 0; v < V; v++) {
			assertTrue(marked[v]);
		}
	}
	
	private void testTopo() {
		ret = null;
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (p = 0; p < 4; p++) {
					for (prob = 1; prob <= 128; prob*=2) {
						g = this.randomEdges(seed, 0, V, prob, true);
						gr = createGraph(V, g);
						ret = gr.topologicalSort();
						assertTopological(g);
						lastV = V;
						lastg = g;
						
						g = this.randomEdges(seed, 0, V, prob, false);
						gr = createGraph(V, g);
						ret = gr.topologicalSort();
						SCC();
						if (count == V) {
							assertTopological(g);
						} else {
							assertEquals(null, ret);
						}
						lastV = V;
						lastg = g;
					}
				}
			}
		}
	}
	
	private void SCC() {
		Stack<Integer> sccOrd = new Stack<Integer>();
		int V = gr.V();
		Digraph original = gr;
		Digraph rev = new Digraph(V);
		
		for (int v = 0; v < V; v++)
			for (int w : gr.adj(v))
				rev.addEdge(w, v);
		
		marked = new boolean[V];
		id = new int[V];
		count = 0;
		revPost = sccOrd;
		gr = rev;
		for (int v = 0; v < V; v++) {
			if (!marked[v]) DFS(v);
		}
		
		marked = new boolean[V];
		revPost = new Stack<Integer>();
		gr = original;
		for (int v : sccOrd) {
			if (!marked[v]) {
				DFS(v);
				count++;
			}
		}
	}
	
	private void DFS(int v) {
		marked[v] = true;
		id[v] = count;
		for (int w : gr.adj(v)) {
			if (!marked[w]) DFS(w);
		}
		revPost.push(v);
	}
	
	private void testRev() {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (p = 0; p < 4; p++) {
					for (prob = 1; prob <= 128; prob*=2) {
						g = this.randomEdges(seed, 0, V, prob, false);
						gr = createGraph(V, g);
						retgr = gr.reverse();
						assertNotEquals(null, retgr);
						for (int v = 0; v < V; v++) {
							for (int w = 0; w < V; w++) {
								boolean answer = (v != w) && g.contains(new Edge(v, w));
								assertEquals(answer, gr.containsEdge(v, w));
								assertEquals(answer, retgr.containsEdge(w, v));
							}
						}
						lastV = V;
						lastg = g;
					}
				}
			}
		}
	}
	
	private void testComp() {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (p = 0; p < 4; p++) {
					for (prob = 1; prob <= 128; prob*=2) {
						g = this.randomEdges(seed, 0, V, prob, false);
						gr = createGraph(V, g);
						retgr = gr.complement();
						assertNotEquals(null, retgr);
						for (int v = 0; v < V; v++) {
							for (int w = 0; w < V; w++) {
								boolean answergr = (v != w) && g.contains(new Edge(v, w));
								boolean answerret = (v != w) && !answergr;
								assertEquals(answergr, gr.containsEdge(v, w));
								assertEquals(answerret, retgr.containsEdge(v, w));
							}
						}
						lastV = V;
						lastg = g;
					}
				}
			}
		}
	}
	
	private void testPath() {
		for (V = 2; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 0; prob <= 128; prob+=16) {
					g = this.randomEdges(seed, 0, V, prob, false);
					gr = createGraph(V, g);
					for (v = 0; v < V; v++) {
						int[] d = new int[V];
						Queue<Integer> q = new Queue<Integer>();
						q.enqueue(v);
						while (!q.isEmpty()) {
							int s = q.dequeue();
							for (int t = 0; t < V; t++) {
								if (d[t] == 0 && s != t && g.contains(new Edge(s, t))) {
									d[t] = d[s] + 1;
									q.enqueue(t);
								}
							}
						}
						for (w = 0; w < V; w++) {
							if (v == w) continue;
							ret = gr.pathTo(v,  w);
							if (d[w] > 0) {
								assertNotEquals(null, ret);
								int p = -1;
								for (int r : ret) {
									if (p == -1) {
 										assertEquals(v, r);
									} else {
										assertTrue(gr.containsEdge(p, r));
									}
									p = r;
								}
								assertEquals(w, p);
							} else {
								assertEquals(null, ret);
							}
						}
					}
					lastV = V;
					lastg = g;
				}
			}
		}
	}

	private void testCycle() {
		for (V = 2; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 0; prob <= 128; prob+=16) {
					g = this.randomEdges(seed, 0, V, prob, false);
					gr = createGraph(V, g);
					SCC();
					for (v = 0; v < V; v++) {
						for (w = v + 1; w < V; w++) {
							ret = gr.cycleBetween(v, w);
							if (id[v] == id[w]) {
								assertNotEquals(null, ret);
								int f = -1, p = -1;
								for (int q : ret) {
									if (f == -1) {
										f = q;
									} else {
										assertTrue(gr.containsEdge(p, q));
									}
									p = q;
								}
								assertTrue(gr.containsEdge(p, f));
							} else {
								assertEquals(null, ret);
							}
						}
					}
					lastV = V;
					lastg = g;
				}
			}
		}
	}
	
	private void testMethod(int method_id) throws Exception {
		try {
			System.out.print(border + test + border);
			switch (method_id) {
			case 0: testRev(); break;
			case 1: testComp(); break;
			case 2: testIsTopo(); break;
			case 3: testPath(); break;
			case 4: testCycle(); break;
			case 5: testTopo(); break;
			}
		} catch(AssertionError aerr) {
			ae = aerr;
		} catch(Exception err) {
			e = err;
		}
		
		if (ae != null || e != null) {
			System.out.print("\n" + border + test + failed + border);
			System.out.println("failing case V = " + V + " seed = " + seed + " prob = " + prob + "/128 and permutation = " + p);
			System.out.println("the corresponding digraph is:");
			System.out.println(graphToString(V, g));
			
			String retStr = null;
			switch (method_id) {
			case 0: // testRev
			case 1: // testComp
				if (retgr == null) retStr = "digraph";
				break;
			case 2: // testIsTopo
				System.out.print("failing vertex order is: ");
				for (int v : topo) System.out.print(v + " ");
				System.out.println();
				break;
			case 3: // testPath
				retStr = "path from " + v + " to " + w;
				break;
			case 4: // testCycle
				retStr = "cycle between " + v + " and " + w;
				break;
			case 5: // testTopo
				retStr = "topological sort";
				break;
			}
			
			if (ret == null) {
				if (retStr != null) System.out.println("didn't return a " + retStr + "!");
			} else {
				System.out.print("returned an erroneous " + retStr + ": ");
				for (int v : ret) System.out.print(v + " ");
				System.out.println();
			}
			
			if (lastg != null) {
				System.out.println("the last successful graph is:");
				System.out.println(graphToString(lastV, lastg));
			}
			
			if (ae != null) throw ae;
			if (e != null) throw e;
		} else {
			System.out.print(border + test + passed + border);
		}
	}
	
	@Test
	public void testTopologicalSort() throws Exception {
		test = "* Testing topological sort                *\n";
		testMethod(5);
	}
	
	@Test
	public void testCycleBetween() throws Exception {
		test = "* Testing cycle between two vertices      *\n";
		testMethod(4);
	}
	
	@Test
	public void testPathTo() throws Exception {
		test = "* Testing path from one vertex to another *\n";
		testMethod(3);
	}
	
	@Test
	public void testIsTopological() throws Exception {
		test = "* Testing is topological                  *\n";
		testMethod(2);
	}
	
	@Test
	public void testComplement() throws Exception {
		test = "* Testing complement digraph              *\n";
		testMethod(1);
	}
	
	@Test
	public void testReverse() throws Exception {
		test = "* Testing reverse digraph                 *\n";
		testMethod(0);
	}
}