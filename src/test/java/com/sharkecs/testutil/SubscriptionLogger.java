package com.sharkecs.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import com.sharkecs.SubscriptionListener;
import com.sharkecs.Transmutation;

public class SubscriptionLogger implements SubscriptionListener {

	private List<Integer> addLog = new ArrayList<>();
	private List<Integer> removeLog = new ArrayList<>();
	private List<Integer> changeLog = new ArrayList<>();
	private List<Transmutation> transmutationLog = new ArrayList<>();

	@Override
	public void removed(int entityId) {
		removeLog.add(entityId);
	}

	@Override
	public void added(int entityId) {
		addLog.add(entityId);
	}

	@Override
	public void changed(int entityId, Transmutation transmutation) {
		changeLog.add(entityId);
		transmutationLog.add(transmutation);
	}

	public void assertAddLog(int... ids) {
		assertLog(addLog, ids);
	}

	public void assertRemoveLog(int... ids) {
		assertLog(removeLog, ids);
	}

	public void assertChangeLog(int... ids) {
		assertLog(changeLog, ids);
	}

	public void assertTransmutationLog(Transmutation... transmutations) {
		Assertions.assertArrayEquals(transmutations, transmutationLog.toArray());
	}

	private void assertLog(List<Integer> log, int... ids) {
		Assertions.assertEquals(Arrays.stream(ids).mapToObj(Integer::valueOf).collect(Collectors.toList()), log);
	}

	public void clear() {
		addLog.clear();
		removeLog.clear();
		changeLog.clear();
		transmutationLog.clear();
	}
}
