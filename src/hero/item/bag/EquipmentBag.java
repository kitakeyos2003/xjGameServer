// Decompiled with: FernFlower
// Class Version: 6
package hero.item.bag;

import hero.item.EquipmentInstance;
import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.detail.EGoodsType;
import org.apache.log4j.Logger;

public class EquipmentBag extends EquipmentContainer {

    private static Logger log = Logger.getLogger(EquipmentBag.class);
    // $FF: synthetic field
    private static int[] $SWITCH_TABLE$hero$item$detail$EBodyPartOfEquipment;

    public EquipmentBag(short _size) {
        super(_size);
    }

    public int add(EquipmentInstance _equipmentInstance) throws BagException {
        if (this.emptyGridNumber == 0) {
            throw PackageExceptionFactory.getInstance().getFullException(EGoodsType.EQUIPMENT);
        } else {
            log.debug("向背包中添加装备 id = " + _equipmentInstance.getInstanceID());
            if (_equipmentInstance != null && _equipmentInstance.getOwnerType() == 1) {
                synchronized (this.equipmentList) {
                    for (int i = 0; i < this.equipmentList.length; ++i) {
                        if (this.equipmentList[i] == null) {
                            this.equipmentList[i] = _equipmentInstance;
                            --this.emptyGridNumber;
                            return i;
                        }
                    }

                    return -1;
                }
            } else {
                throw PackageExceptionFactory.getInstance().getException("无效的装备数据");
            }
        }
    }

    public boolean add(int _index, EquipmentInstance _equipmentInstance) throws BagException {
        if (this.emptyGridNumber == 0) {
            throw PackageExceptionFactory.getInstance().getFullException(EGoodsType.EQUIPMENT);
        } else if (_equipmentInstance != null && _equipmentInstance.getOwnerType() == 1) {
            synchronized (this.equipmentList) {
                if (this.equipmentList[_index] != null) {
                    return false;
                } else {
                    this.equipmentList[_index] = _equipmentInstance;
                    --this.emptyGridNumber;
                    return true;
                }
            }
        } else {
            throw PackageExceptionFactory.getInstance().getException("无效的装备数据");
        }
    }

    public byte getContainerType() {
        return 1;
    }

    public boolean clearUp() {
        boolean changed = false;
        EBodyPartOfEquipment bodyPart = EBodyPartOfEquipment.WEAPON;
        int i = 0;
        int j = i + 1;

        while (true) {
            while (i < this.equipmentList.length - 1) {
                if (this.equipmentList[i] != null && bodyPart == this.equipmentList[i].getArchetype().getWearBodyPart()) {
                    ++i;
                    j = i + 1;
                } else {
                    while (j < this.equipmentList.length) {
                        if (this.equipmentList[j] != null && bodyPart == this.equipmentList[j].getArchetype().getWearBodyPart()) {
                            EquipmentInstance ei = this.equipmentList[i];
                            this.equipmentList[i] = this.equipmentList[j];
                            this.equipmentList[j] = ei;
                            changed = true;
                            ++i;
                            break;
                        }

                        ++j;
                    }

                    if (j < this.equipmentList.length) {
                        ++j;
                    } else {
                        j = i + 1;
                        switch ($SWITCH_TABLE$hero$item$detail$EBodyPartOfEquipment()[bodyPart.ordinal()]) {
                            case 1:
                                bodyPart = EBodyPartOfEquipment.BOSOM;
                                break;
                            case 2:
                                bodyPart = EBodyPartOfEquipment.HAND;
                                break;
                            case 3:
                                bodyPart = EBodyPartOfEquipment.FOOT;
                                break;
                            case 4:
                                bodyPart = EBodyPartOfEquipment.FINGER;
                                break;
                            case 5:
                                bodyPart = EBodyPartOfEquipment.HEAD;
                                break;
                            case 6:
                                bodyPart = EBodyPartOfEquipment.ADORM;
                                break;
                            case 7:
                            default:
                                return changed;
                            case 8:
                                bodyPart = EBodyPartOfEquipment.WRIST;
                        }
                    }
                }
            }

            return changed;
        }
    }

    public boolean upgrade() {
        if (this.getSize() == 40) {
            return false;
        } else {
            EquipmentInstance[] newContainer = new EquipmentInstance[this.getSize() + 8];
            System.arraycopy(this.equipmentList, 0, newContainer, 0, this.getSize());
            this.equipmentList = newContainer;
            this.emptyGridNumber = (short) (this.emptyGridNumber + 8);
            return true;
        }
    }

    // $FF: synthetic method
    static int[] $SWITCH_TABLE$hero$item$detail$EBodyPartOfEquipment() {
        int[] var10000 = $SWITCH_TABLE$hero$item$detail$EBodyPartOfEquipment;
        if (var10000 != null) {
            return var10000;
        } else {
            int[] var0 = new int[EBodyPartOfEquipment.values().length];

            try {
                var0[EBodyPartOfEquipment.ADORM.ordinal()] = 8;
            } catch (NoSuchFieldError var12) {
            }

            try {
                var0[EBodyPartOfEquipment.BOSOM.ordinal()] = 2;
            } catch (NoSuchFieldError var11) {
            }

            try {
                var0[EBodyPartOfEquipment.EXTEND.ordinal()] = 12;
            } catch (NoSuchFieldError var10) {
            }

            try {
                var0[EBodyPartOfEquipment.FINGER.ordinal()] = 6;
            } catch (NoSuchFieldError var9) {
            }

            try {
                var0[EBodyPartOfEquipment.FOOT.ordinal()] = 4;
            } catch (NoSuchFieldError var8) {
            }

            try {
                var0[EBodyPartOfEquipment.HAND.ordinal()] = 3;
            } catch (NoSuchFieldError var7) {
            }

            try {
                var0[EBodyPartOfEquipment.HEAD.ordinal()] = 1;
            } catch (NoSuchFieldError var6) {
            }

            try {
                var0[EBodyPartOfEquipment.PET_F.ordinal()] = 10;
            } catch (NoSuchFieldError var5) {
            }

            try {
                var0[EBodyPartOfEquipment.PET_S.ordinal()] = 11;
            } catch (NoSuchFieldError var4) {
            }

            try {
                var0[EBodyPartOfEquipment.WAIST.ordinal()] = 7;
            } catch (NoSuchFieldError var3) {
            }

            try {
                var0[EBodyPartOfEquipment.WEAPON.ordinal()] = 5;
            } catch (NoSuchFieldError var2) {
            }

            try {
                var0[EBodyPartOfEquipment.WRIST.ordinal()] = 9;
            } catch (NoSuchFieldError var1) {
            }

            $SWITCH_TABLE$hero$item$detail$EBodyPartOfEquipment = var0;
            return var0;
        }
    }
}
