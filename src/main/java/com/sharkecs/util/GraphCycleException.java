package com.sharkecs.util;

import java.util.Arrays;

public class GraphCycleException extends Exception {

	private static final long serialVersionUID = 1L;

	private final transient Object[] cyclePath;

	public GraphCycleException(Object... cyclePath) {
		super("Cycle found in the graph: " + Arrays.toString(cyclePath));
		this.cyclePath = cyclePath;
	}

	public Object[] getCyclePath() {
		return cyclePath;
	}
}
