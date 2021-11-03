// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.message;

import java.io.IOException;
import java.util.Iterator;
import hero.guild.Guild;
import hero.guild.GuildMemberProxy;
import java.util.List;
import hero.guild.EGuildMemberRank;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMemberList extends AbsResponseMessage {

    private int currentPageNumber;
    private int totalPageNumber;
    private EGuildMemberRank rankOfSelf;
    private int memberNumber;
    private int memberNumberUpLimit;
    private List<GuildMemberProxy> currentPageMemberList;
    private Guild guild;
    private int[] moneyList;
    private int[] numberList;

    public ResponseMemberList(final List<GuildMemberProxy> _currentPageMemberList, final EGuildMemberRank _rankOfSelf, final int _currentPageNumber, final int _totalPageNumber, final int _memberNumber, final int _memberNumberUpLimit, final Guild _guild) {
        this.currentPageNumber = _currentPageNumber;
        this.memberNumber = _memberNumber;
        this.totalPageNumber = _totalPageNumber;
        this.rankOfSelf = _rankOfSelf;
        this.memberNumberUpLimit = _memberNumberUpLimit;
        this.currentPageMemberList = _currentPageMemberList;
        this.guild = _guild;
        this.moneyList = this.guild.getUpGuildMoneyList();
        this.numberList = this.guild.getUpGuildNumberList();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.moneyList.length);
        for (int i = 0; i < this.moneyList.length; ++i) {
            this.yos.writeInt(this.moneyList[i]);
        }
        this.yos.writeByte(this.numberList.length);
        for (int i = 0; i < this.numberList.length; ++i) {
            this.yos.writeShort(this.numberList[i]);
        }
        this.yos.writeByte(this.guild.getGuildLevel());
        this.yos.writeByte(this.rankOfSelf.value());
        this.yos.writeShort(this.memberNumber);
        this.yos.writeShort(this.memberNumberUpLimit);
        this.yos.writeByte(this.currentPageNumber);
        this.yos.writeByte(this.totalPageNumber);
        this.yos.writeByte(this.currentPageMemberList.size());
        for (final GuildMemberProxy memberProxy : this.currentPageMemberList) {
            this.yos.writeInt(memberProxy.userID);
            this.yos.writeUTF(memberProxy.name);
            this.yos.writeByte(memberProxy.memberRank.value());
            this.yos.writeByte(memberProxy.isOnline);
            if (memberProxy.isOnline) {
                this.yos.writeByte(memberProxy.vocation.value());
                this.yos.writeShort(memberProxy.level);
                this.yos.writeByte(memberProxy.sex.value());
            }
        }
    }
}
