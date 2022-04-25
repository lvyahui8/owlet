package io.github.lvyahui8.owlet.constants;

public enum OS {
    Windows,
    Linux,
    Mac,
    ;
    public final static OS currentOS;

    public static boolean likeUnix() {
        return currentOS == Mac || currentOS == Linux;
    }

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac") || osName.contains("darwin")) {
            currentOS = Mac;
        } else if (osName.contains("win")) {
            currentOS = Windows;
        } else if (osName.contains("nix") || osName.contains("nux")) {
            currentOS = Linux;
        } else {
            throw new UnsupportedOperationException("Unsupported system " + osName);
        }
    }
}
