// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;

public class UI_Tip {

    public static EUIType getType() {
        return EUIType.TIP;
    }

    public static byte[] getBytes(final String _content) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeUTF(_content);
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
}
