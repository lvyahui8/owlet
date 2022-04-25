package io.github.lvyahui8.owlet.utils;

import io.github.lvyahui8.owlet.constants.OS;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        ProcessBuilder builder = new ProcessBuilder();
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
}
