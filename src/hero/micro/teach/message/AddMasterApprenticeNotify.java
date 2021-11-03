// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.packet.AbsResponseMessage;

public class AddMasterApprenticeNotify extends AbsResponseMessage {

    private byte relationType;
    private int userID;

    public AddMasterApprenticeNotify(final byte _relationType, final int _userID) {
        this.relationType = _relationType;
        this.userID = _userID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        HeroPlayer target = PlayerServiceImpl.getInstance().getPlayerByUserID(this.userID);
        if (target != null) {
            this.yos.writeByte(this.relationType);
            this.yos.writeInt(this.userID);
            this.yos.writeUTF(target.getName());
            this.yos.writeByte(target.getVocation().value());
            this.yos.writeShort(target.getLevel());
            this.yos.writeByte(target.getSex().value());
        }
    }
}
