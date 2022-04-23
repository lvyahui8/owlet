package io.github.lvyahui8.owlet;

import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.Arrays;
import java.util.Map;

public class CallGraphHolder {
    String classpath;
    CallGraph callGraph;
    public void load(String classpath) {
        this.classpath = classpath;
        load();
    }
    public void load() {
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.getCallGraph", new SceneTransformer() {
            @Override
            protected void internalTransform(String phaseName, Map options) {
                CHATransformer.v().transform();

                callGraph =  Scene.v().getCallGraph();
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
        return callGraph;
    }
}
