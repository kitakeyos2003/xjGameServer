// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.item.Goods;
import hero.log.service.FlowLog;
import hero.log.service.LogServiceImpl;
import hero.share.message.Warning;
import hero.item.bag.EquipmentContainer;
import hero.log.service.CauseLog;
import hero.log.service.LoctionLog;
import hero.item.service.GoodsServiceImpl;
import hero.pet.message.ResponseRefreshPetProperty;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.item.message.PetClothesOrWeaponChangeNotify;
import hero.item.EquipmentInstance;
import hero.ui.message.ResponseEuipmentPackageChange;
import hero.item.bag.EBagType;
import hero.item.service.GoodsDAO;
import hero.pet.service.PetServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponsePetEquipmentBag;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class OperatePetEquipmentBag extends AbsClientProcess {

    private static Logger log;
    private static final byte LIST_BAG = 1;
    private static final byte UNLOAD = 2;
    private static final byte DICE_BODY_EQUIPMENT = 3;
    private static final byte WEAR = 4;
    private static final byte SORT = 5;
    private static final byte DICE_BAG_EQUIPMENT = 6;

    static {
        OperatePetEquipmentBag.log = Logger.getLogger((Class) OperatePetEquipmentBag.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte operation = this.yis.readByte();
            switch (operation) {
                case 1: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetEquipmentBag(player.getInventory().getPetEquipmentBag()));
                    break;
                }
                case 2: {
                    int petID = this.yis.readInt();
                    byte gridIndex = this.yis.readByte();
                    int equipmentInsID = this.yis.readInt();
                    OperatePetEquipmentBag.log.debug((Object) ("pet unload equip gridindex = " + gridIndex + "  equipmentInsid=" + equipmentInsID));
                    Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                    if (pet == null) {
                        break;
                    }
                    EquipmentInstance unei = pet.getPetBodyWear().get(gridIndex);
                    if (unei == null || unei.getInstanceID() != equipmentInsID) {
                        break;
                    }
                    OperatePetEquipmentBag.log.debug((Object) ("pet unload unei id= " + unei.getInstanceID()));
                    int bagGridIndex = player.getInventory().getPetEquipmentBag().add(unei);
                    OperatePetEquipmentBag.log.debug((Object) ("bag grid index  = " + bagGridIndex));
                    if (bagGridIndex != -1) {
                        pet.getPetBodyWear().remove(unei);
                        GoodsDAO.changeEquipmentLocation(unei.getInstanceID(), (byte) 3, bagGridIndex);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.PET_EQUIPMENT_BAG.getTypeValue(), bagGridIndex, unei));
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.PET_BODY_WEAR.getTypeValue(), gridIndex, null));
                        AbsResponseMessage msg = new PetClothesOrWeaponChangeNotify(pet.id, (byte) unei.getArchetype().getWearBodyPart().value(), unei.getArchetype().getIconID());
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                        MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true, player.getID());
                        PetServiceImpl.getInstance().updatePetEquipment(pet);
                        GoodsDAO.changeEquipmentLocation(unei.getInstanceID(), (byte) 4, -1);
                        PetServiceImpl.getInstance().reCalculatePetProperty(pet);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRefreshPetProperty(pet));
                        break;
                    }
                    break;
                }
                case 3: {
                    int petID = this.yis.readInt();
                    byte gridIndex = this.yis.readByte();
                    int equipmentInsID = this.yis.readInt();
                    Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                    if (pet == null) {
                        break;
                    }
                    EquipmentInstance unei = pet.getPetBodyWear().get(gridIndex);
                    if (unei != null && unei.getInstanceID() == equipmentInsID && unei != null && unei.getInstanceID() == equipmentInsID) {
                        GoodsServiceImpl.getInstance().diceEquipmentOfBag(pet, player, pet.getPetBodyWear(), unei, LoctionLog.PET_BAG, CauseLog.DEL);
                        AbsResponseMessage msg2 = new PetClothesOrWeaponChangeNotify(player.getID(), (byte) unei.getArchetype().getWearBodyPart().value(), unei.getArchetype().getIconID());
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg2);
                        MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg2, true, player.getID());
                        PetServiceImpl.getInstance().updatePetEquipment(pet);
                        GoodsDAO.changeEquipmentLocation(unei.getInstanceID(), (byte) 4, -1);
                        PetServiceImpl.getInstance().reCalculatePetProperty(pet);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRefreshPetProperty(pet));
                        break;
                    }
                    break;
                }
                case 4: {
                    int petID = this.yis.readInt();
                    byte gridIndex = this.yis.readByte();
                    int equipmentInsID = this.yis.readInt();
                    Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                    if (pet == null) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff01", (byte) 0));
                        break;
                    }
                    if (pet.pk.getStage() != 2 || pet.pk.getType() != 2) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff01\u53ea\u6709\u6210\u5e74\u8089\u98df\u5ba0\u7269\u624d\u80fd\u88c5\u5907\uff01", (byte) 0));
                        break;
                    }
                    EquipmentInstance ei = player.getInventory().getPetEquipmentBag().get(gridIndex);
                    if (ei == null || ei.getInstanceID() != equipmentInsID) {
                        break;
                    }
                    if (pet.level >= ei.getArchetype().getNeedLevel()) {
                        player.getInventory().getPetEquipmentBag().remove(gridIndex);
                        EquipmentInstance uei = pet.getPetBodyWear().wear(ei);
                        if (!ei.isBind() && ei.getArchetype().getBindType() == 2) {
                            ei.bind();
                            GoodsDAO.bindEquipment(ei);
                        }
                        if (uei != null) {
                            player.getInventory().getPetEquipmentBag().add(gridIndex, uei);
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.PET_EQUIPMENT_BAG.getTypeValue(), gridIndex, uei));
                            GoodsDAO.changeEquipmentLocation(uei.getInstanceID(), (byte) 3, gridIndex);
                            LogServiceImpl.getInstance().goodsChangeLog(player, uei.getArchetype(), 1, LoctionLog.PET_BODY, FlowLog.LOSE, CauseLog.UNLOAD);
                            LogServiceImpl.getInstance().goodsChangeLog(player, uei.getArchetype(), 1, LoctionLog.PET_BAG, FlowLog.GET, CauseLog.UNLOAD);
                        } else {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.PET_EQUIPMENT_BAG.getTypeValue(), gridIndex, null));
                        }
                        LogServiceImpl.getInstance().goodsChangeLog(player, ei.getArchetype(), 1, LoctionLog.PET_BODY, FlowLog.GET, CauseLog.WEAR);
                        LogServiceImpl.getInstance().goodsChangeLog(player, ei.getArchetype(), 1, LoctionLog.PET_BAG, FlowLog.LOSE, CauseLog.WEAR);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.PET_BODY_WEAR.getTypeValue(), pet.getPetBodyWear().indexOf(ei), ei));
                        if (uei == null || uei.getArchetype().getIconID() != ei.getArchetype().getIconID()) {
                            OperatePetEquipmentBag.log.debug((Object) ("pet wear equipment body part = " + ei.getArchetype().getWearBodyPart()));
                            AbsResponseMessage msg = new PetClothesOrWeaponChangeNotify(pet.id, (byte) ei.getArchetype().getWearBodyPart().value(), ei.getArchetype().getIconID());
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), msg);
                            MapSynchronousInfoBroadcast.getInstance().put(player.where(), msg, true, player.getID());
                        }
                        PetServiceImpl.getInstance().updatePetEquipment(pet);
                        GoodsDAO.changeEquipmentLocation(ei.getInstanceID(), (byte) 4, -1);
                        PetServiceImpl.getInstance().reCalculatePetProperty(pet);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRefreshPetProperty(pet));
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff01\u5ba0\u7269\u7b49\u7ea7\u4e0d\u591f\uff01", (byte) 0));
                    break;
                }
                case 5: {
                    if (player.getInventory().getPetEquipmentBag().clearUp()) {
                        GoodsDAO.clearUpEquipmentList(player.getInventory().getPetEquipmentBag().getEquipmentList());
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetEquipmentBag(player.getInventory().getPetEquipmentBag()));
                        break;
                    }
                    break;
                }
                case 6: {
                    byte gridIndex2 = this.yis.readByte();
                    int equipmentInsID2 = this.yis.readInt();
                    OperatePetEquipmentBag.log.debug((Object) ("dict pet bag equip id = " + equipmentInsID2));
                    EquipmentInstance ei2 = player.getInventory().getPetEquipmentBag().get(gridIndex2);
                    if (ei2 != null && ei2.getInstanceID() == equipmentInsID2) {
                        OperatePetEquipmentBag.log.debug((Object) ("dict pet bag equip ei id = " + ei2.getInstanceID()));
                        GoodsServiceImpl.getInstance().diceEquipmentOfBag(player, player.getInventory().getPetEquipmentBag(), ei2, LoctionLog.PET_BAG, CauseLog.DEL);
                        break;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
