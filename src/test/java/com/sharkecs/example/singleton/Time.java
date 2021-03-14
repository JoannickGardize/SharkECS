package com.sharkecs.example.singleton;

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
