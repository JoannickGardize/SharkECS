package com.sharkecs.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import com.sharkecs.util.IntBag;

public class BagTestUtils {

	public static void assertBagEquals(IntBag bag, int... values) {
		assertBagEquals(true, bag, values);
	}

	public static void assertBagEquals(boolean assertSize, IntBag bag, int... values) {
		if (assertSize) {
			Assertions.assertEquals(values.length, bag.size());
		}
		for (int i = 0; i < values.length; i++) {
			Assertions.assertEquals(values[i], bag.get(i));
		}
	}

	public static void assertBagEqualsAnyOrder(IntBag bag, int... values) {
		Assertions.assertEquals(values.length, bag.size());
		List<Integer> remainingValues = new ArrayList<>(Arrays.stream(values).mapToObj(i -> Integer.valueOf(i)).collect(Collectors.toList()));
		for (int i = 0; i < bag.size(); i++) {
			if (!remainingValues.remove(Integer.valueOf(bag.get(i)))) {
				Assertions.fail("Bag element not found");
			}
		}
	}
}
