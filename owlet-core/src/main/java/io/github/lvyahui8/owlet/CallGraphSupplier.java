package io.github.lvyahui8.owlet;

import io.github.lvyahui8.owlet.graph.Graph;
import io.github.lvyahui8.owlet.graph.GraphConverter;
import io.github.lvyahui8.owlet.graph.GraphSerializer;
import io.github.lvyahui8.owlet.utils.JavaUtils;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public class CallGraphSupplier implements Supplier<Graph> {
    String classpath;
    Graph callGraph;

    public CallGraphSupplier(String classpath) {
        this.classpath = classpath;
    }

    public void load(String classpath) {
        this.classpath = classpath;
        load();
    }
    public synchronized void load() {
        if (callGraph != null) {
            return;
        }

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.getCallGraph", new SceneTransformer() {
            @Override
            protected void internalTransform(String phaseName, Map options) {
                CHATransformer.v().transform();
                CallGraph cg = Scene.v().getCallGraph();
                GraphConverter converter = new GraphConverter();
                callGraph = converter.convert(cg);
            }
        }));

        soot.Main.main(Arrays.asList("-w",
                "-pp",
                "-process-dir",
                classpath,
                "-process-jar-dir",
                classpath).toArray(new String[0]));

    }

    public Graph getCallGraph() {
        if (callGraph == null) {
            Process process = JavaUtils.forkJavaProcess(this.getClass(), Collections.singletonList(classpath));
            InputStream stdout = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            GraphSerializer serializer = new GraphSerializer();
            File file = GraphSerializer.GetGraphFile(classpath);
            try {
                callGraph = serializer.deserialize(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                file.delete();
            }

        }
        return callGraph;
    }

    @Override
    public Graph get() {
        return getCallGraph();
    }

    public static void main(String[] args) throws IOException {
        String classpath = args[0];

        CallGraphSupplier supplier = new CallGraphSupplier(classpath);
        supplier.load();
        GraphSerializer serializer = new GraphSerializer();
        GraphConverter converter = new GraphConverter();
        serializer.serialize(supplier.getCallGraph(),GraphSerializer.GetGraphFile(classpath));
    }
}
