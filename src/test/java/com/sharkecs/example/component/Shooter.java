package com.sharkecs.example.component;

public class Shooter {

	public enum ShooterCommand {
		NONE,
		LEFT,
		RIGHT;
	}

	private ShooterCommand command;

	private int cooldown;

	private int readyTime;

	public ShooterCommand getCommand() {
		return command;
	}

	public void setCommand(ShooterCommand command) {
		this.command = command;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getReadyTime() {
		return readyTime;
	}

	public void setReadyTime(int readyTime) {
		this.readyTime = readyTime;
	}
}
