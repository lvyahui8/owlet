package io.github.lvyahui8.owlet.cmd;

import com.alibaba.fastjson.JSON;
import io.github.lvyahui8.owlet.graph.Graph;
import io.github.lvyahui8.owlet.graph.MethodNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResultHandler {
    Graph sourceGraph;
    Graph targetGraph;
    Graph diffGraph;

    public ResultHandler(Graph sourceGraph, Graph targetGraph, Graph diffGraph) {
        this.sourceGraph = sourceGraph;
        this.targetGraph = targetGraph;
        this.diffGraph = diffGraph;
    }

    public void output() throws IOException {
        File outputDir = new File("D:\\Temp\\output");
        InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("html/report.html");
        InputStream jsStream = getClass().getClassLoader().getResourceAsStream("html/graph.js");
        assert htmlStream != null;
        assert jsStream != null;
        IOUtils.copy(htmlStream, Files.newOutputStream(new File(outputDir,"report.html").toPath()));
        IOUtils.copy(jsStream, Files.newOutputStream(new File(outputDir,"graph.js").toPath()));
        IOUtils.write(convertToGraphJson(sourceGraph),Files.newOutputStream(new File(outputDir,"source.json").toPath()), Charsets.toCharset("UTF-8"));
        IOUtils.write(convertToGraphJson(targetGraph),Files.newOutputStream(new File(outputDir,"target.json").toPath()), Charsets.toCharset("UTF-8"));
        IOUtils.write(convertToGraphJson(diffGraph),Files.newOutputStream(new File(outputDir,"diff.json").toPath()), Charsets.toCharset("UTF-8"));
    }

    public String convertToGraphJson(Graph graph) {
        List<Node> nodes = new LinkedList<>();
        List<Edge> edges = new LinkedList<>();
        if (graph != null) {
            for (Map.Entry<String, MethodNode> entry : graph.getNodeMap().entrySet()) {
                MethodNode node = entry.getValue();
                nodes.add(new Node(node.getKey(),node.getName(),node.getPkg()));
                for (String callee : node.getCallees()) {
                    edges.add(new Edge(node.getKey(),callee));
                }
            }
        }
        return JSON.toJSONString(new HashMap<String,Object>() {{
            put("nodes",nodes);
            put("edges",edges);
        }});
    }

    @Data
    @AllArgsConstructor
    public static class Node {
        String id;
        String label;
        String pkg;
    }

    @Data
    @AllArgsConstructor
    public static class Edge {
        String source;
        String target;
    }
}
