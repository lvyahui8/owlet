package io.github.lvyahui8.owlet.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.github.lvyahui8.owlet.Analyser;
import io.github.lvyahui8.owlet.CallGraphHolder;

public class Main {
    public static void main(String[] argv) {
        Args args = new Args();
        JCommander commander = JCommander.newBuilder().addObject(args).build();
        try {
            commander.parse(argv);
        } catch (ParameterException e) {
            System.err.println("invalid arguments. " + e.getMessage());
            commander.usage();
            System.exit(1);
        }
        System.out.println(args.originalClasspath);
        System.out.println(args.changedClasspath);
        CallGraphHolder originalHolder = new CallGraphHolder();
        originalHolder.load(args.originalClasspath);
        Analyser analyser = new Analyser();
        analyser.compare(originalHolder.getCallGraph(),null);
    }
}
