/**
 * Your homework is to complete the methods marked TODO.
 * You must not change the declaration of any method.
 */

package hw2;

/**
 *  The SortedArrayST class represents an (ordered) symbol table of
 *  generic key-value pairs.  It supports put, get, and delete methods.
 */
public class SortedArrayST<Key extends Comparable<Key>, Value> {
	private static final int MIN_SIZE = 2;
	private Key[] keys;      // the keys array
	private Value[] vals;    // the values array
	private int N = 0;       // size of the symbol table
	
	/**
	 * Initializes an empty symbol table.
	 */
	public SortedArrayST() {
		this(MIN_SIZE);
	}
	
	/**
	 * Initializes an empty symbol table of given size.
	 */
	@SuppressWarnings("unchecked")
	public SortedArrayST(int size) {
		keys = (Key[])(new Comparable[size]);
		vals = (Value[])(new Object[size]);
	}
	
	/**
	 * Initializes a symbol table with given sorted key-value pairs.
	 * If given keys list is not sorted in (strictly) increasing order,
	 * then the input is discarded and an empty symbol table is initialized.
	 */
	public SortedArrayST(Key[] keys, Value[] vals) {
		this(keys.length < MIN_SIZE ? MIN_SIZE : keys.length);
		N = (keys.length == vals.length ? keys.length : 0);
		int i;
		for (i = 1; i < N && keys[i].compareTo(keys[i - 1]) > 0; i++);
		if (i < N) { // input is not sorted
			System.err.println("SortedArrayST(Key[], Value[]) constructor error:");
			System.err.println("Given keys array of size " + N + " was not sorted!");
			System.err.println("Initializing an empty symbol table!");
			N = 0;
		} else {
			for (i = 0; i < N; i++) {
				this.keys[i] = keys[i];
				this.vals[i] = vals[i];
			}
		}
	}
	
	/**
	 * Returns the keys array of this symbol table.
	 */
	public Comparable<Key>[] keysArray() {
		return keys;
	}
	
	/**
	 * Returns the values array of this symbol table.
	 */
	public Object[] valsArray() {
		return vals;
	}
	
	/**
	 * Returns the number of keys in this symbol table.
	 */
	public int size() {
		return N;
	}
	
	/**
	 * Returns whether the given key is contained in this symbol table at index r.
	 */
	private boolean checkFor(Key key, int r) {
		return (r >= 0 && r < N && key.equals(keys[r]));
	}
	
	/**
	 * Returns the value associated with the given key in this symbol table.
	 */
	public Value get(Key key) {
		int r = rank(key);
		if (checkFor(key, r)) return vals[r];
		else return null;
	}
	
	/**
	 * Inserts the specified key-value pair into the symbol table, overwriting the old 
	 * value with the new value if the symbol table already contains the specified key.
	 * Deletes the specified key (and its associated value) from this symbol table
	 * if the specified value is null.
	 */
	public void put(Key key, Value val) {
		int r = rank(key);
		if (!checkFor(key, r)) {
			shiftRight(r);
			keys[r] = key;
		}
		vals[r] = val;
	}
	
	/**
	 * Removes the specified key and its associated value from this symbol table     
	 * (if the key is in this symbol table).    
	 */
	public void delete(Key key) {
		int r = rank(key);
		if (checkFor(key, r)) {
			shiftLeft(r);
		}
	}
	// Bonus Question with resizing the array.
	
	private void resizeArray(int volume){
		Key[] holderForkeys = (Key[]) new Comparable[volume];
		Value[] holderForvalues = (Value[]) new Object[volume];
		for(int x = 0; x < N; x++){
			holderForkeys[x] = keys[x];
			holderForvalues[x] = vals[x];
		}
		vals = holderForvalues;
		keys = holderForkeys;
	}
	
	/**
	 * Shifts the keys (and values) at indices r and above to the right by one
	 */
	private void shiftRight(int r) {
		if (N == keys.length)
			resizeArray(keys.length*2);
		for (int x= N; x > r; x--){
		keys[x]=keys[x-1];
		vals[x]=vals[x-1];
	}
	N++;
	}
	/**
	 * Shifts the keys (and values) at indices x > r to the left by one
	 * in effect removing the key and value at index r 
	 */
	private void shiftLeft(int r) {
		if (N == keys.length)
			resizeArray(keys.length * 2);
		for (int x = r; x < N; x++){
			keys[x]=keys[x+1];
			vals[x]=vals[x+1];	
		}
		N--;// TODO
	}
	/**
	 * rank returns the number of keys in this symbol table that is less than the given key. 
	 */
	public int rank(Key key) {
		int lowKey = 0;
		int highKey = N - 1;
		
		while (lowKey <= highKey){
			int middle = lowKey + (highKey - lowKey) / 2;
			if(key.compareTo(keys[middle]) > 0){
				lowKey = middle + 1;
			}
			else if (key.compareTo(keys[middle]) < 0){
				highKey = middle - 1;
			}
			else return middle;
			}
		return lowKey;
		//return linearTimeRank(key);
		// TODO : logarithmic time implementation
	}
	
	/**
	 * Linear time implementation of rank
	 */
	private int linearTimeRank(Key key) {
		int r;
		for (r = 0; r < N && key.compareTo(keys[r]) > 0; r++);
		return r;
	}
	
	/**
	 * floor returns the largest key in the symbol table that is less than or equal to key.
	 * it returns null if there is no such key.
	 */
	public Key floor(Key key) {
        int x = rank(key);
        if (x < N && key.compareTo(keys[x]) == 0) 
        	return keys[x];
        if (x == 0)
        	return null;
        else 
        	return keys[x-1];// TODO
	}
	
	/**
	 * countRange returns the number of keys within the range (key1, key2) (inclusive)
	 * note that keys may not be in order (key1 may be larger than key2)
	 */
	public int countRange(Key key1, Key key2) { 
		int range = 0;
		if (key1.compareTo(key2)<=0)
			range= rank(key2)- rank(key1);
		else 
			range= rank(key1)-rank(key2);
		Value key1Value = get(key1);
		Value key2Value = get(key2);
		if (key1.compareTo(key2)< 0 && key2Value!= null)
			range++;
		if (key1.compareTo(key2)>= 0 && key1Value!= null)
			range++;
		return range;
		// TODO
	}
}