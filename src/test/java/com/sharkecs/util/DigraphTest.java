package com.sharkecs.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sharkecs.util.Digraph.Node;

class DigraphTest {

	private String o1 = "o1";
	private String o2 = "o2";
	private String o3 = "o3";
	private String o4 = "o4";
	private String o5 = "o5";
	private String o6 = "o6";
	private String o7 = "o7";
	private String o8 = "o8";
	private String o9 = "o9";

	@Test
	void getAllConnectedNodesTest() {
		Digraph<Object> digraph = new Digraph<>();

		digraph.precedes(o1, o2, o3);
		digraph.follows(o4, o3);
		digraph.precedes(o5, o6);

		Set<Node<Object>> connected = digraph.getAllConnectedNodes(o4);

		Set<Object> expected = new HashSet<>(Arrays.asList(o1, o2, o3, o4));
		Set<Object> actual = new HashSet<>(connected.stream().map(Node::getValue).collect(Collectors.toList()));

		Assertions.assertEquals(expected, actual);
	}

	@Test
	void findAnyCycleTest() {
		Digraph<Object> digraph = new Digraph<>();

		digraph.precedes(o1, o2);
		digraph.precedes(o2, o3);
		digraph.precedes(o3, o1);
		digraph.precedes(o3, o4);

		digraph.precedes(o5, o6, o7);
		digraph.precedes(o6, o7, o8);
		digraph.precedes(o6, o8);

		assertEqualsCyclic(Arrays.asList(o1, o2, o3), digraph.findAnyCycle(digraph.getAllConnectedNodes(o4)).stream().map(Node::getValue).collect(Collectors.toList()));
		assertEqualsCyclic(Arrays.asList(), digraph.findAnyCycle(digraph.getAllConnectedNodes(o6)).stream().map(Node::getValue).collect(Collectors.toList()));
	}

	@Test
	void computeDepthTest() throws GraphCycleException {
		Digraph<Object> digraph = new Digraph<>();

		digraph.precedes(o1, o2, o4);
		digraph.precedes(o2, o4, o8, o7);
		digraph.precedes(o3, o4);
		digraph.precedes(o4, o5, o6, o7);
		digraph.precedes(o5, o7);
		digraph.precedes(o8, o7);
		digraph.precedes(o9, o6);

		Map<Object, Integer> depths = digraph.computeDepth(o4);

		Assertions.assertEquals(9, depths.size());
		int referenceDepth = depths.get(o1);
		Assertions.assertEquals(0, depths.get(o1) - referenceDepth);
		Assertions.assertEquals(1, depths.get(o2) - referenceDepth);
		Assertions.assertEquals(1, depths.get(o3) - referenceDepth);
		Assertions.assertEquals(2, depths.get(o4) - referenceDepth);
		Assertions.assertEquals(3, depths.get(o5) - referenceDepth);
		Assertions.assertEquals(4, depths.get(o6) - referenceDepth);
		Assertions.assertEquals(4, depths.get(o7) - referenceDepth);
		Assertions.assertEquals(3, depths.get(o8) - referenceDepth);
		Assertions.assertEquals(3, depths.get(o9) - referenceDepth);

		Digraph<Object> digraph2 = new Digraph<>();

		digraph2.precedes(o1, o2);
		digraph2.precedes(o2, o3);
		digraph2.precedes(o3, o1);

		Assertions.assertThrows(GraphCycleException.class, () -> digraph2.computeDepth(o1));
	}

	private <T> void assertEqualsCyclic(List<T> expected, List<T> actual) {
		if (expected.isEmpty() && actual.isEmpty()) {
			return;
		}
		if (expected.size() != actual.size()) {
			Assertions.fail("actual: " + actual + ", expected: " + expected);
		}
		T startObject = expected.get(0);
		int startIndexActual = actual.indexOf(startObject);
		if (startIndexActual == -1) {
			Assertions.fail("actual: " + actual + ", expected: " + expected);
		}
		for (int i = 0; i < expected.size(); i++) {
			Assertions.assertEquals(expected.get(i), actual.get((startIndexActual + i) % actual.size()));
		}
	}
}
