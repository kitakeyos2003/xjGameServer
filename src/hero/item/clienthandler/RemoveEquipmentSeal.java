// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.item.EquipmentInstance;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.RemoveEquipmentSealNotify;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.service.GoodsDAO;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.SealPray;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RemoveEquipmentSeal extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        byte gridIndex = this.yis.readByte();
        int equipmentInsID = this.yis.readInt();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        EquipmentInstance ei = player.getInventory().getEquipmentBag().get(gridIndex);
        if (ei != null && ei.getInstanceID() == equipmentInsID) {
            if (ei.existSeal()) {
                int sealPrayID = SealPray.getValidatePrayID(ei.getArchetype().getNeedLevel());
                int firstSealPrayGridIndex = player.getInventory().getSpecialGoodsBag().getFirstGridIndex(sealPrayID);
                if (firstSealPrayGridIndex >= 0) {
                    ei.setSeal(false);
                    GoodsServiceImpl.getInstance().deleteOne(player, player.getInventory().getSpecialGoodsBag(), sealPrayID, CauseLog.REMOVESEAL);
                    GoodsDAO.removeEquipmentSeal(ei);
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new RemoveEquipmentSealNotify(gridIndex, equipmentInsID));
                } else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u7f3a\u5c11\u5408\u9002\u7684\u88c5\u5907\u795d\u798f\u4e4b\u5149", (byte) 0));
                }
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u88c5\u5907\u6ca1\u6709\u5c01\u5370", (byte) 0));
            }
        }
    }
}
