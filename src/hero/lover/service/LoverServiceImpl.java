// 
// Decompiled by Procyon v0.5.36
// 
package hero.lover.service;

import java.util.Date;
import java.util.ArrayList;
import hero.map.Map;
import hero.map.service.MapServiceImpl;
import java.util.TimerTask;
import hero.item.bag.exception.BagException;
import yoyo.core.packet.AbsResponseMessage;
import hero.lover.message.ResponseMarryRelationShow;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.player.HeroPlayer;
import javolution.util.FastMap;
import java.util.Timer;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class LoverServiceImpl extends AbsServiceAdaptor<LoverServerConfig> {

    private static Logger log;
    private static LoverServiceImpl instance;
    private static Object lock;
    private static Object lock1;
    private Timer timer;
    private FastMap<Integer, Timer> removeAllPlayerOutMarryMapTimerMap;
    protected boolean canMarry;
    private static final float[] EXP_MODULE;
    public static final int LOVER_MAX_VALUE = 3000;
    public static final int ADD_LOVER_VALUE_FOR_PER_MINUTE = 1;
    public static final int ADD_LOVER_VALUE_FOR_UPGRADE = 500;
    public static final int ADD_LOVER_VALUE_FOR_FLOWER = 100;
    public static final int ADD_LOVER_VALUE_FOR_CHOCOLATE = 2000;

    static {
        LoverServiceImpl.log = Logger.getLogger((Class) LoverServiceImpl.class);
        LoverServiceImpl.instance = null;
        LoverServiceImpl.lock = new Object();
        LoverServiceImpl.lock1 = new Object();
        EXP_MODULE = new float[]{0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.06f};
    }

    public static LoverServiceImpl getInstance() {
        synchronized (LoverServiceImpl.lock) {
            if (LoverServiceImpl.instance == null) {
                LoverServiceImpl.instance = new LoverServiceImpl();
            }
            // monitorexit(LoverServiceImpl.lock)
            return LoverServiceImpl.instance;
        }
    }

    public LoverServiceImpl() {
        this.timer = new Timer();
        this.removeAllPlayerOutMarryMapTimerMap = (FastMap<Integer, Timer>) new FastMap();
        this.canMarry = true;
        LoverDAO.deleteTimeOut();
    }

    public MarryStatus registerLover(final String _name1, final String _name2) {
        MarryStatus status = LoverDAO.propose(_name1, _name2);
        return status;
    }

    public LoverStatus registerLoverTree(final String _name1, final String _name2) {
        return null;
    }

    public String whoLoveMe(final String name) {
        return LoverDAO.whoLoveMe(name);
    }

    public String whoMarriedMe(final String name) {
        return LoverDAO.whoMarriedMe(name);
    }

    public void showMarryRelation(final HeroPlayer player) throws BagException {
        byte relation = 0;
        String othername = this.whoLoveMe(player.getName());
        if (othername == null) {
            othername = this.whoMarriedMe(player.getName());
            if (othername != null) {
                relation = 2;
            }
        } else {
            relation = 1;
        }
        if (relation > 0) {
            HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(othername);
            if (otherPlayer == null) {
                otherPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(othername);
                otherPlayer.loverLever = player.loverLever;
                otherPlayer.setLoverValue(player.getLoverValue());
            }
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMarryRelationShow(relation, otherPlayer));
        } else {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMarryRelationShow(relation, null));
        }
    }

    public void updateMarryStatus(final String userName, final String otherName, final MarryStatus status) {
        LoverDAO.updateMarryStatus(userName, otherName, status);
    }

    public MarryStatus registerMarriage(final String _uid1, final String _uid2, final short clanID) {
        MarryStatus status = MarryStatus.NO_TIME;
        synchronized (LoverServiceImpl.lock1) {
            if (this.canMarry) {
                status = LoverDAO.registerMarriage(_uid1, _uid2);
                if (status == MarryStatus.SUCCESS) {
                    HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(_uid1);
                    this.canMarry = false;
                    Timer ptimer = new Timer();
                    ptimer.schedule(new MarriageTask(clanID), 7200L);
                    this.removeAllPlayerOutMarryMapTimerMap.put(player.getUserID(), ptimer);
                }
            }
        }
        // monitorexit(LoverServiceImpl.lock1)
        return status;
    }

    public Timer getRemoveAllPlayerOutMarryMapTimer(final int userID) {
        return (Timer) this.removeAllPlayerOutMarryMapTimerMap.get(userID);
    }

    public MarryStatus divorce(final String _name) {
        MarryStatus status;
        synchronized (LoverServiceImpl.lock1) {
            status = LoverDAO.divorceMarriage(_name);
            if (status == MarryStatus.NOT_LOVER) {
                this.canMarry = true;
            }
        }
        // monitorexit(LoverServiceImpl.lock1)
        return status;
    }

    public boolean noHadOtherMarry(final short mapID) {
        Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);
        LoverServiceImpl.log.debug(("loverservice had other marry map = " + map));
        if (map == null) {
            return this.canMarry = false;
        }
        if (map.getPlayerList() != null && map.getPlayerList().size() > 0) {
            return this.canMarry = false;
        }
        return this.canMarry = true;
    }

    public void marryFaild(final String _name1, final String _name2) {
        LoverDAO.marryFaild(_name1, _name2);
        this.timer.cancel();
    }

    public String[] anotherInTream(final String _srcName, final ArrayList<HeroPlayer> _player) {
        return LoverDAO.hasMarried(_srcName, _player);
    }

    @Override
    protected void start() {
        Date tomorrow = new Date();
        tomorrow.setTime(tomorrow.getTime() + 86400000L);
        tomorrow.setHours(4);
        this.timer.schedule(new LoverTask(), tomorrow);
    }

    public Date getTomorrow() {
        Date tomorrow = new Date();
        tomorrow.setTime(new Date().getTime() + 86400000L);
        return tomorrow;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public void loverUpgrade(final HeroPlayer player) {
        LoverLevel currLevel = player.loverLever;
        LoverLevel level = this.getLoverLevel(player.getLoverValue());
        if (level.getLevel() - currLevel.getLevel() == 1) {
            player.loverLever = level;
            player.changeExperienceModulus(LoverServiceImpl.EXP_MODULE[level.getLevel() - 1]);
        }
    }

    public LoverLevel getLoverLevel(final int loverValue) {
        LoverLevel loverLevel = LoverLevel.ZHI;
        if (loverValue >= 40000) {
            loverLevel = LoverLevel.ZUANSHI;
        } else if (loverValue >= 27000) {
            loverLevel = LoverLevel.JIN;
        } else if (loverValue >= 25000) {
            loverLevel = LoverLevel.YIN;
        } else if (loverValue >= 15000) {
            loverLevel = LoverLevel.TONG;
        } else if (loverValue >= 8000) {
            loverLevel = LoverLevel.TIE;
        }
        return loverLevel;
    }

    public enum LoverStatus {
        NONE("NONE", 0),
        REGISTER("REGISTER", 1),
        SUCCESS("SUCCESS", 2),
        REGISTERED("REGISTERED", 3),
        ME_SUCCESSED("ME_SUCCESSED", 4),
        THEM_SUCCESSED("THEM_SUCCESSED", 5);

        private LoverStatus(final String name, final int ordinal) {
        }
    }

    public enum MarryStatus {
        NOT_LOVER("NOT_LOVER", 0, 0),
        NO_TIME("NO_TIME", 1, 1),
        LOVED_SUCCESS("LOVED_SUCCESS", 2, 2),
        SUCCESS("SUCCESS", 3, 3),
        BREAK_UP("BREAK_UP", 4, 4),
        DIVORCE_SUCCESS("DIVORCE_SUCCESS", 5, 5),
        DIVORCED("DIVORCED", 6, 6),
        LOVED_NO_MARRY("LOVED_NO_MARRY", 7, 7),
        MARRIED("MARRIED", 8, 8);

        private int status;

        private MarryStatus(final String name, final int ordinal, final int _status) {
            this.status = _status;
        }

        public int getStatus() {
            return this.status;
        }
    }
}
