package hw2;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class Testing {
	private static class Entry {
		String key;
		Integer val;
		
		public Entry(String key, Integer val) {
			this.key = key;
			this.val = val;
		}
	}
	
	private final static Entry[] entries = new Entry[10];
	private final static int[][] cases = new int[10][];
	private static boolean passed;
	private static String keys[];
	private static Integer vals[];
	
	private static void wrongResult(Object case_id, Object result) {
		System.out.println("Returned incorrect value " + result + " for case " + case_id);
		passed = false;
	}
	
	private static void printIntroMessage(String methodStr) {
		passed = true;
		System.out.println("Testing " + methodStr);
	}
	
	private static void printPassMessage(String methodStr) {
		System.out.print("Done testing " + methodStr + "...");
		if (passed) {
			System.out.println("all tests passed!");
		} else {
			System.out.println("some tests failed!");
		}
		System.out.println();
		System.out.println("**********");
		System.out.println();
	}
	
	private static SortedArrayST<String, Integer> newSortedArray(int caseId) {
		int size = cases[caseId].length;
		keys = new String[size];
		vals = new Integer[size];
		for (int i = 0; i < cases[caseId].length; i++) {
			keys[i] = entries[cases[caseId][i]].key;
			vals[i] = entries[cases[caseId][i]].val;
		}
		SortedArrayST<String, Integer> sortedarray = new SortedArrayST<String, Integer>(keys, vals);
		return sortedarray;
	}
	
	private static final int randomIndices[] = { 1, 5, 9, 2, 0, 4, 3, 8, 6, 7 };
	
	private static void printPutActions(int i, int j) {
		for (int indj = 0; indj <= j; indj++) {
			int k = randomIndices[indj];
			if (k < cases[i].length) {
				System.out.println("put(" + keys[k] + ", " + vals[k] + ")");
			}
		}
	}
	
	private static void printAnyArray(Object []array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print("'" + array[i] + "' ");
		}
	}
	
	private static void printArray(String exp, Object[] keys, Object[] vals) {
		System.out.print(exp + " keys array: ");
		printAnyArray(keys);
		System.out.println();
		System.out.print(exp + " vals array: ");
		printAnyArray(vals);
		System.out.println();
	}

	public static void testShiftRight() {
		int size;
		SortedArrayST<String, Integer> st;
		for (int i = 0; i < cases.length; i++) {
			newSortedArray(i);
			st = new SortedArrayST<String, Integer>();
			size = 0;
			for (int j = 0; j < 10; j++) {
				int k = randomIndices[j];
				if (k < cases[i].length) {
					size++;
					st.put(keys[k], vals[k]);
					// testing size
					int stsize = st.size();
					if (size != stsize) {
						wrongResult("" + i + " (size not correctly updated) --- size should be " + size + " after calling below methods:", stsize);
						printPutActions(i, j);
						return;
					}
					
					Comparable<String>[] stKeys = st.keysArray();
					Object[] stVals = st.valsArray();
					// testing keys and vals
					int lastIndex = 0;
					int ceil;
					for (int s = 0; s < size; s++) {
						ceil = 10;
						for (int ind = 0; ind <= j; ind++) {
							int indk = randomIndices[ind];
							if (indk >= lastIndex && indk < ceil) ceil = indk;
						}
						String ky = keys[ceil];
						Integer vl = vals[ceil];
						if (stKeys[s] == null || stVals[s] == null || !ky.equals(stKeys[s]) || !vl.equals(stVals[s])) {
							wrongResult("" + i + " dictionary not correct after calling below methods: ", "(returned keys and vals array below)");
							printPutActions(i, j);
							printArray("Returned", stKeys, stVals);
							return;
						}
						lastIndex = ceil + 1;
					}
				}
			}
		}
	}
	
	private static void printDeleteActions(int i, int j) {
		for (int indj = 0; indj <= j; indj++) {
			int k = randomIndices[indj];
			if (k < cases[i].length) {
				System.out.println("delete(" + keys[k] + ")");
			}
		}
	}
	
	public static void testShiftLeft() {
		int size;
		SortedArrayST<String, Integer> st;
		for (int i = 0; i < cases.length; i++) {
			st = newSortedArray(i);
			size = st.size();
			for (int j = 0; j < 10; j++) {
				int k = randomIndices[j];
				if (k < cases[i].length) {
					size--;
					st.delete(keys[k]);
					// testing size
					int stsize = st.size();
					if (size != stsize) {
						wrongResult("" + i + " (size not correctly updated) --- size should be " + size + " after calling below methods:", stsize);
						printArray("Original", keys, vals);
						printDeleteActions(i, j);
						return;
					}
					
					Comparable<String>[] stKeys = st.keysArray();
					Object[] stVals = st.valsArray();
					// testing keys and vals
					int lastIndex = 0;
					int ceil;
					for (int s = 0; s < size; s++) {
						ceil = 10;
						for (int ind = j + 1; ind < 10; ind++) {
							int indk = randomIndices[ind];
							if (indk >= lastIndex && indk < ceil) ceil = indk;
						}
						String ky = keys[ceil];
						Integer vl = vals[ceil];
						if (stKeys[s] == null || stVals[s] == null || !ky.equals(stKeys[s]) || !vl.equals(stVals[s])) {
							wrongResult("" + i + " dictionary not correct after calling below methods: ", "(returned keys and vals array below)");
							printArray("Original", keys, vals);
							printDeleteActions(i, j);
							printArray("Returned", stKeys, stVals);
							return;
						}
						lastIndex = ceil + 1;
					}
				}
			}
		}
	}

	private static void randomKeyVals(int size) {
		int mask = 16;
		for (mask = 16; mask < size; mask *= 16);
		
		keys = new String[size];
		vals = new Integer[size];
		
		for (int i = 0; i < size; i++) {
			keys[i] = Integer.toHexString(mask | i).toUpperCase().substring(1);
			vals[i] = i;
		}
	}
	
	private static void printTimes(int l, int[] sizes, long[] times, long[] avg) {
		for (int i = 0; i < l; i++) {
			System.out.println("Time elapsed for size " + sizes[i] + ": " + times[i] + " => average (per key) : " + avg[i]);
			System.out.flush();
		}		
	}
	
	private static void testRankPerformance() {
		ThreadMXBean thread = ManagementFactory.getThreadMXBean();
		SortedArrayST<String, Integer> st;
		String[] stKeys;
		Integer[] stVals; 
		
		int size = 1024 * 1024;
		randomKeyVals(size);
		System.out.println("Testing efficiency for " + size + " keys in total.");
		
		int[] sizes = new int[11];
		long[] times = new long[11];
		long[] avg = new long[11];
		
		long nanoTime = 0;
		boolean usingNanoTime = false;
		int beginInd = 0;
		int failures = 0;

		int i, j, l, sz;
		for (l = 0; l < 11; l++) {
			sizes[l] = size >> (10 - l);
			stKeys = new String[sizes[l]];
			stVals = new Integer[sizes[l]];
			for (i = 0, j = 0, sz = size / sizes[l]; i < sizes[l]; i++, j += sz) {
				stKeys[i] = keys[j];
				stVals[i] = vals[j];
			}
			st = new SortedArrayST<String, Integer>(stKeys, stVals);
			times[l] = thread.getCurrentThreadCpuTime();
			nanoTime = System.nanoTime();
			for (i = 0; i < size; i += (i % sz == 0 ? 1 : sz - 1)) {
				Integer result = st.rank(keys[i]);
				if (result == null || result.intValue() != (i + sz - 1) / sz) {
					wrongResult("rank of " + keys[i] + " is not correct in performance test", result);
					return;
				}
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
				System.out.println("Search takes more than logarithmic time.");
				System.out.println("Failed rank performance test!");
				passed = false;
				return;
			}
		}
		if (usingNanoTime) System.out.println("USING NANO TIME!!!");
		printTimes(l, sizes, times, avg);
		System.out.println("Passed rank performance test!");
	}
	
	public static void testRank() {
		String keys[] = { "A", "B", "F", "S", "Z"};
		int result;
		int answers[][] = { { 0, 0, 0, 0, 0 }, // case 0
				            { 0, 1, 1, 1, 1 }, // case 1
				            { 0, 0, 1, 2, 2 }, // case 2
				            { 0, 1, 2, 3, 3 }, // case 3
				            { 0, 1, 1, 1, 2 }, // case 4
				            { 0, 1, 1, 2, 4 }, // case 5
				            { 0, 0, 0, 2, 2 }, // case 6
				            { 0, 1, 2, 3, 5 }, // case 7
				            { 0, 1, 1, 4, 5 }, // case 8
				            { 0, 1, 3, 6, 9 }, // case 9
				          };  
		
		for (int i = 0; i < cases.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				result = newSortedArray(i).rank(keys[j]);
				if (result != answers[i][j]) {
					wrongResult("" + i + " with elem = " + keys[j], result);
					break;
				}
			}
		}
		
		if (passed) {
			System.out.println("Passed rank correctness test!");
			testRankPerformance();
		}
	}

	public static void testFloor() {
		String keys[] = { "A", "D", "F", "K", "Y"};
		String result;
		String answers[][] = { { null, null, null, null, null }, // case 0
	                           { "A", "A", "A", "A", "A" }, // case 1
	                           { null, null, "E", "K", "K" }, // case 2
	                           { "A", "C", "C", "H", "H" }, // case 3
	                           { "A", "A", "A", "A", "S" }, // case 4
	                           { "A", "A", "A", "H", "X" }, // case 5
	                           { null, null, null, "K", "K" }, // case 6
	                           { "A", "A", "E", "H", "X" }, // case 7
	                           { "A", "A", "A", "K", "X" }, // case 8
	                           { "A", "C", "E", "K", "X" }, // case 9
	                         };  
		
		for (int i = 0; i < cases.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				result = newSortedArray(i).floor(keys[j]);
				if (result == null ? answers[i][j] != null : !result.equals(answers[i][j])) {
					wrongResult("" + i + " with elem = " + keys[j], result);
					break;
				}
			}
		}
	}

	public static void testRange() {
		String keys[][] = { {"A", "A"}, {"A", "F"}, {"B", "H"}, {"E", "X"}, {"K", "Z"} };
		int result;
		int answers[][] = { { 0, 0, 0, 0, 0 }, // case 0
	                        { 1, 1, 0, 0, 0 }, // case 1
	                        { 0, 1, 1, 2, 1 }, // case 2
	                        { 1, 2, 2, 1, 0 }, // case 3
	                        { 1, 1, 0, 1, 2 }, // case 4
	                        { 1, 1, 1, 3, 2 }, // case 5
	                        { 0, 0, 1, 2, 1 }, // case 6
	                        { 1, 2, 2, 4, 3 }, // case 7
	                        { 1, 1, 1, 4, 3 }, // case 8
	                        { 1, 3, 3, 7, 6 }, // case 9
	          			  };  
		
		for (int i = 0; i < cases.length; i++) {
			SortedArrayST<String, Integer> st = newSortedArray(i);
			for (int j = 0; j < keys.length; j++) {
				result = st.countRange(keys[j][0], keys[j][1]);
				if (result != answers[i][j]) {
					wrongResult("" + i + " with range = (" + keys[j][0] + "," + keys[j][1] + ")", result);
					break;
				}
				if (!keys[j][0].equals(keys[j][1])) {
					result = st.countRange(keys[j][1], keys[j][0]);
					if (result != answers[i][j]) {
						wrongResult("" + i + " with range = (" + keys[j][1] + "," + keys[j][0] + ")", result);
						break;
					}
				}
			}
		}
	}
	
	private static void testMethod(int methodId) {
		String methodStr[] = { "shiftRight", "shiftLeft", "rank", "floor", "countRange" };
		
		printIntroMessage(methodStr[methodId]);
		try {
			switch(methodId) {
			case 0: testShiftRight(); break;
			case 1: testShiftLeft(); break;
			case 2: testRank(); break;
			case 3: testFloor(); break;
			case 4: testRange(); break;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace(System.out);
			System.out.flush();
			passed = false;
		}
		printPassMessage(methodStr[methodId]);
	}

	public static void main(String[] args) {
		initializeEntries();
		
		cases[0] = new int[]{ };
		cases[1] = new int[]{ 0 };
		cases[2] = new int[]{ 2, 4 };
		cases[3] = new int[]{ 0, 1, 3 };
		cases[4] = new int[]{ 0, 6, 9 };
		cases[5] = new int[]{ 0, 3, 7, 8 };
		cases[6] = new int[]{ 3, 4 };
		cases[7] = new int[]{ 0, 2, 3, 6, 8, 9 };
		cases[8] = new int[]{ 0, 3, 4, 5, 8 };
		cases[9] = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		
		for (int i = 0; i < 5; i++) {
			testMethod(i);
		}
	}
	
	private static void initializeEntries() {
		entries[0] = new Entry("A", 5);
		entries[1] = new Entry("C", 4);
		entries[2] = new Entry("E", 2);
		entries[3] = new Entry("H", 3);
		entries[4] = new Entry("K", -5);
		entries[5] = new Entry("P", -1);
		entries[6] = new Entry("S", 0);
		entries[7] = new Entry("U", 5);
		entries[8] = new Entry("X", 3);
		entries[9] = new Entry("Z", 9);
	}
}