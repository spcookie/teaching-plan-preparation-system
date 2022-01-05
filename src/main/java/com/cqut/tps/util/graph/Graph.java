package com.cqut.tps.util.graph;

import java.util.function.Consumer;

/**
 * @author Augenstern
 * @date 2021/12/28
 */
public interface Graph<T> {
    void deepSearch(Consumer<T> consumer);
    boolean isDirectedRing();
    void topologicalSort(Consumer<T> consumer) throws IllegalOrderedRingGraphException;
}
