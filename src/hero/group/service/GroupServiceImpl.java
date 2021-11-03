// 
// Decompiled by Procyon v0.5.36
// 
package hero.group.service;

import java.util.Iterator;
import hero.group.message.OthersHpMpNotify;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import java.util.ArrayList;
import hero.share.message.Warning;
import hero.group.message.ReduceMemberNotify;
import hero.dungeon.service.TransmitterOfDungeon;
import hero.group.message.GroupDisbandNotify;
import hero.group.EGroupMemberRank;
import hero.group.GroupMemberProxy;
import hero.group.message.AddMemberNotify;
import hero.lover.service.LoverServiceImpl;
import hero.group.message.GroupMemberListNotify;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.service.IDManager;
import yoyo.core.packet.AbsResponseMessage;
import hero.group.message.MemberChangerNotify;
import hero.player.HeroPlayer;
import hero.group.Group;
import java.util.HashMap;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class GroupServiceImpl extends AbsServiceAdaptor<GroupServerConfig> {

    private static Logger log;
    private static GroupServiceImpl instance;
    private HashMap<Integer, Group> groupTable;

    static {
        GroupServiceImpl.log = Logger.getLogger((Class) GroupServiceImpl.class);
    }

    public static GroupServiceImpl getInstance() {
        if (GroupServiceImpl.instance == null) {
            GroupServiceImpl.instance = new GroupServiceImpl();
        }
        return GroupServiceImpl.instance;
    }

    private GroupServiceImpl() {
        this.groupTable = new HashMap<Integer, Group>();
        this.config = new GroupServerConfig();
    }

    public void refreshMemberLevel(final HeroPlayer _player) {
        if (_player.getGroupID() > 0) {
            this.sendGroupMsg(this.getGroup(_player.getGroupID()), new MemberChangerNotify((byte) 4, _player.getUserID(), _player.getLevel()));
        }
    }

    public void refreshMemberVocation(final HeroPlayer _player) {
        if (_player.getGroupID() > 0) {
            this.sendGroupMsg(this.getGroup(_player.getGroupID()), new MemberChangerNotify((byte) 5, _player.getUserID(), _player.getVocation().value()));
        }
    }

    public Group getGroup(final int _groupID) {
        if (_groupID <= 0) {
            return null;
        }
        return this.groupTable.get(_groupID);
    }

    public void createGroup(final HeroPlayer _leader, final HeroPlayer _acceptor) {
        Group group = new Group(IDManager.buildGroupID(), _leader, _acceptor);
        this.groupTable.put(group.getID(), group);
        ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new GroupMemberListNotify(group));
        ResponseMessageQueue.getInstance().put(_acceptor.getMsgQueueIndex(), new GroupMemberListNotify(group));
        if (LoverServiceImpl.getInstance().anotherInTream(_leader.getName(), group.getPlayerList()) != null) {
            Group group2 = group;
            ++group2.light;
        }
        byte light = group.light;
    }

    public void add(final HeroPlayer _player, final int _groupID) {
        Group group = this.groupTable.get(_groupID);
        if (group != null && group.getMemberNumber() < 10) {
            GroupMemberProxy member = group.add(_player);
            if (member != null) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GroupMemberListNotify(group));
                this.sendGroupMsg(group, new AddMemberNotify(member), _player.getUserID());
                String[] family = null;
                if ((family = LoverServiceImpl.getInstance().anotherInTream(_player.getName(), group.getPlayerList())) != null) {
                    Group group2 = group;
                    ++group2.light;
                }
                byte light = group.light;
            }
        }
    }

    public boolean canInvite(final Group group, final int _invitorID) {
        GroupMemberProxy member = group.getMember(_invitorID);
        return member != null && (member.memberRank == EGroupMemberRank.ASSISTANT || member.memberRank == EGroupMemberRank.LEADER);
    }

    public void removeMember(final HeroPlayer _groupManager, final int _memberUserID) {
        try {
            Group group = this.groupTable.get(_groupManager.getGroupID());
            if (group != null) {
                GroupMemberProxy member = group.remove(_memberUserID);
                GroupServiceImpl.log.debug((Object) ("removed member after groupid = " + member.player.getGroupID()));
                if (member == null) {
                }
                GroupServiceImpl.log.debug((Object) ("remove member username = " + member.player.getName()));
                if (LoverServiceImpl.getInstance().anotherInTream(member.player.getName(), group.getPlayerList()) != null) {
                    Group group2 = group;
                    --group2.light;
                }
                if (member.isOnline()) {
                    ResponseMessageQueue.getInstance().put(member.player.getMsgQueueIndex(), new GroupDisbandNotify("\u4f60\u88ab\u79fb\u51fa\u961f\u4f0d"));
                    TransmitterOfDungeon.getInstance().add(member.player);
                }
                if (group.getMemberNumber() == 1) {
                    GroupMemberProxy lastMember = group.getMemberList().get(0);
                    TransmitterOfDungeon.getInstance().add(lastMember.player);
                    if (lastMember.isOnline()) {
                        ResponseMessageQueue.getInstance().put(lastMember.player.getMsgQueueIndex(), new GroupDisbandNotify("\u961f\u4f0d\u5df2\u89e3\u6563"));
                    }
                    group.destory();
                    this.groupTable.remove(group.getID());
                } else {
                    this.sendGroupMsg(group, new ReduceMemberNotify(_memberUserID));
                }
            }
        } catch (Exception e) {
            GroupServiceImpl.log.error((Object) "\u79fb\u9664\u961f\u4f0d\u91cc\u7684\u73a9\u5bb6 error: ", (Throwable) e);
        }
    }

    public void leftGroup(final HeroPlayer _player) {
        try {
            Group group = this.groupTable.get(_player.getGroupID());
            if (group != null) {
                if (LoverServiceImpl.getInstance().anotherInTream(_player.getName(), group.getPlayerList()) != null) {
                    Group group2 = group;
                    --group2.light;
                }
                GroupMemberProxy member = group.memberLeft(_player.getUserID());
                if (member != null) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GroupDisbandNotify("\u4f60\u79bb\u5f00\u4e86\u961f\u4f0d"));
                    TransmitterOfDungeon.getInstance().add(_player);
                    if (group.getMemberNumber() == 1) {
                        GroupMemberProxy lastMember = group.getMemberList().get(0);
                        TransmitterOfDungeon.getInstance().add(lastMember.player);
                        if (lastMember.isOnline()) {
                            ResponseMessageQueue.getInstance().put(lastMember.player.getMsgQueueIndex(), new GroupDisbandNotify("\u961f\u4f0d\u5df2\u89e3\u6563"));
                        }
                        group.destory();
                        this.groupTable.remove(group.getID());
                    } else {
                        this.sendGroupMsg(group, new ReduceMemberNotify(_player.getUserID()));
                        if (member.memberRank == EGroupMemberRank.LEADER) {
                            this.sendGroupMsg(group, new MemberChangerNotify((byte) 2, group.getLeader().player.getUserID(), EGroupMemberRank.LEADER.value()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            GroupServiceImpl.log.error((Object) "\u73a9\u5bb6\u79bb\u5f00\u961f\u4f0d error: ", (Throwable) e);
        }
    }

    public void login(final HeroPlayer _player) {
        if (_player.getGroupID() != 0) {
            Group group = this.groupTable.get(_player.getGroupID());
            if (group != null) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GroupMemberListNotify(group));
            } else {
                _player.setGroupID(0);
            }
        }
    }

    public void changeGroupLeader(final HeroPlayer _leader, final int _newLeaderUserID) {
        Group group = this.groupTable.get(_leader.getGroupID());
        GroupMemberProxy newLeader = group.getMember(_newLeaderUserID);
        if (newLeader != null) {
            if (!newLeader.isOnline()) {
                ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new Warning("\u65e0\u6cd5\u8f6c\u8ba9\u7ed9\u79bb\u7ebf\u961f\u5458"));
                return;
            }
            group.transferLeader(newLeader);
            this.sendGroupMsg(group, new MemberChangerNotify((byte) 2, _newLeaderUserID, EGroupMemberRank.LEADER.value()));
            this.sendGroupMsg(group, new MemberChangerNotify((byte) 2, _leader.getUserID(), EGroupMemberRank.NORMAL.value()));
        }
    }

    public void addAssistant(final HeroPlayer _leader, final int _memberUserID) {
        Group group = this.groupTable.get(_leader.getGroupID());
        if (group != null) {
            GroupServiceImpl.log.debug((Object) ("\u4efb\u547d\u52a9\u624b" + _memberUserID + "\uff0ccurrent assistantnum = " + group.assistantNum));
            if (group.assistantNum < 2) {
                GroupMemberProxy member = group.addAssistant(_memberUserID);
                if (member != null) {
                    GroupServiceImpl.log.debug((Object) ("\u4efb\u547d\u52a9\u624b:" + member.player.getName()));
                    this.sendGroupMsg(group, new MemberChangerNotify((byte) 2, member.player.getUserID(), EGroupMemberRank.ASSISTANT.value()));
                }
            } else {
                ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new Warning("\u6700\u591a\u53ea\u80fd\u67092\u4e2a\u52a9\u624b\uff01"));
            }
        }
    }

    public void removeAssistant(final HeroPlayer _leader, final int _assistantUserID) {
        Group group = this.groupTable.get(_leader.getGroupID());
        if (group != null) {
            GroupMemberProxy member = group.removeAssistant(_assistantUserID);
            if (member != null) {
                this.sendGroupMsg(group, new MemberChangerNotify((byte) 2, _assistantUserID, EGroupMemberRank.NORMAL.value()));
            }
        }
    }

    public void changeSubGroup(final HeroPlayer _player, final int _memberUserID, final byte _subGroupID) {
        Group group = this.groupTable.get(_player.getGroupID());
        if (group != null) {
            GroupMemberProxy member = group.changeMemberSubGroup(_memberUserID, _subGroupID);
            if (member != null) {
                GroupServiceImpl.log.debug((Object) ("changeSubGroup after member: " + member.player.getName() + ", sub groupid= " + member.subGroupID));
                this.sendGroupMsg(group, new MemberChangerNotify((byte) 1, member.player.getUserID(), _subGroupID));
            }
        }
    }

    public void sendGroupMsg(final Group _group, final AbsResponseMessage _msg, final int _excludeUID) {
        ArrayList<HeroPlayer> list = _group.getPlayerList();
        for (int i = 0; i < list.size(); ++i) {
            HeroPlayer player = list.get(i);
            if (player.getUserID() != _excludeUID) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), _msg);
            }
        }
    }

    public void sendGroupMsg(final Group _group, final AbsResponseMessage _response) {
        ArrayList<HeroPlayer> list = _group.getPlayerList();
        for (int i = 0; i < list.size(); ++i) {
            ResponseMessageQueue.getInstance().put(list.get(i).getMsgQueueIndex(), _response);
        }
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player.getGroupID() > 0) {
            Group group = this.groupTable.get(player.getGroupID());
            if (group != null) {
                this.sendGroupMsg(group, new MemberChangerNotify((byte) 3, player.getUserID(), true), _session.userID);
            }
        }
    }

    @Override
    public void sessionFree(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player != null && player.getGroupID() > 0) {
            Group group = this.groupTable.get(player.getGroupID());
            if (group != null) {
                this.sendGroupMsg(group, new MemberChangerNotify((byte) 3, player.getUserID(), false), _session.userID);
            }
        }
    }

    @Override
    public void clean(final int _userID) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        if (player != null && player.getGroupID() > 0) {
            Group group = this.groupTable.get(player.getGroupID());
            if (group != null) {
                if (LoverServiceImpl.getInstance().anotherInTream(player.getName(), group.getPlayerList()) != null) {
                    Group group2 = group;
                    --group2.light;
                }
                GroupMemberProxy groupMember = group.memberLeft(_userID);
                if (group.getMemberNumber() == 1) {
                    GroupMemberProxy lastMember = group.getMemberList().get(0);
                    TransmitterOfDungeon.getInstance().add(lastMember.player);
                    if (lastMember.isOnline()) {
                        ResponseMessageQueue.getInstance().put(lastMember.player.getMsgQueueIndex(), new GroupDisbandNotify("\u961f\u4f0d\u5df2\u89e3\u6563"));
                    }
                    group.destory();
                    this.groupTable.remove(group.getID());
                } else {
                    this.sendGroupMsg(group, new ReduceMemberNotify(_userID));
                    if (groupMember.memberRank == EGroupMemberRank.LEADER) {
                        this.sendGroupMsg(group, new MemberChangerNotify((byte) 2, group.getLeader().player.getUserID(), EGroupMemberRank.LEADER.value()));
                    }
                }
            }
        }
    }

    public void groupMemberListHpMpNotify(final HeroPlayer player) {
        if (player.getGroupID() > 0) {
            Group group = getInstance().getGroup(player.getGroupID());
            if (group != null) {
                ArrayList<HeroPlayer> playerList = group.getPlayerList();
                for (final HeroPlayer mem : playerList) {
                    if (mem.getUserID() != player.getUserID()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new OthersHpMpNotify(mem));
                    }
                }
            }
        }
    }
}
