// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild;

import java.util.List;
import hero.player.HeroPlayer;
import java.util.ArrayList;

public class Guild {

    private int id;
    private String name;
    private int guildLevel;
    private GuildMemberProxy president;
    private ArrayList<GuildMemberProxy> memberList;
    private int officerSum;
    public static final int[][] MAX_MEMBER_NUMBER;
    private static final byte MAX_MEMBER_PER_PAGE = 100;

    static {
        MAX_MEMBER_NUMBER = new int[][]{{20, 0}, {50, 100000}, {100, 500000}};
    }

    public Guild(final int _id, final String _name, final HeroPlayer _president, final int _guildLevel) {
        this.id = _id;
        this.name = _name;
        this.guildLevel = _guildLevel;
        GuildMemberProxy member = new GuildMemberProxy(_president.getUserID(), _president, EGuildMemberRank.PRESIDENT);
        this.president = member;
        (this.memberList = new ArrayList<GuildMemberProxy>()).add(member);
    }

    public Guild(final int _id, final String _name, final int _guildLevel) {
        this.id = _id;
        this.name = _name;
        this.guildLevel = _guildLevel;
        if (this.guildLevel > this.GetMaxLevel()) {
            this.guildLevel = this.GetMaxLevel();
            System.out.println("");
        }
        this.memberList = new ArrayList<GuildMemberProxy>();
    }

    public void add(final HeroPlayer _newMember) {
        GuildMemberProxy newMember = new GuildMemberProxy(_newMember.getUserID(), _newMember, EGuildMemberRank.NORMAL);
        int i;
        for (i = 0; i < this.memberList.size(); ++i) {
            GuildMemberProxy member = this.memberList.get(i);
            if (!member.isOnline) {
                break;
            }
            if (member.memberRank.value == newMember.memberRank.value && member.level < newMember.level) {
                break;
            }
        }
        this.memberList.add(i, newMember);
    }

    public void add(final int _memberUserID, final String __memberName, final byte _rankValue) {
        GuildMemberProxy newMember = new GuildMemberProxy(_memberUserID, __memberName, _rankValue);
        if (newMember.memberRank == EGuildMemberRank.PRESIDENT) {
            this.president = newMember;
            this.memberList.add(0, newMember);
        } else {
            int i;
            for (i = 0; i < this.memberList.size() && this.memberList.get(i).memberRank.value >= newMember.memberRank.value; ++i) {
            }
            if (newMember.memberRank == EGuildMemberRank.OFFICER) {
                ++this.officerSum;
            }
            this.memberList.add(i, newMember);
        }
    }

    public int GuildLevelUp() {
        if (this.guildLevel < this.GetMaxLevel()) {
            ++this.guildLevel;
        }
        return this.guildLevel;
    }

    public GuildMemberProxy remove(final int _userID) {
        for (int i = 0; i < this.memberList.size(); ++i) {
            GuildMemberProxy member = this.memberList.get(i);
            if (member.userID == _userID) {
                this.memberList.remove(i);
                return member;
            }
        }
        return null;
    }

    public int getOfficerSum() {
        return this.officerSum;
    }

    public void addOfficer() {
        ++this.officerSum;
    }

    public void reduceOfficer() {
        --this.officerSum;
        if (this.officerSum < 0) {
            this.officerSum = 0;
        }
    }

    public GuildMemberProxy getMember(final int _userID) {
        for (int i = 0; i < this.memberList.size(); ++i) {
            GuildMemberProxy member = this.memberList.get(i);
            if (member.userID == _userID) {
                return member;
            }
        }
        return null;
    }

    public GuildMemberProxy getMember(final String _name) {
        for (int i = 0; i < this.memberList.size(); ++i) {
            GuildMemberProxy member = this.memberList.get(i);
            if (member.name.equals(_name)) {
                return member;
            }
        }
        return null;
    }

    public boolean transferPresidentTo(final int _userID) {
        GuildMemberProxy newPresident = this.getMember(_userID);
        if (newPresident != null && newPresident.memberRank != EGuildMemberRank.PRESIDENT) {
            newPresident.memberRank = EGuildMemberRank.PRESIDENT;
            this.president.memberRank = EGuildMemberRank.OFFICER;
            this.memberList.remove(newPresident);
            if (newPresident.isOnline) {
                this.memberList.add(0, newPresident);
            } else {
                int i;
                for (i = 0; i < this.memberList.size() && this.memberList.get(i).isOnline; ++i) {
                }
                this.memberList.add(i, newPresident);
            }
            int j = 0;
            this.memberList.remove(this.president);
            while (j < this.memberList.size()) {
                GuildMemberProxy member = this.memberList.get(j);
                if (!member.isOnline) {
                    break;
                }
                if (this.president.memberRank.value > member.memberRank.value()) {
                    break;
                }
                if (member.memberRank == this.president.memberRank && this.president.level >= member.level) {
                    break;
                }
                ++j;
            }
            this.memberList.add(j, this.president);
            this.president = newPresident;
            return true;
        }
        return false;
    }

    public void memberLogin(final HeroPlayer _player, final GuildMemberProxy _member) {
        if (_member != null) {
            _member.level = _player.getLevel();
            _member.sex = _player.getSex();
            _member.vocation = _player.getVocation();
            _member.isOnline = true;
            this.memberList.remove(_member);
            int i;
            for (i = 0; i < this.memberList.size(); ++i) {
                GuildMemberProxy otherMember = this.memberList.get(i);
                if (!otherMember.isOnline) {
                    break;
                }
                if (_member.memberRank.value > otherMember.memberRank.value) {
                    break;
                }
                if (_member.memberRank == otherMember.memberRank && _member.level > otherMember.level) {
                    break;
                }
            }
            this.memberList.add(i, _member);
        }
    }

    public void memberLogout(final GuildMemberProxy _member) {
        if (_member != null) {
            _member.isOnline = false;
            this.memberList.remove(_member);
            int i;
            for (i = 0; i < this.memberList.size(); ++i) {
                GuildMemberProxy otherMember = this.memberList.get(i);
                if (!otherMember.isOnline && _member.memberRank.value >= otherMember.memberRank.value) {
                    break;
                }
            }
            this.memberList.add(i, _member);
        }
    }

    public boolean changeMemberRank(final int _userID, final EGuildMemberRank _memberRank) {
        GuildMemberProxy member = this.getMember(_userID);
        if (member == null) {
            return false;
        }
        if (member.memberRank == _memberRank) {
            return false;
        }
        this.memberList.remove(member);
        member.memberRank = _memberRank;
        if (member.isOnline) {
            int i;
            for (i = 0; i < this.memberList.size(); ++i) {
                GuildMemberProxy otherMember = this.memberList.get(i);
                if (!otherMember.isOnline) {
                    break;
                }
                if (member.memberRank == otherMember.memberRank && otherMember.level < member.level) {
                    break;
                }
            }
            this.memberList.add(i, member);
        } else {
            int i;
            for (i = 0; i < this.memberList.size(); ++i) {
                GuildMemberProxy otherMember = this.memberList.get(i);
                if (!otherMember.isOnline && member.memberRank == otherMember.memberRank) {
                    break;
                }
            }
            this.memberList.add(i, member);
        }
        return true;
    }

    public EGuildMemberRank getMemberRank(final int _userID) {
        for (int i = 0; i < this.memberList.size(); ++i) {
            GuildMemberProxy member = this.memberList.get(i);
            if (member.userID == _userID) {
                return member.memberRank;
            }
        }
        return null;
    }

    public List<GuildMemberProxy> getMemberList(final int _pageNumber) {
        if (_pageNumber < 1) {
            return this.memberList.subList(0, (this.memberList.size() >= 100) ? 100 : this.memberList.size());
        }
        int totalPageNumer = this.getViewPageNumber();
        if (totalPageNumer >= _pageNumber) {
            return this.memberList.subList(100 * (_pageNumber - 1), (this.memberList.size() >= 100 * _pageNumber) ? (100 * _pageNumber) : this.memberList.size());
        }
        return this.memberList.subList(100 * (totalPageNumer - 1), this.memberList.size());
    }

    public ArrayList<GuildMemberProxy> getMemberList() {
        return this.memberList;
    }

    public int getMemberNumber() {
        return this.memberList.size();
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public GuildMemberProxy getPresident() {
        return this.president;
    }

    public int getViewPageNumber() {
        return (this.memberList.size() % 100 > 0) ? (this.memberList.size() / 100 + 1) : (this.memberList.size() / 100);
    }

    public void clear() {
        this.memberList.clear();
    }

    public int GetMaxMemberNumber() {
        return Guild.MAX_MEMBER_NUMBER[this.guildLevel - 1][0];
    }

    public int GetMaxLevel() {
        return Guild.MAX_MEMBER_NUMBER.length;
    }

    public int getGuildLevel() {
        return this.guildLevel;
    }

    public int getUpGuildMoney() {
        return Guild.MAX_MEMBER_NUMBER[this.guildLevel - 1][1];
    }

    public int[] getUpGuildMoneyList() {
        int[] list = new int[Guild.MAX_MEMBER_NUMBER.length - 1];
        for (int i = 1; i < Guild.MAX_MEMBER_NUMBER.length; ++i) {
            list[i - 1] = Guild.MAX_MEMBER_NUMBER[i][1];
        }
        return list;
    }

    public int[] getUpGuildNumberList() {
        int[] list = new int[Guild.MAX_MEMBER_NUMBER.length - 1];
        for (int i = 1; i < Guild.MAX_MEMBER_NUMBER.length; ++i) {
            list[i - 1] = Guild.MAX_MEMBER_NUMBER[i][0];
        }
        return list;
    }
}
