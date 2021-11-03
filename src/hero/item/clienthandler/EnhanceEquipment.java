// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.item.EquipmentInstance;
import hero.player.HeroPlayer;
import hero.item.enhance.EnhanceService;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class EnhanceEquipment extends AbsClientProcess {

    private static Logger log;
    private static final byte OPERATE_PERFORATE = 0;
    private static final byte OPERATE_ENHANCE = 1;
    private static final byte OPERATE_WRECK = 2;
    private static final byte OPERATE_SEARCH = 3;

    static {
        EnhanceEquipment.log = Logger.getLogger((Class) EnhanceEquipment.class);
    }

    @Override
    public void read() throws Exception {
        int equipmentIndex = this.yis.readByte();
        byte enhanceType = this.yis.readByte();
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        EquipmentInstance ei = player.getInventory().getEquipmentBag().get(equipmentIndex);
        if (ei != null) {
            switch (enhanceType) {
                case 0: {
                    EnhanceService.getInstance().perforateEquipment(player, ei);
                    break;
                }
                case 1: {
                    int crystalID = this.yis.readInt();
                    byte jewelIndex = this.yis.readByte();
                    EnhanceService.getInstance().enhanceEquipment(player, crystalID, jewelIndex, ei);
                    break;
                }
                case 2: {
                    EnhanceEquipment.log.debug((Object) "enhanceType == 2 \u5265\u79bb\u5b9d\u77f3..");
                    byte jewelIndex2 = this.yis.readByte();
                    EnhanceEquipment.log.debug((Object) ("jewelIndex = " + jewelIndex2));
                    EnhanceService.getInstance().jewelWreck(player, ei, jewelIndex2);
                    break;
                }
                case 3: {
                    int crystalID = this.yis.readInt();
                    byte jewelIndex = this.yis.readByte();
                    EnhanceService.getInstance().enhanceQuest(player, crystalID, jewelIndex, ei);
                    break;
                }
            }
        }
    }
}
