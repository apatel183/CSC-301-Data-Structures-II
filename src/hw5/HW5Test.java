package hw5;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class HW5Test {
	String border = "*******************************************\n";
	String passed = "* Passed!                                 *\n";
	String failed = "* Failed!                                 *\n";
	String test;
	
	AssertionError ae;
	Exception e;
	
	private static class Edge implements Comparable<Edge> {
		public static final Comparator<Edge> DEF = Comparator.naturalOrder();
		public final int v, w; // Vertices of this undirected edge
		
		// Constructor for an edge (no self-loops allowed)
		public Edge(int v, int w) {
			if (v < 0 || w < 0 || v == w) {
				throw new IllegalArgumentException("Vertices of an edge must be different nonnegative integers, v = " + v + ", w = " + w);
			} else if (v < w) {
				this.v = v;
				this.w = w;
			} else {
				this.w = v;
				this.v = w;
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
	
	int seed, prob;
	int V, lastV, w, lastw;
	int p;
	LinkedList<Edge> g, g2, lastg, lastg2;
	List<Integer> dlist, lastdlist;
	String str, laststr;
	
	public HW5Test() {
	}

	public static int randomPerm(int i, int V, int v) {
		int[] mult = {1, 277, 281, 283};
		return (v * mult[i % 4] + i) % V;
	}
	
	private int r(int v) {
		return randomPerm(p, V, v);
	}

	// generates a random set of edges of the form (v, w), v0 <= v < v1, w0 <= w < w1 
	// where each edge is independently included in the set with prob/128 probability
	public LinkedList<Edge> randomEdges(int seed, int v0, int v1, int w0, int w1, int prob) {
		LinkedList<Edge> edges = new LinkedList<Edge>();
		Random rand = new Random(seed);
		
		if (w0 < v0) {
			int temp = w0;
			w0 = v0;
			v0 = temp;
			temp = w1;
			w1 = v1;
			v1 = temp;
		}
		
		for (int v = v0; v < v1; v++) {
			for (int w = (w0 > v + 1 ? w0 : (v + 1)); w < w1; w++) {
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
	
	private String dlistToString(List<Integer> dlist) {
		if (dlist == null) return "null";
		StringBuilder s = new StringBuilder();
		for (int d : dlist) {
			s.append(d + "-");
		}
		return s.toString();
	}
	
	private void assertSameGraph(int V, LinkedList<Edge> edges, Graph gr) {
		assertNotEquals(null, gr);
		assertEquals(V, gr.V());
		int numEdges = 0;
		for (Edge e : edges)
			if (e.valid(V)) numEdges++; // count the number of edges
		int numEdgesGr = 0;
		for (int v = 0; v < V; v++) {
			for (int w : gr.adj(v)) {
				assertTrue(w < V);
				Edge e = new Edge(v, w);
				assertTrue(e.valid(V));
				assertTrue(edges.contains(e));
				numEdgesGr++;
			}
		}
		assertEquals(numEdges * 2, numEdgesGr);
		assertEquals(numEdges, gr.E());
	}
	
	private void testAddVertex() {
		for (int W = 1; W <= 20; W++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 21 - W; prob <= 128; prob*=2) {
					g = this.randomEdges(seed, 0, W, 0, W, prob);
					Graph gr = new Graph(1);
					for (V = 2; V <= W; V++) { // add vertices (and edges) until V > W
						gr.addVertex();
						for (Edge e : g) {
							if (e.valid(V) && e.w == V - 1)	gr.addEdge(e.v, e.w);
						}
						assertSameGraph(V, g, gr);
						// save last successful test
						lastV = V;
						lastg = g;
					}
				}
			}
		}
	}
	
	private void testTriangle() {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 1; prob <= 128; prob*=2) {
					g = this.randomEdges(seed, 0, V, 0, V, prob);
					Graph gr = new Graph(V);
					Edge t = null;
					for (Edge e : g) {
						boolean ex = false;
						for (int u = 0; u < V; u++) {
							if (u == e.v || u == e.w) continue;
							if (gr.containsEdge(e.v, u) && gr.containsEdge(e.w, u)) {
								ex = true;
								t = e;
								break;
							}
						}
						if (!ex) gr.addEdge(e.v, e.w);
					}
					
					assertFalse(gr.containsTriangle());
					// save last successful test
					lastV = V;
					lastg = g;
					
					if (t != null) {
						gr.addEdge(t.v, t.w);
						assertTrue(gr.containsTriangle());
						// save last successful test
						lastV = V;
						lastg = g;
					}
				}
			}
		}
	}
	
	private void testBipartite() {
		for (V = 1; V <= 32; V++) {
			for (int W = 1; W < V; W++) {
				for (p = 0; p < 4; p++) {
					for (seed = 0; seed < 8; seed++) {
						for (prob = 1; prob <= 128; prob*=2) {
							g = this.randomEdges(seed, 0, W, W, V, prob);
							Graph gr = new Graph(V);
							for (Edge e : g) {
								gr.addEdge(e.v, e.w);
							}
							assertTrue(gr.isBipartite());
							// save last successful test
							lastV = V;
							lastg = g;
							
							int[] x = new int[2];
							x[0] = x[1] = -1;
							int[][] b = {{0, W}, {W, V}};
							boolean[] marked = new boolean[V];
							Queue<Integer>[] q = new Queue[2];
							q[0] = new Queue<Integer>();
							q[1] = new Queue<Integer>();
							for (Edge e : g) {
								marked[e.v] = true;
								marked[e.w] = true;
								q[0].enqueue(e.v);
								q[1].enqueue(e.w);
								while (!q[0].isEmpty() && !q[1].isEmpty()) {
									Queue<Integer>[] qN = new Queue[2];
									qN[0] = new Queue<Integer>();
									qN[1] = new Queue<Integer>();
									for (int i = 0; i< 2; i++) {
										while (!q[i].isEmpty()) {
											int v = q[i].dequeue();
											for (int w = b[i][0]; w < b[i][1]; w++) {
												if (!marked[w] && gr.containsEdge(w, v)) {
													marked[w] = true;
													x[i] = w;
													qN[1 - i].enqueue(w);
												}
											}
										}
									}
									q[0] = qN[0];
									q[1] = qN[1];
								}
								if (x[0] != -1) {
									gr.addEdge(e.w, x[0]);
								} else if (x[1] != -1) {
									gr.addEdge(e.v, x[1]);
								} else {
									continue;
								}
								
								assertFalse(gr.isBipartite());
								// save last successful test
								lastV = V;
								lastg = g;
								break;
							}
						}
					}
				}
			}
		}
	}

	private void testCycle() {
		for (V = 1; V <= 32; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 1; prob <= 128; prob*=2) {
					g = this.randomEdges(seed, 0, V, 0, V, prob);
					Graph gr = new Graph(V);
					for (Edge e : g) {
						gr.addEdge(e.v, e.w);
					}
					boolean exists = false;
					int[] markdist = new int[V];
					Queue<Integer> q = new Queue<Integer>();
					for (int x = 0; x < V; x++) {
						if (markdist[x] == 0) {
							markdist[x] = 1;
							q.enqueue(x);
							while (!q.isEmpty()) {
								int v = q.dequeue();
								for (int w = 0; w < V; w++) {
									if (markdist[w] == 0 && gr.containsEdge(v, w)) {
										markdist[w] = markdist[v] + 1;
										q.enqueue(w);
									} else if (markdist[w] > 0 && markdist[w] >= markdist[v] && v != w && gr.containsEdge(v, w)) {
										exists = true;
									}
								}
							}
						}
					}
					
					assertEquals(exists, gr.containsCycle());
					// save last successful test
					lastV = V;
					lastg = g;
				}
			}
		}
	}
	
	private void testAdjMatrix() {
		for (V = 1; V <= 20; V++) {
			for (seed = 0; seed < 8; seed++) {
				for (prob = 21 - V; prob <= 128; prob*=2) {
					g = this.randomEdges(seed, 0, V, 0, V, prob);
					Graph gr = new Graph(V);
					for (Edge e : g) {
						gr.addEdge(e.v, e.w);
					}
					assertSameGraph(V, g, gr);
					boolean[][] adjmtrx = gr.adjMatrix();
					assertNotEquals(null, adjmtrx);
					assertEquals(V, adjmtrx.length);
					for (int v = 0; v < V; v++) {
						assertEquals(V, adjmtrx[v].length);
						for (int w = 0; w < V; w++) {
							assertEquals(v != w && g.contains(new Edge(v, w)), adjmtrx[v][w]);
						}
					}
					// save last successful test
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
			case 1: testAddVertex(); break;
			case 2: testCycle(); break;
			case 3: testBipartite(); break;
			case 4: testTriangle(); break;
			case 5: testAdjMatrix(); break;
			}
		} catch(AssertionError aerr) {
			ae = aerr;
		} catch(Exception err) {
			e = err;
		}
		
		if (ae != null || e != null) {
			System.out.print("\n" + border + test + failed + border);
			System.out.println("failing case seed = " + seed + " prob = " + prob + "/128");
			System.out.println("the corresponding graph is:");
			System.out.println(graphToString(V, g));
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
	public void testGraphMatrix() throws Exception {
		test = "* Testing edges as adjacency matrix       *\n";
		testMethod(5);
	}
	
	@Test
	public void testContainsTriangle() throws Exception {
		test = "* Testing containsTriangle                *\n";
		testMethod(4);
	}
	
	@Test
	public void testIsBipartite() throws Exception {
		test = "* Testing isBipartite                     *\n";
		testMethod(3);
	}
	
	@Test
	public void testContainsCycle() throws Exception {
		test = "* Testing containsCycle                   *\n";
		testMethod(2);
	}
	
	@Test
	public void testGraphAddVertex() throws Exception {
		test = "* Testing addVertex                       *\n";
		testMethod(1);
	}
}