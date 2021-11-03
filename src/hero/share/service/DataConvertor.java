// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.text.NumberFormat;
import java.util.Locale;
import yoyo.tools.YOYOOutputStream;
import org.apache.log4j.Logger;

public class DataConvertor {

    private static Logger log;

    static {
        DataConvertor.log = Logger.getLogger((Class) DataConvertor.class);
    }

    public static int byte2Int(final byte _input) {
        return Integer.parseInt(String.valueOf(_input));
    }

    public static byte int2Byte(final int _input) {
        return Byte.parseByte(String.valueOf(_input));
    }

    public static void int2Chars(final int _input, final char[] _output) {
        _output[0] = (char) (_input >>> 16);
        _output[1] = (char) (_input & 0xFFFF);
    }

    public static short byte2Short(final byte _input) {
        return Short.parseShort(String.valueOf(_input));
    }

    public static byte short2Byte(final short _input) {
        return Byte.parseByte(String.valueOf(_input));
    }

    public static char byte2Char(final byte _input) {
        return (char) Short.parseShort(String.valueOf(_input));
    }

    public static byte char2Byte(final char _input) {
        return Byte.parseByte(String.valueOf(_input));
    }

    public static int chars2Int(final char[] _input) {
        return chars2Int(_input, 0);
    }

    public static int chars2Int(final char[] _input, final int _offset) {
        int high = _input[_offset];
        int low = _input[_offset + 1];
        return high << 16 | (low & 0xFFFF);
    }

    public static short bytes2Short(final byte[] _input) {
        byte high = _input[0];
        byte low = _input[1];
        return (short) (high << 8 | (low & 0xFF));
    }

    public static int bytes2Int(final byte[] bRefArr, final int _start, final int _length) {
        int iOutcome = 0;
        int temp = 0;
        for (int i = _start; i < _start + _length; ++i) {
            byte bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << 8 * (3 - temp);
            ++temp;
        }
        return iOutcome;
    }

    public static short bytes2Short(final byte[] bRefArr, final int _startIndex) {
        short sOutcome = 0;
        sOutcome += (short) ((bRefArr[_startIndex] & 0xFF) << 8);
        sOutcome += (short) (bRefArr[_startIndex + 1] & 0xFF);
        return sOutcome;
    }

    public static void main(final String[] args) {
        try {
            YOYOOutputStream a = new YOYOOutputStream();
            a.writeShort((short) 120);
            a.flush();
            byte[] b = a.getBytes();
            short c = bytes2Short(b, 0);
            DataConvertor.log.info((Object) ("c:" + c));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static char bytes2Char(final byte[] _input) {
        byte high = _input[0];
        byte low = _input[1];
        return (char) (high << 8 | (low & 0xFF));
    }

    public static int bytes2Int(final byte[] _input) {
        byte byte1 = _input[0];
        byte byte2 = _input[1];
        byte byte3 = _input[2];
        byte byte4 = _input[3];
        return (char) ((byte4 << 32) + (byte3 << 16) + (byte2 << 8) + byte1);
    }

    public static byte[] int2bytes(final int _value) {
        byte[] value = {(byte) (_value >>> 24), (byte) (_value << 8 >>> 24), (byte) (_value << 16 >>> 24), (byte) (_value & 0xFF)};
        return value;
    }

    public static void bytes2Shorts(final short _arrayLength, final byte[] _input, final short[] _output) {
        for (short arrayLength = (short) (_arrayLength / 2), i = 0; i < arrayLength; ++i) {
            byte high = _input[i * 2];
            byte low = _input[i * 2 + 1];
            if (low < 0) {
                _output[i] = (short) (low + 256);
            } else {
                _output[i] = low;
            }
            if (high < 0) {
                _output[i] += (short) (high + 256 << 8);
            } else {
                _output[i] += (short) (high << 8);
            }
        }
    }

    public static void shorts2Bytes(final short _arrayLength, final short[] _input, final byte[] _output) {
        for (short i = 0; i < _arrayLength; ++i) {
            _output[i * 2] = (byte) (_input[i] >>> 8);
            _output[i * 2 + 1] = (byte) (_input[i] & 0xFF);
        }
    }

    public static void short2Bytes(final short _input, final byte[] _output) {
        _output[0] = (byte) (_input >>> 8);
        _output[1] = (byte) (_input & 0xFF);
    }

    public static void char2Bytes(final char _input, final byte[] _output) {
        _output[0] = (byte) (_input >>> 8);
        _output[1] = (byte) (_input & '\u00ff');
    }

    public static void bytes2Chars(final short _arrayLength, final byte[] _input, final char[] _output) {
        for (short arrayLength = (short) (_arrayLength / 2), i = 0; i < arrayLength; ++i) {
            char high = (_input[i * 2] < 0) ? ((char) (_input[i * 2] + 256)) : ((char) _input[i * 2]);
            char low = (_input[i * 2 + 1] < 0) ? ((char) (_input[i * 2 + 1] + 256)) : ((char) _input[i * 2 + 1]);
            high <<= 8;
            _output[i] = (char) (high + low);
        }
    }

    public static void bytes2Chars(final short _arrayLength, final byte[] _input, final char[] _output, final short _off) {
        for (short arrayLength = (short) (_arrayLength / 2), i = 0; i < arrayLength; ++i) {
            char high = (_input[i * 2] < 0) ? ((char) (_input[i * 2] + 256)) : ((char) _input[i * 2]);
            char low = (_input[i * 2 + 1] < 0) ? ((char) (_input[i * 2 + 1] + 256)) : ((char) _input[i * 2 + 1]);
            high <<= 8;
            _output[i + _off] = (char) (high + low);
        }
    }

    public static String bytes2String(final byte[] _input, final int len) {
        int n = (len + 1) / 2;
        if (n == 0) {
            return "";
        }
        char[] chs = new char[n];
        for (int i = 0; i < n; ++i) {
            chs[i] = (char) (_input[2 * i] << 8 | (_input[2 * i + 1] & 0xFF));
        }
        return new String(chs);
    }

    public static String bytes2String(final byte[] _input) {
        int n = (_input.length + 1) / 2;
        if (n == 0) {
            return "";
        }
        char[] chs = new char[n];
        for (int i = 0; i < n; ++i) {
            chs[i] = (char) (_input[2 * i] << 8 | (_input[2 * i + 1] & 0xFF));
        }
        return new String(chs);
    }

    public static void bytes2Chars(short _arrayLength, final short _off, final byte[] _input, final char[] _output) {
        _arrayLength += _off;
        short j = 0;
        for (short i = _off; i < _arrayLength; i += 2) {
            char high = (_input[i] < 0) ? ((char) (_input[i] + 256)) : ((char) _input[i]);
            char low = (_input[i + 1] < 0) ? ((char) (_input[i + 1] + 256)) : ((char) _input[i + 1]);
            high <<= 8;
            _output[j] = (char) (high + low);
            ++j;
        }
    }

    public static void chars2Bytes(final short _arrayLength, final char[] _input, final byte[] _output) {
        for (short i = 0; i < _arrayLength; ++i) {
            char high = (char) (_input[i] >>> 8);
            char low = (char) (_input[i] & '\u00ff');
            _output[i * 2] = ((high >= '\u0080') ? ((byte) (high - '\u0100')) : ((byte) high));
            _output[i * 2 + 1] = ((low >= '\u0080') ? ((byte) (low - '\u0100')) : ((byte) low));
        }
    }

    public static void string2Shorts(final String _input, final short[] _output, final int _off) {
        char[] chars = _input.toCharArray();
        int length = chars.length;
        for (short i = 0; i < length; ++i) {
            _output[_off + i] = (short) chars[i];
        }
    }

    public static short intel2Net(final short _input) {
        byte high = (byte) (_input >>> 8);
        byte low = (byte) (_input & 0xFF);
        return (short) ((low << 8) + high);
    }

    public static char intel2Net(final char _input) {
        byte high = (byte) (_input >>> 8);
        byte low = (byte) (_input & '\u00ff');
        return (char) ((low << 8) + high);
    }

    public static int intel2Net(final int _input) {
        byte byte1 = (byte) (_input >>> 32);
        byte byte2 = (byte) (_input >>> 16 & 0xFF);
        byte byte3 = (byte) (_input >>> 8 & 0xFF);
        byte byte4 = (byte) (_input & 0xFF);
        return (char) ((byte4 << 32) + (byte3 << 16) + (byte2 << 8) + byte1);
    }

    public static void intel2Net(final short _arrayLength, final char[] _input, final char[] _output) {
        for (short i = 0; i < _arrayLength; ++i) {
            byte high = (byte) (_input[i] >>> 8);
            byte low = (byte) (_input[i] & '\u00ff');
            _output[i] = (char) ((low << 8) + high);
        }
    }

    public static void intel2Net(final short _arrayLength, final short[] _input, final short[] _output) {
        for (short i = 0; i < _arrayLength; ++i) {
            byte high = (byte) (_input[i] >>> 8);
            byte low = (byte) (_input[i] & 0xFF);
            _output[i] = (short) ((low << 8) + high);
        }
    }

    public static void net2Intel(final short _arrayLength, final char[] _input, final char[] _output) {
        for (short i = 0; i < _arrayLength; ++i) {
            byte high = (byte) (_input[i] >>> 8);
            byte low = (byte) (_input[i] & '\u00ff');
            _output[i] = (char) ((low << 8) + high);
        }
    }

    public static float percentString2Float(final String _percent) {
        float value = 0.0f;
        try {
            value = NumberFormat.getPercentInstance(Locale.US).parse(_percent).floatValue();
        } catch (Exception ex) {
        }
        return value;
    }

    public static float percentElementsString2Float(final String _percent) {
        return Float.parseFloat(_percent) / 100.0f;
    }
}
