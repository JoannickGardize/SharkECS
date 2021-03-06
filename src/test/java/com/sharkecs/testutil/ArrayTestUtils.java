package com.sharkecs.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;

public class ArrayTestUtils {

	public static void assertEqualsAnyOrder(Object[] actual, Object... expected) {
		Assertions.assertEquals(expected.length, actual.length);
		List<Object> remainingValues = new ArrayList<>(Arrays.asList(actual));
		for (int i = 0; i < expected.length; i++) {
			if (!remainingValues.remove(expected[i])) {
				Assertions.fail("Array element not found: " + expected[i]);
			}
		}
	}
}
