// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;
import hero.player.HeroPlayer;

public class UI_PlayerView {

    private static final String ENTER = "\n";

    public static byte[] getBytes(final byte _id, final HeroPlayer _player) {
        YOYOOutputStream output = new YOYOOutputStream();
        StringBuffer str = new StringBuffer();
        str.append("\u6635\u79f0\uff1a");
        str.append(_player.getName());
        str.append("\n");
        str.append("\u804c\u4e1a\uff1a");
        str.append(_player.getVocation().getDesc());
        str.append("\n");
        str.append("\u7b49\u7ea7\uff1a");
        str.append(_player.getLevel());
        str.append("\n");
        str.append("\u751f\u547d\u503c\uff1a");
        str.append(_player.getActualProperty().getHpMax());
        str.append("\n");
        if (_player.getVocation().getType().getID() == 1) {
            str.append("\u529b\u6c14\u503c\uff1a50|50");
        } else {
            str.append("\u9b54\u6cd5\u503c\uff1a");
            str.append(_player.getActualProperty().getMpMax());
        }
        str.append("\n");
        str.append("\u4f4d\u7f6e\uff1a");
        str.append(_player.where().getName());
        try {
            output.writeByte(_id);
            output.writeUTF(str.toString());
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

    public static byte[] getBytes(final byte _id, final String _name) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            StringBuffer str = new StringBuffer();
            str.append("\u6635\u79f0\uff1a");
            str.append(_name);
            str.append("\n");
            str.append("\n");
            str.append("\u5f53\u524d\u4e0d\u5728\u7ebf");
            output.writeByte(_id);
            output.writeUTF(str.toString());
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
        return null;
    }

    public static EUIType getType() {
        return EUIType.GROUP_VIEW;
    }
}
