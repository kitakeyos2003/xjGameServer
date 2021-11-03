// Decompiled with: Procyon 0.5.36
// Class Version: 6
package hero.item.bag;

import java.util.Iterator;
import java.util.List;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsType;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.pet.Pet;
import org.apache.log4j.Logger;

public class PetContainer {

    private static Logger log;
    public short emptyGridNumber;
    public Pet[] petList;

    static {
        PetContainer.log = Logger.getLogger(PetContainer.class);
    }

    public PetContainer(final short emptyGridNumber) {
        this.petList = new Pet[emptyGridNumber];
        this.emptyGridNumber = emptyGridNumber;
    }

    public int getSize() {
        return this.petList.length;
    }

    public int getFullGridNumber() {
        return this.petList.length - this.emptyGridNumber;
    }

    public short getEmptyGridNumber() {
        return this.emptyGridNumber;
    }

    public void setEmptyGridNumber(final short emptyGridNumber) {
        this.emptyGridNumber = emptyGridNumber;
    }

    public int add(final Pet pet) throws BagException {
        if (this.emptyGridNumber == 0) {
            throw PackageExceptionFactory.getInstance().getFullException(EGoodsType.PET);
        }
        if (pet != null && pet.viewStatus == 0) {
            synchronized (this.petList) {
                for (int i = 0; i < this.petList.length; ++i) {
                    if (this.petList[i] == null && this.notExists(pet)) {
                        this.petList[i] = pet;
                        --this.emptyGridNumber;
                        // monitorexit(this.petList)
                        return i;
                    }
                }
            }
            // monitorexit(this.petList)
        }
        return -1;
    }

    protected boolean notExists(final Pet pet) throws BagException {
        if (pet != null) {
            for (int i = 0; i < this.petList.length; ++i) {
                if (this.petList[i] != null && pet.id == this.petList[i].id) {
                    return false;
                }
            }
        }
        return true;
    }

    public void init(final List<Pet> list) throws BagException {
        PetContainer.log.debug("init player petContainer petlist size = " + list.size() + ", emptyGridNumber=" + this.emptyGridNumber);
        if (this.emptyGridNumber == 0) {
            throw PackageExceptionFactory.getInstance().getFullException(EGoodsType.PET);
        }
        synchronized (this.petList) {
            for (final Pet pet : list) {
                PetContainer.log.debug(" init petContainer add pet id = " + pet.id + ", viewstatus = " + pet.viewStatus);
                if (pet.viewStatus == 0) {
                    for (int i = 0; i < this.petList.length; ++i) {
                        if (this.petList[i] == null && this.notExists(pet)) {
                            this.petList[i] = pet;
                            --this.emptyGridNumber;
                        }
                    }
                }
            }
            PetContainer.log.debug("init petContainer length = " + this.getFullGridNumber());
        }
        // monitorexit(this.petList)
    }

    public Pet remove(final short n) throws BagException {
        if (n >= 0 && n < this.petList.length) {
            synchronized (this.petList) {
                PetContainer.log.debug("remove pet, curr pet list size = " + this.getFullGridNumber());
                Pet pet = this.petList[n];
                this.petList[n] = null;
                ++this.emptyGridNumber;
                // monitorexit(this.petList)
                return pet;
            }
        }
        throw PackageExceptionFactory.getInstance().getException("无效的宠物位置：" + n);
    }

    public int remove(final Pet pet) throws BagException {
        if (pet != null) {
            synchronized (this.petList) {
                PetContainer.log.debug("remove pet, curr pet list size = " + this.getFullGridNumber());
                for (int i = 0; i < this.petList.length; ++i) {
                    if (this.petList[i] != null && this.petList[i] == pet) {
                        this.petList[i] = null;
                        ++this.emptyGridNumber;
                        return i;
                    }
                }
                throw PackageExceptionFactory.getInstance().getException("不存在的宠物");
            }
        }
        return -1;
    }

    public Pet remove(final int n) {
        synchronized (this.petList) {
            PetContainer.log.debug("remove pet, curr pet list size = " + this.getFullGridNumber());
            for (int i = 0; i < this.petList.length; ++i) {
                if (this.petList[i] != null && this.petList[i].id == n) {
                    Pet pet = this.petList[i];
                    this.petList[i] = null;
                    ++this.emptyGridNumber;
                    PetContainer.log.debug("remove [" + i + "], petid= " + n);
                    // monitorexit(this.petList)
                    return pet;
                }
            }
        }
        // monitorexit(this.petList)
        return null;
    }

    public Pet getPet(final int n) throws BagException {
        if (n >= 0 && n < this.petList.length) {
            return this.petList[n];
        }
        return null;
    }

    public Pet[] getPetList() throws BagException {
        return this.petList;
    }

    public short getGridNumber(final Pet pet) throws BagException {
        if (pet != null) {
            for (int i = 0; i < this.petList.length; ++i) {
                if (this.petList[i] == pet) {
                    return (short) i;
                }
            }
        }
        return -1;
    }

    public boolean upgrade() {
        if (this.petList.length == 40) {
            return false;
        }
        Pet[] petList = new Pet[this.petList.length + 8];
        System.arraycopy(this.petList, 0, petList, 0, this.petList.length);
        this.petList = petList;
        this.emptyGridNumber += 8;
        return true;
    }
}
