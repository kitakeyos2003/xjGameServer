// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;

public class UI_GridGoodsNumsChanged {

    public static EUIType getType() {
        return EUIType.GRID_CHANGED;
    }

    public static byte[] getBytes(final int _gridIndex, final int _goodsID, final int _currentNums) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeByte(_gridIndex);
            output.writeInt(_goodsID);
            output.writeByte(_currentNums);
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
