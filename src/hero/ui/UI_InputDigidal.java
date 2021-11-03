// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;

public class UI_InputDigidal {

    public static byte[] getBytes(final String _tip, final int _valueLowerLimit, final int _valueUpLimit) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeByte(1);
            output.writeUTF(_tip);
            output.writeInt(_valueLowerLimit);
            output.writeInt(_valueUpLimit);
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
            output.writeByte(0);
            output.writeUTF(_tip);
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
        return EUIType.INPUT_DIGITAL;
    }
}
