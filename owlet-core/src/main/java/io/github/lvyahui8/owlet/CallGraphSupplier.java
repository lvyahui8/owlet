package io.github.lvyahui8.owlet;

import com.beust.jcommander.JCommander;
import io.github.lvyahui8.owlet.graph.Graph;
import io.github.lvyahui8.owlet.graph.GraphConverter;
import io.github.lvyahui8.owlet.graph.GraphSerializer;
import io.github.lvyahui8.owlet.graph.MethodNode;
import io.github.lvyahui8.owlet.utils.JavaUtils;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public class CallGraphSupplier implements Supplier<Graph> {
    String classpath;
    Graph callGraph;

    Set<String> keptPackages;

    public CallGraphSupplier(String classpath, Collection<String> keptPackages) {
        this.classpath = classpath;
        this.keptPackages = new HashSet<>(keptPackages);
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
                callGraph = removeMeaninglessNode(converter.convert(cg));
            }
        }));

        soot.Main.main(Arrays.asList("-w",
                "-pp",
                "-process-dir",
                classpath,
                "-process-jar-dir",
                classpath).toArray(new String[0]));

    }

    public Graph removeMeaninglessNode(Graph graph) {
        // 删除无意义的节点
        for (Iterator<Map.Entry<String, MethodNode>> it = graph.getNodeMap().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, MethodNode> entry = it.next();
            MethodNode m = entry.getValue();
            if (m.isJavaLibMethod()) {
                if (m.getCallees().isEmpty() && m.getCallers().isEmpty()) {
                    it.remove();
                }
                if (! useful(m)) {
                    it.remove();
                }
            }
        }
        return graph;
    }

    private boolean useful(MethodNode node) {
        return useful(node.getCallees())  || useful(node.getCallers());
    }

    private boolean useful(Set<String> edges) {
        for (String edgeKey : edges) {
            for (String keptPackage : keptPackages) {
                if (edgeKey.startsWith(keptPackage)) {
                    return true;
                }
            }
        }
        return false;
    }


    public Graph getCallGraph() {
        if (callGraph == null) {
            List<String> args = new LinkedList<>();
            args.add("-cp");
            args.add(classpath);
            for (String keptPackage : keptPackages) {
                args.add("-kps");
                args.add(keptPackage);
            }
            Process process = JavaUtils.forkJavaProcess(this.getClass(),args);
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
        SupplierArgs supplierArgs = new SupplierArgs();
        JCommander commander = JCommander.newBuilder().addObject(supplierArgs).build();
        commander.parse(args);
        CallGraphSupplier supplier = new CallGraphSupplier(supplierArgs.classpath,supplierArgs.keptPackages);
        supplier.load();
        GraphSerializer serializer = new GraphSerializer();
        serializer.serialize(supplier.getCallGraph(),GraphSerializer.GetGraphFile(supplierArgs.classpath));
    }
}
