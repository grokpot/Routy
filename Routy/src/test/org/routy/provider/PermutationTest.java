package org.routy.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.test.AndroidTestCase;

public class PermutationTest extends AndroidTestCase {

	public void setUp() throws Exception {
		super.setUp();
	}
	
	
	public void testPermutationBuilder() {
		Integer[] intList = {1, 2, 3, 4, 5};
		List<Integer> startingList = Arrays.asList(intList);
		
		long start = System.currentTimeMillis();
		permute(startingList, new ArrayList<Integer>());
		System.out.println("Computed permutations in " + (System.currentTimeMillis() - start) + "ms");
	}
	
	
	private void permute(List<Integer> pool, List<Integer> permutation) {
		if (pool.size() == 0) {
			printPermutation(permutation);
		} else {
			for (int i = 0; i < pool.size(); i++) {
				List<Integer> newPermutation = new ArrayList<Integer>(permutation);
				newPermutation.add(pool.get(i));
				
				List<Integer> newPool = new ArrayList<Integer>(pool);
				newPool.remove(i);
				
				permute(newPool, newPermutation);
			}
		}
	}
	
	
	private void printPermutation(List<Integer> permutation) {
		StringBuilder sb = new StringBuilder("[");
		
		for (int i = 0; i < permutation.size() - 1; i++) {
			sb.append(permutation.get(i));
			sb.append(", ");
		}
		
		sb.append(permutation.get(permutation.size() - 1));
		sb.append("]");
		
		System.out.println(sb.toString());
	}
	
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
}
