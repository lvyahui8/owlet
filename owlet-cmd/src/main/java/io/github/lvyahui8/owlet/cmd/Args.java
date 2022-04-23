package io.github.lvyahui8.owlet.cmd;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = {"-ocp"},description = "original program classpath",required = true)
    String originalClasspath;
    @Parameter(names = {"-ccp"},description = "changed program classpath",required = true)
    String changedClasspath;
}
