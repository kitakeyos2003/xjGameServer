// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.bag;

import hero.item.detail.EBodyPartOfEquipment;
import hero.item.bag.exception.BagException;
import java.util.Iterator;
import hero.pet.Pet;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class PlayerBodyWearPetList extends PetContainer {

    private static Logger log;

    static {
        PlayerBodyWearPetList.log = Logger.getLogger((Class) PlayerBodyWearPetList.class);
    }

    public PlayerBodyWearPetList() {
        super((short) 2);
    }

    public void init(final HashMap<Integer, Pet> petViewList) throws BagException {
        Iterator<Pet> it = petViewList.values().iterator();
        while (it.hasNext()) {
            this.add(it.next());
            PlayerBodyWearPetList.log.debug((Object) ("init player body pet size = " + this.getFullGridNumber()));
        }
    }

    @Override
    public int add(final Pet pet) throws BagException {
        int gridnum = EBodyPartOfEquipment.PET_F.value();
        if (pet != null && pet.viewStatus == 1) {
            synchronized (this.petList) {
                if (this.notExists(pet)) {
                    if (pet.pk.getStage() == 2) {
                        PlayerBodyWearPetList.log.debug((Object) "player wear body pet stage adult ..");
                        if (pet.pk.getType() == 2) {
                            this.petList[0] = pet;
                        } else {
                            this.petList[1] = pet;
                            gridnum = EBodyPartOfEquipment.PET_S.value();
                        }
                        --this.emptyGridNumber;
                    } else {
                        PlayerBodyWearPetList.log.debug((Object) "player wear body pet stage not adult ..");
                        if (this.petList[0] == null) {
                            this.petList[0] = pet;
                        } else if (this.petList[1] == null) {
                            this.petList[1] = pet;
                            gridnum = EBodyPartOfEquipment.PET_S.value();
                        }
                        --this.emptyGridNumber;
                    }
                }
            }
            // monitorexit(this.petList)
        }
        return gridnum;
    }
}
