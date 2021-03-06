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
    /**
     * <a href="https://github.com/soot-oss/soot/blob/develop/tutorial/guide/examples/call_graph/src/dk/brics/soot/callgraphs/CallGraphExample.java">https://github.com/soot-oss/soot/blob/develop/tutorial/guide/examples/call_graph/src/dk/brics/soot/callgraphs/CallGraphExample.java</a>
     * <p>
     *     also see {@link  soot.Scene#getSootClassPath()}
     * </p>
     * */
    public static void main(String[] args) {

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


        PackManager.v().getPack("wjtp").add(new Transform("wjtp.getCallGraph", new SceneTransformer() {
            @Override
            protected void internalTransform(String phaseName, Map options) {
                CHATransformer.v().transform();

                CallGraph cg = Scene.v().getCallGraph();
//                System.out.println(cg);
                System.out.printf("get callgraph success.  len : %d",cg.size());
            }

        }));

        args = argsList.toArray(new String[0]);

        soot.Main.main(args);
    }
}
