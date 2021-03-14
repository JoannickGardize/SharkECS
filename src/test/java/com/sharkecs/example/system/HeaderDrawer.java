package com.sharkecs.example.system;

import com.sharkecs.Processor;
import com.sharkecs.example.singleton.Time;

public class HeaderDrawer implements Processor {

	private Time time;

	@Override
	public void process() {
		System.out.println("Current time: " + time.getElapsedTime());
		System.out.println();
	}

	public void setTime(Time time) {
		this.time = time;
	}
}
