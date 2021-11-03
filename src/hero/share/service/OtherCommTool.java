// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.util.Date;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Random;

public class OtherCommTool {

    private static Random random;
    private static byte[] dateBuf;
    protected static byte[][] bdays;
    protected static byte[][] bmonthes;

    static {
        OtherCommTool.dateBuf = null;
        OtherCommTool.bdays = new byte[][]{{80, 97, 100}, {83, 117, 110}, {77, 111, 110}, {84, 117, 101}, {87, 101, 100}, {84, 104, 117}, {70, 114, 105}, {83, 97, 116}};
        OtherCommTool.bmonthes = new byte[][]{{74, 97, 110}, {70, 101, 98}, {77, 97, 114}, {65, 112, 114}, {77, 97, 121}, {74, 117, 110}, {74, 117, 108}, {65, 117, 103}, {83, 101, 112}, {79, 99, 116}, {78, 111, 118}, {68, 101, 99}};
        (OtherCommTool.random = new Random()).setSeed(Calendar.getInstance().getTimeInMillis());
        OtherCommTool.dateBuf = new byte[29];
    }

    private OtherCommTool() {
    }

    public static void clear(final char[] _input) {
        for (short length = (short) _input.length, i = 0; i < length; ++i) {
            _input[i] = '\0';
        }
    }

    public static String getCurrentTime() {
        Calendar now = Calendar.getInstance();
        StringBuffer time = new StringBuffer("");
        time.append(now.get(1));
        time.append("-");
        time.append(now.get(2) + 1);
        time.append("-");
        time.append(now.get(5));
        time.append(" ");
        time.append(now.get(11));
        time.append(":");
        time.append(now.get(12));
        return time.toString();
    }

    public static boolean moveFile(final String _soureFile, final String _destFile) {
        File soureFile = new File(_soureFile);
        File destFile = new File(_destFile);
        return moveFile(soureFile, destFile);
    }

    public static boolean moveFile(final File _soureFile, final File _destFile) {
        boolean moveSuccess = false;
        if (_soureFile != null && _destFile != null && _soureFile.exists()) {
            try {
                new File(_destFile.getParent()).mkdirs();
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(_destFile));
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(_soureFile));
                for (int currentByte = in.read(); currentByte != -1; currentByte = in.read()) {
                    out.write(currentByte);
                }
                in.close();
                out.close();
                moveSuccess = true;
                _soureFile.delete();
            } catch (Exception e) {
                moveSuccess = false;
            }
        }
        return moveSuccess;
    }

    public static final int getRandomInt(final int _range) {
        return OtherCommTool.random.nextInt(_range);
    }

    public static boolean containBlackSpace(final char[] _input, final int _length) {
        for (int i = 0; i < _length; ++i) {
            if (_input[i] <= ' ') {
                return true;
            }
        }
        return false;
    }

    public static boolean containBlackSpace(final String input, final int _length) {
        char[] _input = input.toCharArray();
        for (int i = 0; i < _length; ++i) {
            if (_input[i] <= ' ') {
                return true;
            }
        }
        return false;
    }

    public static boolean isValideChar(final char[] _input, final int _length) {
        for (int i = 0; i < _length; ++i) {
            if (_input[i] < '!' || _input[i] > '~') {
                return false;
            }
        }
        return true;
    }

    public static boolean isValideChar(final String _input, final int _length) {
        char[] input = _input.toCharArray();
        for (int i = 0; i < _length; ++i) {
            if (input[i] < '!' || input[i] > '~') {
                return false;
            }
        }
        return true;
    }

    public static byte[] getHTTPDate() {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        int dayofweek = cal.get(7);
        int j = 0;
        for (int i = 0; i < 3; ++i) {
            OtherCommTool.dateBuf[j++] = OtherCommTool.bdays[dayofweek][i];
        }
        OtherCommTool.dateBuf[j++] = 44;
        OtherCommTool.dateBuf[j++] = 32;
        int day = cal.get(5);
        if (day < 10) {
            OtherCommTool.dateBuf[j++] = 48;
            OtherCommTool.dateBuf[j++] = (byte) (48 + day);
        } else {
            OtherCommTool.dateBuf[j++] = (byte) (48 + day / 10);
            OtherCommTool.dateBuf[j++] = (byte) (48 + day % 10);
        }
        OtherCommTool.dateBuf[j++] = 32;
        int month = cal.get(2);
        for (int k = 0; k < 3; ++k) {
            OtherCommTool.dateBuf[j++] = OtherCommTool.bmonthes[month][k];
        }
        OtherCommTool.dateBuf[j++] = 32;
        int year = cal.get(1);
        OtherCommTool.dateBuf[j + 3] = (byte) (48 + year % 10);
        year /= 10;
        OtherCommTool.dateBuf[j + 2] = (byte) (48 + year % 10);
        year /= 10;
        OtherCommTool.dateBuf[j + 1] = (byte) (48 + year % 10);
        year /= 10;
        OtherCommTool.dateBuf[j] = (byte) (48 + year);
        j += 4;
        OtherCommTool.dateBuf[j++] = 32;
        int hour = cal.get(11);
        if (hour < 10) {
            OtherCommTool.dateBuf[j++] = 48;
            OtherCommTool.dateBuf[j++] = (byte) (48 + hour);
        } else {
            OtherCommTool.dateBuf[j++] = (byte) (48 + hour / 10);
            OtherCommTool.dateBuf[j++] = (byte) (48 + hour % 10);
        }
        OtherCommTool.dateBuf[j++] = 58;
        int minute = cal.get(12);
        if (minute < 10) {
            OtherCommTool.dateBuf[j++] = 48;
            OtherCommTool.dateBuf[j++] = (byte) (48 + minute);
        } else {
            OtherCommTool.dateBuf[j++] = (byte) (48 + minute / 10);
            OtherCommTool.dateBuf[j++] = (byte) (48 + minute % 10);
        }
        OtherCommTool.dateBuf[j++] = 58;
        int second = cal.get(13);
        if (second < 10) {
            OtherCommTool.dateBuf[j++] = 48;
            OtherCommTool.dateBuf[j++] = (byte) (48 + second);
        } else {
            OtherCommTool.dateBuf[j++] = (byte) (48 + second / 10);
            OtherCommTool.dateBuf[j++] = (byte) (48 + second % 10);
        }
        OtherCommTool.dateBuf[j++] = 32;
        OtherCommTool.dateBuf[j++] = 71;
        OtherCommTool.dateBuf[j++] = 77;
        OtherCommTool.dateBuf[j++] = 84;
        return OtherCommTool.dateBuf;
    }
}
