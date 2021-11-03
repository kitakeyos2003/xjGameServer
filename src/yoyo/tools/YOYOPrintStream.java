// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.tools;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import yoyo.service.YOYOSystem;
import java.io.PrintStream;

public class YOYOPrintStream extends PrintStream {

    private static String logOut;
    private static String logErr;
    PrintStream ps;

    static {
        YOYOPrintStream.logOut = String.valueOf(YOYOSystem.HOME) + "log" + File.separator + "out.log";
        YOYOPrintStream.logErr = String.valueOf(YOYOSystem.HOME) + "log" + File.separator + "err.log";
    }

    public static void init() {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(YOYOPrintStream.logOut));
            PrintStream pStream = new YOYOPrintStream(System.out, out);
            System.setOut(pStream);
            PrintStream err = new PrintStream(new FileOutputStream(YOYOPrintStream.logErr));
            pStream = new YOYOPrintStream(System.err, err);
            System.setErr(pStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public YOYOPrintStream(final PrintStream ps1, final PrintStream ps2) {
        super(ps1);
        this.ps = ps2;
    }

    @Override
    public void write(final byte[] bytes, final int offset, final int length) {
        try {
            super.write(bytes, offset, length);
            this.ps.write(bytes, offset, length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        super.flush();
        this.ps.flush();
    }
}
