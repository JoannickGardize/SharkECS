package com.sharkecs.example;

import com.sharkecs.Engine;
import com.sharkecs.builder.EngineBuilder;
import com.sharkecs.example.system.ConsoleCleaner;
import com.sharkecs.example.system.EntityDrawingSystem;
import com.sharkecs.example.system.HeaderDrawer;
import com.sharkecs.example.system.HealthDrawerSystem;
import com.sharkecs.example.system.annotation.DrawingPhase;
import com.sharkecs.example.system.annotation.LogicPhase;

public class ExampleConsole {

	public static void main(String[] args) throws InterruptedException {
		EngineBuilder builder = ExampleBuilder.buider();

		builder.with(new ConsoleCleaner());
		builder.then(new HeaderDrawer());
		builder.then(new HealthDrawerSystem());
		builder.then(new EntityDrawingSystem());

		builder.before(LogicPhase.class, DrawingPhase.class);

		Engine engine = builder.build();

		for (int i = 0; i < 40; i++) {
			engine.process();
			Thread.sleep(500);
		}
	}
}
