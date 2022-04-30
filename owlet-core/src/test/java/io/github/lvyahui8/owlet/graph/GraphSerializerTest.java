package io.github.lvyahui8.owlet.graph;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;

public class GraphSerializerTest extends TestCase {
    public static final File graphFile = new File("D:/tmp/test.gp");

    public void testSerialize() throws IOException {
        Graph graph = new Graph();
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