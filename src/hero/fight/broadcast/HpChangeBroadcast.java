// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.broadcast;

import java.util.ArrayList;
import hero.share.ME2GameObject;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.service.ME2ObjectList;
import hero.group.service.GroupServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.fight.message.HpRefreshNotify;
import hero.fight.HpChange;
import hero.map.broadcast.ME2ArrayList;
import hero.map.broadcast.IBroadcastThread;

public class HpChangeBroadcast extends IBroadcastThread {

    private static ME2ArrayList list;

    static {
        HpChangeBroadcast.list = new ME2ArrayList();
    }

    @Override
    public void broadcast() {
        int nums = HpChangeBroadcast.list.size();
        for (int j = 0; j < nums; ++j) {
            try {
                HpChange hpOrMpChange = (HpChange) HpChangeBroadcast.list.get(j);
                ME2ObjectList mapPlayerList = hpOrMpChange.where.getPlayerList();
                AbsResponseMessage message = new HpRefreshNotify(hpOrMpChange.changerObjectType, hpOrMpChange.changerID, hpOrMpChange.currentHp, hpOrMpChange.changeHpValue, false, false);
                HeroPlayer player = null;
                for (int i = 0; i < mapPlayerList.size(); ++i) {
                    player = (HeroPlayer) mapPlayerList.get(i);
                    if (player.getID() != hpOrMpChange.changerID) {
                        if (player.getID() != hpOrMpChange.triggerID) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), message);
                            GroupServiceImpl.getInstance().groupMemberListHpMpNotify(player);
                        }
                    }
                }
                player = null;
                hpOrMpChange = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HpChangeBroadcast.list.remove(0, nums);
    }

    @Override
    public long getExcuteInterval() {
        return 150L;
    }

    @Override
    public long getStartTime() {
        return 10000L;
    }

    public static void put(final ME2GameObject _s, final ME2GameObject _t, final int _change) {
        HpChange change = new HpChange();
        change.triggerID = _s.getID();
        change.changerID = _t.getID();
        change.changerObjectType = _t.getObjectType().value();
        change.changerVocationType = _t.getVocation().getType();
        change.currentHp = _t.getHp();
        change.changeHpValue = _change;
        change.where = _t.where();
        HpChangeBroadcast.list.add(change);
    }
}
