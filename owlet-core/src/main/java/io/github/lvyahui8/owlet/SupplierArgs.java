package io.github.lvyahui8.owlet;

import com.beust.jcommander.Parameter;

import java.util.List;

public class SupplierArgs {
    @Parameter(names = {"-cp"},description = "program classpath",required = true)
    String classpath;

    @Parameter(names = {"-kps","-keptPackages"},description = "Which packages should be kept in the graph?",required = true)
    List<String> keptPackages;
}
