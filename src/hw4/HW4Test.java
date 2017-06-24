package hw4;

import static org.junit.Assert.*;
import java.util.LinkedList;
import org.junit.Test;

public class HW4Test {
	String border = "*******************************************\n";
	String passed = "* Passed!                                 *\n";
	String failed = "* Failed!                                 *\n";
	String test;
	
	RandomRB rb;
	AssertionError ae;
	Exception e;
	
	public HW4Test () {
		rb = new RandomRB();
	}
	
	private boolean testValid(LLRB llrb) {
		if (llrb.root == null) return true;
		LinkedList<LLRB.Node> nodes = new LinkedList<LLRB.Node>();
		nodes.add(null);
		nodes.add(llrb.root);
		boolean done = false;
		while (!nodes.isEmpty()) {
			LLRB.Node n = nodes.removeFirst();
			if (n == null) {
				n = nodes.removeFirst();
				done = n.right == null;
				if (!done) nodes.add(null);
			}
			if (!done) {
				if (n.left == null || n.right == null) return false;
				if (n.left.color) {
					if (n.left.left == null || n.left.right == null) return false;
					nodes.add(n.left.left);
					nodes.add(n.left.right);
				} else {
					nodes.add(n.left);
				}
				nodes.add(n.right);
			} else {
				if (n.left != null && (!n.left.color || n.left.left != null || n.left.right != null)) return false;
				if (n.right != null) return false;
			}
		}
		return true;
	}
	
	public void assertMrgNodes(LLRB llrb, int max4, int max3) {
		assertMrgTraversal(llrb.root, 0, max4, max3);
	}
	private void assertMrgTraversal(LLRB.Node n, int low, int max4, int max3) {
		if (n == null) {
			assertTrue(low / 4 * 4 + 4 > max4);
			assertTrue(low / 3 * 3 + 3 > max3);
			return;
		}
		
		// left subtree
		int lmax4 = Math.min(max4, n.key - 1);
		int lmax3 = Math.min(max3, n.key - 1);
		assertMrgTraversal(n.left, low, lmax4, lmax3);
		// right subtree
		assertMrgTraversal(n.right, n.key + 1, max4, max3);
	}
	
	public void assertNodes(LLRB llrb, int max, int cons) {
		assertTraversal(llrb.root, 1, max, cons);
	}
	private void assertTraversal(LLRB.Node n, int low, int max, int cons) {
		if (n == null) {
			assertTrue(low / 4 * 4 + 4 > max);
			assertTrue(low > cons || low / 4 * 4 == cons);
			return;
		}
		
		// left subtree
		int lmax = Math.min(max, n.key - 1);
		int lcons = Math.min(cons, n.key - 1);
		assertTraversal(n.left, low, lmax, lcons);
		// right subtree
		assertTraversal(n.right, n.key + 1, max, cons);
	}
	
	private void testFix() {
		LLRB llrb;
		// Testing on valid red black trees
		for (int i = 0; i < 128; i++) {
			llrb = rb.resetRandomTree(i);
			assertTrue(testValid(llrb));
			llrb.fixLLRB();
			assertTrue(testValid(llrb));
		}
		
		// Testing on invalid red black trees
		for (int i = 0; i < 128; i++) {
			for (int k = 0; k < 32; k++) {
				llrb = rb.resetRandomTree(i);
				llrb.bstInsert(k * 4 + 3);
				if (testValid(llrb)) {
					llrb.bstInsert(k * 4 + 2);       // left invalid except when root was null in rb
					assertFalse(rb.depth > 0 && testValid(llrb));
					llrb.fixLLRB();
					assertTrue(testValid(llrb));
					llrb = rb.toLLRB();
					llrb.bstInsert(k * 4 + 2);
					llrb.bstInsert(k * 4 + 3);       // right invalid
					assertFalse(testValid(llrb));
					llrb.fixLLRB();
					assertTrue(testValid(llrb));
				} else {                             // right invalid
					llrb.fixLLRB();
					assertTrue(testValid(llrb));
					llrb.bstInsert(k * 4 + 2);
					llrb.bstInsert(k * 4 + 1);       // left invalid
					assertFalse(testValid(llrb));
					llrb.fixLLRB();
					llrb.fixLLRB();
					assertTrue(testValid(llrb));
				}
			}
		}
	}

	private void testMinBlackDepth() {
		LLRB llrb;
		// Testing on balanced red black trees
		for (int i = 0; i < 128; i++) {
			llrb = rb.resetRandomTree(i);
			for (int k = 0; k < 128; k++) {
				llrb.bstInsert(k);
				// DEBUG : System.out.println("Verifying minimum black edge count of tree for seed = " + i + " after red-edged insertion of " + k + " is " + rb.depth);
				assertEquals(rb.depth, llrb.minBlackEdgesDepth());
			}
		}
		
		// Testing on unbalanced red black trees
		for (int i = 0; i < 128; i++) {
			for (int k = 0; k < 96; k++) {
				rb.resetRandomTree(i);
				int key = k / 3 * 4 + (k % 3) + 1;
				rb.bstInsert(key, false);
				// DEBUG : System.out.println("Verifying minimum black edge count of tree for seed = " + i + " after black-edged insertion of " + k + " is still " + rb.depth);
				assertEquals(rb.depth == 0 ? 1 : rb.depth, rb.toLLRB().minBlackEdgesDepth());
			}
		}
	}
	
	private void testMaxRedDepth() {
		LLRB llrb;
		// Testing on balanced red black trees
		for (int i = 0; i < 128; i++) {
			llrb = rb.resetRandomTree(i);
			assertEquals(rb.reddepth, llrb.maxRedEdgesDepth());
		}
	}
	
	private void testLeftRed() {
		LLRB llrb;
		// Testing on valid red black trees
		for (int i = 0; i < 128; i++) {
			llrb = rb.resetRandomTree(i);
			// DEBUG : System.out.println("Checking validity of tree for seed = " + i);
			assertFalse(llrb.containsConsecutiveLeftRedEdges());
		}
		
		// Testing on invalid red black trees
		for (int i = 0; i < 128; i++) {
			for (int k = 0; k < 32; k++) {
				rb.resetRandomTree(i);
				rb.bstInsert(k * 4 + 3, false);
				rb.bstInsert(k * 4 + 2, true);
				rb.bstInsert(k * 4 + 1, true);
				// DEBUG : System.out.println("Checking invalidity of tree for seed = " + i + " after inserting two edges on the left at " + (k * 4 + 2) + " and " + (k * 4 + 1));
				assertTrue(rb.toLLRB().containsConsecutiveLeftRedEdges());
			}
		}
	}
	
	private void testRightRed() {
		LLRB llrb;
		// Testing on valid red black trees
		for (int i = 0; i < 128; i++) {
			llrb = rb.resetRandomTree(i);
			// DEBUG : System.out.println("Checking validity of tree for seed = " + i);
			assertFalse(llrb.containsRightRedEdge());
		}
		
		// Testing on invalid red black trees
		for (int i = 0; i < 128; i++) {
			for (int k = 0; k < 32; k++) {
				rb.resetRandomTree(i);
				rb.bstInsert(k * 4 + 1, false);
				rb.bstInsert(k * 4 + 2, true);
				// DEBUG : System.out.println("Checking invalidity of tree for seed = " + i + " after inserting red edge on the right at " + (k * 4 + 2));
				assertTrue(rb.toLLRB().containsRightRedEdge());
			}
		}
	}
	
	private void testIns() {
		// Testing serial insertions into random red black trees
		for (int i = 0; i < 128; i++) {
			// DEBUG : System.out.print("For seed = " + i + " inserting keys :");
			LLRB llrb = rb.resetRandomTree(i);
			for (int k = 0; k < 96; k++) {
				int key = k / 3 * 4 + (k % 3) + 1;
				// DEBUG : System.out.print(" " + key);
				llrb.insert(key);
				assertTrue(testValid(llrb));
				assertNodes(llrb, rb.max, key);
			}
			// DEBUG : System.out.println("");
		}
	}

	private void testMethod(int method_id) throws Exception {
		try {
			System.out.print(border + test + border);
			switch (method_id) {
			case 0: testIns(); break;
			case 1: testRightRed(); break;
			case 2: testLeftRed(); break;
			case 3: testMaxRedDepth(); break;
			case 4: testMinBlackDepth(); break;
			case 5: testFix(); break;
			}
		} catch(AssertionError aerr) {
			ae = aerr;
		} catch(Exception err) {
			e = err;
		}
		
		if (ae != null || e != null) {
			System.out.print("\n" + border + test + failed + border);
			System.out.println("failing case seed = " + rb.seed + " and the corresponding tree:");
			System.out.println(rb.toString());
			if (ae != null) throw ae;
			if (e != null) throw e;
		} else {
			System.out.print(border + test + passed + border);
		}
	}
	
	@Test
	public void testFixLLRB() throws Exception {
		test = "* Testing fix LLRB                        *\n";
		testMethod(5);
	}
	
	@Test
	public void testMinBlackEdgesDepth() throws Exception {
		test = "* Testing minimum black edges depth       *\n";
		testMethod(4);
	}
	
	@Test
	public void testMaxRedEdgesDepth() throws Exception {
		test = "* Testing maximum red edges depth         *\n";
		testMethod(3);
	}
	
	@Test
	public void testContainsConsecutiveLeftRedEdges() throws Exception {
		test = "* Testing consecutive left red edges      *\n";
		testMethod(2);
	}
	
	@Test
	public void testContainsRightRedEdge() throws Exception {
		test = "* Testing right red edge                  *\n";
		testMethod(1);
	}
	
	@Test
	public void testInsert() throws Exception {
		test = "* Testing insert                          *\n";
		testMethod(0);
	}
}