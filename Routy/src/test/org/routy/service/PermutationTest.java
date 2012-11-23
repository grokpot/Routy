package org.routy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.test.AndroidTestCase;

public class PermutationTest extends AndroidTestCase {

	private int permutations;
	
	@Override
  public void setUp() throws Exception {
		super.setUp();
		permutations = 0;
	}
	
	
	public void testPermutationBuilder() {
		Integer[] intList = {1, 2, 3, 4, 5};
		List<Integer> startingList = Arrays.asList(intList);
		
		long start = System.currentTimeMillis();
		permute(startingList, new ArrayList<Integer>());
		System.out.println("Total time: " + (System.currentTimeMillis() - start) + "ms");
		
		if (permutations != factorial(intList.length)) {
			System.err.println("Incorrect number of permutations.");
		} else {
			System.out.println(permutations + " permutations computed.");
		}
	}
	
	
	private int factorial(int n) {
		if (n == 2) {
			return n;
		} else {
			return n * factorial(n-1);
		}
	}
	
	
	private void permute(List<Integer> pool, List<Integer> permutation) {
		if (pool.size() == 0) {
			printPermutation(permutation);
			permutations++;
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
	
	
	@Override
  public void tearDown() throws Exception {
		super.tearDown();
	}
	
}
