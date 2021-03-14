package com.sharkecs.example.component;

public class Health {

	private int maximum;
	private int value;

	public void initialize(int maximum) {
		this.maximum = maximum;
		value = maximum;
	}

	public void takeDamage(int amount) {
		value -= amount;
		ensurePositiveValue();
	}

	public int getMaximum() {
		return maximum;
	}

	public int getValue() {
		return value;
	}

	private void ensurePositiveValue() {
		if (value < 0) {
			value = 0;
		}
	}

}
