package com.vahabilisim.deskbar;

public class OSInfo {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix")
                || OS.contains("nux")
                || OS.indexOf("aix") > 0;
    }

}
