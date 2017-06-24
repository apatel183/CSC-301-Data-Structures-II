package hw7;

public class HashLinearProbe<Value> {
    private static final int INIT_CAPACITY = 4;

    private int N;           // number of strings in the hash table
    private int M;           // size of linear probing table
    private String[] keys;   // the keys
    private Value[] vals;    // the values


    /**
     * Initializes an empty symbol table.
     */
    public HashLinearProbe() {
        this(INIT_CAPACITY);
    }

    /**
     * Initializes an empty hash table with the specified initial capacity.
     */
    @SuppressWarnings("unchecked")
	public HashLinearProbe(int capacity) {
        M = capacity;
        keys = new String[M];
        vals = (Value[])new Object[M];
    }

    /**
     * Returns the number of strings in this table.
     */
    public int size() {
        return N;
    }

    // hash function for keys - returns value between 0 and M-1
    private int hash(String s) {
        return (s.hashCode() & 0x7fffffff) % M;
    }


    /**
     * Returns true if this table contains the specified string.
     */
    public int index(String s) {
    	int i;
        for (i = hash(s); keys[i] != null; i = (i + 1) % M) { 
            if (keys[i].equals(s)) return i;
        }
        return i;
    }

    /**
     * Returns the value associated with the given String s, or null if s is not in the symbol table.
     */
	public Value get(String s) {
    	int i = index(s);
    	if (keys[i] == null) return null;
    	return vals[i];
    }
    
    /**
     * Inserts the specified string into the hash table (no duplicates)
     */
    public void put(String s, Value v) {
    	int i = index(s);
    	if (keys[i] == null) {
    		keys[i] = s;
    		vals[i] = v;
    		N++;
            // double table size if 50% full
            if (N >= M/2) resize(2*M);
    	} else {
    		vals[i] = v;
    	}
    }

    // resizes the hash table to the given capacity by re-hashing all of the keys
    private void resize(int newM) {
    	// TODO
    	HashLinearProbe<Value> tempHashtable = new HashLinearProbe<Value>(newM);
    	for( int i = 0; i < M; i++){
    		if(keys[i] !=null){
    			tempHashtable.put(keys[i],vals[i]);
    		
    		}
    		
    		}
    	keys = tempHashtable.keys;
    	vals = tempHashtable.vals;
    	M = tempHashtable.M;
    	
    }
    
    // Returns the minimum string in the table
    // Returns null if no such string exists
    public String min() {
    	// TODO
    	String minimustringinHashtable = null;
    	for(int i = 0; i < M; i++){
    		if(keys[i] !=null){
    			if(minimustringinHashtable == null || keys[i].compareTo(minimustringinHashtable) <= 0){
        			minimustringinHashtable = keys[i];	
    			}
    		}
    	}
    	return minimustringinHashtable;
    }
    
    // Returns the number of String that is less than the given String s
    public int rank(String s) {
    	// TODO
    	int keepingTrack =0;
    	//String temp= null;
    	for(int i = 0; i < keys.length; i++){
    		if(keys[i] == null)continue;
    			if(keys[i].compareTo(s) < 0) keepingTrack++;	
    		}
    	return keepingTrack;
    	}
    	//return count;
    

    /**
     * Removes the specified string from this hash table     
     * (if the string is in this table).    
     */
    public void delete(String s) {
    	// TODO

        // halves size of array if it's 12.5% full or less
        if (N > 0 && N <= M/8) resize(M/2);
    }
}