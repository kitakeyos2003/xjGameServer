// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import yoyo.tools.YOYOOutputStream;

public class UI_SelectOperation {

    public static byte[] getBytes(final String _tip, final String[] _operateMenuArray) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(_operateMenuArray.length);
            for (final String menu : _operateMenuArray) {
                output.writeUTF(menu);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static byte[] getBytes(final String _tip, final String[] _operateMenuArray, final ArrayList<byte[]>[] _followOptionData) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(_operateMenuArray.length);
            for (int i = 0; i < _operateMenuArray.length; ++i) {
                output.writeUTF(_operateMenuArray[i]);
                if (_followOptionData != null && _followOptionData[i] != null) {
                    output.writeByte(_followOptionData[i].size());
                    for (final byte[] _data : _followOptionData[i]) {
                        output.writeBytes(_data);
                    }
                } else {
                    output.writeByte(0);
                }
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static EUIType getType() {
        return EUIType.SELECT;
    }
}
