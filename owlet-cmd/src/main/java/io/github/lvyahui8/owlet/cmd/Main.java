package io.github.lvyahui8.owlet.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.github.lvyahui8.owlet.Analyser;
import io.github.lvyahui8.owlet.CallGraphSupplier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] argv) throws ExecutionException, InterruptedException {
        Args args = new Args();
        JCommander commander = JCommander.newBuilder().addObject(args).build();
        try {
            commander.parse(argv);
        } catch (ParameterException e) {
            System.err.println("invalid arguments. " + e.getMessage());
            commander.usage();
            System.exit(1);
        }
        System.out.println("original:" + args.originalClasspath);
        System.out.println("changed:" + args.changedClasspath);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CallGraphSupplier originalSupplier = new CallGraphSupplier(args.originalClasspath,args.keptPackages);
        CallGraphSupplier changedSupplier = new CallGraphSupplier(args.changedClasspath,args.keptPackages);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                CompletableFuture.supplyAsync(originalSupplier,pool), CompletableFuture.supplyAsync(changedSupplier,pool));
        allOf.get();
        Analyser analyser = new Analyser();
        analyser.compare(originalSupplier.getCallGraph(),changedSupplier.getCallGraph());
    }
}
