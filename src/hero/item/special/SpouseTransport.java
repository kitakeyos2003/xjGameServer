// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.map.Map;
import hero.log.service.LogServiceImpl;
import hero.share.message.Warning;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.ResponseMapBottomData;
import yoyo.core.queue.ResponseMessageQueue;
import hero.map.service.MapServiceImpl;
import hero.map.EMapType;
import hero.player.service.PlayerServiceImpl;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class SpouseTransport extends SpecialGoods {

    public SpouseTransport(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean res = false;
        if (!_player.isSelling()) {
            if (_player.marryed && _player.spouse.trim().length() > 0) {
                HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(_player.spouse);
                if (!other.isSelling()) {
                    if (other.isEnable()) {
                        if (other.where().getMapType() == EMapType.GENERIC) {
                            Map entranceMap = MapServiceImpl.getInstance().getNormalMapByID(other.where().getID());
                            _player.setCellX(other.getCellX());
                            _player.setCellY(other.getCellY());
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, entranceMap, _player.where()));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, entranceMap));
                            _player.gotoMap(entranceMap);
                            EffectServiceImpl.getInstance().sendEffectList(_player, entranceMap);
                            res = true;
                        } else {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u526f\u672c\u4e0d\u80fd\u4f20\u9001", (byte) 1));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u5728\u7ebf\uff0c\u4e0d\u80fd\u4f20\u9001", (byte) 1));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u6b63\u5728\u6446\u644a\uff0c\u4e0d\u80fd\u4f20\u9001\uff01"));
                }
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u8fd8\u6ca1\u6709\u7ed3\u5a5a\uff0c\u4e0d\u80fd\u4f20\u9001", (byte) 1));
            }
            if (res) {
                LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getGoodsType().getDescription());
            }
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6446\u644a\u72b6\u6001\u4e2d\u4e0d\u80fd\u4f7f\u7528\u6b64\u529f\u80fd"));
        }
        return res;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.SPOUSE_TRANSPORT;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public void initDescription() {
    }
}
