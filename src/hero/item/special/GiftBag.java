// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.item.Goods;
import hero.item.SpecialGiftBagData;
import hero.log.service.LogServiceImpl;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import hero.item.detail.EGoodsType;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.dictionary.GoodsContents;
import hero.item.dictionary.SpecialGoodsDict;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class GiftBag extends SpecialGoods {

    public GiftBag(final int _id, final short nums) {
        super(_id, nums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean remove = false;
        SpecialGiftBagData giftBag = SpecialGoodsDict.getInstance().getBagData(this.getID());
        EGoodsType goodsType = null;
        int equipment = 0;
        int taskTool = 0;
        int medicament = 0;
        int material = 0;
        int special = 0;
        for (int i = 0; i < giftBag.goodsSum; ++i) {
            goodsType = GoodsContents.getGoodsType(giftBag.goodsList[i]);
            if (goodsType != null) {
                switch (goodsType) {
                    case EQUIPMENT: {
                        ++equipment;
                        break;
                    }
                    case MEDICAMENT: {
                        ++medicament;
                        break;
                    }
                    case MATERIAL: {
                        ++material;
                        break;
                    }
                    case TASK_TOOL: {
                        ++taskTool;
                        break;
                    }
                    case SPECIAL_GOODS: {
                        ++special;
                    }
                }
            }
        }
        boolean equipEmpty = true;
        boolean medicamentEmpty = true;
        boolean materialEmpty = true;
        boolean taskEmpty = true;
        boolean specialEmpty = true;
        int empty = _player.getInventory().getEquipmentBag().getEmptyGridNumber();
        if (empty < equipment) {
            equipEmpty = false;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("%fn\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440".replaceAll("%fn", EGoodsType.EQUIPMENT.getDescription())));
        }
        empty = _player.getInventory().getMedicamentBag().getEmptyGridNumber();
        if (empty < medicament) {
            medicamentEmpty = false;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("%fn\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440".replaceAll("%fn", EGoodsType.MEDICAMENT.getDescription())));
        }
        empty = _player.getInventory().getMaterialBag().getEmptyGridNumber();
        if (empty < material) {
            materialEmpty = false;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("%fn\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440".replaceAll("%fn", EGoodsType.MATERIAL.getDescription())));
        }
        empty = _player.getInventory().getTaskToolBag().getEmptyGridNumber();
        if (empty < taskTool) {
            taskEmpty = false;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("%fn\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440".replaceAll("%fn", EGoodsType.MATERIAL.getDescription())));
        }
        empty = _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber();
        if (empty < special) {
            specialEmpty = false;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("%fn\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440".replaceAll("%fn", EGoodsType.SPECIAL_GOODS.getDescription())));
        }
        if (equipEmpty && medicamentEmpty && materialEmpty && taskEmpty && specialEmpty) {
            Goods goods = null;
            for (int j = 0; j < giftBag.goodsSum; ++j) {
                goods = GoodsContents.getGoods(giftBag.goodsList[j]);
                GoodsServiceImpl.getInstance().addGoods2Package(_player, goods, giftBag.numberList[j], CauseLog.OPENGIFTBAG);
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseSpecialGoodsBag(_player.getInventory().getSpecialGoodsBag(), _player.getShortcutKeyList()));
            remove = true;
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        } else {
            remove = false;
        }
        return remove;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.GIFT_BAG;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }
}
