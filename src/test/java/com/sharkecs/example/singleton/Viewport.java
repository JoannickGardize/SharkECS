package com.sharkecs.example.singleton;

/**
 * Represents the currently viewing portion of a one-dimensional world.
 * 
 * @author Joannick Gardize
 *
 */
public class Viewport {

	private int start;
	private int end;

	/**
	 * @return the starting position of the viewport, inclusive
	 */
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end position of the viewport, exclusive
	 */
	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
}
