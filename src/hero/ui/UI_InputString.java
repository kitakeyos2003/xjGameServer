// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;

public class UI_InputString {

    public static byte[] getBytes(final String _tip, final int _charNumLowerLimit, final int _charNumUpLimit) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(_charNumLowerLimit);
            output.writeByte(_charNumUpLimit);
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

    public static byte[] getBytes(final String _tip) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(1);
            output.writeByte(20);
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
        return EUIType.INPUT_STRING;
    }
}
