// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import java.util.Iterator;
import hero.item.legacy.RaidPickerBox;
import hero.item.legacy.PersonalPickerBox;
import hero.item.legacy.MonsterLegacyBox;
import java.util.ArrayList;
import yoyo.core.packet.AbsResponseMessage;

public class SendLegacyBoxList extends AbsResponseMessage {

    private ArrayList<MonsterLegacyBox> legacyBoxList;
    private int playerUserID;

    public SendLegacyBoxList(final ArrayList<MonsterLegacyBox> _legacyBoxList, final int _playerUserID) {
        this.legacyBoxList = _legacyBoxList;
        this.playerUserID = _playerUserID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.legacyBoxList.size());
        for (final MonsterLegacyBox legacyBox : this.legacyBoxList) {
            this.yos.writeInt(legacyBox.getID());
            this.yos.writeInt(legacyBox.getMonsterID());
            if (legacyBox instanceof PersonalPickerBox) {
                this.yos.writeByte(true);
            } else {
                this.yos.writeByte(((RaidPickerBox) legacyBox).getStateOfPicking(this.playerUserID));
            }
            this.yos.writeByte(legacyBox.getLocationX());
            this.yos.writeByte(legacyBox.getLocationY());
        }
    }
}
