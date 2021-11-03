// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.core;

import hero.player.HeroPlayer;

public class MsgItem {

    public byte type;
    public String srcName;
    public String destName;
    public HeroPlayer target;
    public String content;
    public short clan;
    public int groupID;
    public int guildID;
    public boolean showMiddle;

    public MsgItem() {
        this.showMiddle = false;
    }
}
