package io.github.lvyahui8.owlet;

import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

public class CallGraphSupplier implements Supplier<CallGraph> {
    String classpath;
    CallGraph callGraph;

    public CallGraphSupplier(String classpath) {
        this.classpath = classpath;
    }

    static ThreadLocal<CallGraph> resultLocal = new ThreadLocal<>();

    static {
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.getCallGraph", new SceneTransformer() {
            @Override
            protected void internalTransform(String phaseName, Map options) {
                CHATransformer.v().transform();
                resultLocal.set(Scene.v().getCallGraph());
            }
        }));
    }

    public void load(String classpath) {
        this.classpath = classpath;
        load();
    }
    public synchronized void load() {
        if (callGraph != null) {
            return;
        }


        soot.Main.main(Arrays.asList("-w",
                "-pp",
                "-process-dir",
                classpath,
                "-process-jar-dir",
                classpath).toArray(new String[0]));

       callGraph = resultLocal.get();
        resultLocal.remove();
    }

    public CallGraph getCallGraph() {
        if (callGraph == null) {
            load();
        }
        return callGraph;
    }

    @Override
    public CallGraph get() {
        return getCallGraph();
    }
}
