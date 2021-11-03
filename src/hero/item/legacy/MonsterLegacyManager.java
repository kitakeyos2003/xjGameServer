// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.legacy;

import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.chat.service.ChatServiceImpl;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.chat.service.ChatQueue;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.item.message.GoodsDistributeNotify;
import java.util.Iterator;
import hero.share.service.LogWriter;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.LegacyBoxStatusDisappearNotify;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.message.LegacyBoxDisappearNotify;
import hero.player.HeroPlayer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Timer;
import java.util.ArrayList;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class MonsterLegacyManager {

    private static Logger log;
    private FastMap<Short, DistributeGoods> distributeGoodsTable;
    private ArrayList<MonsterLegacyBox> boxList;
    private static final int BOX_CHECK_INTERVAL = 30000;
    private static final int GOODS_DISTRIBUTE_INTERVAL = 5000;
    private static MonsterLegacyManager instance;
    private Timer timer;
    private static final Random RANDOM;

    static {
        MonsterLegacyManager.log = Logger.getLogger((Class) MonsterLegacyManager.class);
        RANDOM = new Random();
    }

    private MonsterLegacyManager() {
        this.boxList = new ArrayList<MonsterLegacyBox>();
        this.distributeGoodsTable = (FastMap<Short, DistributeGoods>) new FastMap();
    }

    public void startMonitor() {
        if (this.timer == null) {
            (this.timer = new Timer()).schedule(new BoxBecomeDueCheckTask(), 30000L, 30000L);
            this.timer.schedule(new GoodsDistributeCheck(), 5000L, 5000L);
        }
    }

    public static MonsterLegacyManager getInstance() {
        if (MonsterLegacyManager.instance == null) {
            MonsterLegacyManager.instance = new MonsterLegacyManager();
        }
        return MonsterLegacyManager.instance;
    }

    public void addMonsterLegacyBox(final MonsterLegacyBox _box) {
        this.boxList.add(_box);
    }

    public void playerPickBox(final HeroPlayer _player, final int _boxID) {
        try {
            int i = 0;
            while (i < this.boxList.size()) {
                MonsterLegacyBox box = this.boxList.get(i);
                if (box.getID() == _boxID) {
                    if (!box.bePicked(_player)) {
                        break;
                    }
                    if (box.isEmpty()) {
                        this.boxList.remove(box);
                        box.where().getLegacyBoxList().remove(box);
                        AbsResponseMessage msg = new LegacyBoxDisappearNotify(box.getID(), box.getLocationY());
                        if (2 == box.getPickerType()) {
                            RaidPickerBox raidBox = (RaidPickerBox) box;
                            for (final HeroPlayer player : raidBox.getVisitorList()) {
                                if (player.isEnable() && player.where() == raidBox.where()) {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                                }
                            }
                            break;
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                        break;
                    } else {
                        if (2 == box.getPickerType()) {
                            RaidPickerBox raidBox2 = (RaidPickerBox) box;
                            for (final HeroPlayer player2 : raidBox2.getVisitorList()) {
                                if (raidBox2.statusIsChanged(player2.getUserID())) {
                                    ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new LegacyBoxStatusDisappearNotify(box.getID(), box.getLocationY()));
                                }
                            }
                            break;
                        }
                        break;
                    }
                } else {
                    ++i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogWriter.error(null, e);
        }
    }

    public synchronized void notifyGoodsDistributeUI(final DistributeGoods _distributeGoods) {
        _distributeGoods.distributeTime = System.currentTimeMillis();
        this.distributeGoodsTable.put(_distributeGoods.id, _distributeGoods);
        AbsResponseMessage msg = new GoodsDistributeNotify(_distributeGoods.id, _distributeGoods.goods, _distributeGoods.number, 90000);
        for (final HeroPlayer player : _distributeGoods.box.getVisitorList()) {
            MonsterLegacyManager.log.debug(("\u901a\u77e5\u5ba2\u6237\u7aef\u5f39\u51fa\u7269\u54c1\u5206\u914d\u6846 player name = " + player.getName()));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
        }
    }

    public void selectDistributeGoods(final short _distributeGoodsID, final HeroPlayer _player, final boolean _needOrCancel) {
        DistributeGoods distributeGoods = (DistributeGoods) this.distributeGoodsTable.get(_distributeGoodsID);
        if (distributeGoods != null) {
            if (_needOrCancel) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u63b7\u51fa\u4e86" + distributeGoods.distribute(_player.getUserID(), MonsterLegacyManager.RANDOM.nextInt(100) + 1) + "\u70b9", (byte) 0));
            } else {
                distributeGoods.distribute(_player.getUserID(), 0);
            }
            if (distributeGoods.hasOperated) {
                this.distributeGoodsTable.remove(_distributeGoodsID);
                if (distributeGoods.pickerUserID != 0) {
                    HeroPlayer gettor = PlayerServiceImpl.getInstance().getPlayerByUserID(distributeGoods.pickerUserID);
                    ChatQueue.getInstance().addGoodsMsg(gettor, "\u4f60\u4ee5" + distributeGoods.maxRandom + "\u70b9\u8d62\u5f97\u4e86", distributeGoods.goods.getName(), distributeGoods.goods.getTrait().getViewRGB(), distributeGoods.number);
                    if (GoodsServiceImpl.getInstance().addGoods2Package(gettor, distributeGoods.goods, distributeGoods.number, CauseLog.DROP) != null) {
                        distributeGoods.box.removeNormalGoods(distributeGoods);
                        if (distributeGoods.box.isEmpty()) {
                            distributeGoods.box.where().getLegacyBoxList().remove(distributeGoods.box);
                            AbsResponseMessage msg = new LegacyBoxDisappearNotify(distributeGoods.box.getID(), distributeGoods.box.getLocationY());
                            for (final HeroPlayer player : distributeGoods.box.getVisitorList()) {
                                if (player.isEnable() && player.where() == distributeGoods.box.where()) {
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                                }
                            }
                        } else {
                            for (final HeroPlayer player2 : distributeGoods.box.getVisitorList()) {
                                if (distributeGoods.box.statusIsChanged(player2.getUserID())) {
                                    ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new LegacyBoxStatusDisappearNotify(distributeGoods.box.getID(), distributeGoods.box.getLocationY()));
                                }
                            }
                        }
                    }
                    ChatServiceImpl.getInstance().sendGroupGoods(_player.getGroupID(), String.valueOf(gettor.getName()) + distributeGoods.maxRandom + "\u70b9\u8d62\u5f97\u4e86", distributeGoods.goods, distributeGoods.number, true, gettor.getID());
                } else {
                    ChatServiceImpl.getInstance().sendGroupGoods(_player.getGroupID(), "\u6240\u6709\u4eba\u90fd\u653e\u5f03\u4e86", distributeGoods.goods, distributeGoods.number, false, 0);
                }
            }
        }
    }

    public void removeDistributeFromMonitor(final short _distributeGoodsID) {
        this.distributeGoodsTable.remove(_distributeGoodsID);
    }

    class BoxBecomeDueCheckTask extends TimerTask {

        @Override
        public void run() {
            for (int i = 0; i < MonsterLegacyManager.this.boxList.size(); ++i) {
                MonsterLegacyBox box = MonsterLegacyManager.this.boxList.get(i);
                if (box.becomeDue()) {
                    MonsterLegacyManager.this.boxList.remove(box);
                    box.where().getLegacyBoxList().remove(box);
                    if (1 == box.getPickerType()) {
                        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(box.getPickerUserID());
                        if (player != null && player.isEnable() && player.where() == box.where()) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new LegacyBoxDisappearNotify(box.getID(), box.getLocationY()));
                        }
                    } else {
                        MapSynchronousInfoBroadcast.getInstance().put(box.where(), new LegacyBoxDisappearNotify(box.getID(), box.getLocationY()), false, 0);
                    }
                }
            }
        }
    }

    class GoodsDistributeCheck extends TimerTask {

        @Override
        public void run() {
            synchronized (MonsterLegacyManager.this.distributeGoodsTable) {
                Iterator<DistributeGoods> distributeGoodsSet = MonsterLegacyManager.this.distributeGoodsTable.values().iterator();
                long currentTime = System.currentTimeMillis();
                while (distributeGoodsSet.hasNext()) {
                    DistributeGoods distributeGoods = distributeGoodsSet.next();
                    if (currentTime - distributeGoods.distributeTime >= 93000L) {
                        MonsterLegacyManager.this.distributeGoodsTable.remove(distributeGoods.id);
                        distributeGoods.hasOperated = true;
                        if (distributeGoods.pickerUserID != 0) {
                            HeroPlayer gettor = PlayerServiceImpl.getInstance().getPlayerByUserID(distributeGoods.pickerUserID);
                            if (GoodsServiceImpl.getInstance().addGoods2Package(gettor, distributeGoods.goods, distributeGoods.number, CauseLog.DROP) != null) {
                                distributeGoods.box.removeNormalGoods(distributeGoods);
                                ChatQueue.getInstance().addGoodsMsg(gettor, "\u4f60\u4ee5" + distributeGoods.maxRandom + "\u70b9\u8d62\u5f97\u4e86", distributeGoods.goods.getName(), distributeGoods.goods.getTrait().getViewRGB(), distributeGoods.number);
                                if (distributeGoods.box.isEmpty()) {
                                    distributeGoods.box.where().getLegacyBoxList().remove(distributeGoods.box);
                                    AbsResponseMessage msg = new LegacyBoxDisappearNotify(distributeGoods.box.getID(), distributeGoods.box.getLocationY());
                                    for (final HeroPlayer player : distributeGoods.box.getVisitorList()) {
                                        if (player.isEnable() && player.where() == distributeGoods.box.where()) {
                                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                                        }
                                    }
                                } else {
                                    for (final HeroPlayer player2 : distributeGoods.box.getVisitorList()) {
                                        if (distributeGoods.box.statusIsChanged(player2.getUserID())) {
                                            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new LegacyBoxStatusDisappearNotify(distributeGoods.box.getID(), distributeGoods.box.getLocationY()));
                                        }
                                    }
                                }
                            }
                            ChatServiceImpl.getInstance().sendGroupGoods(gettor.getGroupID(), String.valueOf(gettor.getName()) + distributeGoods.maxRandom + "\u70b9\u8d62\u5f97\u4e86", distributeGoods.goods, distributeGoods.number, true, gettor.getUserID());
                        } else {
                            ChatServiceImpl.getInstance().sendGroupGoods(distributeGoods.box.getGroupID(), "\u6240\u6709\u4eba\u90fd\u653e\u5f03\u4e86", distributeGoods.goods, distributeGoods.number, false, 0);
                        }
                    }
                }
            }
            // monitorexit(MonsterLegacyManager.access$1(this.this$0))
        }
    }
}
