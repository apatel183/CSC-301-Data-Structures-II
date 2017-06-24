//
// LLRB --- L(eft)-L(eaning) R(ed)-B(lack) BST
// 
// This class stores a set of integer keys using a left-leaning red-black BST
//
// HOMEWORK in this file is to implement:
//
// 1) public void insert()
// 2) public boolean containsRightRedEdge()
// 3) public boolean containsConsecutiveLeftRedEdges()
// 4) public int countBlackEdgesOnLeftmostPath()
// 5) public boolean sameBlackEdgesCountOnAllPaths(int count)
//
// As BONUS, there is one additional method to implement
//
// 1) public void fixLLRB()
//

package hw4;

public class LLRB {
    private static final boolean RED   = true;
    private static final boolean BLACK = false;
  
    
    public Node root;
	
	public class Node {
		public int key;
		public boolean color;
		public Node left, right;
		
		public Node(int key, boolean color) {
			this.key = key;
			this.color = color;
		}
	}
	
	// Constructor for LLRB
	public LLRB() {
	}
	
	// Is parent link for node x red? false if x is null
	private boolean isRed(Node x) {
		if (x == null) return BLACK;
		return x.color == RED;
	}
	
	// Inserts a key without fixing the tree (this is a purposefully erroneous insertion method)
	public void bstInsert(int key) {
		root = bstInsert(root, key);
	}
	
	// Recursive helper method for bstInsert (erroneous)
	private Node bstInsert(Node x, int key) {
		if (x == null) return new Node(key, RED);
		if (key < x.key) x.left  = bstInsert(x.left, key);
		else if (key > x.key) x.right = bstInsert(x.right, key);
		return x;
	}
	
	// Inserts a key fixing the red-black tree property
	public void insert(int key) {
		// TODO : complete this method
		root  = insertHelper(root, key);
	}
	
	private Node insertHelper (Node x, int key){
		if ( x == null) return new Node(key, RED);
		if (key < x.key) x.left = insertHelper(x.left, key);
		else if (key > x.key) x.right = insertHelper(x.right, key);
		else {
			x.key = key;
		}
		if(isRed(x.right)) x = rotateLeft(x);
		if(isRed(x.left) && isRed(x.left.left)) x = rotateRight(x);
		if(isRed(x.left) && isRed(x.right)) flipColors(x);
		return x;
		
		}
	/***************************************************************************
	    *  Red-black tree helper functions.
	    ***************************************************************************/

	    // make a left-leaning link lean to the right
	    private Node rotateRight(Node h) {
	        // assert (h != null) && isRed(h.left);
	        Node x = h.left;
	        h.left = x.right;
	        x.right = h;
	        x.color = x.right.color;
	        x.right.color = RED;
	        return x;
	    }

	    // make a right-leaning link lean to the left
	    private Node rotateLeft(Node h) {
	        // assert (h != null) && isRed(h.right);
	        Node x = h.right;
	        h.right = x.left;
	        x.left = h;
	        x.color = x.left.color;
	        x.left.color = RED;
	        return x;
	    }

	    // flip the colors of a node and its two children
	    private void flipColors(Node h) {
	        // h must have opposite color of its two children
	        // assert (h != null) && (h.left != null) && (h.right != null);
	        // assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
	        //    || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
	        h.color = !h.color;
	        h.left.color = !h.left.color;
	        h.right.color = !h.right.color;
	    }
	// Checks whether the tree contains a red right edge
	public boolean containsRightRedEdge() {
		// TODO : complete this method
		return containsRightRedEdge(root);
	}
	
	private boolean containsRightRedEdge(Node x){
		if (x == null) return false;
		if (isRed(x.right)) return true;
		else{
			return containsRightRedEdge(x.left) || containsRightRedEdge(x.right);
		}
		
	}
	
	// Checks whether the tree contains two left red edges in a row
	public boolean containsConsecutiveLeftRedEdges() {
		// TODO : complete this method
		return containsConsecutiveLeftRedEdges(root);
	}
	
	private boolean containsConsecutiveLeftRedEdges(Node x){
		if (x == null){
			return false;
		}
		if (isRed(x.left)&& isRed(x.left.left)){
			return true;
		}
		else {
			return containsConsecutiveLeftRedEdges(x.left) || containsConsecutiveLeftRedEdges(x.right);
		}
	}
	
	// Returns the maximum number of red edges (nodes) on any path from root to null
	public int maxRedEdgesDepth() {
		// TODO : complete this method
		return maxRedEdgesDepthHelper(root);
	}
	private int maxRedEdgesDepthHelper(Node x){
		if(x==null) return 0;
		else{
			int leftDepth = maxRedEdgesDepthHelper(x.left);
			int rightDepth = maxRedEdgesDepthHelper(x.right);
			int red =isRed(x)?1:0;
			if(leftDepth>rightDepth){
				return leftDepth+red;}
			else return rightDepth+red;
		}
	}
	
	// Returns the minimum number of black edges (nodes) on any path from root to null
	public int minBlackEdgesDepth() {
		// TODO : complete this method
		return minBlackEdgesDepthHelper(root);
	}
    
	private int minBlackEdgesDepthHelper(Node x){
		if(x==null) return 0;
		else{
			int leftDepth = minBlackEdgesDepthHelper(x.left);
			int rightDepth = minBlackEdgesDepthHelper(x.right);
			int black =!isRed(x)? 1:0;
			if(leftDepth<rightDepth){
				return leftDepth+black;}
			else return rightDepth+black;
		}
	
	}
	// Fixes a balanced red-black tree so that it becomes a left leaning red black tree.
	// The tree should already be balanced (wrt black nodes) before making a call to this method.
	public void fixLLRB() {
		// TODO : complete this method
		fixLLRB(root);
	}
	public void fixLLRB(Node x){
		if(x==null) return;
		root = balanceCheck(x, x.key);
		//System.out.println(root.key);
		fixLLRB(x.left);
		fixLLRB(x.right);
	}
	private Node balanceCheck(Node x, int key){ //check
		if (x == null) return null;
		if (key < x.key) x.left  = insertHelper(x.left, key);
		else if (key > x.key) x.right = insertHelper(x.right, key);
		else{
		if(isRed(x.right) && !isRed(x.left)) x = rotateLeft(x);
		if(isRed(x.left)    && isRed(x.left.left)) x = rotateRight(x);
		if(isRed(x.left)    && isRed(x.right)) flipColors(x);}
		return x;
	}
	
	
}
