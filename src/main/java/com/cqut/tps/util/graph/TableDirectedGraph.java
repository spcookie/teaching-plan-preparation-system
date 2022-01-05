package com.cqut.tps.util.graph;

import java.util.Stack;
import java.util.function.Consumer;

/**
 * @author Augenstern
 * @date 2021/12/28
 */
@SuppressWarnings("unused")
public class TableDirectedGraph<T> implements Graph<T> {
    private Vertex<T>[] vertexes;

    private TableDirectedGraph() {
    }

    public static <E> TableDirectedGraph<E> build(E[] values, int[][] edges) {
        TableDirectedGraph<E> graph = new TableDirectedGraph<>();
        if (values.length != 0) {
            graph.createVertex(values);
            graph.createEdge(edges);
        }
        return graph;
    }

    private void createVertex(T[] values) {
        this.vertexes = new Vertex[values.length];
        int i = 0;
        for (T value : values) {
            this.vertexes[i++] = new Vertex<>(value);
        }
    }

    private void createEdge(int[][] edges) {
        for (int[] edge : edges) {
            Vertex<T> vertex = vertexes[edge[0]];
            Edge te = vertex.getEdge();
            if (te == null) {
                vertex.setEdge(new Edge(edge[2], edge[1]));
            } else {
                while (true) {
                    Edge next = te.getNext();
                    if (next == null) {
                        te.setNext(new Edge(edge[2], edge[1]));
                        break;
                    }
                    te = next;
                }
            }
            //修改入度
            Vertex<T> end = this.vertexes[edge[1]];
            end.setPenetration(end.getPenetration() + 1);
        }
    }

    @Override
    public void deepSearch(Consumer<T> consumer) {
        int len = this.vertexes.length;
        boolean[] isAccess = new boolean[len];
        for (int i = 0; i < len; i++) {
            if (!isAccess[i]) {
                this.DFS(i, isAccess, consumer);
            }
        }
    }

    private void DFS(int index, boolean[] isAccess, Consumer<T> consumer) {
        if (!isAccess[index]) {
            isAccess[index] = true;
            Vertex<T> vertex = this.vertexes[index];
            consumer.accept(vertex.getData());
            Edge edge = vertex.getEdge();
            while (edge != null) {
                int i = edge.getIndex();
                DFS(i, isAccess, consumer);
                edge = edge.getNext();
            }
        }
    }

    @Override
    public boolean isDirectedRing() {
        int len = this.vertexes.length;
        boolean[] isAccess = new boolean[len];
        for (int i = 0; i < len; i++) {
            if (!isAccess[i]) {
                //DFS遍历
                Stack<Vertex<T>> list = new Stack<>();
                Stack<Vertex<T>> stack = new Stack<>();
                Stack<Integer> flog = new Stack<>();
                stack.push(this.vertexes[i]);
                flog.push(i);
                while (!stack.isEmpty()) {
                    //弹出栈顶元素
                    Vertex<T> v = stack.pop();
                    isAccess[flog.pop()] = true;
                    Edge edge = v.getEdge();
                    boolean isFlashBack = edge == null;
                    //将邻接点入栈
                    while (edge != null) {
                        int index = edge.getIndex();
                        Vertex<T> vertex = this.vertexes[index];
                        stack.push(vertex);
                        flog.push(index);
                        edge = edge.getNext();
                    }
                    //比较DFS查找的元素是否成环
                    if (!list.isEmpty()) {
                        for (Vertex<T> vertex : list) {
                            if (vertex.equals(v)) {
                                return true;
                            }
                        }
                    }
                    list.push(v);
                    //深度遍历回溯
                    if (isFlashBack) {
                        while (!list.isEmpty()) {
                            Vertex<T> peek = list.peek();
                            Edge e = peek.getEdge();
                            if (e == null) {
                                list.pop();
                            } else {
                                boolean is = true;
                                while (e != null) {
                                    int index = e.getIndex();
                                    boolean access = isAccess[index];
                                    if (!access) {
                                        is = false;
                                        break;
                                    }
                                    e = e.getNext();
                                }
                                if (is) {
                                    list.pop();
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void topologicalSort(Consumer<T> consumer) throws IllegalOrderedRingGraphException {
        if (this.isDirectedRing()) {
            throw new IllegalOrderedRingGraphException("有向环图，无法拓扑排序");
        }
        int length = this.vertexes.length;
        int[] penetration = new int[length];
        Stack<Vertex<T>> stack = new Stack<>();
        for (int i = 0; i < length; i++) {
            Vertex<T> vertex = this.getVertexes()[i];
            penetration[i] = vertex.getPenetration();
            if (penetration[i] == 0) {
                stack.push(vertex);
            }
        }
        while (!stack.isEmpty()) {
            Vertex<T> v = stack.pop();
            consumer.accept(v.getData());
            Edge edge = v.getEdge();
            //删除入度
            while (edge != null) {
                int index = edge.getIndex();
                penetration[index]--;
                if (penetration[index] == 0) {
                    stack.push(this.getVertexes()[index]);
                }
                edge = edge.getNext();
            }
        }
    }


    public Vertex<T>[] getVertexes() {
        return vertexes;
    }

    private static class Vertex<T> {
        private T data;
        private int Penetration;
        private Edge edge;

        public Vertex() {
        }

        private Vertex(T val) {
            this.data = val;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public int getPenetration() {
            return Penetration;
        }

        public void setPenetration(int penetration) {
            Penetration = penetration;
        }

        public Edge getEdge() {
            return edge;
        }

        private void setEdge(Edge edge) {
            this.edge = edge;
        }
    }

    private static class Edge {
        private int weight;
        private int index;
        private Edge next;

        public Edge(int weight, int index) {
            this.weight = weight;
            this.index = index;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Edge getNext() {
            return next;
        }

        public void setNext(Edge next) {
            this.next = next;
        }
    }
}
