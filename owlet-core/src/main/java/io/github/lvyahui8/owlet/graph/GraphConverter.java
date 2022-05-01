package io.github.lvyahui8.owlet.graph;

import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Type;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.queue.QueueReader;

public class GraphConverter {
    public Graph convert(CallGraph callGraph) {
        Graph graph = new Graph();
        QueueReader<Edge> listener = callGraph.listener();
        while(listener.hasNext()) {
            Edge edge = listener.next();
            MethodNode srcNode = findNode(graph,edge.getSrc());
            MethodNode tgtNode = findNode(graph,edge.getTgt());
            srcNode.callees.add(tgtNode.getKey());
            tgtNode.callers.add(srcNode.getKey());
        }
        return graph;
    }



    private MethodNode findNode(Graph graph,MethodOrMethodContext method) {
        SootMethod m = method.method();
        MethodNode node = new MethodNode();
        node.setName(m.getName());
        node.setDeclareClassFullName(m.getDeclaringClass().getName());
        for (Type type : m.getParameterTypes()) {
            node.getParamTypeList().add(type.toQuotedString());
        }
        MethodNode exist = graph.nodeMap.get(node.getKey());
        if (exist != null) {
            return exist;
        }
        graph.nodeMap.put(node.getKey(),node);
        return node;
    }
}
