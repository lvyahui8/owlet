package io.github.lvyahui8.owlet.graph;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class GraphSerializerTest extends TestCase {
    public static final File graphFile = new File("D:/tmp/test.gp");

    public void testSerialize() throws IOException {
        Graph graph = new Graph();
        graph.nodes = new HashSet<>();
        graph.nodes.add(new Node(null));
        GraphSerializer serializer = new GraphSerializer();
        serializer.serialize(graph,graphFile);
    }

    public void testDeserialize() throws IOException {
        GraphSerializer serializer = new GraphSerializer();
        Graph graph = serializer.deserialize(graphFile);
        Assert.assertNotNull(graph);
        System.out.println(graph);
    }
}