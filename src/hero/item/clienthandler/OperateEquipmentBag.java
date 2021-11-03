// Decompiled with: Procyon 0.5.36
// Class Version: 8
package hero.item.clienthandler;

import hero.item.EqGoods;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import hero.log.service.FlowLog;
import hero.log.service.LogServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LoctionLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsType;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.share.message.RefreshObjectViewValue;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.item.message.ClothesOrWeaponChangeNotify;
import hero.item.Armor;
import hero.item.Weapon;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.EquipmentInstance;
import hero.ui.message.ResponseEuipmentPackageChange;
import hero.item.bag.EBagType;
import hero.item.service.GoodsDAO;
import hero.pet.message.ResponseWearPetGridNumber;
import hero.item.message.ResponseEquipmentBag;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class OperateEquipmentBag extends AbsClientProcess {

    private static final byte LIST_BAG = 1;
    private static final byte UNLOAD = 2;
    private static final byte DICE_BODY_EQUIPMENT = 3;
    private static final byte WEAR = 4;
    private static final byte SORT = 5;
    private static final byte DICE_BAG_EQUIPMENT = 6;

    @Override
    public void read() throws Exception {
        final HeroPlayer playerBySessionID = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            switch (this.yis.readByte()) {
                case 1: {
                    ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new ResponseEquipmentBag(playerBySessionID.getInventory().getEquipmentBag(), playerBySessionID));
                    ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new ResponseWearPetGridNumber(playerBySessionID.getBodyWearPetList()));
                    break;
                }
                case 2: {
                    try {
                        final byte byte1 = this.yis.readByte();
                        final int int1 = this.yis.readInt();
                        final EquipmentInstance value = playerBySessionID.getBodyWear().get(byte1);
                        if (value == null || value.getInstanceID() != int1) {
                            break;
                        }
                        final int add = playerBySessionID.getInventory().getEquipmentBag().add(value);
                        if (-1 != add) {
                            final EquipmentInstance remove = playerBySessionID.getBodyWear().remove(byte1);
                            GoodsDAO.changeEquipmentLocation(remove.getInstanceID(), (byte) 1, add);
                            ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.EQUIPMENT_BAG.getTypeValue(), add, remove));
                            ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.BODY_WEAR.getTypeValue(), byte1, null));
                            if (remove.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON || remove.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
                                EBodyPartOfEquipment eBodyPartOfEquipment;
                                EqGoods eqGoods;
                                short n;
                                short n2;
                                if (remove.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON) {
                                    playerBySessionID.isRemotePhysicsAttack = false;
                                    eBodyPartOfEquipment = ((Weapon) remove.getArchetype()).getWearBodyPart();
                                    eqGoods = remove.getArchetype();
                                    n = remove.getGeneralEnhance().getFlashView()[0];
                                    n2 = remove.getGeneralEnhance().getFlashView()[1];
                                } else {
                                    eBodyPartOfEquipment = ((Armor) remove.getArchetype()).getWearBodyPart();
                                    eqGoods = remove.getArchetype();
                                    n = remove.getGeneralEnhance().getArmorFlashView()[0];
                                    n2 = remove.getGeneralEnhance().getArmorFlashView()[1];
                                }
                                final ClothesOrWeaponChangeNotify clothesOrWeaponChangeNotify = new ClothesOrWeaponChangeNotify(playerBySessionID, eBodyPartOfEquipment, remove.getArchetype().getImageID(), remove.getArchetype().getAnimationID(), remove.getGeneralEnhance().getLevel(), eqGoods, true, n, n2);
                                ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), clothesOrWeaponChangeNotify);
                                MapSynchronousInfoBroadcast.getInstance().put(playerBySessionID.where(), clothesOrWeaponChangeNotify, true, playerBySessionID.getID());
                            }
                            PlayerServiceImpl.getInstance().reCalculateRoleProperty(playerBySessionID);
                            PlayerServiceImpl.getInstance().refreshRoleProperty(playerBySessionID);
                            MapSynchronousInfoBroadcast.getInstance().put(playerBySessionID.where(), new RefreshObjectViewValue(playerBySessionID), true, playerBySessionID.getID());
                            break;
                        }
                        break;
                    } catch (BagException ex3) {
                        throw PackageExceptionFactory.getInstance().getFullException(EGoodsType.EQUIPMENT);
                    }
                }
                case 3: {
                    try {
                        final byte byte2 = this.yis.readByte();
                        final int int2 = this.yis.readInt();
                        final EquipmentInstance value2 = playerBySessionID.getBodyWear().get(byte2);
                        if (value2 != null && value2.getInstanceID() == int2) {
                            GoodsServiceImpl.getInstance().diceEquipmentOfBag(playerBySessionID, playerBySessionID.getBodyWear(), value2, LoctionLog.BAG, CauseLog.DEL);
                            if (value2.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON || value2.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
                                EBodyPartOfEquipment eBodyPartOfEquipment2;
                                EqGoods eqGoods2;
                                short n3;
                                short n4;
                                if (value2.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON) {
                                    playerBySessionID.isRemotePhysicsAttack = false;
                                    eBodyPartOfEquipment2 = ((Weapon) value2.getArchetype()).getWearBodyPart();
                                    eqGoods2 = value2.getArchetype();
                                    n3 = value2.getGeneralEnhance().getFlashView()[0];
                                    n4 = value2.getGeneralEnhance().getFlashView()[1];
                                } else {
                                    eBodyPartOfEquipment2 = ((Armor) value2.getArchetype()).getWearBodyPart();
                                    eqGoods2 = value2.getArchetype();
                                    n3 = value2.getGeneralEnhance().getArmorFlashView()[0];
                                    n4 = value2.getGeneralEnhance().getArmorFlashView()[1];
                                }
                                final ClothesOrWeaponChangeNotify clothesOrWeaponChangeNotify2 = new ClothesOrWeaponChangeNotify(playerBySessionID, eBodyPartOfEquipment2, value2.getArchetype().getImageID(), value2.getArchetype().getAnimationID(), value2.getGeneralEnhance().getLevel(), eqGoods2, true, n3, n4);
                                ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), clothesOrWeaponChangeNotify2);
                                MapSynchronousInfoBroadcast.getInstance().put(playerBySessionID.where(), clothesOrWeaponChangeNotify2, true, playerBySessionID.getID());
                            }
                            PlayerServiceImpl.getInstance().reCalculateRoleProperty(playerBySessionID);
                            PlayerServiceImpl.getInstance().refreshRoleProperty(playerBySessionID);
                            MapSynchronousInfoBroadcast.getInstance().put(playerBySessionID.where(), new RefreshObjectViewValue(playerBySessionID), true, playerBySessionID.getID());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
                case 4: {
                    final byte byte3 = this.yis.readByte();
                    final int int3 = this.yis.readInt();
                    final EquipmentInstance value3 = playerBySessionID.getInventory().getEquipmentBag().get(byte3);
                    if (value3 == null) {
                        break;
                    }
                    if (value3.getInstanceID() != int3) {
                        break;
                    }
                    if (playerBySessionID.getLevel() >= value3.getArchetype().getNeedLevel() && value3.getArchetype().canBeUse(playerBySessionID.getVocation()) && !value3.existSeal()) {
                        playerBySessionID.getInventory().getEquipmentBag().remove(byte3);
                        final EquipmentInstance wear = playerBySessionID.getBodyWear().wear(value3);
                        if (!value3.isBind() && value3.getArchetype().getBindType() == 2) {
                            value3.bind();
                            GoodsDAO.bindEquipment(value3);
                        }
                        if (wear != null) {
                            playerBySessionID.getInventory().getEquipmentBag().add(byte3, wear);
                            ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.EQUIPMENT_BAG.getTypeValue(), byte3, wear));
                            GoodsDAO.changeEquipmentLocation(wear.getInstanceID(), (byte) 1, byte3);
                            LogServiceImpl.getInstance().goodsChangeLog(playerBySessionID, wear.getArchetype(), 1, LoctionLog.BODY, FlowLog.LOSE, CauseLog.UNLOAD);
                            LogServiceImpl.getInstance().goodsChangeLog(playerBySessionID, wear.getArchetype(), 1, LoctionLog.BAG, FlowLog.GET, CauseLog.UNLOAD);
                        } else {
                            ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.EQUIPMENT_BAG.getTypeValue(), byte3, null));
                        }
                        LogServiceImpl.getInstance().goodsChangeLog(playerBySessionID, value3.getArchetype(), 1, LoctionLog.BODY, FlowLog.GET, CauseLog.WEAR);
                        LogServiceImpl.getInstance().goodsChangeLog(playerBySessionID, value3.getArchetype(), 1, LoctionLog.BAG, FlowLog.LOSE, CauseLog.WEAR);
                        ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.BODY_WEAR.getTypeValue(), playerBySessionID.getBodyWear().indexOf(value3), value3));
                        if ((value3.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON || value3.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM) && GoodsServiceImpl.getInstance().changeEquimentViewDifference(wear, value3)) {
                            EBodyPartOfEquipment eBodyPartOfEquipment3;
                            EqGoods eqGoods3;
                            short n5;
                            short n6;
                            if (value3.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.WEAPON) {
                                playerBySessionID.isRemotePhysicsAttack = (((Weapon) value3.getArchetype()).getWeaponType() == Weapon.EWeaponType.TYPE_GONG);
                                eBodyPartOfEquipment3 = ((Weapon) value3.getArchetype()).getWearBodyPart();
                                eqGoods3 = value3.getArchetype();
                                n5 = value3.getGeneralEnhance().getFlashView()[0];
                                n6 = value3.getGeneralEnhance().getFlashView()[1];
                            } else {
                                eBodyPartOfEquipment3 = ((Armor) value3.getArchetype()).getWearBodyPart();
                                eqGoods3 = value3.getArchetype();
                                n5 = value3.getGeneralEnhance().getArmorFlashView()[0];
                                n6 = value3.getGeneralEnhance().getArmorFlashView()[1];
                            }
                            final ClothesOrWeaponChangeNotify clothesOrWeaponChangeNotify3 = new ClothesOrWeaponChangeNotify(playerBySessionID, eBodyPartOfEquipment3, value3.getArchetype().getImageID(), value3.getArchetype().getAnimationID(), value3.getGeneralEnhance().getLevel(), eqGoods3, false, n5, n6);
                            ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), clothesOrWeaponChangeNotify3);
                            MapSynchronousInfoBroadcast.getInstance().put(playerBySessionID.where(), clothesOrWeaponChangeNotify3, true, playerBySessionID.getID());
                        }
                        GoodsDAO.changeEquipmentLocation(value3.getInstanceID(), (byte) 2, -1);
                        PlayerServiceImpl.getInstance().reCalculateRoleProperty(playerBySessionID);
                        PlayerServiceImpl.getInstance().refreshRoleProperty(playerBySessionID);
                        MapSynchronousInfoBroadcast.getInstance().put(playerBySessionID.where(), new RefreshObjectViewValue(playerBySessionID), true, playerBySessionID.getID());
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new Warning("Không có sẵn", (byte) 0));
                    break;
                }
                case 5: {
                    ResponseMessageQueue.getInstance().put(playerBySessionID.getMsgQueueIndex(), new Warning("Tính năng này tạm thời không khả dụng"));
                    break;
                }
                case 6: {
                    final byte byte4 = this.yis.readByte();
                    final int int4 = this.yis.readInt();
                    final EquipmentInstance value4 = playerBySessionID.getInventory().getEquipmentBag().get(byte4);
                    if (value4 != null && value4.getInstanceID() == int4) {
                        GoodsServiceImpl.getInstance().diceEquipmentOfBag(playerBySessionID, playerBySessionID.getInventory().getEquipmentBag(), value4, LoctionLog.BAG, CauseLog.DEL);
                        break;
                    }
                    break;
                }
            }
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }
}
