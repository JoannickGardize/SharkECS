package com.sharkecs.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.testutil.BagTestUtils;

class IntBagTest {

	@Test
	void insertionTest() {
		IntBag bag = new IntBag(1);
		bag.add(1);
		bag.add(2);
		bag.add(4);
		BagTestUtils.assertBagEquals(bag, 1, 2, 4);
	}

	@Test
	void addAllTest() {
		IntBag bag = new IntBag(2);
		IntBag toAdd = new IntBag(10);

		bag.add(1);
		bag.add(2);

		toAdd.add(4);
		toAdd.add(8);
		toAdd.add(16);

		bag.addAll(toAdd);

		BagTestUtils.assertBagEquals(bag, 1, 2, 4, 8, 16);
	}

	@Test
	void removeTest() {
		IntBag bag = new IntBag(10);
		bag.add(1);
		bag.add(2);
		bag.add(4);

		Assertions.assertEquals(4, bag.remove(1));
		BagTestUtils.assertBagEquals(bag, 1, 4);
		Assertions.assertEquals(4, bag.remove(1));
		BagTestUtils.assertBagEquals(bag, 1);
	}

	@Test
	void removeLastTest() {
		IntBag bag = new IntBag(10);
		bag.add(1);
		bag.add(2);
		bag.add(4);

		Assertions.assertEquals(4, bag.removeLast());
		BagTestUtils.assertBagEquals(bag, 1, 2);
	}

	@Test
	void putTest() {
		IntBag bag = new IntBag(4);
		bag.put(1, 10);
		bag.put(5, 20);
		BagTestUtils.assertBagEquals(false, bag, 0, 10, 0, 0, 0, 20);
	}

	@Test
	void setTest() {
		IntBag bag = new IntBag(4);
		bag.set(1, 1);
		bag.set(3, 3);
		bag.set(2, 2);
		BagTestUtils.assertBagEquals(bag, 0, 1, 2, 3);
	}

	@Test
	void clearTest() {
		IntBag bag = new IntBag(10);
		bag.add(1);
		bag.add(2);

		bag.clear();

		BagTestUtils.assertBagEquals(bag);
	}

	void isEmptyTest() {
		IntBag bag = new IntBag(10);
		Assertions.assertTrue(bag.isEmpty());
		bag.add(1);
		Assertions.assertFalse(bag.isEmpty());
		bag.remove(0);
		Assertions.assertTrue(bag.isEmpty());
	}
}
