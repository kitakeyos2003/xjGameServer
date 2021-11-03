// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.service;

import java.util.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.net.URLEncoder;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import org.apache.log4j.Logger;

public class M {

    private static final char[] DIGITS;
    private static Logger log;

    static {
        DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        M.log = Logger.getLogger((Class) M.class);
    }

    public static String md5(final String text, final String charSet) {
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        try {
            msgDigest.update(text.getBytes(charSet));
        } catch (UnsupportedEncodingException e2) {
            throw new IllegalStateException("System doesn't support your  EncodingException.");
        }
        byte[] bytes = msgDigest.digest();
        String md5Str = new String(encodeHex(bytes));
        return md5Str;
    }

    public static String sha256(final String text, final String charSet) {
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support SHA-256 algorithm.");
        }
        try {
            msgDigest.update(text.getBytes(charSet));
        } catch (UnsupportedEncodingException e2) {
            throw new IllegalStateException("System doesn't support your  EncodingException.");
        }
        byte[] bytes = msgDigest.digest();
        return Bytes2HexString(bytes);
    }

    public static String Bytes2HexString(final byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = String.valueOf('0') + hex;
            }
            ret = String.valueOf(ret) + hex.toUpperCase();
        }
        return ret;
    }

    public static String bytes2Hex(final byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; ++i) {
            tmp = Integer.toHexString(bts[i] & 0xFF);
            if (tmp.length() == 1) {
                des = String.valueOf(des) + "0";
            }
            des = String.valueOf(des) + tmp;
        }
        return des;
    }

    public static char[] encodeHex(final byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;
        int j = 0;
        while (i < l) {
            out[j++] = M.DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = M.DIGITS[0xF & data[i]];
            ++i;
        }
        return out;
    }

    public static InputStream httpRequest(final String urlvalue, final String des) {
        try {
            URL url = new URL(urlvalue);
            System.out.println("url:" + urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("content-type", "text/html");
            urlConnection.setRequestProperty("Accept-Charset", "utf-8");
            return urlConnection.getInputStream();
        } catch (Exception e) {
            M.log.error((Object) (String.valueOf(des) + "\u901a\u4fe1\u5f02\u5e38"), (Throwable) e);
            return null;
        }
    }

    public static InputStream httpPostRequest(final String content, final String urlvalue, final String des) {
        HttpURLConnection c = null;
        InputStream instr = null;
        System.out.println("url:" + urlvalue);
        try {
            URL url = new URL(urlvalue);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Accept-Charset", "utf-8");
            c.setDoOutput(true);
            c.setDoInput(true);
            c.connect();
            PrintWriter out = new PrintWriter(c.getOutputStream());
            System.out.println(content);
            out.print(content);
            out.flush();
            out.close();
            int res = 0;
            res = c.getResponseCode();
            if (res == 200) {
                instr = c.getInputStream();
            } else {
                M.log.error((Object) ("\u8bba\u575bpost\u6c22\u6c14\u6570\u636e\u5f02\u5e38code=" + res));
            }
        } catch (Exception e) {
            M.log.error((Object) "post\u8bf7\u6c42\u6570\u636e\u5f02\u5e38", (Throwable) e);
        }
        return instr;
    }

    public static String getEncodeURL(final String url, final String enc, final Class subClass) {
        try {
            return URLEncoder.encode(url, enc);
        } catch (UnsupportedEncodingException e) {
            M.log.error((Object) (String.valueOf(subClass.getName()) + "\u4e2dgetEncodeURL(url, enc):" + enc + "\u4e3a\u4e0d\u652f\u6301\u7684\u7f16\u7801\u65b9\u5f0f\u3002"), (Throwable) e);
            return url;
        }
    }

    public static String getTimeDes(final String time) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time.trim());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date.getTime());
            Calendar calendar2 = Calendar.getInstance();
            if (calendar.get(6) == calendar2.get(6)) {
                return "\u4eca\u5929";
            }
            calendar2.add(6, -1);
            if (calendar.get(6) == calendar2.get(6)) {
                return "\u6628\u5929";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "\u5f88\u4e45";
    }
}
