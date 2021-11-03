// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.item.message.RefreshEquipmentDurabilityPoint;
import hero.item.service.GoodsServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.EquipmentInstance;
import java.util.ArrayList;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class EquipmentRepairSolvent extends SpecialGoods {

    public EquipmentRepairSolvent(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean needRefreshPlayerProperty = false;
        ArrayList<EquipmentInstance> needRepairList = new ArrayList<EquipmentInstance>();
        int money = 0;
        EquipmentInstance[] equipmentList;
        for (int length = (equipmentList = _player.getBodyWear().getEquipmentList()).length, i = 0; i < length; ++i) {
            EquipmentInstance ei = equipmentList[i];
            if (ei != null) {
                money += ei.getRepairCharge();
            }
        }
        if (money > _player.getMoney()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f"));
            return false;
        }
        EquipmentInstance[] equipmentList2;
        for (int length2 = (equipmentList2 = _player.getBodyWear().getEquipmentList()).length, j = 0; j < length2; ++j) {
            EquipmentInstance ei = equipmentList2[j];
            if (ei != null) {
                int currentDurabilityPoint = ei.getCurrentDurabilityPoint();
                if (currentDurabilityPoint < ei.getArchetype().getMaxDurabilityPoint()) {
                    needRepairList.add(ei);
                    if (currentDurabilityPoint == 0) {
                        needRefreshPlayerProperty = true;
                    }
                }
            }
        }
        if (needRepairList.size() == 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u88c5\u5907\u4e1d\u6beb\u65e0\u635f", (byte) 0));
            return false;
        }
        GoodsServiceImpl.getInstance().restoreEquipmentDurability(needRepairList);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new RefreshEquipmentDurabilityPoint(_player.getBodyWear()));
        if (needRefreshPlayerProperty) {
            PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
            PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
        }
        PlayerServiceImpl.getInstance().addMoney(_player, -money, 1.0f, 0, "\u4fee\u7406\u88c5\u5907\u82b1\u8d39");
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u82b1\u8d39\u4e86%fmoney\u91d1".replaceAll("%fmoney", String.valueOf(money))));
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.EQUIPMENT_REPAIR;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
