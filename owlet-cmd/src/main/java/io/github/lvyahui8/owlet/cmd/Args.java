package io.github.lvyahui8.owlet.cmd;

import com.beust.jcommander.Parameter;

import java.util.List;

public class Args {
    @Parameter(names = {"-ocp"},description = "original program classpath",required = true)
    String originalClasspath;
    @Parameter(names = {"-ccp"},description = "changed program classpath",required = true)
    String changedClasspath;

    @Parameter(names = {"-kps","-keptPackages"},description = "Which packages should be kept in the graph?",required = true)
    List<String> keptPackages;
}
