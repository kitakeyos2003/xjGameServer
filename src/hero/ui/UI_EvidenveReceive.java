// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;

public class UI_EvidenveReceive {

    public static EUIType getType() {
        return EUIType.EVIDENVE_RECEIVE;
    }

    public static EUIType getEndType() {
        return EUIType.TIP_UI;
    }

    public static byte[] getBytes(final int[] inputLenghts, final String[] inputContents, final String _title) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeUTF(_title);
            output.writeByte(inputLenghts.length);
            for (int i = 0; i < inputLenghts.length; ++i) {
                output.writeByte(inputLenghts[i]);
                output.writeUTF(inputContents[i]);
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.getBytes();
    }

    public static byte[] getEndBytes(final String _endContent, final String[] _award, final String _title) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getEndType().getID());
            output.writeUTF(_title);
            String utf = "";
            for (int i = 0; i < _award.length; ++i) {
                utf = String.valueOf(_award[i]) + "#HH";
            }
            output.writeUTF(utf);
            output.writeUTF(_endContent);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.getBytes();
    }
}
