/**
 * Your homework is to complete the methods marked TODO.
 * You must not change the declaration of any method.
 */

package hw1;

/**
 *  The LinkedListST class represents an (unordered) symbol table of
 *  generic key-value pairs.  It supports put, get, and delete methods.
 */
public class LinkedListST<Key extends Comparable<Key>, Value extends Comparable<Value>> {
    private Node first; // the linked list of key-value pairs
   // private int N; 
   

    // a helper linked list data type
    private class Node {
        private Key key;
        private Value val;
        private Node next;
        

        public Node(Key key, Value val, Node next)  {
            this.key  = key;
            this.val  = val;
            this.next = next;
           // this.N = N;
        }
    }

    /**
     * Initializes an empty symbol table.
     */
    public LinkedListST() {
    }
    
    /**
     * Returns the value associated with the given key in this symbol table.
     */
    public Value get(Key key) {
        if (key == null) throw new NullPointerException("argument to get() is null"); 
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key))
                return x.val;
        }
        return null;
    }

    /**
     * Inserts the specified key-value pair into the symbol table, overwriting the old 
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is null.
     */
    public void put(Key key, Value val) {
        if (key == null) throw new NullPointerException("first argument to put() is null"); 
        if (val == null) {
            delete(key);
            return;
        }
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key)) {
                x.val = val;
                return;
            }
        }
        first = new Node(key, val, first);
    }

    /**
     * Removes the specified key and its associated value from this symbol table     
     * (if the key is in this symbol table).    
     */
    public void delete(Key key) {
        if (key == null) throw new NullPointerException("argument to delete() is null"); 
        first = delete(first, key);
    }

    // delete key in linked list beginning at Node x
    // warning: function call stack too large if table is large
    private Node delete(Node x, Key key) {
        if (x == null) return null;
        if (key.equals(x.key)) {
            return x.next;
        }
        x.next = delete(x.next, key);
        return x;
    }

    /**
     * size returns the number of key-value pairs in the symbol table.
     * it returns 0 if the symbol table is empty.
     */
    public int size () {
    	int sizeOFpairs = 0; 
    	for (Node n = first; n!=null; n  = n.next){ 
    		sizeOFpairs++; 	
    	}
    	return sizeOFpairs; 
    }
   
    /**
     * secondMaxKey returns the second maximum key in the symbol table.
     * it returns null if the symbol table is empty or if it has only one key.
     */
    public Key secondMaxKey () {
    	Key maximumKey = null;
    	Key secondMaxKey= null;
    	if (first == null || first.next == null){
    		return null;
    	}
    	if (first.key.compareTo(first.next.key)>=0){
    		maximumKey = first.key;
    		secondMaxKey = first.next.key;
    	}
    	else {
    		maximumKey = first.next.key;
    		secondMaxKey = first.key;
    	}
    	for (Node x = first; x!=null; x=x.next){
    		if (x.key.compareTo(secondMaxKey)>=0){
    			if (x.key.compareTo(maximumKey)>0){
    				secondMaxKey= maximumKey;
    				maximumKey= x.key;
    			}
    			if (x.key.compareTo(maximumKey) <0)
    				secondMaxKey =x.key;
    		}
    	}
    	return secondMaxKey; // TODO
    }
   

    /**
     * rank returns the number of keys in this symbol table that is less than the given key.
     * your implementation should be recursive. 
     */
    public int rank (Key key) {
     if (first == null){
    	   return 0;
       }
       int keepTrackofCount = 0;
       for (Node x =first; x!=null; x =x.next){
    	   if (key.compareTo(x.key)>0){
    		   keepTrackofCount++;
    	   }
       }
    	return keepTrackofCount;// TODO
    }

    /**
     * floor returns the largest key in the symbol table that is less than or equal to the given key.
     * it returns null if there is no such key.
     */
    public Key floor (Key key) {
    	Key secondlargestKey = null;
    	for (Node x = first; x != null; x=x.next)
    		if (key.compareTo(x.key) >=0){
    			if (secondlargestKey == null){
    				secondlargestKey = x.key;
    			}
    			else if (secondlargestKey.compareTo(x.key)<= 0){
    					secondlargestKey = x.key;	
    			}
    		}
    	return secondlargestKey;
    	
    }
    public Iterable<Key> keys() // add for bonuse 
     {
     	return keys();
     }
     
    /**
     * inverse returns the inverse of this symbol table.
     * if the symbol table contains duplicate values, you can use any of the keys for the inverse
     */
    public LinkedListST<Value, Key> inverse () {
    	return null; // TODO
    }
   
}