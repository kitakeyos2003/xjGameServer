// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.service;

import yoyo.service.base.session.Session;
import hero.guild.message.RefreshMemberListNotify;
import java.util.ArrayList;
import hero.guild.message.GuildDisbandNotify;
import hero.guild.GuildMemberProxy;
import hero.guild.message.GuildUpNotify;
import hero.guild.message.ResponseGuildInfo;
import java.util.Iterator;
import hero.log.service.LogServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.item.bag.exception.BagException;
import hero.item.Goods;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.guild.EGuildMemberRank;
import hero.guild.message.GuildChangeNotify;
import hero.player.service.PlayerServiceImpl;
import hero.share.DirtyStringDict;
import hero.share.message.Warning;
import hero.item.dictionary.GoodsContents;
import hero.item.special.GuildBuild;
import yoyo.core.packet.AbsResponseMessage;
import hero.guild.message.MemberRankChangeNotify;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.guild.Guild;
import java.util.HashMap;
import yoyo.service.base.AbsServiceAdaptor;

public class GuildServiceImpl extends AbsServiceAdaptor<GuildConfig> {

    private static GuildServiceImpl instance;
    private HashMap<Integer, Guild> guildTable;
    protected int USABLE_GUILD_ID;

    public static GuildServiceImpl getInstance() {
        if (GuildServiceImpl.instance == null) {
            GuildServiceImpl.instance = new GuildServiceImpl();
        }
        return GuildServiceImpl.instance;
    }

    private GuildServiceImpl() {
        this.USABLE_GUILD_ID = 1000;
        this.guildTable = new HashMap<Integer, Guild>();
        this.config = new GuildConfig();
    }

    @Override
    protected void start() {
        GuildDAO.load(this.guildTable);
    }

    public void setUseableGuildID(final int _id) {
        this.USABLE_GUILD_ID = _id;
    }

    public void sendGuildRank(final HeroPlayer _player) {
        Guild guild = this.guildTable.get(_player.getGuildID());
        if (guild != null) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new MemberRankChangeNotify(guild.getMemberRank(_player.getUserID()).value()));
        }
    }

    public boolean createGuild(final HeroPlayer _player, final String _guildName) {
        int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(GuildBuild.GUILD_BUILD_ID);
        GuildBuild build = (GuildBuild) GoodsContents.getGoods(GuildBuild.GUILD_BUILD_ID);
        if (stoneNum < 1) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u6ca1\u6709%fn,\u65e0\u6cd5\u521b\u5efa\u5e2e\u6d3e".replaceAll("%fn", build.getName())));
            return false;
        }
        if (_player.getGuildID() != 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u6709\u5e2e\u6d3e"));
            return false;
        }
        if (_player.getLevel() < ((GuildConfig) this.config).level_of_creator) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(((GuildConfig) this.config).level_of_creator) + "\u7ea7\u624d\u53ef\u4ee5\u521b\u5efa\u5e2e\u6d3e"));
            return false;
        }
        if (_player.getMoney() < ((GuildConfig) this.config).money_of_create) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f" + ((GuildConfig) this.config).money_of_create));
            return false;
        }
        if (!DirtyStringDict.getInstance().isCleanString(_guildName)) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0d\u548c\u8c10\u7684\u5e2e\u6d3e\u540d\u79f0"));
            return false;
        }
        synchronized (this.guildTable) {
            for (final Guild guild : this.guildTable.values()) {
                if (guild.equals(_guildName)) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0d\u80fd\u5c71\u5be8\u522b\u4eba\u7684\u5e2e\u6d3e\u540d\u79f0"));
                    // monitorexit(this.guildTable)
                    return false;
                }
            }
            Guild guild = new Guild(this.USABLE_GUILD_ID++, _guildName, _player, 1);
            _player.setGuildID(guild.getID());
            this.guildTable.put(guild.getID(), guild);
            GuildDAO.create(guild);
            PlayerServiceImpl.getInstance().addMoney(_player, -((GuildConfig) this.config).money_of_create, 1.0f, 0, "\u521b\u5efa\u5e2e\u6d3e");
            GuildChangeNotify guildChangeNotify = new GuildChangeNotify(_player.getID(), _guildName);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), guildChangeNotify);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new MemberRankChangeNotify(EGuildMemberRank.PRESIDENT.value()));
            try {
                GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), build, 1, CauseLog.GUILDBUILD);
            } catch (BagException e) {
                e.printStackTrace();
            }
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(), guildChangeNotify, true, _player.getID());
            LogServiceImpl.getInstance().guildChangeLog(_player.getUserID(), _player.getName(), guild.getID(), guild.getName(), 1, _player.getUserID(), _player.getName(), "\u521b\u5efa");
            // monitorexit(this.guildTable)
            return true;
        }
    }

    public void SeeGuildInfo(final HeroPlayer _player) {
        Guild guild = this.guildTable.get(_player.getGuildID());
        if (guild == null) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u6ca1\u6709\u5e2e\u6d3e"));
            return;
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseGuildInfo(guild));
    }

    public void GuildUp(final HeroPlayer _guildManager) {
        Guild guild = this.guildTable.get(_guildManager.getGuildID());
        if (guild == null) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u60a8\u6ca1\u6709\u5e2e\u6d3e"));
            return;
        }
        GuildMemberProxy managerProxy = guild.getMember(_guildManager.getUserID());
        if (managerProxy.memberRank != EGuildMemberRank.PRESIDENT) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u60a8\u7684\u6743\u9650\u592a\u4f4e\u4e86"));
            return;
        }
        if (guild.getGuildLevel() >= guild.GetMaxLevel()) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u5e2e\u6d3e\u5df2\u8fbe\u6700\u5927\u7b49\u7ea7,\u65e0\u6cd5\u518d\u8fdb\u884c\u63d0\u5347"));
            return;
        }
        if (guild.getUpGuildMoney() > _guildManager.getMoney()) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u60a8\u7684\u91d1\u94b1\u4e0d\u591f,\u65e0\u6cd5\u8fdb\u884c\u63d0\u5347"));
            return;
        }
        int level = guild.GuildLevelUp();
        GuildDAO.guildUpLevel(guild.getID(), level);
        _guildManager.setMoney(-guild.getUpGuildMoney());
        ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u60a8\u7684\u5e2e\u6d3e\u5347\u7ea7\u6210\u529f"));
        ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new GuildUpNotify(level, guild.GetMaxMemberNumber()));
        LogServiceImpl.getInstance().guildChangeLog(_guildManager.getUserID(), _guildManager.getName(), guild.getID(), guild.getName(), guild.getMemberNumber(), guild.getPresident().userID, guild.getPresident().name, "\u5347\u7ea7\u5e2e\u6d3e");
    }

    public boolean disbandGuild(final HeroPlayer _player) {
        Guild guild = this.guildTable.remove(_player.getGuildID());
        if (guild != null && guild.getPresident().userID == _player.getUserID()) {
            GuildDAO.distory(guild.getID());
            this.sendGuildMsg(guild, new GuildDisbandNotify(), 0);
            ArrayList<GuildMemberProxy> list = guild.getMemberList();
            for (int i = 0; i < list.size(); ++i) {
                HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(list.get(i).userID);
                if (player != null) {
                    player.setGuildID(0);
                    if (player.isEnable()) {
                        MapSynchronousInfoBroadcast.getInstance().put(player.where(), new GuildChangeNotify(player.getID()), true, player.getID());
                    }
                }
            }
            guild.clear();
            LogServiceImpl.getInstance().guildChangeLog(_player.getUserID(), _player.getName(), guild.getID(), guild.getName(), guild.getMemberNumber(), _player.getUserID(), _player.getName(), "\u89e3\u6563\u5e2e\u6d3e");
            return true;
        }
        return false;
    }

    public void add(final HeroPlayer _invitor, final HeroPlayer _target, final int _guildID) {
        if (_target.getGuildID() > 0) {
            ResponseMessageQueue.getInstance().put(_invitor.getMsgQueueIndex(), new Warning(String.valueOf(_target.getName()) + "\u60a8\u5df2\u6709\u5e2e\u6d3e"));
            return;
        }
        if (_invitor.getGuildID() != _guildID) {
            return;
        }
        Guild guild = this.guildTable.get(_guildID);
        if (guild != null) {
            if (guild.getMemberList().size() >= guild.GetMaxMemberNumber()) {
                ResponseMessageQueue.getInstance().put(_target.getMsgQueueIndex(), new Warning("\u5e2e\u6d3e\u4eba\u6570\u5df2\u8fbe\u4e0a\u9650"));
                return;
            }
            if (GuildDAO.add(_guildID, _target.getUserID(), _target.getName())) {
                guild.add(_target);
                _target.setGuildID(_guildID);
                this.sendGuildMsg(guild, new RefreshMemberListNotify(), _target.getID());
                this.sendGuildMsg(guild, new Warning(String.valueOf(_target.getName()) + "\u52a0\u5165\u4e86\u5e2e\u6d3e"), _target.getUserID());
                MapSynchronousInfoBroadcast.getInstance().put(_target.where(), new GuildChangeNotify(_target.getID(), guild.getName()), false, 0);
                LogServiceImpl.getInstance().guildMemberChangeLog(_invitor.getUserID(), _invitor.getName(), guild.getID(), guild.getName(), _target.getUserID(), _target.getName(), "\u6dfb\u52a0");
            }
        }
    }

    public void removeMember(final HeroPlayer _guildManager, final int _userIDWillBeRemove) {
        Guild guild = this.guildTable.get(_guildManager.getGuildID());
        if (guild == null) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u60a8\u6ca1\u6709\u5e2e\u6d3e"));
            return;
        }
        GuildMemberProxy managerProxy = guild.getMember(_guildManager.getUserID());
        GuildMemberProxy beRemoveMemberProxy = guild.getMember(_userIDWillBeRemove);
        if (beRemoveMemberProxy == null) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u662f\u6df7\u4f60\u8fd9\u91cc\u7684"));
            return;
        }
        if (managerProxy.memberRank.value() <= beRemoveMemberProxy.memberRank.value()) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u60a8\u7684\u6743\u9650\u592a\u4f4e\u4e86"));
            return;
        }
        guild.remove(_userIDWillBeRemove);
        GuildDAO.removeGuildMember(_userIDWillBeRemove);
        this.sendGuildMsg(guild, new RefreshMemberListNotify(), 0);
        this.sendGuildMsg(guild, new Warning(String.valueOf(beRemoveMemberProxy.name) + "\u88ab\u5e2e\u6d3e\u626b\u5730\u51fa\u95e8"), 0);
        if (beRemoveMemberProxy.isOnline) {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userIDWillBeRemove);
            if (player != null) {
                player.setGuildID(0);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u88ab\u5e2e\u6d3e\u626b\u5730\u51fa\u95e8"));
                MapSynchronousInfoBroadcast.getInstance().put(player.where(), new GuildChangeNotify(player.getID()), false, 0);
            }
        }
        LogServiceImpl.getInstance().guildMemberChangeLog(_guildManager.getUserID(), _guildManager.getName(), guild.getID(), guild.getName(), beRemoveMemberProxy.userID, beRemoveMemberProxy.name, "\u5f00\u9664");
    }

    public void menberUpgrade(final HeroPlayer _guildMember) {
        Guild guild = this.guildTable.get(_guildMember.getGuildID());
        if (guild != null) {
            GuildMemberProxy memberProxy = guild.getMember(_guildMember.getUserID());
            memberProxy.level = _guildMember.getLevel();
        }
    }

    public void leftGuild(final HeroPlayer _guildMember) {
        Guild guild = this.guildTable.get(_guildMember.getGuildID());
        if (guild == null) {
            ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(), new Warning("\u60a8\u6ca1\u6709\u5e2e\u6d3e"));
            return;
        }
        GuildMemberProxy memberProxy = guild.getMember(_guildMember.getUserID());
        if (memberProxy == null) {
            ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u662f\u6df7\u4f60\u8fd9\u91cc\u7684"));
            return;
        }
        if (memberProxy.memberRank == EGuildMemberRank.PRESIDENT) {
            ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(), new Warning("\u8f6c\u8ba9\u5e2e\u4e3b\u540e\u65b9\u53ef\u9690\u9000"));
            return;
        }
        guild.remove(memberProxy.userID);
        GuildDAO.removeGuildMember(memberProxy.userID);
        this.sendGuildMsg(guild, new RefreshMemberListNotify(), 0);
        this.sendGuildMsg(guild, new Warning(String.valueOf(memberProxy.name) + "\u79bb\u5f00\u4e86\u5e2e\u6d3e"), memberProxy.userID);
        _guildMember.setGuildID(0);
        ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(), new Warning("\u60a8\u79bb\u5f00\u4e86\u5e2e\u6d3e"));
        GuildChangeNotify guildChangeNotifyMsg = new GuildChangeNotify(_guildMember.getID());
        ResponseMessageQueue.getInstance().put(_guildMember.getMsgQueueIndex(), guildChangeNotifyMsg);
        MapSynchronousInfoBroadcast.getInstance().put(_guildMember.where(), guildChangeNotifyMsg, true, _guildMember.getID());
        LogServiceImpl.getInstance().guildMemberChangeLog(memberProxy.userID, memberProxy.name, guild.getID(), guild.getName(), memberProxy.userID, memberProxy.name, "\u79bb\u5f00");
    }

    public void deleteRole(final int _userID) {
        for (final Guild guild : this.guildTable.values()) {
            GuildMemberProxy guildMemberProxy = guild.remove(_userID);
            if (guildMemberProxy != null) {
                GuildDAO.removeGuildMember(_userID);
                if (guild.getMemberNumber() == 0) {
                    GuildDAO.distory(guild.getID());
                    this.guildTable.remove(guild.getID());
                } else if (guildMemberProxy.memberRank == EGuildMemberRank.PRESIDENT) {
                    GuildMemberProxy newPresident = guild.getMemberList().get(0);
                    newPresident.memberRank = EGuildMemberRank.PRESIDENT;
                    GuildDAO.changeMemberRank(newPresident.userID, EGuildMemberRank.PRESIDENT);
                    GuildDAO.updatePresident(guild.getID(), newPresident.userID);
                    GuildDAO.changeMemberRank(_userID, EGuildMemberRank.PRESIDENT);
                    this.sendGuildMsg(guild, new Warning(String.valueOf(newPresident.name) + "\u6210\u4e3a\u4e86" + EGuildMemberRank.PRESIDENT.getDesc()), newPresident.userID);
                    if (newPresident.isOnline) {
                        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(newPresident.userID);
                        if (player != null && player.isEnable()) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6210\u4e3a\u4e86" + EGuildMemberRank.PRESIDENT.getDesc()));
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MemberRankChangeNotify(EGuildMemberRank.PRESIDENT.value()));
                        }
                    }
                }
            }
        }
    }

    public String getMemberRank(final HeroPlayer _player) {
        String desc = "";
        Guild guild = this.guildTable.get(_player.getGuildID());
        if (guild != null) {
            GuildMemberProxy managerProxy = guild.getMember(_player.getUserID());
            desc = managerProxy.memberRank.getDesc();
        }
        return desc;
    }

    public boolean isAssociate(final String _p1, final String _p2) {
        boolean result = false;
        HeroPlayer p1 = PlayerServiceImpl.getInstance().getPlayerByName(_p1);
        HeroPlayer p2 = PlayerServiceImpl.getInstance().getPlayerByName(_p2);
        if (p1.getGuildID() == p2.getGuildID()) {
            result = true;
        }
        return result;
    }

    public String getGuildName(final HeroPlayer _player) {
        String desc = "";
        Guild guild = this.guildTable.get(_player.getGuildID());
        if (guild != null) {
            desc = guild.getName();
        }
        return desc;
    }

    public void changeMemberRank(final HeroPlayer _guildManager, final int _memberUserID, final EGuildMemberRank _guildRank) {
        Guild guild = this.guildTable.get(_guildManager.getGuildID());
        if (guild == null) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u60a8\u6ca1\u6709\u5e2e\u6d3e"));
            return;
        }
        GuildMemberProxy managerProxy = guild.getMember(_guildManager.getUserID());
        GuildMemberProxy beRemoveMemberProxy = guild.getMember(_memberUserID);
        if (beRemoveMemberProxy == null) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u662f\u6df7\u4f60\u8fd9\u91cc\u7684"));
            return;
        }
        if (managerProxy.memberRank != EGuildMemberRank.PRESIDENT) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u60a8\u7684\u6743\u9650\u592a\u4f4e\u4e86"));
            return;
        }
        if (_guildRank == EGuildMemberRank.OFFICER && guild.getOfficerSum() >= getInstance().getConfig().officer_sum) {
            ResponseMessageQueue.getInstance().put(_guildManager.getMsgQueueIndex(), new Warning("\u804c\u52a1\u5df2\u8fbe\u5230\u4e0a\u9650"));
            return;
        }
        guild.changeMemberRank(_memberUserID, _guildRank);
        GuildDAO.changeMemberRank(_memberUserID, _guildRank);
        this.sendGuildMsg(guild, new Warning(String.valueOf(beRemoveMemberProxy.name) + "\u6210\u4e3a\u4e86" + _guildRank.getDesc()), _memberUserID);
        if (beRemoveMemberProxy.isOnline) {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_memberUserID);
            if (player != null) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MemberRankChangeNotify(_guildRank.value()));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6210\u4e3a\u4e86" + _guildRank.getDesc()));
            }
        }
        if (_guildRank == EGuildMemberRank.OFFICER) {
            guild.addOfficer();
        } else if (_guildRank == EGuildMemberRank.NORMAL) {
            guild.reduceOfficer();
        }
        this.sendGuildMsg(guild, new RefreshMemberListNotify(), 0);
    }

    public boolean transferPresident(final Guild _guild, final HeroPlayer _president, final int _memberUserID) {
        GuildMemberProxy targetMemberProxy = _guild.getMember(_memberUserID);
        if (targetMemberProxy == null) {
            ResponseMessageQueue.getInstance().put(_president.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u662f\u6df7\u4f60\u8fd9\u91cc\u7684"));
            return false;
        }
        if (_guild.transferPresidentTo(_memberUserID)) {
            GuildDAO.transferPresident(_guild.getID(), _president.getUserID(), _memberUserID);
            ResponseMessageQueue.getInstance().put(_president.getMsgQueueIndex(), new MemberRankChangeNotify(EGuildMemberRank.OFFICER.value()));
            this.sendGuildMsg(_guild, new Warning(String.valueOf(targetMemberProxy.name) + "\u6210\u4e3a\u4e86" + EGuildMemberRank.PRESIDENT.getDesc()), _memberUserID);
            if (targetMemberProxy.isOnline) {
                HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_memberUserID);
                if (player != null) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MemberRankChangeNotify(EGuildMemberRank.PRESIDENT.value()));
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6210\u4e3a\u4e86" + EGuildMemberRank.PRESIDENT.getDesc()));
                }
            }
            this.sendGuildMsg(_guild, new RefreshMemberListNotify(), _president.getUserID());
            return true;
        }
        return false;
    }

    public Guild getGuild(final int _guildID) {
        if (_guildID > 0) {
            return this.guildTable.get(_guildID);
        }
        return null;
    }

    public void sendGuildMsg(final Guild _guild, final AbsResponseMessage _msg, final int _excludeMemberUserID) {
        ArrayList<GuildMemberProxy> list = _guild.getMemberList();
        for (int i = 0; i < list.size(); ++i) {
            GuildMemberProxy member = list.get(i);
            if (_excludeMemberUserID != member.userID && member.isOnline) {
                HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(member.userID);
                if (player != null && player.isEnable()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), _msg);
                }
            }
        }
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player != null) {
            if (player.getGuildID() == 0) {
                if (!this.guildTable.isEmpty()) {
                    for (final Guild guild : this.guildTable.values()) {
                        GuildMemberProxy guildMemberProxy = guild.getMember(_session.userID);
                        if (guildMemberProxy != null) {
                            player.setGuildID(guild.getID());
                            guild.memberLogin(player, guildMemberProxy);
                        }
                    }
                }
            } else {
                Guild guild2 = this.guildTable.get(player.getGuildID());
                if (guild2 != null) {
                    GuildMemberProxy guildMemberProxy2 = guild2.getMember(player.getUserID());
                    guild2.memberLogin(player, guildMemberProxy2);
                }
            }
        }
    }

    @Override
    public void sessionFree(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player != null) {
            Guild guild = this.guildTable.get(player.getGuildID());
            if (guild != null) {
                GuildMemberProxy member = guild.getMember(player.getUserID());
                guild.memberLogout(member);
            }
        }
    }
}
