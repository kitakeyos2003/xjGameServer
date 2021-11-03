// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import hero.map.Map;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class InviteAttendWedding extends AbsResponseMessage {

    private HeroPlayer inviter;
    private Map marryMap;
    private int dungeonID;

    public InviteAttendWedding(final HeroPlayer _inviter, final Map _map, final int _dungeonID) {
        this.inviter = _inviter;
        this.marryMap = _map;
        this.dungeonID = _dungeonID;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.dungeonID);
        this.yos.writeShort(this.marryMap.getID());
        this.yos.writeUTF(String.valueOf(this.inviter.getName()) + " \u9080\u8bf7\u60a8\u53c2\u52a0\u4ed6\u7684\u5a5a\u793c\uff0c\n\u60a8\u8981\u53bb\u5417\uff1f");
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
