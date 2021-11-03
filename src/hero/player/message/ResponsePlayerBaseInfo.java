// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import hero.micro.service.MicroServiceImpl;
import hero.guild.service.GuildServiceImpl;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ResponsePlayerBaseInfo extends AbsResponseMessage {

    private HeroPlayer player;

    public ResponsePlayerBaseInfo(final HeroPlayer _player) {
        this.player = _player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        if (this.player == null) {
            this.yos.writeUTF("");
        } else {
            StringBuffer sb = new StringBuffer("\u6635\u79f0\uff1a");
            sb.append(this.player.getName());
            sb.append("\n");
            sb.append("\u804c\u4e1a\uff1a");
            sb.append(this.player.getVocation().getDesc());
            sb.append("\n");
            sb.append("\u7b49\u7ea7\uff1a");
            sb.append(this.player.getLevel());
            sb.append("\n");
            sb.append("\u6027\u522b\uff1a");
            sb.append(this.player.getSex().getDesc());
            sb.append("\n");
            sb.append("\u79cd\u65cf\uff1a");
            sb.append(this.player.getClan().getDesc());
            sb.append("\n");
            sb.append("\u5e2e\u6d3e\uff1a");
            sb.append(GuildServiceImpl.getInstance().getGuildName(this.player));
            sb.append("\n");
            sb.append("\u5e2e\u6d3e\u804c\u52a1\uff1a");
            sb.append(GuildServiceImpl.getInstance().getMemberRank(this.player));
            sb.append("\n");
            sb.append("\u914d\u5076\uff1a");
            sb.append(this.player.spouse);
            sb.append("\n");
            sb.append("\u5e08\u5085\uff1a");
            sb.append((MicroServiceImpl.getInstance().getMasterName(this.player).length() > 0) ? MicroServiceImpl.getInstance().getMasterName(this.player) : "\u65e0");
            sb.append("\n");
            sb.append("\u5f92\u5f1f\uff1a");
            sb.append((MicroServiceImpl.getInstance().getApprenticeNameList(this.player).length() > 0) ? MicroServiceImpl.getInstance().getApprenticeNameList(this.player) : "\u65e0");
            sb.append("\n");
            this.yos.writeUTF(sb.toString());
        }
    }
}
