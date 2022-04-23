package io.github.lvyahui8.owlet.cmd;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = {"-cp","-classpath"},description = "classpath",required = true)
    String classpath;
}
