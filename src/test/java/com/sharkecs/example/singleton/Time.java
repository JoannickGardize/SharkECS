package com.sharkecs.example.singleton;

/**
 * Provides total time and delta time.
 * 
 * @author Joannick gardize
 *
 */
public class Time {

	private int elapsedTime = 0;

	public int getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public int getDeltaTime() {
		return 1;
	}
}
