// 
// Decompiled by Procyon v0.5.36
// 
package hero.group.message;

import java.io.IOException;
import hero.group.GroupMemberProxy;
import yoyo.core.packet.AbsResponseMessage;

public class AddMemberNotify extends AbsResponseMessage {

    private GroupMemberProxy member;

    public AddMemberNotify(final GroupMemberProxy _member) {
        this.member = _member;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.member.subGroupID);
        this.yos.writeInt(this.member.player.getUserID());
        this.yos.writeUTF(this.member.player.getName());
        this.yos.writeByte(this.member.player.getVocation().value());
        this.yos.writeByte(this.member.player.getSex().value());
        this.yos.writeShort(this.member.player.getLevel());
        this.yos.writeByte(this.member.memberRank.value());
    }
}
