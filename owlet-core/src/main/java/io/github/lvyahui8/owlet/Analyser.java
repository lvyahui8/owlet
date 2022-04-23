package io.github.lvyahui8.owlet;

import io.github.lvyahui8.owlet.graph.Graph;
import soot.MethodOrMethodContext;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.queue.QueueReader;

public class Analyser {
    public Graph compare(CallGraph originalGraph,CallGraph changedGraph) {
        QueueReader<Edge> listener = originalGraph.listener();
        while(listener.hasNext()) {
            Edge edge = listener.next();
            MethodOrMethodContext src = edge.getSrc();
            MethodOrMethodContext tgt = edge.getTgt();
            System.out.printf("%s -> %s\n",src,tgt);
        }
        return null;
    }
}
