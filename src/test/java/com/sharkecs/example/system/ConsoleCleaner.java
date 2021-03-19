package com.sharkecs.example.system;

import java.io.IOException;

import com.sharkecs.Processor;
import com.sharkecs.example.system.annotation.DrawingPhase;

@DrawingPhase
public class ConsoleCleaner implements Processor {

	@Override
	public void process() {
//		System.out.print("\033[H\033[2J");
//		System.out.flush();
		clearConsole();
	}

	public final static void clearConsole() {
		// Clears Screen in java
		try {
			if (System.getProperty("os.name").contains("Windows"))
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			else
				Runtime.getRuntime().exec("clear");
		} catch (IOException | InterruptedException ex) {
		}
	}
}
