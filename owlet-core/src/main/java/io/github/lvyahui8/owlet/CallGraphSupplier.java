package io.github.lvyahui8.owlet;

import com.beust.jcommander.JCommander;
import io.github.lvyahui8.owlet.graph.Graph;
import io.github.lvyahui8.owlet.graph.GraphConverter;
import io.github.lvyahui8.owlet.graph.GraphSerializer;
import io.github.lvyahui8.owlet.graph.MethodNode;
import io.github.lvyahui8.owlet.utils.JavaUtils;
import soot.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public class CallGraphSupplier implements Supplier<Graph> {
    String classpath;
    Graph callGraph;

    Set<String> keptPackages;

    List<String[]> keptPackagesList;

    public CallGraphSupplier(String classpath, Collection<String> keptPackages) {
        this.classpath = classpath;
        this.keptPackages = new HashSet<>(keptPackages);
        this.keptPackagesList = new ArrayList<>(keptPackages.size());
        for (String pkg : this.keptPackages) {
            keptPackagesList.add(new String[]{pkg,pkg + ".",pkg + "_"});
        }
    }

    public void load(String classpath) {
        this.classpath = classpath;
        load();
    }

    public List<String> getJarFiles(String jarDirName) {
        List<String> dirs = new LinkedList<>();
        File jarDir = new File(jarDirName);
        File[] files = jarDir.listFiles();
        for (File f : files) {
            if (f.getAbsolutePath().endsWith(".jar")) {
                dirs.add(f.getAbsolutePath());
            }
        }
        return dirs;
    }
    public synchronized void load() {
        // https://stackoverflow.com/questions/65982919/soot-not-finding-class-without-a-main-for-call-graph
        if (callGraph != null) {
            return;
        }
        String mainClass =  "org.lyh.http.proxy.TesetServerMain";
        String scp =  "C:\\Program Files\\Java\\jdk1.8.0_212\\jre\\lib\\rt.jar"
                + File.pathSeparator + "C:\\Program Files\\Java\\jdk1.8.0_212\\jre\\lib\\jce.jar";

        Options.v().set_soot_classpath(scp);
        Options.v().set_process_dir(getJarFiles(classpath));
        Options.v().set_whole_program(true);
        Options.v().set_prepend_classpath(true);
        Options.v().allow_phantom_refs();
        Scene.v().loadNecessaryClasses();
        SootClass mainClazz = Scene.v().getSootClass(mainClass);
        Scene.v().setMainClass(mainClazz);

        PackManager.v().getPack("wjtp").add(new Transform("wjtp.getCallGraph", new SceneTransformer() {
            @Override
            protected void internalTransform(String phaseName, Map options) {
                CHATransformer.v().transform();
                CallGraph cg = Scene.v().getCallGraph();
                GraphConverter converter = new GraphConverter();
                callGraph = removeMeaninglessNode(converter.convert(cg));
            }
        }));

        /*
         * https://stackoverflow.com/questions/48620178/how-can-i-set-up-soot-when-using-it-as-a-library
         * https://o2lab.github.io/710/p/a1.html
         * https://github.com/soot-oss/soot/issues/1346
         * https://mayuwan.github.io/2018/05/08/soot/
         */
        //Enable Spark
        HashMap<String, String> opt = new HashMap<String, String>();
        opt.put("on-fly-cg", "true");
        SparkTransformer.v().transform("", opt);
        PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");

        PackManager.v().runPacks();
//        soot.Main.main(Arrays.asList("-w",
//                "-main-class",
//               ,
//                "-pp",
//                "-cp",
//             ,
//                "-process-dir",
//                classpath,
//                "-process-jar-dir",
//                classpath
//                ).toArray(new String[0]));

    }

    public Graph removeMeaninglessNode(Graph graph) {
        // 删除无意义的节点
        for (Iterator<Map.Entry<String, MethodNode>> it = graph.getNodeMap().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, MethodNode> entry = it.next();
            MethodNode m = entry.getValue();
            if (m.isJavaLibMethod() || !inKeptPackages(m.getPkg())) {
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

    private boolean inKeptPackages(String pkg) {
        for (String[] items : keptPackagesList) {
            if (pkg.equals(items[0]) || pkg.startsWith(items[1])) {
                return true;
            }
        }
        return false;
    }

    private boolean useful(Set<String> edges) {
        for (String edgeKey : edges) {
            for (String[] items : keptPackagesList) {
                if (edgeKey.equals(items[1]) || edgeKey.startsWith(items[2])) {
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
