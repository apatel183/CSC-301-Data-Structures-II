package hw7;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Random;

import java.lang.management.*;

public class HW7Test {
	String border = "*******************************************\n";
	String passed = "* Passed!                                 *\n";
	String failed = "* Failed!                                 *\n";
	String test;
	
	private interface HashSet<Value> {
		public int size();
		public void put(String s, Value v);
		public Value get(String s);
		public String min();
		public void delete(String s);
		public int rank(String arg);
	}
	
	private class HashLP extends HashLinearProbe<Integer> implements HashSet<Integer> {
		public HashLP() { super(); }
		public HashLP(int htsize) { super(htsize); }
	}	
	private class HashSC extends HashSepChain<Integer> implements HashSet<Integer> {
		public HashSC() { super(); }
		public HashSC(int htsize) { super(htsize); }
	}	
	
	AssertionError ae;
	Exception e;
	
	int size, psize;
	int p;
	String[] keys;
	Integer[] vals;
	boolean[] picked;
	
	String arg;
	
	HashSet<Integer> st;
	boolean sepChain;
	
	private HashSet<Integer> createSet(Random rand, int cap, int n) {
		HashSet<Integer> st = sepChain ? new HashSC() : new HashLP();
		if (cap > 0) st = sepChain ? new HashSC(cap) : new HashLP(cap);
		if (keys != null) {
			randomVals(rand, keys.length);
			for (int i = 0; i < keys.length && i < n; i++) st.put(keys[i],vals[i]);
		}
		return st;
	}
	
	private void randomVals(Random rand, int size) {
		vals = new Integer [size];
		for (int i = 0; i < size; i++) vals[i] = rand.nextInt(200);
	}
	
	private String keysToString(boolean justPicked) {
		int sz = justPicked ? psize : size;
		StringBuilder s = new StringBuilder();
		s.append("size = " + sz + ", keys = ");
		int i, k;
		for (i = 0, k = 0; i < sz - 1; i++, k++) {
			if (justPicked) for (; p == k || !picked[k]; k++);
			s.append(keys[k] + ", ");
		}
		if (sz > 0) {
			if (justPicked) for (; p == k || !picked[k]; k++);
			s.append(keys[k]);
		}
		
		return s.toString();
	}

	public static String twoLetterString(int val) {
		char chars[] = new char[2];
		chars[0] = (char)('A' + ((val / 26) % 26));
		chars[1] = (char)('A' + (val % 26));
		return String.valueOf(chars);
	}
	
	public static String randomTwoLetterAbove(Random rand, int threshold) {
		int val = threshold + 1 + rand.nextInt(26 * 26 - threshold - 1);
		return twoLetterString(val);
	}
	
	public static String randomTwoLetterBelow(Random rand, int threshold) {
		int val = rand.nextInt(threshold);
		return twoLetterString(val);
	}
	
	private void randomTwoLetterStringsAbove(Random rand, int i0, int i1, int threshold) {
		threshold++;
		int space = 26 * 26 - threshold;
		int size = i1 - i0;
		int i, val;
		
		boolean[] taken = new boolean[space];
		for (i = 0; i < size; i++) {
			while(true) {
				val = rand.nextInt(space);
				if (!taken[val]) break;
			}
			taken[val] = true;
		}
		for (i = i0, val = 0; val < space; val++) {
			if (taken[val]) keys[i++] = twoLetterString(val + threshold);
		}
	}
	
	private void randomTwoLetterStringsBelow(Random rand, int i0, int i1, int threshold) {
		int space = threshold;
		int size = i1 - i0;
		int i, val;
		
		boolean[] taken = new boolean[space];
		for (i = 0; i < size; i++) {
			while(true) {
				val = rand.nextInt(space);
				if (!taken[val]) break;
			}
			taken[val] = true;
		}
		for (i = i0, val = 0; val < space; val++) {
			if (taken[val]) keys[i++] = twoLetterString(val);
		}
	}
	
	private void tstRank() {
		Random rand = new Random(0);
		for (size = 0; size <= 100; size++) {
			keys = new String[size];
			int threshold = rand.nextInt(13 * 13 * 2) + 13 * 13; 
			String randStr = twoLetterString(threshold);
			for (int j = size - 1; j >= 0; j--) {
				randomTwoLetterStringsBelow(rand, 0, j, threshold - 1);
				keys[j] = randStr;
				randomTwoLetterStringsAbove(rand, j + 1, size, threshold + 1);
				HashSet<Integer> st = createSet(rand, size * 4, size);
				arg = keys[j];
				assertEquals(j, st.rank(arg));
				arg = twoLetterString(threshold - 1);
				assertEquals(j, st.rank(arg));
				arg = twoLetterString(threshold + 1);
				assertEquals(j + 1, st.rank(arg));
			}
			arg = randStr;
			assertEquals(0, createSet(rand, size * 4, size).rank(arg));
		}
	}
	
	private void tstDel() {
		HashSet<Integer> st;
		Random rand = new Random(0);
		int i, k;
		for (size = 1; size <= 100; size++) {
			keys = new String[size];
			picked = new boolean[size];
			randomTwoLetterStringsBelow(rand, 0, size, 26 * 26);
			st = createSet(rand, size * 4, size);
			for (psize = 0; psize < size; psize++) {
				while(true) {
					p = rand.nextInt(size);
					if (!picked[p]) break;
				}
				picked[p] = true;
				st.delete(keys[p]);
				// testing size and keys
				assertEquals(size - psize - 1, st.size());
				for (i = 0, k = 0; i < size - psize - 1; i++, k++) {
					for (; picked[k]; k++);
					assertTrue("does not contain " + keys[k], st.get(keys[k]) != null);
				}
			}
		}
	}
	
	private void tstMin() {
		// base case (size  == 0)
		size = 0;
		p = 0;
		assertEquals(null, createSet(null, 0, 0).min());
		
		// random cases (size >= 1)
		Random rand = new Random(0);
		for (size = 1; size <= 100; size++) {
			keys = new String[size];
			int threshold = rand.nextInt(13 * 13); 
			keys[0] = twoLetterString(threshold);
			randomTwoLetterStringsAbove(rand, 1, size, threshold);
			assertEquals(keys[0], createSet(rand, size * 4, size).min());
		}
	}
	
	private void randomKeys(int size) {
		this.size = size;
		int mask = 16;
		for (mask = 16; mask < size; mask *= 16);
		
		keys = new String[size];
		vals = new Integer[size];
		
		for (int i = 0; i < size; i++) {
			keys[i] = Integer.toHexString(mask | i).toUpperCase().substring(1);
			vals[i] = i;
		}
	}
	
	private void printTimes(int l, int[] sizes, long[] times, long[] avg) {
		for (int i = 0; i < l; i++) {
	
			System.out.println("Time elapsed for size " + sizes[i] + ": " + times[i] + " => average (per key) : " + avg[i]);
		}		
	}
	
	private void tstResize() {
		ThreadMXBean thread = ManagementFactory.getThreadMXBean();
		
		st = createSet(null, 0, 0);
		randomKeys(1024 * 1024);
		System.out.println("Testing efficiency for " + size + " keys in total.");
		
		int[] sizes = new int[11];
		long[] times = new long[11];
		long[] avg = new long[11];
		
		long nanoTime = 0;
		boolean usingNanoTime = false;
		int beginInd = 0;
		int failures = 0;

		int i, l;
		for (l = 0; l < 11; l++) {
			sizes[l] = size >> (10 - l);
			times[l] = thread.getCurrentThreadCpuTime();
			nanoTime = System.nanoTime();
			for (i = 0; i < sizes[l]; i++) {
				st.put(keys[i], vals[i]);
			}
			times[l] = thread.getCurrentThreadCpuTime() - times[l];
			if (times[l] == 0) usingNanoTime = true;
			if (usingNanoTime) {
				times[l] = System.nanoTime() - nanoTime;
			}
			avg[l] = times[l] / sizes[l];
			if (usingNanoTime && l < 3) {
				if (avg[beginInd] > 2 * avg[l]) beginInd = l;
			} else {
				if (avg[l] > avg[beginInd] * 8) failures++;
			}
			if ((!usingNanoTime && failures > 0) || failures > 2) {
				if (usingNanoTime) System.out.println("USING NANO TIME!!!");
				printTimes(l + 1, sizes, times, avg);
				System.out.println("Put is inefficient without resize!");
				fail("Put is inefficient without resize!");
			}
		}
		if (usingNanoTime) System.out.println("USING NANO TIME!!!");
		printTimes(l, sizes, times, avg);
		System.out.println("Passed put performance test!");
	}
	
	private void testMethod(int methodID, boolean sepChain) throws Exception {
		try {
			this.sepChain = sepChain;
			System.out.print(border + test + border);
			switch (methodID) {
			case 0: tstResize(); break;
			case 1: tstMin(); break;
			case 2: tstRank(); break;
			case 3: tstDel(); break;
			}
		} catch(AssertionError aerr) {
			ae = aerr;
		} catch(Exception err) {
			e = err;
		}
		
		if (ae != null || e != null) {

			if (size <= 100) {
				System.out.println("Fails on the below strings:");
				System.out.println(keysToString(false));
				switch (methodID) {
				case 0: break;
				case 1: System.out.println("Failure while computing the minimum String"); break;
				case 2: System.out.println("Failure while computing the rank of " + arg); break;
				case 3: System.out.println("Failure in deleting key = " + keys[p] +  " after successfully deleting the following:");
						System.out.println(keysToString(true));
						break;
				}
			}
			
			System.out.print("\n" + border + test + failed + border);
			
			if (ae != null) throw ae;
			if (e != null) throw e;
		} else {
			System.out.print(border + test + passed + border);
		}
	}
	
	@Test
	public void testResizeSepChain() throws Exception {
		test = "* Testing resize (SepChain)               *\n";
		testMethod(0, true);
	}
	
	@Test
	public void testMinSepChain() throws Exception {
		test = "* Testing min (SepChain)                  *\n";
		testMethod(1, true);
	}
	
	@Test
	public void testRankSepChain() throws Exception {
		test = "* Testing rank (SepChain)                 *\n";
		testMethod(2, true);
	}
	
	@Test
	public void testDeleteSepChain() throws Exception {
		test = "* Testing delete (SepChain)               *\n";
		testMethod(3, true);
	}
	
	@Test
	public void testResizeLinProbe() throws Exception {
		test = "* Testing resize (LinProbe)               *\n";
		// comment out the below fail line to test resize in linear probe.
		//fail("Avoiding infinite loop");
		testMethod(0, false);
	}
	
	@Test
	public void testMinLinProbe() throws Exception {
		test = "* Testing min (LinProbe)                  *\n";
		testMethod(1, false);
	}
	
	@Test
	public void testKeysLinProbe() throws Exception {
		test = "* Testing rank (LinProbe)                 *\n";
		testMethod(2, false);
	}
	
	@Test
	public void testDeleteLinProbe() throws Exception {
		test = "* Testing delete (LinProbe)               *\n";
		testMethod(3, false);
	}
}
