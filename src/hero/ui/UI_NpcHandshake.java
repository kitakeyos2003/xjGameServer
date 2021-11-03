// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.util.Iterator;
import java.io.IOException;
import yoyo.tools.YOYOOutputStream;
import hero.npc.detail.NpcHandshakeOptionData;
import java.util.ArrayList;

public class UI_NpcHandshake {

    public static EUIType getType() {
        return EUIType.NPC_HANDSHAKE;
    }

    public static byte[] getBytes(final ArrayList<NpcHandshakeOptionData> _funcitonMarkList) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            if (_funcitonMarkList != null && _funcitonMarkList.size() > 0) {
                output.writeByte((byte) _funcitonMarkList.size());
                for (final NpcHandshakeOptionData data : _funcitonMarkList) {
                    output.writeInt(data.functionMark);
                    output.writeShort(data.miniImageID);
                    output.writeUTF(data.optionDesc);
                    if (data.followOptionData != null) {
                        output.writeByte(data.followOptionData.size());
                        for (final byte[] b : data.followOptionData) {
                            output.writeBytes(b);
                        }
                    } else {
                        output.writeByte(0);
                    }
                }
            } else {
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
}
