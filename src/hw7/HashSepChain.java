package hw7;

public class HashSepChain<Value> {
    private static final int INIT_CAPACITY = 4;

    private int N;                                // number of strings
    private int M;                                // hash table size
    private Node[] st;                            // array of linked-lists

    private static class Node {
    	String key;
    	Object val;
    	Node next;
    	
    	public Node(String s, Object v) {
    		key = s;
    		val = v;
    	}
    	
    	public Node(String s, Object v, Node n) {
    		this(s, v);
    		next = n;
    	}
    }

    /**
     * Initializes an empty table.
     */
    public HashSepChain() {
        this(INIT_CAPACITY);
    } 

    /**
     * Initializes an empty table with M chains.
     */
    public HashSepChain(int M) {
        this.M = M;
        st = (Node[]) new Node[M];
    } 

    // hash value between 0 and M-1
    private int hash(String s) {
        return (s.hashCode() & 0x7fffffff) % M;
    } 

    /**
     * Returns the number of strings in this hash table.
     */
    public int size() {
        return N;
    } 

    /**
     * Returns the value associated with the given String s, or null if s is not in the symbol table.
     */
    @SuppressWarnings("unchecked")
	public Value get(String s) {
    	int i = hash(s);
    	Node n;
        for (n = st[i]; n != null; n = n.next) {
        	if (n.key.equals(s)) return (Value) n.val;
        }
        return null;
    }
    
    /**
     * Inserts the specified String, Value pair into the symbol table
     */
    public void put(String s, Value v) {
    	int i = hash(s);
    	Node n;
        for (n = st[i]; n != null; n = n.next) {
        	if (n.key.equals(s)) {
        		n.val = v;
        		return;
        	}
        }
    	
        // double table size if average length of list >= 10
    	N++;
        if (N >= 8*M) resize(2*M);
    	st[i] = new Node(s, v, st[i]);
    }

    // resizes the hash table to the given capacity by re-hashing all of the keys
    private void resize(int newM) {
    	// TODO
    	Node[] newhashTable = new Node[newM];
    	int oldhastTablesizeofM = M;
    	M = newM;
    	for(int i = 0; i < oldhastTablesizeofM; i++){
    		for(Node x = st[i]; x != null; x=x.next){
    			String newhashKey = st[i].key;
    			st[i] = new Node(newhashKey,st[i]);
    		}
    	}
    	st = newhashTable;
    }
    
    // Returns the minimum string in the table
    // Returns null if no such string exists
    public String min() {
    	// TODO
    	String minimustringinHashtable  = null;
    	for(int i = 0; i < M; i++){
    		for (Node x = st[i]; x !=null; x=x.next){
    			if(minimustringinHashtable == null || x.key.compareTo(minimustringinHashtable) <=0){
    				minimustringinHashtable = x.key;
    			}
    		}
    	}
    	return minimustringinHashtable;
    }
    
    // Returns the number of String that is less than the given String s
    public int rank(String s) {
    	// TODO
    	int keepingTrack = 0;
    	for(int i = 0; i < M; i++){
    		for(Node x = st[i]; x!=null; x=x.next){
    			if(s.compareTo(x.key)> 0) keepingTrack++;
    		}
    	}
    	return keepingTrack;
    }

    /**
     * Removes the specified string from this hash table     
     * (if the string is in this table).    
     */
    public void delete(String s) {
    	// TODO

        // halve table size if average length of list <= 2
        if (M > INIT_CAPACITY && N <= 2*M) resize(M/2);
    }
}