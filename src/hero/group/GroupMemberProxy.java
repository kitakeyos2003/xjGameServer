// 
// Decompiled by Procyon v0.5.36
// 
package hero.group;

import hero.player.HeroPlayer;

public class GroupMemberProxy {

    public HeroPlayer player;
    public EGroupMemberRank memberRank;
    public int subGroupID;
    private byte pattern;

    public byte getPattern() {
        return this.pattern;
    }

    public void setPattern(final byte pattern) {
        this.pattern = pattern;
    }

    public GroupMemberProxy(final HeroPlayer _player, final int _subGroupID) {
        this.player = _player;
        this.subGroupID = _subGroupID;
        this.memberRank = EGroupMemberRank.NORMAL;
    }

    public boolean isOnline() {
        return this.player != null && this.player.isEnable();
    }
}
