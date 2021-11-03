// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;

public class UI_SelectOperationWithTip {

    public static byte[] getBytes(final String _tip, final String[][] _operateMenuArray) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeUTF(_tip);
            output.writeByte(_operateMenuArray.length);
            for (final String[] menu : _operateMenuArray) {
                output.writeUTF(menu[0]);
                output.writeUTF(menu[1]);
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

    public static EUIType getType() {
        return EUIType.SELECT_WITH_TIP;
    }
}
