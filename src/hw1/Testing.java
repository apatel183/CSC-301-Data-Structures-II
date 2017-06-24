package hw1;

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
	
	private static LinkedListST<String, Integer> newLinkedList(int caseId) {
		LinkedListST<String, Integer> list = new LinkedListST<String, Integer>();
		for (int i = 0; i < cases[caseId].length; i++) {
			list.put(entries[cases[caseId][i]].key, entries[cases[caseId][i]].val);
		}
		return list;
	}

	public static void testSize() {
		int result;
		int answers[] = { 0, 1, 2, 3, 3, 4, 2, 6, 5, 10 };  
		
		for (int i = 0; i < cases.length; i++) {
			result = newLinkedList(i).size();
			if (result != answers[i]) wrongResult(i, result);
		}
	}
	
	public static void test2ndMax() {
		String result;
		String answers[] = { null, null, "E", "C", "S", "U", "H", "X", "P", "X" };  
		
		for (int i = 0; i < cases.length; i++) {
			result = newLinkedList(i).secondMaxKey();
			if (result == null ? answers[i] != null : !result.equals(answers[i])) {
				wrongResult(i, result);
			}
		}
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
				result = newLinkedList(i).rank(keys[j]);
				if (result != answers[i][j]) {
					wrongResult("" + i + " with elem = " + keys[j], result);
					break;
				}
			}
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
				result = newLinkedList(i).floor(keys[j]);
				if (result == null ? answers[i][j] != null : !result.equals(answers[i][j])) {
					wrongResult("" + i + " with elem = " + keys[j], result);
					break;
				}
			}
		}
	}

	public static void testInverse() {
		LinkedListST<String, Integer> list;
		LinkedListST<Integer, String> result;
		String errorStr;
		
		for (int i = 0; i < cases.length; i++) {
			list = newLinkedList(i);
			result = list.inverse();
			list = newLinkedList(i);
			errorStr = "";
			for (int j = 0; j < cases[i].length; j++) {
				Integer val = entries[cases[i][j]].val;
				String k = result.get(val);
				if (k == null) {
					errorStr = "value " + val + " is not in inverse";
					break;
				}
				if (list.get(k) == null) {
					errorStr = "value " + val + " has been assigned a key that is not in the original list";
					break;
				}
			}
			if (errorStr != "") wrongResult(i + " with error " + errorStr, result);
		}
	}
	
	private static void testMethod(int methodId) {
		String methodStr[] = { "size", "secondMax", "rank", "floor", "inverse" };
		
		printIntroMessage(methodStr[methodId]);
		try {
			switch(methodId) {
			case 0: testSize(); break;
			case 1: test2ndMax(); break;
			case 2: testRank(); break;
			case 3: testFloor(); break;
			case 4: testInverse(); break;
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
		cases[3] = new int[]{ 1, 0, 3 };
		cases[4] = new int[]{ 9, 0, 6 };
		cases[5] = new int[]{ 0, 3, 7, 8 };
		cases[6] = new int[]{ 4, 3 };
		cases[7] = new int[]{ 8, 9, 0, 2, 3, 6 };
		cases[8] = new int[]{ 8, 0, 3, 4, 5 };
		cases[9] = new int[]{ 2, 3, 4, 0, 5, 6, 1, 7, 8, 9 };
		
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