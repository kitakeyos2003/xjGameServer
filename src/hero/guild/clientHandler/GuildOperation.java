// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.clientHandler;

import hero.guild.GuildMemberProxy;
import hero.guild.Guild;
import hero.player.HeroPlayer;
import java.io.IOException;
import hero.guild.message.GuildInviteNotify;
import hero.social.service.SocialServiceImpl;
import hero.guild.EGuildMemberRank;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.guild.message.ResponseMemberList;
import yoyo.core.queue.ResponseMessageQueue;
import hero.guild.service.GuildServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class GuildOperation extends AbsClientProcess {

    private static final byte OPERATION_OF_VIEW_LIST = 1;
    private static final byte OPERATION_OF_ADD_MEMBER = 2;
    private static final byte OPERATION_OF_REMOVE_MEMBER = 3;
    private static final byte OPERATION_OF_CHANGE_RANK = 4;
    private static final byte OPERATION_OF_LEFT_GUILD = 5;
    private static final byte OPERATION_OF_CONFIRM_INVITE = 6;
    private static final byte OPERATION_OF_GUILD_UP = 7;
    private static final byte OPERATION_OF_GUILD_SEE = 8;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte operation = this.yis.readByte();
            switch (operation) {
                case 1: {
                    Guild guild = GuildServiceImpl.getInstance().getGuild(player.getGuildID());
                    if (guild == null) {
                        break;
                    }
                    GuildMemberProxy guildMemberProxy = guild.getMember(player.getUserID());
                    if (guildMemberProxy != null) {
                        byte page = this.yis.readByte();
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMemberList(guild.getMemberList(page), guildMemberProxy.memberRank, page, guild.getViewPageNumber(), guild.getMemberNumber(), guild.GetMaxMemberNumber(), guild));
                        break;
                    }
                    break;
                }
                case 2: {
                    Guild guild = GuildServiceImpl.getInstance().getGuild(player.getGuildID());
                    if (guild == null) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6ca1\u6709\u5e2e\u6d3e"));
                        return;
                    }
                    GuildMemberProxy guildMemberProxy = guild.getMember(player.getUserID());
                    if (guildMemberProxy == null || guildMemberProxy.memberRank == EGuildMemberRank.NORMAL) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u6743\u9650\u592a\u4f4e\u4e86"));
                        return;
                    }
                    String name = this.yis.readUTF();
                    HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(name);
                    if (otherPlayer.isInFighting()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u6b63\u5fd9"));
                        return;
                    }
                    if (guild.getMemberList().size() >= guild.GetMaxMemberNumber()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5e2e\u6d3e\u4eba\u6570\u5df2\u8fbe\u4e0a\u9650"));
                        return;
                    }
                    if (otherPlayer.getGuildID() > 0) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u73a9\u5bb6\u5df2\u6709\u5e2e\u6d3e"));
                        return;
                    }
                    if (otherPlayer == null || !otherPlayer.isEnable()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u73a9\u5bb6\u4e0d\u5728\u7ebf"));
                        return;
                    }
                    if (otherPlayer.getClan() != player.getClan()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u73a9\u5bb6\u4e0e\u60a8\u79cd\u65cf\u4e0d\u540c"));
                        return;
                    }
                    if (SocialServiceImpl.getInstance().beBlack(player.getUserID(), otherPlayer.getUserID())) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u88ab\u4eba\u5bb6\u62c9\u9ed1\u4e86"));
                        return;
                    }
                    if (otherPlayer.getGuildID() > 0) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u73a9\u5bb6\u5df2\u6709\u5e2e\u6d3e"));
                        return;
                    }
                    ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(), new GuildInviteNotify(player.getUserID(), player.getName(), guild.getID(), guild.getName()));
                    break;
                }
                case 3: {
                    int userIDWillBeRemove = this.yis.readInt();
                    GuildServiceImpl.getInstance().removeMember(player, userIDWillBeRemove);
                    break;
                }
                case 4: {
                    byte rankValue = this.yis.readByte();
                    int memberUserID = this.yis.readInt();
                    GuildServiceImpl.getInstance().changeMemberRank(player, memberUserID, EGuildMemberRank.getRank(rankValue));
                    break;
                }
                case 5: {
                    GuildServiceImpl.getInstance().leftGuild(player);
                    break;
                }
                case 6: {
                    byte receiveOrRefuse = this.yis.readByte();
                    int invitorUserID = this.yis.readInt();
                    int guildID = this.yis.readInt();
                    HeroPlayer invitor = PlayerServiceImpl.getInstance().getPlayerByUserID(invitorUserID);
                    if (receiveOrRefuse == 0) {
                        if (invitor != null && invitor.isEnable()) {
                            ResponseMessageQueue.getInstance().put(invitor.getMsgQueueIndex(), new Warning(String.valueOf(player.getName()) + "\u4e0d\u5c51\u4e8e\u52a0\u5165\u60a8\u7684\u5e2e\u6d3e"));
                        }
                        return;
                    }
                    if (invitor == null || !invitor.isEnable()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u9080\u8bf7\u8005\u5df2\u79bb\u7ebf"));
                        return;
                    }
                    GuildServiceImpl.getInstance().add(invitor, player, guildID);
                    break;
                }
                case 7: {
                    GuildServiceImpl.getInstance().GuildUp(player);
                    break;
                }
                case 8: {
                    GuildServiceImpl.getInstance().SeeGuildInfo(player);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
