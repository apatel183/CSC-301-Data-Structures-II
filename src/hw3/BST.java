/**
 * Your homework is to complete the methods marked TODO.
 * You must not change the declaration of any method.
 */

package hw3;

/**
 *  The B(inary)S(earch)T(ree) class represents a symbol table of
 *  generic key-value pairs.  It supports put, get, and delete methods.
 */
public class BST<Key extends Comparable<Key>, Value> {
	private Node root;             // root of BST

	private class Node {
		private Key key;           // sorted by key
		private Value val;         // associated data
		private Node left, right;  // left and right subtrees
		
		public Node(Key key, Value val) {
			this.key = key;
			this.val = val;
		}
		
		/**
		 * Appends the preorder string representation of the sub-tree to the given StringBuilder.
		 */
		public void buildString(StringBuilder s) {
			s.append(left == null ? '[' : '(');
			s.append(key + "," + val);
			s.append(right == null ? ']' : ')');
			if (left != null) left.buildString(s);
			if (right != null) right.buildString(s);
		}
	}
	
	/**
	 * Initializes an empty symbol table.
	 */
	public BST() {
	}
	
	/**
	 * Returns the value associated with the given key.
	 */
	public Value get(Key key) {
		return get(root, key);
	}
	
	private Value get(Node x, Key key) {
		if (x == null) return null;
		int cmp = key.compareTo(x.key);
		if      (cmp < 0) return get(x.left, key);
		else if (cmp > 0) return get(x.right, key);
		else              return x.val;
	}
	
	/**
	 * Inserts the specified key-value pair into the symbol table, overwriting the old 
	 * value with the new value if the symbol table already contains the specified key.
	 */
	public void put(Key key, Value val) {
		if (key == null) throw new NullPointerException("first argument to put() is null");
		root = put(root, key, val);
	}
	
	private Node put(Node x, Key key, Value val) {
		if (x == null) return new Node(key, val);
		int cmp = key.compareTo(x.key);
		if      (cmp < 0) x.left  = put(x.left,  key, val);
		else if (cmp > 0) x.right = put(x.right, key, val);
		else              x.val   = val;
		return x;
	}
	
	/**
	 * Returns the preorder string representation of the BST.
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		root.buildString(s);
		return s.toString();
	}
	// return size of key- value pairs
	public int size() {
		
		return size(root);// TODO
	}
	private int size(Node x){
		if(x == null){
			return 0;
		}
		else{
			return 1 + size(x.left) + size(x.right);
		}
	}
	/**
	 * Returns the number of null nodes in this symbol table.
	 */
	public int numNull() {
		return numNull(root); // TODO
	}
	
	private int numNull(Node x){
		if(x==null) return 1;
		else return numNull(x.left)+numNull(x.right);
	}
	/**
	 * Returns the length of the shortest path from root to a null node.
	 */
	public int lenShortestPathToNull() {
		return shortestPath(root); // TODO
	}
	private int shortestPath(Node x){
		if(x==null) return 0;
		if (x.left == null && x.right ==null) return 1;
		else if (x.left == null) return 1;
		else if (x.right == null) return 1;
		else return 1 + Math.min(shortestPath(x.right), shortestPath(x.left));
	}

	 public Key select(int k) {
	        if (k < 0 || k >= size()) throw new IllegalArgumentException();
	        Node x = select(root, k);
	        return x.key;
	    }

	    // Return key of rank k. 
	    private Node select(Node x, int k) {
	        if (x == null) return null; 
	        int t = size(x.left); 
	        if      (t > k) return select(x.left,  k); 
	        else if (t < k) return select(x.right, k-t-1); 
	        else            return x; 
	    }
	/**
	 * median returns the median of all keys in the symbol table.
	 */
	public Key median() 
	{
		if (size() == 0){
			return null; // TODO
		}
		int medianKey= size()/2;
		return select(medianKey);
	}
	/**
	 *  * Returns the largest key in the symbol table less than or equal to key.
	 * @param key
	 * @return
	 */
	 public Key floor(Key key) {
	        if (key == null) throw new NullPointerException("argument to floor() is null");
	        Node x = floor(root, key);
	        if (x == null) return null;
	        else return x.key;
	    } 

	    private Node floor(Node x, Key key) {
	        if (x == null) return null;
	        int cmp = key.compareTo(x.key);
	        if (cmp == 0) return x;
	        if (cmp <  0) return floor(x.left, key);
	        Node t = floor(x.right, key); 
	        if (t != null) return t;
	        else return x; 
	    } 
	    /**
	     * /**
	     * Returns the smallest key in the symbol table greater than or equal to
	     * @param x
	     * @param key
	     * @return
	     */
	    public Key ceiling(Key key) {
	        if (key == null) throw new NullPointerException("argument to ceiling() is null");
	        Node x = ceiling(root, key);
	        if (x == null) return null;
	        else return x.key;
	    }
	  
	    private Node ceiling(Node x, Key key) {
	        if (x == null) return null;
	        int cmp = key.compareTo(x.key);
	        if (cmp == 0) return x;
	        if (cmp < 0) { 
	            Node t = ceiling(x.left, key); 
	            if (t != null) return t;
	            else return x; 
	        } 
	        return ceiling(x.right, key); 
	    } 

	/**
	 * closest returns a key in the symbol table that is equal to
	 * or otherwise that is the closest on either side of the given key.
	 * it returns null if there is no such key (empty symbol table).
	 */
	public Key closest(Key key) {
		Key smallestKey = ceiling(key);
		Key largestKey	 = floor(key);
		if (smallestKey == null){
			return largestKey;
		}
		if (largestKey == null){
			return smallestKey;
		}
		return smallestKey; // TODO
	}
	
	/**
	 * Merges the given symbol table into this symbol table.
	 * If the given symbol table contains keys that is already in this symbol table
	 * merge overwrites those keys' values with the values in the given symbol table.
	 */
	public void merge(BST<Key, Value> bst) {
		merge(bst.root);
	}
	private void merge(Node x){
		if (x == null){
			return;		
		}
		put(x.key,x.val);
		merge(x.left);
		merge(x.right);
	}
	
}