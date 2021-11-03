// 
// Decompiled by Procyon v0.5.36
// 
package hero.group.message;

import java.io.IOException;
import java.util.ArrayList;
import hero.group.GroupMemberProxy;
import hero.group.Group;
import yoyo.core.packet.AbsResponseMessage;

public class GroupMemberListNotify extends AbsResponseMessage {

    private Group group;

    public GroupMemberListNotify(final Group _group) {
        this.group = _group;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        ArrayList<GroupMemberProxy> list = this.group.getMemberList();
        this.yos.writeByte(list.size());
        for (int i = 0; i < list.size(); ++i) {
            GroupMemberProxy member = list.get(i);
            this.yos.writeByte(member.subGroupID);
            this.yos.writeInt(member.player.getUserID());
            this.yos.writeUTF(member.player.getName());
            this.yos.writeByte(member.player.getVocation().value());
            this.yos.writeByte(member.player.getSex().value());
            this.yos.writeShort(member.player.getLevel());
            this.yos.writeByte(member.memberRank.value());
            this.yos.writeByte(member.isOnline());
        }
    }
}
