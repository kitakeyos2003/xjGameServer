// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild;

import hero.player.HeroPlayer;
import hero.share.EVocation;
import hero.player.define.ESex;

public class GuildMemberProxy {

    public int userID;
    public String name;
    public short level;
    public ESex sex;
    public EVocation vocation;
    public boolean isOnline;
    public EGuildMemberRank memberRank;

    public GuildMemberProxy(final int _userID, final HeroPlayer _player, final EGuildMemberRank _memberRank) {
        this.userID = _userID;
        this.name = _player.getName();
        this.level = _player.getLevel();
        this.sex = _player.getSex();
        this.vocation = _player.getVocation();
        this.memberRank = _memberRank;
        this.isOnline = true;
    }

    public GuildMemberProxy(final int _userID, final String _name, final byte _memberRankValue) {
        this.userID = _userID;
        this.name = _name;
        this.memberRank = EGuildMemberRank.getRank(_memberRankValue);
    }
}
