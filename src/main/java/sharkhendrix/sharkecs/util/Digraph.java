/*
 * Copyright 2024 Joannick Gardize
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sharkhendrix.sharkecs.util;

import java.util.*;

/**
 * <p>
 * Represents a directed graph.
 * <p>
 * Each node is identified by a value instance (by identity, not equality).
 * <p>
 * The goal of this class is the method {@link #topologicalSort()} for priority
 * resolution.
 *
 * @param <T>
 */
public class Digraph<T> {

    public static class Node<T> {
        private T value;
        private Set<Node<T>> edges = new HashSet<>();

        private Node(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public Set<Node<T>> getEdges() {
            return Collections.unmodifiableSet(edges);
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
     * Add a directed edge.
     *
     * @param from the node's value of the arrow's tail
     * @param to   the node's value of the arrow's head
     */
    public void addEdge(T from, T to) {
        nodes.computeIfAbsent(from, Node::new).edges.add(nodes.computeIfAbsent(to, Node::new));
    }

    /**
     * @return all values with at least one connection
     */
    public Set<T> values() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    /**
     * Compute the topological order of all nodes of this graph.
     *
     * @return the list containing all node values in any valid topological order,
     * such that for every directed edge uv from node u to node v, u comes
     * before v in the list
     * @throws GraphCycleException if there is a cycle anywhere in the graph
     */
    public List<T> topologicalSort() throws GraphCycleException {
        Map<Node<T>, Boolean> toTreat = new HashMap<>();
        for (Node<T> node : nodes.values()) {
            toTreat.put(node, false);
        }
        List<T> result = new ArrayList<>(toTreat.size());
        while (!toTreat.isEmpty()) {
            topologicalSortVisit(result, toTreat, toTreat.keySet().iterator().next());
        }
        Collections.reverse(result);
        return result;
    }

    private void topologicalSortVisit(List<T> result, Map<Node<T>, Boolean> toTreat, Node<T> node)
            throws GraphCycleException {
        if (!toTreat.containsKey(node)) {
            return;
        } else if (Boolean.TRUE.equals(toTreat.get(node))) {
            throw new GraphCycleException("Cycle detected around " + node);
        }
        toTreat.put(node, true);
        for (Node<T> following : node.edges) {
            topologicalSortVisit(result, toTreat, following);
        }
        toTreat.remove(node);
        result.add(node.value);
    }
}
