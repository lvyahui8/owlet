package io.github.lvyahui8.owlet.graph;

import soot.MethodOrMethodContext;

import java.util.Set;

public class Node {
    MethodOrMethodContext method;

    Set<Node> edgeOuts;
    Set<Node> edgeIns;

    public Node(MethodOrMethodContext method) {
        this.method = method;
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Node && method.equals(((Node) obj).method);
    }
}
