package io.github.lvyahui8.owlet;

import io.github.lvyahui8.owlet.utils.JavaUtils;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public class CallGraphSupplier implements Supplier<CallGraph> {
    String classpath;
    CallGraph callGraph;

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
                callGraph = Scene.v().getCallGraph();
            }
        }));

        soot.Main.main(Arrays.asList("-w",
                "-pp",
                "-process-dir",
                classpath,
                "-process-jar-dir",
                classpath).toArray(new String[0]));

    }

    public CallGraph getCallGraph() {
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
        }
        return callGraph;
    }

    @Override
    public CallGraph get() {
        return getCallGraph();
    }

    public static void main(String[] args) {
        String classpath = args[0];

        CallGraphSupplier supplier = new CallGraphSupplier(classpath);
        supplier.load();
        System.out.println("graph size : "  + supplier.getCallGraph().size());
    }
}
