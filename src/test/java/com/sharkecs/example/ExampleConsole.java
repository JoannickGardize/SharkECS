package com.sharkecs.example;

import com.sharkecs.Engine;

public class ExampleConsole {

	public static void main(String[] args) throws InterruptedException {
		Engine engine = ExampleUtils.createEngine(true);
		for (int i = 0; i < 40; i++) {
			engine.process();
			Thread.sleep(500);
		}
	}
}
