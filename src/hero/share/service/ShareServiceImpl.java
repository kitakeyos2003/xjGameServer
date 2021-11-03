// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import hero.share.EVocation;
import hero.share.EMagic;
import hero.log.service.ServiceType;
import hero.charge.service.ChargeServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.item.special.HookExp;
import hero.share.message.Warning;
import java.sql.Timestamp;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.ResponseIndexNoticeList;
import yoyo.core.queue.ResponseMessageQueue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import hero.share.cd.CDTimeDAO;
import hero.share.cd.CDUnit;
import java.util.ArrayList;
import hero.player.HeroPlayer;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Iterator;
import hero.share.RankMenuField;
import hero.share.exchange.ExchangeDict;
import yoyo.service.base.session.Session;
import hero.share.letter.LetterService;
import java.util.HashMap;
import hero.share.Inotice;
import hero.share.RankInfo;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class ShareServiceImpl extends AbsServiceAdaptor<ShareConfig> {

    private static Logger log;
    private static ShareServiceImpl instance;
    private static final short REQUEST_OFFLINE_HOOK_COMMAND = 23842;
    private static final String DEFAULT_LOGOUT_TIME = "1980-01-01 00:00:00";
    private static final int OFFLINE_INTERVATE = 3600000;
    private static Map<Integer, Integer> requestExchangePlayerList;
    private static List<RankInfo> playerPowerRankAllVocation;
    private static Map<Byte, List<RankInfo>> playerPowerRankSingleVocation;
    private static List<Inotice> inoticeList;

    static {
        ShareServiceImpl.log = Logger.getLogger((Class) ShareServiceImpl.class);
    }

    private ShareServiceImpl() {
        this.config = new ShareConfig();
        ShareServiceImpl.requestExchangePlayerList = new HashMap<Integer, Integer>();
    }

    @Override
    protected void start() {
        LetterService.getInstance();
        AllPictureDataDict.getInstance();
    }

    @Override
    public void sessionFree(final Session _session) {
        ExchangeDict.getInstance().playerOutline(_session.nickName);
    }

    public static ShareServiceImpl getInstance() {
        if (ShareServiceImpl.instance == null) {
            ShareServiceImpl.instance = new ShareServiceImpl();
        }
        return ShareServiceImpl.instance;
    }

    public Map<Byte, RankMenuField> getRankTypeMap() {
        return ((ShareConfig) this.config).rankTypeMap;
    }

    public boolean canRequest(final int userID) {
        ShareServiceImpl.log.debug(("can Request exchange = " + ShareServiceImpl.requestExchangePlayerList.get(userID)));
        return ShareServiceImpl.requestExchangePlayerList.get(userID) == null;
    }

    public void addRequestExchangePlayer(final int userID, final int targetUserID) {
        if (ShareServiceImpl.requestExchangePlayerList.get(userID) == null) {
            ShareServiceImpl.log.debug(("requestExchangePlayerList add userid = " + userID));
            ShareServiceImpl.requestExchangePlayerList.put(userID, targetUserID);
        }
    }

    public void removePlayerFromRequestExchangeList(final int userID) {
        Iterator<Integer> it = ShareServiceImpl.requestExchangePlayerList.keySet().iterator();
        while (it.hasNext()) {
            if (it.next() == userID) {
                ShareServiceImpl.log.debug(("removePlayerFromRequestExchangeList userid= " + userID));
                it.remove();
            }
        }
    }

    public void removePlayerFromRequestExchangeListByTarget(final int targetUserID) {
        Iterator<Integer> it = ShareServiceImpl.requestExchangePlayerList.values().iterator();
        while (it.hasNext()) {
            if (it.next() == targetUserID) {
                ShareServiceImpl.log.debug(("removePlayerFromRequestExchangeListByTarget targetUserID= " + targetUserID));
                it.remove();
            }
        }
    }

    public final Connection getResourceConnection() {
        String dbname = ((ShareConfig) this.config).getResourceDBname();
        Connection conn;
        try {
            String dbName = ((ShareConfig) this.config).getResourceDBname();
            String dbUrl = "jdbc:mysql://" + ((ShareConfig) this.config).getResourceDBurl() + "/" + dbName + "?connectTimeout=0&autoReconnect=true&failOverReadOnly=false";
            String dbUser = ((ShareConfig) this.config).getResourceDBusername();
            String dbPassword = ((ShareConfig) this.config).getResourceDBpassword();
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (Exception ex) {
            conn = null;
            ex.printStackTrace();
            LogWriter.error("GmServiceImpl.getConnection error!", ex);
        }
        return conn;
    }

    public void saveCD(final HeroPlayer _player) {
        ArrayList<CDUnit> list = new ArrayList<CDUnit>();
        Set<Integer> set = _player.userCDMap.keySet();
        Iterator<Integer> iter = set.iterator();
        CDUnit cd = null;
        while (iter.hasNext()) {
            cd = _player.userCDMap.get(iter.next());
            if (cd.getTimeBySec() > 10) {
                list.add(cd);
            }
            cd.stop();
            iter.remove();
        }
        _player.userCDMap.clear();
        if (list.size() > 0) {
            CDTimeDAO.insertCD(_player.getUserID(), list);
        }
    }

    public static String DateTimeToString(final Date _date) {
        String dateStr = "";
        Date date = _date;
        try {
            SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateStr = DF.format(date);
        } catch (Exception ex) {
        }
        return dateStr;
    }

    public static String DateToString(final Date _date) {
        String dateStr = "";
        Date date = _date;
        try {
            SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = DF.format(date);
        } catch (Exception ex) {
        }
        return dateStr;
    }

    public void showIndexNoticeList(final HeroPlayer player) {
        ShareServiceImpl.inoticeList = this.getInoticeList(0);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseIndexNoticeList(ShareServiceImpl.inoticeList));
    }

    public List<Inotice> getInoticeList(final int type) {
        return ShareDAO.getInoticeList(type);
    }

    public int hexStr2Int(String hex) {
        int res = 0;
        if (hex != null && hex.trim().length() > 0) {
            String hxs = "0123456789abcdef";
            hex = hex.substring(hex.indexOf("0x") + 2);
            String[] ses = hex.toLowerCase().split("");
            int i;
            for (int legth = i = ses.length - 1; i >= 0; --i) {
                if (ses[i].trim().length() > 0) {
                    res += (int) (hxs.indexOf(ses[i]) * Math.pow(16.0, legth - i));
                }
            }
        }
        return res;
    }

    public void offLineHook(final HeroPlayer player) {
        if (player.getLevel() < PlayerServiceImpl.getInstance().getConfig().max_level) {
            long defaultLastLogoutTime = Timestamp.valueOf("1980-01-01 00:00:00").getTime();
            if (player.lastLogoutTime > defaultLastLogoutTime) {
                long offLineTime = System.currentTimeMillis() - player.lastLogoutTime;
                int offLineHours = (int) offLineTime / 3600000;
                if (offLineHours >= ((ShareConfig) this.config).hookHours) {
                    int offLineDays = offLineHours / 24;
                    int hookHours = offLineHours;
                    if (offLineDays >= 1) {
                        hookHours = offLineDays * 8;
                        if (hookHours > 24) {
                            hookHours = 24;
                        }
                    } else if (hookHours > 8) {
                        hookHours = 8;
                    }
                    player.currHookHours = hookHours;
                    ShareServiceImpl.log.debug(("player[" + player.getName() + "] hook hours=" + hookHours));
                    this.warnBuyHookExp(player);
                }
            }
        }
    }

    public void warnBuyHookExp(final HeroPlayer player) {
        long offLineTime = System.currentTimeMillis() - player.lastLogoutTime;
        int offLineHours = (int) offLineTime / 3600000;
        if (offLineHours > 8) {
            if (player.currHookHours > 0) {
                int exp = this.calHookExp(player.currHookHours, player.getLevel());
                int point = this.calHookExpPoint(exp, player.getLevel());
                String warnStr = "\u79bb\u7ebf\u7ecf\u9a8c\u5956\u52b1: \n\u5c0a\u656c\u7684\u73a9\u5bb6\uff0c\u60a8\u5df2\u5f88\u4e45\u6ca1\u6709\u767b\u9646\u6e38\u620f\u4e86\uff0c\u4e3a\u4e86\u4e0d\u4f7f\u60a8\u4e0e\u60a8\u7684\u961f\u53cb\u843d\u4e0b\u5de8\u5927\u7684\u7b49\u7ea7\u5dee\u8ddd\uff0c\u6211\u4eec\u7279\u610f\u4e3a\u60a8\u7d2f\u79ef\u4e86" + player.currHookHours + "\u5c0f\u65f6\u7684\u79bb\u7ebf\u7ecf\u9a8c\uff0c\u5408\u8ba1" + exp + "\u70b9\u7ecf\u9a8c\uff0c\u60a8\u613f\u610f\u82b1\u8d39" + point + "\u70b9\u6570\u8d2d\u4e70\u8fd9\u4e9b\u7ecf\u9a8c\u5417\uff1f";
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning(warnStr, (byte) 4, (short) 23842));
            } else {
                player.buyHookExp = false;
            }
        }
    }

    public void startBuyHookExp(final HeroPlayer player) {
        int exp = this.calHookExp(player.currHookHours, player.getLevel());
        int point = this.calHookExpPoint(exp, player.getLevel());
        if (player.getChargeInfo().pointAmount >= point) {
            HookExp hookExp = (HookExp) GoodsContents.getGoods(((ShareConfig) this.config).hookExpGoodsID);
            ShareServiceImpl.log.debug(("HookExp goods = " + hookExp));
            boolean buyRes = ChargeServiceImpl.getInstance().reducePoint(player, point, hookExp.getID(), hookExp.getName(), 1, ServiceType.OFFLINE_HOOK_EXP);
            ShareServiceImpl.log.debug(("buy hook exp res = " + buyRes));
            if (buyRes) {
                player.buyHookExp = false;
                player.currHookHours = 0;
                PlayerServiceImpl.getInstance().addExperience(player, exp, 1.0f, 2);
            }
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u70b9\u6570\u4e0d\u8db3\uff0c\u786e\u5b9a\u5148\u5145\u503c\u518d\u8d2d\u4e70\u8fd9\u4e9b\u79bb\u7ebf\u7ecf\u9a8c\u5417\uff1f", (byte) 2, (byte) 5));
        }
    }

    private int calHookExp(final int hours, final int level) {
        int expBase = 3 + 6 * (level - 1);
        return expBase * hours * 60;
    }

    private int calHookExpPoint(final int exp, final int level) {
        int per_point_exp = 300 + (level + 3) / 30 * 300;
        int point = exp / per_point_exp;
        if (point < 1) {
            point = 1;
        }
        return point;
    }

    public List<RankInfo> getRankInfoList(final byte type, final int vocation1, final int vocation2, final boolean moreVocations) {
        List<RankInfo> rankInfoList = null;
        switch (type) {
            case 1: {
                rankInfoList = ShareDAO.getKillerRankInfoList(vocation1, vocation2, moreVocations);
                break;
            }
            case 2: {
                rankInfoList = ShareDAO.getLevelRankInfoList(vocation1, vocation2, moreVocations);
                break;
            }
            case 3: {
                rankInfoList = ShareDAO.getMoneyRankInfoList(vocation1, vocation2, moreVocations);
                break;
            }
            case 4: {
                rankInfoList = ShareDAO.getPowerRankList(vocation1, vocation2, moreVocations);
                break;
            }
            case 5: {
                rankInfoList = ShareDAO.getLoverValueRankInfoList(vocation1, vocation2, moreVocations);
                break;
            }
            case 6: {
                rankInfoList = ShareDAO.getGuildRankList();
                break;
            }
        }
        return rankInfoList;
    }

    public int calPlayerPower(final HeroPlayer player) {
        int strength = player.getActualProperty().getStrength();
        int agility = player.getActualProperty().getAgility();
        int stamina = player.getActualProperty().getStamina();
        int inte = player.getActualProperty().getInte();
        int spirit = player.getActualProperty().getSpirit();
        int lucky = player.getActualProperty().getLucky();
        int physicsAttack = player.getActualProperty().getMaxPhysicsAttack();
        float magicHarmValue = player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.UMBRA) + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SANCTITY) + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.FIRE) + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.WATER) + player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SOIL);
        return this.calPowerValue(player.getVocation().value(), strength, agility, stamina, inte, spirit, lucky, physicsAttack, magicHarmValue);
    }

    private int calPowerValue(final byte vocation, final int strength, final int agility, final int stamina, final int inte, final int spirit, final int lucky, final int physicsAttack, final float magicHarmValue) {
        int power = 0;
        if (vocation == EVocation.LI_SHI.value() || vocation == EVocation.JIN_GANG_LI_SHI.value() || vocation == EVocation.LUO_CHA_LI_SHI.value() || vocation == EVocation.QING_TIAN_LI_SHI.value() || vocation == EVocation.XIU_LUO_LI_SHI.value()) {
            power = (int) (strength * 0.5 + agility * 0.3 + inte * 0.1 + spirit * 0.1 + stamina * 1 + lucky);
        }
        if (vocation == EVocation.FA_SHI.value() || vocation == EVocation.TIAN_JI_FA_SHI.value() || vocation == EVocation.XUAN_MING_FA_SHI.value() || vocation == EVocation.YAN_MO_FA_SHI.value() || vocation == EVocation.YU_HUO_FA_SHI.value()) {
            power = (int) (strength * 0.1 + agility * 0.1 + inte * 0.5 + spirit * 0.3 + stamina * 1 + lucky);
        }
        if (vocation == EVocation.CHI_HOU.value() || vocation == EVocation.GUI_YI_CHI_HOU.value() || vocation == EVocation.LI_JIAN_CHI_HOU.value() || vocation == EVocation.SHEN_JIAN_CHI_HOU.value() || vocation == EVocation.XIE_REN_CHI_HOU.value()) {
            power = (int) (strength * 0.3 + agility * 0.5 + inte * 0.1 + spirit * 0.1 + stamina * 1 + lucky);
        }
        if (vocation == EVocation.WU_YI.value() || vocation == EVocation.LING_QUAN_WU_YI.value() || vocation == EVocation.MIAO_SHOU_WU_YI.value() || vocation == EVocation.XIE_JI_WU_YI.value() || vocation == EVocation.YIN_YANG_WU_YI.value()) {
            power = (int) (strength * 0.1 + agility * 0.1 + inte * 0.3 + spirit * 0.5 + stamina * 1 + lucky);
        }
        return power;
    }
}
