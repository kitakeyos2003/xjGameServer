// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.service;

import java.util.Iterator;
import hero.gather.message.UseGourdMessage;
import hero.gather.dict.SoulInfo;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.item.detail.EGoodsTrait;
import hero.chat.service.ChatQueue;
import hero.gather.message.TakeSoulMessage;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.Gourd;
import hero.npc.Monster;
import hero.gather.MonsterSoul;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.gather.dict.Refined;
import hero.player.HeroPlayer;
import java.util.ArrayList;
import yoyo.service.base.session.Session;
import hero.gather.dict.SoulInfoDict;
import hero.gather.dict.RefinedDict;
import java.util.TimerTask;
import java.util.Timer;
import hero.gather.Gather;
import java.util.HashMap;
import yoyo.service.base.AbsServiceAdaptor;

public class GatherServerImpl extends AbsServiceAdaptor<GatherServerConfig> {

    private static GatherServerImpl instance;
    private HashMap<Integer, Gather> gatherList;
    public static final int[] MONEY_OF_UPGRADE;
    public static final int[] POINT_LIMIT;
    public static final int FREIFHT_OF_NEW_SKILL = 400;
    public static final int SUCK_SOUL_SKILL_ID = -50;
    private Timer mCheckTimer;
    public static final long GATHER_SAVE_TIME = 300000L;
    private static final String TIP_OF_GET_HEADER = "\u83b7\u5f97\u4e86";
    private static final String TIP_GOURD_IS_FULL = "\u846b\u82a6\u5df2\u6ee1";
    private static final String NEED_GATHER_SKILL = "\u9700\u8981 \u70bc\u5316\u5e08";
    private static final String STUDYED_MANUF_SKILL = "\u5df2\u4f1a\u7684\u6280\u80fd";

    static {
        MONEY_OF_UPGRADE = new int[]{2000, 10000, 50000, 250000};
        POINT_LIMIT = new int[]{1000, 6000, 31000, 156000, 156000};
    }

    private GatherServerImpl() {
        this.config = new GatherServerConfig();
        this.gatherList = new HashMap<Integer, Gather>();
        (this.mCheckTimer = new Timer()).schedule(new GatherTimerTask(), 300000L, 300000L);
    }

    public static GatherServerImpl getInstance() {
        if (GatherServerImpl.instance == null) {
            GatherServerImpl.instance = new GatherServerImpl();
        }
        return GatherServerImpl.instance;
    }

    @Override
    protected void start() {
        RefinedDict.getInstance().loadRefineds(((GatherServerConfig) this.config).gatherDataPath);
        SoulInfoDict.getInstance().loadSoulInfos(((GatherServerConfig) this.config).soulsDataPath);
    }

    @Override
    public void createSession(final Session _session) {
        Gather _gather = GatherDAO.loadGatherByUserID(_session.userID);
        if (_gather != null) {
            this.gatherList.put(_session.userID, _gather);
        }
    }

    @Override
    public void sessionFree(final Session _session) {
        Gather _gather = this.gatherList.remove(_session.userID);
        if (_gather != null) {
            GatherDAO.saveGahterByUserID(_session.userID, _gather.getMonsterSoul());
        }
    }

    public Gather getGatherByUserID(final int _userID) {
        return this.gatherList.get(_userID);
    }

    public boolean studyGather(final int _userID) {
        if (this.gatherList.get(_userID) != null) {
            return false;
        }
        this.gatherList.put(_userID, new Gather());
        GatherDAO.studyGather(_userID);
        return true;
    }

    public ArrayList<Integer> getCanUseManufIDs(final int _userID) {
        Gather skill = this.gatherList.get(_userID);
        if (skill != null) {
            return skill.getRefinedList();
        }
        return null;
    }

    public boolean addRefinedItem(final HeroPlayer _player, final Refined _refined, final GetTypeOfSkillItem _getType) {
        Gather skill = this.gatherList.get(_player.getUserID());
        if (skill == null) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u9700\u8981 \u70bc\u5316\u5e08"));
            return false;
        }
        if (!skill.isStudyedRefinedID(_refined.id)) {
            skill.addRefinedID(_refined.id);
            GatherDAO.addRefinedID(_player.getUserID(), _refined.id);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(_getType.toString()) + _refined.name));
            return true;
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5df2\u4f1a\u7684\u6280\u80fd"));
        return false;
    }

    public void forgetGatherByUserID(final int _userID) {
        Gather skill = this.gatherList.remove(_userID);
        if (skill != null) {
            GatherDAO.forgetGatherByUserID(_userID);
        }
    }

    public ArrayList<MonsterSoul> getMonsterSouls(final int _userID) {
        Gather skill = this.gatherList.get(_userID);
        if (skill != null) {
            return skill.getMonsterSoul();
        }
        return null;
    }

    public boolean processSoulWhenMonsterDied(final Monster _monster) {
        if (_monster.getTakeSoulUserID() != 0) {
            int soulID = _monster.getSoulID();
            if (soulID != 0) {
                HeroPlayer player = _monster.where().getPlayer(_monster.getTakeSoulUserID());
                int gourdID = this.getGourdID(player);
                if (gourdID > 0 && this.canTakeSoul(player, _monster.getAttackerAtFirst())) {
                    Gourd gourd = (Gourd) GoodsServiceImpl.getInstance().getGoodsByID(gourdID);
                    Gather skill = this.getGatherByUserID(player.getUserID());
                    if (skill != null) {
                        if (skill.addMosnterSoul(soulID, gourd)) {
                            AbsResponseMessage message = new TakeSoulMessage(_monster.getID(), _monster.getTakeSoulUserID());
                            SoulInfo soulInfo = SoulInfoDict.getInstance().getSoulInfoByID(soulID);
                            ChatQueue.getInstance().addGoodsMsg(player, "\u83b7\u5f97\u4e86", soulInfo.soulName, EGoodsTrait.SHI_QI.getViewRGB(), 1);
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), message);
                            MapSynchronousInfoBroadcast.getInstance().put(_monster.where(), message, true, player.getID());
                            return true;
                        }
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u846b\u82a6\u5df2\u6ee1"));
                    }
                }
            }
        }
        return false;
    }

    public void useGourd(final HeroPlayer _player, final Monster _monster) {
        if (_monster.getTakeSoulUserID() == 0 && _monster.getSoulID() > 0) {
            _monster.beHarmed(_player, 0);
            _monster.setTakeSoulUserID(_player.getID());
            MapSynchronousInfoBroadcast.getInstance().put(_monster.where(), new UseGourdMessage(_monster.getID()), false, 0);
        }
    }

    private boolean canTakeSoul(final HeroPlayer _player, final HeroPlayer _fristPlayer) {
        return _player != null && _fristPlayer != null && _player.isEnable() && !_player.isDead() && (_player.getUserID() == _fristPlayer.getUserID() || (_fristPlayer.getGroupID() != 0 && _player.getGroupID() == _fristPlayer.getGroupID()));
    }

    public int getGourdID(final HeroPlayer _player) {
        if (_player != null) {
            int[][] items = _player.getInventory().getSpecialGoodsBag().getAllItem();
            for (int i = 0; i < items.length; ++i) {
                if (items[i][0] >= 50001 && items[i][0] <= 50005) {
                    return items[i][0];
                }
            }
        }
        return 0;
    }

    public void lvlUp(final int _userID, final Gather _gather) {
        if (_gather.lvlUp()) {
            GatherDAO.updateGather(_userID, _gather);
        }
    }

    public void addPoint(final int _userID, final Gather _gather, final int _addPoint) {
        if (_gather.addPoint(_addPoint)) {
            GatherDAO.updateGather(_userID, _gather);
        }
    }

    private void saveSouls() {
        synchronized (this) {
            for (final int _userID : this.gatherList.keySet()) {
                Gather _gather = this.gatherList.get(_userID);
                if (_gather.isSave()) {
                    GatherDAO.saveGahterByUserID(_userID, _gather.getMonsterSoul());
                    _gather.setSave(false);
                }
            }
        }
    }

    class GatherTimerTask extends TimerTask {

        @Override
        public void run() {
            GatherServerImpl.this.saveSouls();
        }
    }
}
