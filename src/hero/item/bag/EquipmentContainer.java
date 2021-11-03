// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.bag;

import hero.item.bag.exception.BagException;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.EquipmentInstance;

public abstract class EquipmentContainer {

    protected short emptyGridNumber;
    protected EquipmentInstance[] equipmentList;
    public static final byte BAG = 1;
    public static final byte BODY = 2;
    public static final byte PET_BAG = 3;
    public static final byte PET_BODY = 4;

    public EquipmentContainer(final short _size) {
        this.equipmentList = new EquipmentInstance[_size];
        this.emptyGridNumber = _size;
    }

    public int getSize() {
        return this.equipmentList.length;
    }

    public int getFullGridNumber() {
        return this.equipmentList.length - this.emptyGridNumber;
    }

    public int getEmptyGridNumber() {
        return this.emptyGridNumber;
    }

    public void setEmptyGridNumber(final int emptyGridNumber) {
        this.emptyGridNumber = (short) emptyGridNumber;
    }

    public EquipmentInstance remove(final int _gridIndex) throws BagException {
        if (_gridIndex >= 0 && _gridIndex < this.equipmentList.length) {
            synchronized (this.equipmentList) {
                EquipmentInstance ei = this.equipmentList[_gridIndex];
                this.equipmentList[_gridIndex] = null;
                ++this.emptyGridNumber;
                // monitorexit(this.equipmentList)
                return ei;
            }
        }
        throw PackageExceptionFactory.getInstance().getException("\u65e0\u6548\u7684\u88c5\u5907\u4f4d\u7f6e\uff1a" + _gridIndex);
    }

    public EquipmentInstance removeByInstanceID(final int _equipmentInsID) throws BagException {
        synchronized (this.equipmentList) {
            for (int i = 0; i < this.equipmentList.length; ++i) {
                if (this.equipmentList[i] != null && _equipmentInsID == this.equipmentList[i].getInstanceID()) {
                    EquipmentInstance ei = this.equipmentList[i];
                    this.equipmentList[i] = null;
                    ++this.emptyGridNumber;
                    // monitorexit(this.equipmentList)
                    return ei;
                }
            }
        }
        // monitorexit(this.equipmentList)
        throw PackageExceptionFactory.getInstance().getException("\u4e0d\u5b58\u5728\u7684\u88c5\u5907");
    }

    public int remove(final EquipmentInstance _equipmentInstance) throws BagException {
        if (_equipmentInstance != null) {
            synchronized (this.equipmentList) {
                for (int i = 0; i < this.equipmentList.length; ++i) {
                    if (this.equipmentList[i] == _equipmentInstance) {
                        this.equipmentList[i] = null;
                        ++this.emptyGridNumber;
                        // monitorexit(this.equipmentList)
                        return i;
                    }
                }
            }
            // monitorexit(this.equipmentList)
            throw PackageExceptionFactory.getInstance().getException("\u4e0d\u5b58\u5728\u7684\u88c5\u5907");
        }
        return -1;
    }

    public EquipmentInstance getEquipmentByInstanceID(final int _equipmentInstanceID) {
        EquipmentInstance[] equipmentList;
        for (int length = (equipmentList = this.equipmentList).length, i = 0; i < length; ++i) {
            EquipmentInstance ei = equipmentList[i];
            if (ei != null && ei.getInstanceID() == _equipmentInstanceID) {
                return ei;
            }
        }
        return null;
    }

    public EquipmentInstance get(final int _gridIndex) {
        if (_gridIndex >= 0 && _gridIndex < this.equipmentList.length) {
            return this.equipmentList[_gridIndex];
        }
        return null;
    }

    public EquipmentInstance[] getEquipmentList() {
        return this.equipmentList;
    }

    public abstract byte getContainerType();

    public int indexOf(final EquipmentInstance _equipmentIns) {
        if (_equipmentIns != null) {
            for (int i = 0; i < this.equipmentList.length; ++i) {
                if (_equipmentIns == this.equipmentList[i]) {
                    return i;
                }
            }
        }
        return -1;
    }
}
