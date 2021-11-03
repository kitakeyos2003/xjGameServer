// Decompiled with: FernFlower
// Class Version: 6
package hero.item.bag;

import hero.item.EquipmentInstance;
import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.detail.EGoodsType;
import hero.item.detail.EPetBodyPartOfEquipment;

public class PetEquipmentBag extends EquipmentContainer {

    // $FF: synthetic field
    private static int[] $SWITCH_TABLE$hero$item$detail$EPetBodyPartOfEquipment;

    public PetEquipmentBag(short size) {
        super(size);
    }

    public byte getContainerType() {
        return 3;
    }

    public int add(EquipmentInstance _equipmentInstance) throws BagException {
        if (this.emptyGridNumber == 0) {
            throw PackageExceptionFactory.getInstance().getFullException(EGoodsType.PET_EQUIQ_GOODS);
        } else if (_equipmentInstance != null && _equipmentInstance.getOwnerType() == 2) {
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
            throw PackageExceptionFactory.getInstance().getException("无效的宠物装备数据");
        }
    }

    public boolean add(int _index, EquipmentInstance _equipmentInstance) throws BagException {
        if (this.emptyGridNumber == 0) {
            throw PackageExceptionFactory.getInstance().getFullException(EGoodsType.PET_EQUIQ_GOODS);
        } else if (_equipmentInstance != null && _equipmentInstance.getOwnerType() == 2) {
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
            throw PackageExceptionFactory.getInstance().getException("无效的宠物装备数据");
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

    public boolean clearUp() {
        boolean changed = false;
        EPetBodyPartOfEquipment bodyPart = EPetBodyPartOfEquipment.HEAD;
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
                        switch ($SWITCH_TABLE$hero$item$detail$EPetBodyPartOfEquipment()[bodyPart.ordinal()]) {
                            case 1:
                                bodyPart = EPetBodyPartOfEquipment.BODY;
                                break;
                            case 2:
                                bodyPart = EPetBodyPartOfEquipment.CLAW;
                                break;
                            case 3:
                                bodyPart = EPetBodyPartOfEquipment.TAIL;
                                break;
                            default:
                                return changed;
                        }
                    }
                }
            }

            return changed;
        }
    }

    // $FF: synthetic method
    static int[] $SWITCH_TABLE$hero$item$detail$EPetBodyPartOfEquipment() {
        int[] var10000 = $SWITCH_TABLE$hero$item$detail$EPetBodyPartOfEquipment;
        if (var10000 != null) {
            return var10000;
        } else {
            int[] var0 = new int[EPetBodyPartOfEquipment.values().length];

            try {
                var0[EPetBodyPartOfEquipment.BODY.ordinal()] = 2;
            } catch (NoSuchFieldError var4) {
            }

            try {
                var0[EPetBodyPartOfEquipment.CLAW.ordinal()] = 3;
            } catch (NoSuchFieldError var3) {
            }

            try {
                var0[EPetBodyPartOfEquipment.HEAD.ordinal()] = 1;
            } catch (NoSuchFieldError var2) {
            }

            try {
                var0[EPetBodyPartOfEquipment.TAIL.ordinal()] = 4;
            } catch (NoSuchFieldError var1) {
            }

            $SWITCH_TABLE$hero$item$detail$EPetBodyPartOfEquipment = var0;
            return var0;
        }
    }
}
