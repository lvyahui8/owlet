package io.github.lvyahui8.owlet.graph;

import lombok.Data;
import soot.MethodOrMethodContext;

import java.io.Serializable;
import java.util.Set;

@Data
public class Node implements Serializable {
    MethodOrMethodContext method;

    Set<Node> edgeOuts;
    Set<Node> edgeIns;

    public Node(MethodOrMethodContext method) {
        this.method = method;
    }

    @Override
    public int hashCode() {
        if (method == null) {
            return 0;
        }
        return method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Node && method.equals(((Node) obj).method);
    }
}
