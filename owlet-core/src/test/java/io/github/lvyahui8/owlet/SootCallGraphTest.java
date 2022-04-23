package io.github.lvyahui8.owlet;

import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SootCallGraphTest {
    public static void main(String[] args) {
        /*
        * https://github.com/soot-oss/soot/blob/develop/tutorial/guide/examples/call_graph/src/dk/brics/soot/callgraphs/CallGraphExample.java
        * */
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));
        String codeDir =  "D:\\work\\http-proxy\\test-server\\lib";
        argsList.addAll(Arrays.asList(new String[]{
                "-w",
                "-pp",
                "-process-dir",
                codeDir,
                "-process-jar-dir",
                codeDir,
//                "-main-class",
//                "org.lyh.http.proxy.client.TestClientApp",//main-class
        }));


        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {
            @Override
            protected void internalTransform(String phaseName, Map options) {
                CHATransformer.v().transform();

                CallGraph cg = Scene.v().getCallGraph();
                System.out.println(cg);
            }

        }));

        args = argsList.toArray(new String[0]);

        soot.Main.main(args);
    }
}
