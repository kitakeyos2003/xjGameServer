// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.player.service.PlayerServiceImpl;
import hero.item.message.RefreshEquipmentDurabilityPoint;
import hero.item.service.GoodsServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.EquipmentInstance;
import java.util.ArrayList;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.share.service.Tip;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.BaseNpcFunction;

public class Repair extends BaseNpcFunction {

    private static final short[] mainMenuMarkImageIDList;

    static {
        mainMenuMarkImageIDList = new short[]{1003};
    }

    public Repair(final int _npcID) {
        super(_npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.REPAIR;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < Tip.FUNCTION_NPC_REPAIR_MAIN.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = Tip.FUNCTION_NPC_REPAIR_MAIN[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        int repairCharge = 0;
        boolean needRefreshPlayerProperty = false;
        ArrayList<EquipmentInstance> needRepairList = new ArrayList<EquipmentInstance>();
        EquipmentInstance[] eiList = _player.getBodyWear().getEquipmentList();
        int money = 0;
        EquipmentInstance[] array;
        for (int length = (array = eiList).length, i = 0; i < length; ++i) {
            EquipmentInstance ei = array[i];
            if (ei != null) {
                money = ei.getRepairCharge();
                if (money > 0) {
                    needRepairList.add(ei);
                    repairCharge += money;
                }
                if (ei.getCurrentDurabilityPoint() == 0) {
                    needRefreshPlayerProperty = true;
                }
            }
        }
        eiList = _player.getInventory().getEquipmentBag().getEquipmentList();
        EquipmentInstance[] array2;
        for (int length2 = (array2 = eiList).length, j = 0; j < length2; ++j) {
            EquipmentInstance ei = array2[j];
            if (ei != null) {
                money = ei.getRepairCharge();
                if (money > 0) {
                    needRepairList.add(ei);
                    repairCharge += money;
                }
            }
        }
        if (repairCharge > 0) {
            if (repairCharge > _player.getMoney()) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f"));
            } else {
                GoodsServiceImpl.getInstance().restoreEquipmentDurability(needRepairList);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new RefreshEquipmentDurabilityPoint(_player.getBodyWear()));
                PlayerServiceImpl.getInstance().addMoney(_player, -repairCharge, 1.0f, 0, "\u4fee\u7406\u88c5\u5907\u82b1\u8d39");
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u82b1\u8d39\u4e86%fmoney\u91d1".replaceAll("%fmoney", String.valueOf(repairCharge))));
                if (needRefreshPlayerProperty) {
                    PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
                    PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
                }
            }
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u88c5\u5907\u672a\u635f\u574f"));
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }
}
