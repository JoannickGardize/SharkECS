package com.sharkecs.example.system;

import com.sharkecs.Processor;
import com.sharkecs.example.singleton.Time;
import com.sharkecs.example.system.annotation.LogicPhase;

@LogicPhase
public class TimeManager implements Processor {

	private Time time;

	@Override
	public void process() {
		time.setElapsedTime(time.getElapsedTime() + time.getDeltaTime());
	}

	public void setTime(Time time) {
		this.time = time;
	}
}
