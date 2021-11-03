// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.sports;

import hero.map.message.DisappearNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.Npc;
import hero.share.ME2GameObject;
import hero.map.Map;
import java.util.TimerTask;
import hero.group.Group;
import hero.chat.service.ChatServiceImpl;
import hero.group.service.GroupServiceImpl;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import javolution.util.FastList;
import javolution.util.FastMap;

public class SportsService {

    private static FastMap<Integer, short[]> sportsPointTable;
    private FastList<SportsTeam> waitingQueue;
    private FastList<SportsUnit> sportsUnitList;
    private static final short MIN_LEVEL = 20;
    public static final byte STATUS_OF_READY = 1;
    public static final byte STATUS_OF_FIGHTING = 2;
    public static final long READY_TIME = 120000L;
    public static final long COUNT_TIME_INVERVAL = 15000L;
    public static final long SPORTS_KEEP_MAX_TIME = 1800000L;
    private static final String DOOR_MODEL_ID = "n300";
    private static final short INIT_POINT = 1000;
    private static final short DIFFERENCE = 500;

    private SportsService() {
        SportsService.sportsPointTable = (FastMap<Integer, short[]>) new FastMap();
        this.waitingQueue = (FastList<SportsTeam>) new FastList();
        this.sportsUnitList = (FastList<SportsUnit>) new FastList();
    }

    public short[] getSportsPointList(final int _userID) {
        return (short[]) SportsService.sportsPointTable.get(_userID);
    }

    public short getSportsPoint(final int _userID, final ESportsClan _sportsClan) {
        short[] sportsPointList = (short[]) SportsService.sportsPointTable.get(_userID);
        if (sportsPointList != null) {
            return sportsPointList[_sportsClan.getID() - 1];
        }
        return 1000;
    }

    public void add(final HeroPlayer _leader, final ESportsClan _sportsClan, final byte _type) {
        if (_leader.getGroupID() <= 0) {
            ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new Warning("\u9700\u8981\u5728\u961f\u4f0d\u4e2d"));
            return;
        }
        Group group = GroupServiceImpl.getInstance().getGroup(_leader.getGroupID());
        if (group == null) {
            ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new Warning("\u9700\u8981\u5728\u961f\u4f0d\u4e2d"));
            return;
        }
        if (_leader.getLevel() < 20) {
            ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new Warning("20\u7ea7\u624d\u80fd\u8fdb\u884c\u7ade\u6280"));
            return;
        }
        if (group.getMemberNumber() > _type) {
            ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new Warning("\u961f\u5458\u6570\u91cf\u8d85\u8fc7\u9009\u62e9\u7684\u961f\u4f0d\u7c7b\u578b"));
            return;
        }
        short levelZoon = (short) (_leader.getLevel() / 10 * 10);
        synchronized (group.getPlayerList()) {
            SportsTeam sportsTeam = new SportsTeam(group.getID(), _type, _leader.getUserID());
            int teamPointTotal = 0;
            for (final HeroPlayer member : group.getPlayerList()) {
                if (!member.isEnable()) {
                    ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new Warning("\u961f\u4f0d\u6709\u6210\u5458\u79bb\u7ebf"));
                    // monitorexit(group.getPlayerList())
                    return;
                }
                if (levelZoon != _leader.getLevel() / 10 * 10) {
                    ResponseMessageQueue.getInstance().put(_leader.getMsgQueueIndex(), new Warning("\u961f\u4f0d\u6210\u5458\u7b49\u7ea7\u533a\u95f4\u4e0d\u4e00\u81f4"));
                    // monitorexit(group.getPlayerList())
                    return;
                }
                short memberPoint = this.getSportsPoint(member.getUserID(), _sportsClan);
                sportsTeam.addMemberInfo(member.getUserID(), memberPoint);
                teamPointTotal += memberPoint;
            }
            sportsTeam.levelZoon = levelZoon;
            sportsTeam.teamPointTotal = teamPointTotal;
            this.waitingQueue.add(sportsTeam);
            ChatServiceImpl.getInstance().sendGroupBottomSys(group, "\u52a0\u5165\u4e86\u7ade\u6280\u7b49\u5f85\u961f\u5217");
        }
        // monitorexit(group.getPlayerList())
    }

    public class MatchTask extends TimerTask {

        @Override
        public void run() {
            if (SportsService.this.waitingQueue.size() >= 2) {
                for (int i = 0; i < SportsService.this.waitingQueue.size(); ++i) {
                    SportsTeam sportsTeamActive = (SportsTeam) SportsService.this.waitingQueue.get(i);
                    for (int j = i + 1; j < SportsService.this.waitingQueue.size(); ++j) {
                        SportsTeam sportsTeamPassive = (SportsTeam) SportsService.this.waitingQueue.get(j);
                        if (sportsTeamActive.queueType == sportsTeamPassive.queueType && sportsTeamActive.levelZoon == sportsTeamPassive.levelZoon && Math.abs(sportsTeamActive.teamPointTotal / sportsTeamActive.queueType - sportsTeamActive.teamPointTotal / sportsTeamActive.queueType) <= 500) {
                            SportsService.this.sportsUnitList.add(new SportsUnit(sportsTeamActive, sportsTeamPassive, null));
                            SportsService.this.waitingQueue.remove(j);
                            SportsService.this.waitingQueue.remove(i);
                        }
                    }
                }
            }
        }
    }

    public class MonitorTask extends TimerTask {

        @Override
        public void run() {
            if (SportsService.this.sportsUnitList.size() > 0) {
                int i = 0;
                while (i < SportsService.this.sportsUnitList.size()) {
                    SportsUnit sportsUnit = (SportsUnit) SportsService.this.sportsUnitList.get(i);
                    if (1 == sportsUnit.status) {
                        if (sportsUnit.readyKeepTime >= 120000L) {
                            sportsUnit.start();
                            Map map = sportsUnit.site;
                            for (final ME2GameObject npc : map.getNpcList()) {
                                if (((Npc) npc).getModelID().equalsIgnoreCase("n300")) {
                                    MapSynchronousInfoBroadcast.getInstance().put(map, new DisappearNotify(npc.getObjectType().value(), npc.getID()), false, 0);
                                }
                            }
                            SportsService.this.sportsUnitList.remove(i);
                        } else {
                            SportsUnit sportsUnit2 = sportsUnit;
                            sportsUnit2.readyKeepTime += 15000L;
                            ++i;
                        }
                    } else if (sportsUnit.fightKeepTime >= 1800000L) {
                        if (sportsUnit.team1.getLiveMemberNumber() > 0 && sportsUnit.team2.getLiveMemberNumber() > 0) {
                            if (sportsUnit.team1.getLiveMemberNumber() > sportsUnit.team2.getLiveMemberNumber()) {
                                ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team1.teamID, "\u60a8\u8d62\u5f97\u4e86\u6bd4\u8d5b");
                                ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team2.teamID, "\u60a8\u8f93\u6389\u4e86\u6bd4\u8d5b");
                            } else if (sportsUnit.team1.getLiveMemberNumber() == sportsUnit.team2.getLiveMemberNumber()) {
                                ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team1.teamID, "\u6bd4\u8d5b\u7ed3\u675f\uff0c\u672a\u5206\u80dc\u8d1f");
                                ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team2.teamID, "\u6bd4\u8d5b\u7ed3\u675f\uff0c\u672a\u5206\u80dc\u8d1f");
                            } else {
                                ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team1.teamID, "\u60a8\u8f93\u6389\u4e86\u6bd4\u8d5b");
                                ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team2.teamID, "\u60a8\u8d62\u5f97\u4e86\u6bd4\u8d5b");
                            }
                        } else if (sportsUnit.team1.getLiveMemberNumber() == 0) {
                            ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team1.teamID, "\u60a8\u8f93\u6389\u4e86\u6bd4\u8d5b");
                            ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team2.teamID, "\u60a8\u8d62\u5f97\u4e86\u6bd4\u8d5b");
                        } else {
                            ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team1.teamID, "\u60a8\u8d62\u5f97\u4e86\u6bd4\u8d5b");
                            ChatServiceImpl.getInstance().sendGroupBottomSys(sportsUnit.team2.teamID, "\u60a8\u8f93\u6389\u4e86\u6bd4\u8d5b");
                        }
                        SportsService.this.sportsUnitList.remove(i);
                        sportsUnit.site.destroy();
                    } else {
                        SportsUnit sportsUnit3 = sportsUnit;
                        sportsUnit3.fightKeepTime += 15000L;
                        ++i;
                    }
                }
            }
        }
    }
}
