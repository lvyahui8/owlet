package io.github.lvyahui8.owlet.utils;

import io.github.lvyahui8.owlet.constants.OS;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JavaUtils {
    public static final Set<String> usualJavaBinPaths = new HashSet<>(Arrays.asList(
            "/bin/java",
            "/usr/bin/java",
            "/usr/local/bin/java"
    ));
    public static String getJava() {
        String javaHome = System.getProperty("JAVA_HOME");
        if (StringUtils.isNotBlank(javaHome)) {
            return javaHome + File.separator + "bin" + File.separator + "java";
        }
        if (OS.likeUnix()) {
            for (String path : usualJavaBinPaths) {
                File file = new File(path);
                if (file.exists()) {
                    return path;
                }
            }
        }
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"java", "-version"});
            int status = process.waitFor();
            if (status == 0) {
                return "java";
            }
        } catch (Exception ignored) {
        }
        throw new UnsupportedOperationException("Java not found");
    }

    public static Process forkJavaProcess(Class<?> mainClass, List<String> args) {
        String classpath = System.getProperty("java.class.path");
        List<String> commands = new ArrayList<>(Arrays.asList(
                getJava(), "-cp", classpath, mainClass.getName()
        ));
        commands.addAll(args);
        try {
            ProcessBuilder builder = new ProcessBuilder(commands.toArray(new String[0]));
            builder.redirectErrorStream(true);
            return builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
