// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import java.util.Iterator;
import hero.player.service.PlayerServiceImpl;
import hero.share.RankInfo;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseRankData extends AbsResponseMessage {

    private List<RankInfo> rankInfoList;
    private List<String> fieldList;

    public ResponseRankData(final List<RankInfo> rankInfoList, final List<String> fieldList) {
        this.rankInfoList = rankInfoList;
        this.fieldList = fieldList;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.fieldList.size());
        for (final String field : this.fieldList) {
            this.yos.writeUTF(field);
        }
        this.yos.writeByte(this.rankInfoList.size());
        for (final RankInfo info : this.rankInfoList) {
            this.yos.writeByte(info.rank);
            this.yos.writeUTF(info.name);
            this.yos.writeUTF(info.vocation);
            this.yos.writeInt(info.value);
            this.yos.writeInt(info.userID);
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(info.userID);
            if (player != null && player.isEnable()) {
                this.yos.writeByte(1);
            } else {
                this.yos.writeByte(0);
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
