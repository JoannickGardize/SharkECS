package com.sharkecs.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * <p>
 * Represents a directed graph.
 * <p>
 * Each node is identified by a value instance (by identity, not equality), and
 * is doubly-linked to its following and preceding nodes. Any possible value
 * instance is considered by the graph as a single node with zero to many
 * connections.
 * <p>
 * The goal of this class is the method {@link #computeDepth(Object)} for
 * priority resolution.
 * 
 * @author Joannick Gardize
 *
 * @param <T>
 */
public class Digraph<T> {

	public static class Node<T> {
		private T value;
		private Set<Node<T>> followings = new HashSet<>();
		private Set<Node<T>> precedings = new HashSet<>();

		private Node(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public Set<Node<T>> getFollowings() {
			return Collections.unmodifiableSet(followings);
		}

		public Set<Node<T>> getPrecedings() {
			return Collections.unmodifiableSet(precedings);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(value);
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(Object obj) {
			if (!(obj instanceof Node)) {
				return false;
			}
			return value == ((Node<T>) obj).value;
		}

		@Override
		public String toString() {
			return "Node " + value;
		}
	}

	private Map<T, Node<T>> nodes = new IdentityHashMap<>();

	/**
	 * <p>
	 * Connects the {@code value} node to all {@code precedings} nodes as a
	 * following node.
	 * <p>
	 * 
	 * @param value      the value that will follows all {@code precedings} values
	 * @param precedings the values that will precedes the {@code value}
	 */
	@SuppressWarnings("unchecked")
	public void follows(T value, T... precedings) {
		Node<T> toNode = nodes.computeIfAbsent(value, Node::new);
		for (T from : precedings) {
			connect(from, toNode);
		}
	}

	/**
	 * <p>
	 * Connects the {@code value} node to all {@code followings} nodes as a
	 * preceding node.
	 * <p>
	 * 
	 * @param value      the value that will precedes all {@code followings} values
	 * @param followings the values that will follows the {@code value}
	 */
	@SuppressWarnings("unchecked")
	public void precedes(T value, T... followings) {
		Node<T> fromNode = nodes.computeIfAbsent(value, Node::new);
		for (T to : followings) {
			connect(fromNode, to);
		}
	}

	/**
	 * @return all values with at least one connection
	 */
	public Set<T> values() {
		return Collections.unmodifiableSet(nodes.keySet());
	}

	/**
	 * @param startingValue
	 * @return all nodes connected, directly or indirectly, in any direction to the
	 *         {@code startingValue}
	 */
	public Set<Node<T>> getAllConnectedNodes(T startingValue) {
		Node<T> startingNode = nodes.get(startingValue);
		if (startingNode == null) {
			return Collections.singleton(new Node<T>(startingValue));
		}
		Set<Node<T>> result = new HashSet<>();
		List<Node<T>> waiting = new ArrayList<>();
		waiting.add(startingNode);
		while (!waiting.isEmpty()) {
			Node<T> current = waiting.remove(waiting.size() - 1);
			forAllConnectedNode(current, n -> {
				if (result.add(n)) {
					waiting.add(n);
				}
			});
		}
		return result;
	}

	/**
	 * Search any cycle path in the given node set and returns its path.
	 * 
	 * @param nodes the node cloud to test
	 * @return the path of any cycle in the given set of node, or an empty list if
	 *         there is no cycle
	 */
	public List<Node<T>> findAnyCycle(Set<Node<T>> nodes) {
		Set<Node<T>> remainingNodes = new HashSet<>(nodes);
		Set<Node<T>> pathSet = new HashSet<>();
		List<Node<T>> pathList = new ArrayList<>();
		while (!remainingNodes.isEmpty()) {
			Node<T> startNode = remainingNodes.iterator().next();
			remainingNodes.remove(startNode);
			pathSet.clear();
			pathSet.add(startNode);
			pathList.clear();
			pathList.add(startNode);
			if (searchCycle(remainingNodes, pathList, pathSet, startNode)) {
				return pathList;
			}
		}
		return Collections.emptyList();
	}

	/**
	 * <p>
	 * Compute the "depth" of all connected nodes to {@code startingValue}.
	 * <p>
	 * Computing depth means assigning an integer number to all nodes so that for
	 * any node value pair X and Y, where X precedes (directly or indirectly) Y, the
	 * resulting map will give {@code depth.get(X) < depth.get(Y)}. Beyond this
	 * rule, the resulting depth values are arbitrary and could be negative and/or
	 * positive.
	 * <p>
	 * This requires the absence of any cycle.
	 * 
	 * @param startingValue an arbitrary starting point of the graph to compute
	 * @return a map associating the computed depth number to all node values
	 *         directly or indirectly connected to {@code startingValue}
	 * @throws GraphCycleException if there is a cycle anywhere in the graph
	 *                             connected to {@code startingValue}
	 */
	public Map<T, Integer> computeDepth(T startingValue) throws GraphCycleException {
		Set<Node<T>> allNodes = getAllConnectedNodes(startingValue);
		if (allNodes.isEmpty()) {
			return Collections.singletonMap(startingValue, 0);
		}
		List<Node<T>> cyclePath = findAnyCycle(allNodes);
		if (!cyclePath.isEmpty()) {
			throw new GraphCycleException(cyclePath.stream().map(Node::getValue).toArray());
		}
		List<Node<T>> waiting = new ArrayList<>();
		Map<T, Integer> depths = new IdentityHashMap<>();
		Node<T> startingNode = allNodes.iterator().next();
		depths.put(startingNode.value, 0);
		waiting.add(startingNode);
		while (!waiting.isEmpty()) {
			Node<T> node = waiting.remove(waiting.size() - 1);
			int nodeDepth = depths.get(node.value);
			processFollowingsDepth(depths, waiting, node, nodeDepth);
			processPrecedingsDepth(depths, waiting, node, nodeDepth);
		}
		return depths;
	}

	private void processFollowingsDepth(Map<T, Integer> depths, List<Node<T>> waiting, Node<T> node, int nodeDepth) {
		for (Node<T> following : node.followings) {
			Integer followingDepth = depths.get(following.value);
			if (followingDepth == null) {
				depths.put(following.value, nodeDepth + 1);
				waiting.add(following);
			} else if (followingDepth <= nodeDepth) {
				for (Entry<T, Integer> entry : depths.entrySet()) {
					if (entry.getValue() > nodeDepth) {
						entry.setValue(entry.getValue() + 1);
					}
				}
				depths.put(following.value, nodeDepth + 1);
			}
		}
	}

	private void processPrecedingsDepth(Map<T, Integer> depths, List<Node<T>> waiting, Node<T> node, int nodeDepth) {
		for (Node<T> preceding : node.precedings) {
			Integer precedingDepth = depths.get(preceding.value);
			if (precedingDepth == null) {
				depths.put(preceding.value, nodeDepth - 1);
				waiting.add(preceding);
			} else if (precedingDepth >= nodeDepth) {
				for (Entry<T, Integer> entry : depths.entrySet()) {
					if (entry.getValue() < nodeDepth) {
						entry.setValue(entry.getValue() - 1);
					}
				}
				depths.put(preceding.value, nodeDepth - 1);
			}
		}
	}

	private boolean searchCycle(Set<Node<T>> remainingNodes, List<Node<T>> pathList, Set<Node<T>> pathSet, Node<T> currentNode) {
		for (Node<T> following : currentNode.followings) {
			if (!pathSet.add(following)) {
				return true;
			}
			pathList.add(following);
			if (remainingNodes.remove(following) && searchCycle(remainingNodes, pathList, pathSet, following)) {
				return true;
			}
			pathList.remove(pathList.size() - 1);
			pathSet.remove(following);
		}
		return false;
	}

	private void connect(T from, Node<T> to) {
		connect(nodes.computeIfAbsent(from, Node::new), to);
	}

	private void connect(Node<T> from, T to) {
		connect(from, nodes.computeIfAbsent(to, Node::new));
	}

	private void connect(Node<T> from, Node<T> to) {
		from.followings.add(to);
		to.precedings.add(from);
	}

	private void forAllConnectedNode(Node<T> node, Consumer<Node<T>> neighborAction) {
		node.followings.forEach(neighborAction);
		node.precedings.forEach(neighborAction);
	}
}
