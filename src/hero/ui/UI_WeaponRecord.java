// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.util.Iterator;
import javolution.util.FastList;
import java.io.IOException;
import hero.share.service.LogWriter;
import hero.item.service.WeaponRankUnit;
import hero.item.service.WeaponRankManager;
import yoyo.tools.YOYOOutputStream;

public class UI_WeaponRecord {

    public static byte[] getBytes() {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            FastList<WeaponRankUnit> list = WeaponRankManager.getInstance().getRankList();
            output.writeByte(list.size());
            int i = 1;
            for (final WeaponRankUnit rank : list) {
                output.writeByte(i);
                output.writeUTF(rank.ownerName);
                output.writeShort(rank.weapon.getIconID());
                output.writeUTF(rank.weapon.getName());
                output.writeBytes(rank.weapon.getFixPropertyBytes());
                output.writeByte(0);
                output.writeByte(rank.existSeal);
                output.writeShort(rank.weapon.getMaxDurabilityPoint());
                output.writeInt(rank.weapon.getRetrievePrice());
                output.writeInt(rank.bloodyEnhance.getPveNumber());
                output.writeInt(rank.bloodyEnhance.getPveUpgradeNumber());
                output.writeByte(rank.bloodyEnhance.getPveLevel());
                if (rank.bloodyEnhance.getPveLevel() > 0) {
                    output.writeUTF(rank.bloodyEnhance.getPveBuff().desc);
                }
                output.writeInt(rank.bloodyEnhance.getPvpNumber());
                output.writeInt(rank.bloodyEnhance.getPvpUpgradeNumber());
                output.writeByte(rank.bloodyEnhance.getPvpLevel());
                if (rank.bloodyEnhance.getPvpLevel() > 0) {
                    output.writeUTF(rank.bloodyEnhance.getPvpBuff().desc);
                }
                output.writeBytes(rank.genericEnhance);
                ++i;
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            LogWriter.error(null, e);
        } finally {
            try {
                output.close();
                output = null;
            } catch (IOException ex) {
            }
        }
        return null;
    }

    public static EUIType getType() {
        return EUIType.WEAPON_RECORD_LIST;
    }
}
