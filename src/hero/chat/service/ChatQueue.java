// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.service;

import hero.share.service.LogWriter;
import hero.chat.message.GetGoodsNofity;
import hero.guild.Guild;
import hero.group.Group;
import hero.share.ME2GameObject;
import hero.share.service.ME2ObjectList;
import javolution.util.FastList;
import java.util.Iterator;
import hero.guild.GuildMemberProxy;
import hero.guild.service.GuildServiceImpl;
import hero.share.message.Warning;
import hero.group.service.GroupServiceImpl;
import hero.social.service.SocialServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.chat.message.ChatResponse;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.chat.core.MsgItem;
import java.util.ArrayList;

public class ChatQueue {

    private static ChatQueue queue;
    ArrayList<MsgItem> messageList;

    static {
        ChatQueue.queue = null;
    }

    public static ChatQueue getInstance() {
        if (ChatQueue.queue == null) {
            ChatQueue.queue = new ChatQueue();
        }
        return ChatQueue.queue;
    }

    private ChatQueue() {
        this.messageList = new ArrayList<MsgItem>();
        new Task().start();
    }

    protected void flush() {
        synchronized (this.messageList) {
            Iterator<MsgItem> iterator = this.messageList.iterator();
            while (iterator.hasNext()) {
                MsgItem item = iterator.next();
                iterator.remove();
                switch (item.type) {
                    case 1: {
                        FastList<HeroPlayer> list = PlayerServiceImpl.getInstance().getPlayerList();
                        // monitorenter(list8 = list)
                        try {
                            for (int i = 0; i < list.size(); ++i) {
                                HeroPlayer player = (HeroPlayer) list.get(i);
                                if (player != null && player.isEnable() && player.openWorldChat) {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content, item.showMiddle));
                                }
                            }
                            // monitorexit(list8)
                            continue;
                        } finally {
                        }
                    }
                    case 5: {
                        FastList<HeroPlayer> list = PlayerServiceImpl.getInstance().getPlayerList();
                        // monitorenter(list7 = list)
                        try {
                            for (int i = 0; i < list.size(); ++i) {
                                HeroPlayer player = (HeroPlayer) list.get(i);
                                if (player != null && player.isEnable()) {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content, item.showMiddle));
                                }
                            }
                        } // monitorexit(list7)
                        finally {
                        }
                        if (item.type != 1) {
                            byte type = item.type;
                            continue;
                        }
                        continue;
                    }
                    case 0: {
                        if (item.target != null && item.target.isEnable() && item.target.openSingleChat) {
                            ResponseMessageQueue.getInstance().put(item.target.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.destName, item.content));
                            item.target.getName().equals(item.srcName);
                            continue;
                        }
                        continue;
                    }
                    case 2: {
                        HeroPlayer speaker = PlayerServiceImpl.getInstance().getPlayerByName(item.srcName);
                        if (speaker != null && speaker.isEnable()) {
                            ME2ObjectList list2 = speaker.where().getPlayerList();
                            HeroPlayer destPlayer = null;
                            Iterator<ME2GameObject> iteraor = list2.iterator();
                            while (iteraor.hasNext()) {
                                destPlayer = (HeroPlayer) iteraor.next();
                                if (destPlayer.isEnable() && destPlayer.openMapChat && !SocialServiceImpl.getInstance().beBlack(speaker.getUserID(), destPlayer.getUserID())) {
                                    ResponseMessageQueue.getInstance().put(destPlayer.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content));
                                }
                            }
                            continue;
                        }
                        continue;
                    }
                    case 3: {
                        HeroPlayer speaker = PlayerServiceImpl.getInstance().getPlayerByName(item.srcName);
                        if (speaker == null || !speaker.isEnable()) {
                            continue;
                        }
                        Group group = GroupServiceImpl.getInstance().getGroup(speaker.getGroupID());
                        if (group != null) {
                            HeroPlayer player2 = null;
                            ArrayList<HeroPlayer> list3 = group.getPlayerList();
                            for (int j = 0; j < list3.size(); ++j) {
                                player2 = list3.get(j);
                                if (player2 != null && player2.isEnable() && !SocialServiceImpl.getInstance().beBlack(speaker.getUserID(), player2.getUserID())) {
                                    ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content));
                                }
                            }
                            continue;
                        }
                        ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), new Warning("\u961f\u4f0d\u4e0d\u5b58\u5728", (byte) 0));
                        continue;
                    }
                    case 4: {
                        HeroPlayer speaker = PlayerServiceImpl.getInstance().getPlayerByName(item.srcName);
                        Guild guild = GuildServiceImpl.getInstance().getGuild(speaker.getGuildID());
                        if (guild != null) {
                            ArrayList<GuildMemberProxy> list4 = guild.getMemberList();
                            for (int k = 0; k < list4.size(); ++k) {
                                GuildMemberProxy guildMember = list4.get(k);
                                if (guildMember != null && guildMember.isOnline) {
                                    HeroPlayer player3 = PlayerServiceImpl.getInstance().getPlayerByUserID(guildMember.userID);
                                    if (player3 != null && player3.isEnable() && !SocialServiceImpl.getInstance().beBlack(speaker.getUserID(), guildMember.userID)) {
                                        ResponseMessageQueue.getInstance().put(player3.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content));
                                    }
                                }
                            }
                            continue;
                        }
                        ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), new Warning("\u4f60\u8fd8\u6ca1\u6709\u52a0\u5165\u4efb\u4f55\u5e2e\u6d3e", (byte) 0));
                        continue;
                    }
                    case 8: {
                        Group group2 = GroupServiceImpl.getInstance().getGroup(item.groupID);
                        if (group2 != null) {
                            HeroPlayer player = null;
                            ArrayList<HeroPlayer> list5 = group2.getPlayerList();
                            for (int i = 0; i < list5.size(); ++i) {
                                player = list5.get(i);
                                if (player != null && player.isEnable()) {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content));
                                }
                            }
                            continue;
                        }
                        continue;
                    }
                    case 9: {
                        Guild guild2 = GuildServiceImpl.getInstance().getGuild(item.guildID);
                        if (guild2 != null) {
                            ArrayList<GuildMemberProxy> list6 = guild2.getMemberList();
                            for (int j = 0; j < list6.size(); ++j) {
                                GuildMemberProxy guildMember2 = list6.get(j);
                                HeroPlayer player4 = PlayerServiceImpl.getInstance().getPlayerByUserID(guildMember2.userID);
                                if (player4 != null && player4.isEnable()) {
                                    ResponseMessageQueue.getInstance().put(player4.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content));
                                }
                            }
                            continue;
                        }
                        continue;
                    }
                    default: {
                        continue;
                    }
                    case 10: {
                        FastList<HeroPlayer> list = PlayerServiceImpl.getInstance().getPlayerList();
                        for (int l = 0; l < list.size(); ++l) {
                            HeroPlayer player;
                            try {
                                player = (HeroPlayer) list.get(l);
                            } catch (Exception e) {
                                break;
                            }
                            if (player != null && player.isEnable() && item.clan == player.getClan().getID() && player.openClanChat) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content));
                            }
                        }
                        continue;
                    }
                    case 12: {
                        HeroPlayer player5 = PlayerServiceImpl.getInstance().getPlayerByName(item.destName);
                        ResponseMessageQueue.getInstance().put(player5.getMsgQueueIndex(), new ChatResponse(item.type, item.srcName, item.content, item.showMiddle));
                        continue;
                    }
                }
            }
        }
        // monitorexit(this.messageList)
    }

    public void add(final byte _type, final HeroPlayer _speaker, final String _destName, final HeroPlayer _target, final String _content, final short _clan) {
        MsgItem item = new MsgItem();
        item.type = _type;
        item.srcName = _speaker.getName();
        item.destName = _destName;
        item.target = _target;
        item.content = _content;
        item.clan = _clan;
        synchronized (this.messageList) {
            this.messageList.add(item);
        }
        // monitorexit(this.messageList)
    }

    public void add(final byte _type, final HeroPlayer _speaker, final String _destName, final HeroPlayer _target, final String _content) {
        this.add(_type, _speaker, _destName, _target, _content, false);
    }

    public void add(final byte _type, final HeroPlayer _speaker, final String _destName, final HeroPlayer _target, final String _content, final boolean showMiddle) {
        MsgItem item = new MsgItem();
        item.type = _type;
        if (_speaker != null) {
            item.srcName = _speaker.getName();
        } else {
            item.srcName = "";
        }
        item.destName = _destName;
        item.target = _target;
        item.content = _content;
        item.clan = -1;
        item.showMiddle = showMiddle;
        synchronized (this.messageList) {
            this.messageList.add(item);
        }
        // monitorexit(this.messageList)
    }

    public void add(final byte _type, final String _speakerName, final String _content, final boolean showMiddle) {
        MsgItem item = new MsgItem();
        item.type = _type;
        item.srcName = _speakerName;
        item.content = _content;
        item.clan = -1;
        item.showMiddle = showMiddle;
        synchronized (this.messageList) {
            this.messageList.add(item);
        }
        // monitorexit(this.messageList)
    }

    public void add(final byte _type, final String _srcName, final String _content) {
        MsgItem item = new MsgItem();
        item.type = _type;
        item.srcName = _srcName;
        item.destName = "";
        item.target = null;
        item.content = _content;
        item.clan = -1;
        synchronized (this.messageList) {
            this.messageList.add(item);
        }
        // monitorexit(this.messageList)
    }

    public void addGoodsMsg(final HeroPlayer _target, final String _content, final String _name, final int _traitRGB, final int _num) {
        if (_target.isEnable()) {
            ResponseMessageQueue.getInstance().put(_target.getMsgQueueIndex(), new GetGoodsNofity(_content, _name, _traitRGB, _num));
        }
    }

    public void addGoodsMsg(final HeroPlayer _target, final GetGoodsNofity _msg) {
        if (_target.isEnable()) {
            ResponseMessageQueue.getInstance().put(_target.getMsgQueueIndex(), _msg);
        }
    }

    public void addGroupSys(final int _groupID, final String _content) {
        MsgItem item = new MsgItem();
        item.type = 8;
        item.groupID = _groupID;
        item.content = _content;
        synchronized (this.messageList) {
            this.messageList.add(item);
        }
        // monitorexit(this.messageList)
    }

    public void addGuildSys(final int _guildID, final String _content) {
        MsgItem item = new MsgItem();
        item.type = 9;
        item.guildID = _guildID;
        item.content = _content;
        synchronized (this.messageList) {
            this.messageList.add(item);
        }
        // monitorexit(this.messageList)
    }

    class Task extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    ChatQueue.this.flush();
                } catch (Exception e) {
                    LogWriter.error(this, e);
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}
