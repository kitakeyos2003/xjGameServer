// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.service;

import hero.map.Map;
import hero.share.message.Warning;
import hero.fight.message.MpRefreshNotify;
import hero.fight.message.HpRefreshNotify;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.share.EObjectLevel;
import hero.expressions.service.CEService;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.define.EClan;
import hero.map.service.MapServiceImpl;
import hero.map.EMapType;
import hero.dungeon.Dungeon;
import hero.player.HeroPlayer;
import javolution.util.FastList;
import java.util.TimerTask;

public class TransmitterOfDungeon extends TimerTask {

    private FastList<ObjectWhillBeTransmit> waitingQueue;
    private static TransmitterOfDungeon instance;
    private static final String TRANSMIT_TIP = "\u79d2\u540e\u88ab\u79fb\u51fa\u526f\u672c";
    private static final int MAX_TIME_OF_WAITING = 30000;
    public static final int START_RELAY = 15000;
    public static final short CHECK_PERIOD = 5000;

    private TransmitterOfDungeon() {
        this.waitingQueue = (FastList<ObjectWhillBeTransmit>) new FastList();
    }

    public static TransmitterOfDungeon getInstance() {
        if (TransmitterOfDungeon.instance == null) {
            TransmitterOfDungeon.instance = new TransmitterOfDungeon();
        }
        return TransmitterOfDungeon.instance;
    }

    public void add(final HeroPlayer _player) {
        if (_player != null) {
            Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(_player.getUserID());
            if (dungeon != null) {
                this.waitingQueue.add(new ObjectWhillBeTransmit(_player, dungeon));
            }
        }
    }

    @Override
    public void run() {
        int i = 0;
        while (i < this.waitingQueue.size()) {
            ObjectWhillBeTransmit info = (ObjectWhillBeTransmit) this.waitingQueue.get(i);
            HeroPlayer player = info.player;
            if (!player.isEnable()) {
                this.waitingQueue.remove(i);
                DungeonServiceImpl.getInstance().playerLeftDungeon(player);
            } else if (player.getGroupID() > 0) {
                this.waitingQueue.remove(i);
            } else {
                Map where = player.where();
                if (where.getMapType() == EMapType.GENERIC || info.dungeon.getGroupID() == player.getGroupID()) {
                    this.waitingQueue.remove(i);
                } else {
                    ObjectWhillBeTransmit objectWhillBeTransmit = info;
                    objectWhillBeTransmit.traceTime -= 5000;
                    if (info.traceTime <= 0) {
                        this.waitingQueue.remove(i);
                        Map destinationMap = MapServiceImpl.getInstance().getNormalMapByID(where.getTargetMapIDAfterUseGoods());
                        if (player.getClan() == EClan.HE_MU_DU) {
                            destinationMap = MapServiceImpl.getInstance().getNormalMapByID(where.getMozuTargetMapIDAfterUseGoods());
                        }
                        if (destinationMap == null || destinationMap.getID() == where.getID()) {
                            continue;
                        }
                        player.setCellX(destinationMap.getBornX());
                        player.setCellY(destinationMap.getBornY());
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapBottomData(player, destinationMap, where));
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapGameObjectList(player.getLoginInfo().clientType, destinationMap));
                        DungeonServiceImpl.getInstance().playerLeftDungeon(player);
                        player.gotoMap(destinationMap);
                        if (!player.isDead()) {
                            continue;
                        }
                        int stamina = CEService.hpByStamina(CEService.playerBaseAttribute(player.getLevel(), player.getVocation().getStaminaCalPara()), player.getLevel(), player.getObjectLevel().getHpCalPara());
                        player.setHp(stamina);
                        player.setMp(CEService.mpByInte(CEService.playerBaseAttribute(player.getLevel(), player.getVocation().getInteCalcPara()), player.getLevel(), EObjectLevel.NORMAL.getMpCalPara()));
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SpecialStatusChangeNotify(player.getObjectType().value(), player.getID(), (byte) 3));
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new HpRefreshNotify(player.getObjectType().value(), player.getID(), player.getHp(), player.getHp(), false, false));
                        MpRefreshNotify notify = new MpRefreshNotify(player.getObjectType().value(), player.getID(), player.getMp(), false);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), notify);
                        player.revive(null);
                    } else {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning(String.valueOf(info.traceTime / 1000) + "\u79d2\u540e\u88ab\u79fb\u51fa\u526f\u672c"));
                        ++i;
                    }
                }
            }
        }
    }

    public void remove(final int _playerUserID) {
        for (int i = 0; i < this.waitingQueue.size(); ++i) {
            if (((ObjectWhillBeTransmit) this.waitingQueue.get(i)).player.getUserID() == _playerUserID) {
                this.waitingQueue.remove(i);
                return;
            }
        }
    }

    class ObjectWhillBeTransmit {

        HeroPlayer player;
        int traceTime;
        Dungeon dungeon;

        ObjectWhillBeTransmit(final HeroPlayer _player, final Dungeon _dungeon) {
            this.player = _player;
            this.dungeon = _dungeon;
            this.traceTime = 30000;
        }
    }
}
