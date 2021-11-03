// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.npc.Npc;
import hero.map.Map;
import hero.task.service.TaskServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import hero.map.service.MapServiceImpl;
import hero.item.service.GoodsDAO;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.bag.exception.BagException;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import hero.item.dictionary.GoodsContents;

public class SpecialGoodsService {

    private static WorldHorn worldHorn;

    static {
        SpecialGoodsService.worldHorn = (WorldHorn) GoodsContents.getGoods(52031);
    }

    private SpecialGoodsService() {
    }

    public static boolean chatInWorld(final HeroPlayer _player) {
        int firstGoodsGridIndex = _player.getInventory().getSpecialGoodsBag().getFirstGridIndex(SpecialGoodsService.worldHorn.getID());
        if (firstGoodsGridIndex >= 0) {
            try {
                GoodsServiceImpl.getInstance().deleteOne(_player, _player.getInventory().getSpecialGoodsBag(), SpecialGoodsService.worldHorn.getID(), CauseLog.WORLDCHAT);
                return true;
            } catch (BagException ex) {
                return false;
            }
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Thiếu loa nhỏ", (byte) 0));
        return false;
    }

    public static void operateSoulGoods(final HeroPlayer _player, final byte _operationType, final byte _locationOfBag) {
        try {
            switch (_operationType) {
                case 1: {
                    _player.setHomeID(_player.where().getID());
                    GoodsDAO.updateHome(_player.getUserID(), _player.where().getID());
                    ((SoulMark) GoodsContents.getGoods(52071)).remove(_player, _locationOfBag);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Nhận %fx phút gấp đôi thời gian trải nghiệm", (byte) 0));
                    break;
                }
                case 2: {
                    ((SoulChannel) GoodsContents.getGoods(52072)).remove(_player, _locationOfBag);
                    Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(_player.getHomeID());
                    _player.setCellX(targetMap.getBornX());
                    _player.setCellY(targetMap.getBornY());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, targetMap, _player.where()));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, targetMap));
                    _player.gotoMap(targetMap);
                    EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                    Npc escortNpc = _player.getEscortTarget();
                    if (escortNpc != null) {
                        TaskServiceImpl.getInstance().endEscortNpcTask(_player, escortNpc);
                        break;
                    }
                    break;
                }
            }
        } catch (BagException ex) {
        }
    }
}
