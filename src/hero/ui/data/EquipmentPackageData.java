// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.data;

import hero.item.EquipmentInstance;
import java.io.IOException;
import hero.item.PetArmor;
import hero.item.PetWeapon;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.Armor;
import hero.item.Weapon;
import hero.expressions.service.CEService;
import yoyo.tools.YOYOOutputStream;
import hero.item.bag.EquipmentContainer;
import org.apache.log4j.Logger;

public class EquipmentPackageData {

    private static Logger log;

    static {
        EquipmentPackageData.log = Logger.getLogger((Class) EquipmentPackageData.class);
    }

    public static byte[] getData(final EquipmentContainer _equipmentPackage, final String _tabName) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte((byte) 1);
            EquipmentInstance[] equipmentDataList = _equipmentPackage.getEquipmentList();
            output.writeUTF(_tabName);
            output.writeByte(equipmentDataList.length);
            EquipmentPackageData.log.debug((Object) ("@@@ player equip bag fullnumber = " + _equipmentPackage.getFullGridNumber()));
            output.writeByte(_equipmentPackage.getFullGridNumber());
            for (int i = 0; i < equipmentDataList.length; ++i) {
                EquipmentInstance instance = equipmentDataList[i];
                int money = 0;
                if (instance != null) {
                    money = CEService.sellPriceOfEquipment(instance.getArchetype().getSellPrice(), instance.getCurrentDurabilityPoint(), instance.getArchetype().getMaxDurabilityPoint());
                    output.writeByte(i);
                    output.writeInt(instance.getInstanceID());
                    output.writeShort(instance.getArchetype().getIconID());
                    StringBuffer name = new StringBuffer();
                    name.append(instance.getArchetype().getName());
                    int level = instance.getGeneralEnhance().getLevel();
                    if (level > 0) {
                        name.append("+");
                        name.append(level);
                    }
                    int flash = instance.getGeneralEnhance().getFlash();
                    if (flash > 0) {
                        name.append("(\u95ea");
                        name.append(flash);
                        name.append(")");
                    }
                    output.writeUTF(name.toString());
                    EquipmentPackageData.log.debug((Object) ("player body equip = " + instance.getArchetype().getName()));
                    if (instance.getArchetype() instanceof Weapon) {
                        output.writeByte(1);
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(money);
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
                        output.writeShort(instance.getGeneralEnhance().getFlashView()[0]);
                        output.writeShort(instance.getGeneralEnhance().getFlashView()[1]);
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; ++j) {
                            if (instance.getGeneralEnhance().detail[j][0] == 1) {
                                output.writeByte(instance.getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                output.writeByte(0);
                            }
                        }
                    } else if (instance.getArchetype() instanceof Armor) {
                        output.writeByte(2);
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(money);
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
                        if (instance.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
                            output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[0]);
                            output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[1]);
                        } else {
                            output.writeShort(-1);
                            output.writeShort(-1);
                        }
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; ++j) {
                            if (instance.getGeneralEnhance().detail[j][0] == 1) {
                                output.writeByte(instance.getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                output.writeByte(0);
                            }
                        }
                    } else if (instance.getArchetype() instanceof PetWeapon) {
                        EquipmentPackageData.log.debug((Object) "## EquipmentPackageData \u5ba0\u7269\u722a\u90e8");
                        output.writeByte(3);
                        int size = instance.getArchetype().getFixPropertyBytes().length;
                        EquipmentPackageData.log.debug((Object) ("pet weapon property size = " + size));
                        output.writeInt(size);
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeInt(money);
                        EquipmentPackageData.log.debug((Object) "## EquipmentPackageData \u5ba0\u7269\u722a\u90e8  end ");
                    } else if (instance.getArchetype() instanceof PetArmor) {
                        EquipmentPackageData.log.debug((Object) "## EquipmentPackageData \u5ba0\u7269\u9632\u5177");
                        output.writeByte(4);
                        int size = instance.getArchetype().getFixPropertyBytes().length;
                        EquipmentPackageData.log.debug((Object) ("pet armor property size = " + size));
                        output.writeInt(size);
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeInt(money);
                        EquipmentPackageData.log.debug((Object) "## EquipmentPackageData \u5ba0\u7269\u9632\u5177  end");
                    }
                }
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static byte[] getStorageData(final EquipmentContainer _equipmentPackage) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte((byte) 1);
            EquipmentInstance[] equipmentDataList = _equipmentPackage.getEquipmentList();
            output.writeByte(equipmentDataList.length);
            output.writeByte(_equipmentPackage.getFullGridNumber());
            for (int i = 0; i < equipmentDataList.length; ++i) {
                EquipmentInstance instance = equipmentDataList[i];
                int money = 0;
                if (instance != null) {
                    money = CEService.sellPriceOfEquipment(instance.getArchetype().getSellPrice(), instance.getCurrentDurabilityPoint(), instance.getArchetype().getMaxDurabilityPoint());
                    output.writeByte(i);
                    output.writeInt(instance.getInstanceID());
                    output.writeShort(instance.getArchetype().getIconID());
                    output.writeUTF(instance.getArchetype().getName());
                    if (instance.getArchetype() instanceof Weapon) {
                        output.writeByte(1);
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(money);
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
                        output.writeShort(instance.getGeneralEnhance().getFlashView()[0]);
                        output.writeShort(instance.getGeneralEnhance().getFlashView()[1]);
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; ++j) {
                            if (instance.getGeneralEnhance().detail[j][0] == 1) {
                                output.writeByte(instance.getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                output.writeByte(0);
                            }
                        }
                    } else {
                        output.writeByte(2);
                        output.writeBytes(instance.getArchetype().getFixPropertyBytes());
                        output.writeByte(instance.isBind());
                        output.writeByte(instance.existSeal());
                        output.writeShort(instance.getCurrentDurabilityPoint());
                        output.writeInt(money);
                        output.writeUTF(instance.getGeneralEnhance().getUpEndString());
                        output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[0]);
                        output.writeShort(instance.getGeneralEnhance().getArmorFlashView()[1]);
                        output.writeByte(instance.getGeneralEnhance().detail.length);
                        for (int j = 0; j < instance.getGeneralEnhance().detail.length; ++j) {
                            if (instance.getGeneralEnhance().detail[j][0] == 1) {
                                output.writeByte(instance.getGeneralEnhance().detail[j][1] + 1);
                            } else {
                                output.writeByte(0);
                            }
                        }
                    }
                }
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }
}
