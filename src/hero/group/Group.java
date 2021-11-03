// 
// Decompiled by Procyon v0.5.36
// 
package hero.group;

import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import java.util.Iterator;
import hero.player.HeroPlayer;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class Group {

    private static Logger log;
    private int ID;
    private GroupMemberProxy leader;
    private ArrayList<GroupMemberProxy> memberList;
    private int memberMaxLevel;
    private int goodsPickerGroupIndex;
    public byte light;
    public int assistantNum;
    public static final int ASSISTANT_MAX_NUM = 2;
    private int subGroupMemberNum;
    private int subGroup2MemberNum;
    private int subGroup3MemberNum;
    private int subGroup4MemberNum;
    public static final int MAX_NUMBER_OF_MEMBER = 10;
    public static final int MAX_SUB_GROUP = 2;
    public static final int MAX_MEMBER_OF_SUB_GROUP = 5;

    static {
        Group.log = Logger.getLogger((Class) Group.class);
    }

    public Group(final int _ID, final HeroPlayer _leader, final HeroPlayer _member) {
        this.subGroupMemberNum = 0;
        this.subGroup2MemberNum = 0;
        this.subGroup3MemberNum = 0;
        this.subGroup4MemberNum = 0;
        this.ID = _ID;
        this.memberList = new ArrayList<GroupMemberProxy>();
        this.leader = new GroupMemberProxy(_leader, 1);
        this.leader.memberRank = EGroupMemberRank.LEADER;
        this.memberList.add(this.leader);
        this.flushSubGroupMemberNumber(1, 1);
        _leader.setGroupID(this.ID);
        this.memberList.add(new GroupMemberProxy(_member, 1));
        _member.setGroupID(this.ID);
        this.flushSubGroupMemberNumber(1, 1);
        this.memberMaxLevel = ((_leader.getLevel() >= _member.getLevel()) ? _leader.getLevel() : _member.getLevel());
    }

    public GroupMemberProxy getMember(final int _userID) {
        synchronized (this.memberList) {
            for (final GroupMemberProxy member : this.memberList) {
                if (member.player.getUserID() == _userID) {
                    // monitorexit(this.memberList)
                    return member;
                }
            }
            // monitorexit(this.memberList)
            return null;
        }
    }

    public synchronized GroupMemberProxy add(final HeroPlayer _player) {
        if (this.memberList.size() < 10) {
            byte subGroupID = 1;
            Group.log.debug((Object) ("subGroupMemberNum = " + this.subGroupMemberNum));
            Group.log.debug((Object) ("subGroup2MemberNum = " + this.subGroup2MemberNum));
            Group.log.debug((Object) ("subGroup3MemberNum = " + this.subGroup3MemberNum));
            Group.log.debug((Object) ("subGroup4MemberNum = " + this.subGroup4MemberNum));
            if (this.subGroupMemberNum == 5 && this.subGroup2MemberNum < 5) {
                subGroupID = 2;
            } else if (this.subGroup2MemberNum == 5 && this.subGroup3MemberNum < 5) {
                subGroupID = 3;
            } else if (this.subGroup3MemberNum == 5 && this.subGroup4MemberNum < 5) {
                subGroupID = 4;
            }
            Group.log.debug((Object) ("add member in subgroupID = " + subGroupID));
            GroupMemberProxy newMember = new GroupMemberProxy(_player, subGroupID);
            this.memberList.add(newMember);
            _player.setGroupID(this.getID());
            this.flushSubGroupMemberNumber(subGroupID, 1);
            Group.log.debug((Object) ("add new member name = " + newMember.player.getName() + ", subgroup id = " + newMember.subGroupID));
            for (final GroupMemberProxy member : this.memberList) {
                Group.log.debug((Object) ("add new member after " + member.player.getUserID() + " -- " + member.player.getName() + " -- " + member.subGroupID));
            }
            Group.log.debug((Object) ("add after subGroupMemberNum = " + this.subGroupMemberNum));
            Group.log.debug((Object) ("add after subGroup2MemberNum = " + this.subGroup2MemberNum));
            Group.log.debug((Object) ("add after subGroup3MemberNum = " + this.subGroup3MemberNum));
            Group.log.debug((Object) ("add after subGroup4MemberNum = " + this.subGroup4MemberNum));
            return newMember;
        }
        return null;
    }

    public synchronized GroupMemberProxy remove(final int _userID) {
        GroupMemberProxy member = null;
        int tempMaxLevel = 0;
        Iterator<GroupMemberProxy> it = this.memberList.iterator();
        while (it.hasNext()) {
            member = it.next();
            int tempSubGroupID = member.subGroupID;
            Group.log.debug((Object) ("member userid = " + member.player.getName()));
            if (member.player.getUserID() == _userID) {
                Group.log.debug((Object) ("remove member userID == " + member.player.getUserID()));
                it.remove();
                member.player.setGroupID(0);
                if (member.memberRank == EGroupMemberRank.ASSISTANT) {
                    --this.assistantNum;
                }
                tempMaxLevel = member.player.getLevel();
                this.flushSubGroupMemberNumber(tempSubGroupID, 2);
                break;
            }
        }
        if (this.memberList.size() > 1 && this.memberMaxLevel == tempMaxLevel) {
            int maxLevel = 0;
            it = this.memberList.iterator();
            while (it.hasNext()) {
                GroupMemberProxy tempMember = it.next();
                if (tempMember.player.getLevel() > maxLevel) {
                    maxLevel = tempMember.player.getLevel();
                }
            }
            this.memberMaxLevel = maxLevel;
        }
        for (final GroupMemberProxy memberProxy : this.memberList) {
            Group.log.debug((Object) ("remove member after " + memberProxy.player.getUserID() + " -- " + memberProxy.player.getName()));
        }
        return member;
    }

    private void flushSubGroupMemberNumber(final int subGroupID, final int type) {
        if (type == 1) {
            switch (subGroupID) {
                case 1: {
                    ++this.subGroupMemberNum;
                    break;
                }
                case 2: {
                    ++this.subGroup2MemberNum;
                    break;
                }
                case 3: {
                    ++this.subGroup3MemberNum;
                    break;
                }
                case 4: {
                    ++this.subGroup4MemberNum;
                    break;
                }
            }
        }
        if (type == 2) {
            switch (subGroupID) {
                case 1: {
                    --this.subGroupMemberNum;
                    break;
                }
                case 2: {
                    --this.subGroup2MemberNum;
                    break;
                }
                case 3: {
                    --this.subGroup3MemberNum;
                    break;
                }
                case 4: {
                    --this.subGroup4MemberNum;
                    break;
                }
            }
        }
    }

    public synchronized GroupMemberProxy memberLeft(final int _memberUserID) {
        GroupMemberProxy leftMember = null;
        for (int i = 0; i < this.memberList.size(); ++i) {
            leftMember = this.memberList.get(i);
            if (leftMember.player.getUserID() == _memberUserID) {
                try {
                    int tempSubGroupID = leftMember.subGroupID;
                    this.memberList.remove(i);
                    leftMember.player.setGroupID(0);
                    if (leftMember.memberRank == EGroupMemberRank.ASSISTANT) {
                        --this.assistantNum;
                    }
                    this.flushSubGroupMemberNumber(tempSubGroupID, 2);
                    if (this.memberList.size() > 1) {
                        if (leftMember.memberRank == EGroupMemberRank.LEADER) {
                            int j;
                            for (j = 0; j < this.memberList.size(); ++j) {
                                GroupMemberProxy member = this.memberList.get(j);
                                if (member.memberRank == EGroupMemberRank.ASSISTANT) {
                                    this.leader = member;
                                    this.leader.memberRank = EGroupMemberRank.LEADER;
                                    break;
                                }
                            }
                            if (j == this.memberList.size()) {
                                this.leader = this.memberList.get(0);
                                this.leader.memberRank = EGroupMemberRank.LEADER;
                            }
                        }
                        int maxLevel = 0;
                        for (int k = 0; k < this.memberList.size(); ++k) {
                            GroupMemberProxy member = this.memberList.get(k);
                            if (member.player.getLevel() > maxLevel) {
                                maxLevel = member.player.getLevel();
                            }
                        }
                        this.memberMaxLevel = maxLevel;
                        break;
                    }
                    break;
                } catch (Exception ex) {
                }
            }
        }
        return leftMember;
    }

    public GroupMemberProxy transferLeader(final GroupMemberProxy _member) {
        synchronized (this.leader) {
            if (_member != null) {
                this.leader.memberRank = EGroupMemberRank.NORMAL;
                this.leader = _member;
                if (_member.memberRank == EGroupMemberRank.ASSISTANT) {
                    --this.assistantNum;
                }
                this.leader.memberRank = EGroupMemberRank.LEADER;
            }
            // monitorexit(this.leader)
            return _member;
        }
    }

    public GroupMemberProxy addAssistant(final int _normalMemberUserID) {
        GroupMemberProxy member = this.getMember(_normalMemberUserID);
        if (member != null && member.memberRank == EGroupMemberRank.NORMAL) {
            member.memberRank = EGroupMemberRank.ASSISTANT;
            ++this.assistantNum;
            return member;
        }
        return null;
    }

    public GroupMemberProxy removeAssistant(final int _assistantUserID) {
        GroupMemberProxy member = this.getMember(_assistantUserID);
        if (member != null && member.memberRank == EGroupMemberRank.ASSISTANT) {
            member.memberRank = EGroupMemberRank.NORMAL;
            --this.assistantNum;
            return member;
        }
        return null;
    }

    public synchronized GroupMemberProxy changeMemberSubGroup(final int _memberUserID, final byte _subGroupID) {
        boolean canChange = false;
        switch (_subGroupID) {
            case 1: {
                canChange = (this.subGroupMemberNum < 5);
                break;
            }
            case 2: {
                canChange = (this.subGroup2MemberNum < 5);
                break;
            }
            case 3: {
                canChange = (this.subGroup3MemberNum < 5);
                break;
            }
            case 4: {
                canChange = (this.subGroup4MemberNum < 5);
                break;
            }
        }
        GroupMemberProxy member = this.getMember(_memberUserID);
        Group.log.debug((Object) ("change member sub group member= " + member));
        if (member == null) {
            return null;
        }
        if (!canChange) {
            ResponseMessageQueue.getInstance().put(member.player.getMsgQueueIndex(), new Warning("\u8be5\u5c0f\u961f\u5df2\u6ee1\u5458\uff0c\u4e0d\u80fd\u6539\u53d8\u4f4d\u7f6e!"));
            return null;
        }
        if (member.subGroupID == _subGroupID) {
            ResponseMessageQueue.getInstance().put(member.player.getMsgQueueIndex(), new Warning("\u5df2\u7ecf\u5728\u8fd9\u4e2a\u5c0f\u961f!"));
            return null;
        }
        int tempSubGroupID = member.subGroupID;
        member.subGroupID = _subGroupID;
        this.flushSubGroupMemberNumber(tempSubGroupID, 2);
        this.flushSubGroupMemberNumber(_subGroupID, 1);
        return member;
    }

    public ArrayList<HeroPlayer> getPlayerList() {
        ArrayList<HeroPlayer> list = new ArrayList<HeroPlayer>();
        for (int i = 0; i < this.memberList.size(); ++i) {
            try {
                GroupMemberProxy member = this.memberList.get(i);
                if (member.isOnline()) {
                    list.add(member.player);
                }
            } catch (Exception ex) {
            }
        }
        return list;
    }

    public int getMemberNumber() {
        return this.memberList.size();
    }

    public int getID() {
        return this.ID;
    }

    public GroupMemberProxy getLeader() {
        return this.leader;
    }

    public ArrayList<HeroPlayer> getValidatePlayerList(final int _mapID) {
        ArrayList<HeroPlayer> list = new ArrayList<HeroPlayer>();
        for (int i = 0; i < this.memberList.size(); ++i) {
            try {
                GroupMemberProxy member = this.memberList.get(i);
                if (member.isOnline() && member.player.where().getID() == _mapID) {
                    list.add(member.player);
                }
            } catch (Exception ex) {
            }
        }
        return list;
    }

    public ArrayList<GroupMemberProxy> getMemberList() {
        return this.memberList;
    }

    public int getGoodsPickerUserID(final int _mapID) {
        for (int lookTimes = 1; lookTimes <= this.memberList.size(); ++lookTimes) {
            if (this.goodsPickerGroupIndex >= this.memberList.size()) {
                this.goodsPickerGroupIndex = 0;
            }
            try {
                GroupMemberProxy member = this.memberList.get(this.goodsPickerGroupIndex++);
                if (member.isOnline() && member.player.where().getID() == _mapID) {
                    return member.player.getUserID();
                }
            } catch (Exception e) {
                this.memberList.get(0).player.getUserID();
            }
        }
        return 0;
    }

    public synchronized void destory() {
        for (final GroupMemberProxy member : this.memberList) {
            member.player.setGroupID(0);
        }
        this.memberList.clear();
        this.leader = null;
    }

    public int getMemberMaxLevel() {
        return this.memberMaxLevel;
    }

    public boolean isLeader(final int userID) {
        return this.leader != null && this.leader.player.getUserID() == userID;
    }
}
