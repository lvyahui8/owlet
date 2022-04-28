package io.github.lvyahui8.owlet.graph;

import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.queue.QueueReader;

import java.util.HashSet;

public class GraphConverter {
    public Graph convert(CallGraph callGraph) {
        Graph graph = new Graph();
        graph.nodes = new HashSet<>();
        QueueReader<Edge> listener = callGraph.listener();
        while(listener.hasNext()) {
            Edge edge = listener.next();
            graph.nodes.add(new Node(edge.getSrc()));
            graph.nodes.add(new Node(edge.getTgt()));
        }
        return graph;
    }
}
