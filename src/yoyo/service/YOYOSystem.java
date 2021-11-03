// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service;

public class YOYOSystem {

    public static String HOME;

    static {
        YOYOSystem.HOME = System.getenv("YOYO_HOME");
        if (YOYOSystem.HOME == null || YOYOSystem.HOME.equals("")) {
            YOYOSystem.HOME = "./";
        }
    }
}
