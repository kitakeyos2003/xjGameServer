// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

public class PetList {

    private static Logger log;
    private List<Integer> lastTimesViewPetID;
    private HashMap<Integer, Pet> viewPet;
    private ArrayList<Pet> petList;
    public static final short MAX_NUMBER = 100;
    public static final short MAX_SHOW_NUMBER = 2;

    static {
        PetList.log = Logger.getLogger((Class) PetList.class);
    }

    public PetList() {
        this.lastTimesViewPetID = new ArrayList<Integer>();
        this.viewPet = new HashMap<Integer, Pet>();
        this.petList = new ArrayList<Pet>(100);
    }

    public boolean add(final Pet _pet) {
        return 100 > this.petList.size() && this.petList.add(_pet);
    }

    public boolean exists(final Pet _pet) {
        return this.petList.contains(_pet);
    }

    public void changeViewPet(final Pet _opet, final Pet _npet) {
        if (_opet != null) {
            _opet.viewStatus = 0;
            _opet.isView = false;
            this.viewPet.remove(_opet.id);
        }
        if (_npet != null) {
            _npet.viewStatus = 1;
            _npet.isView = true;
            this.viewPet.put(_npet.id, _npet);
        }
    }

    public void removeViewPet(final Pet _pet) {
        this.viewPet.remove(_pet.id);
        _pet.viewStatus = 0;
        _pet.isView = false;
    }

    public int setViewPet(final Pet pet) {
        PetList.log.debug((Object) ("set view pet id = " + pet.id));
        if (pet.pk.getStage() != 0) {
            pet.isView = true;
        } else {
            pet.isView = false;
        }
        pet.viewStatus = 1;
        this.viewPet.put(pet.id, pet);
        return this.viewPet.size();
    }

    public HashMap<Integer, Pet> getViewPet() {
        return this.viewPet;
    }

    public int setLastTimesViewPetID(final int _petID) {
        this.lastTimesViewPetID.add(_petID);
        return this.lastTimesViewPetID.size();
    }

    public List<Integer> getLastTimesViewPetID() {
        return this.lastTimesViewPetID;
    }

    public boolean dicePet(Pet _pet) {
        if (this.petList.remove(_pet)) {
            this.removeLastTimesViewPetID(_pet.id);
            this.viewPet.remove(_pet.id);
            PetList.log.debug((Object) ("dict pet removed pet is null? " + this.viewPet.get(_pet.id)));
            _pet = null;
            return true;
        }
        return false;
    }

    public void removeLastTimesViewPetID(final Integer petID) {
        Iterator<Integer> it = this.lastTimesViewPetID.iterator();
        while (it.hasNext()) {
            if (it.next() == petID) {
                it.remove();
            }
        }
    }

    public ArrayList<Pet> getPetList() {
        return this.petList;
    }

    public Pet getPet(final int id) {
        for (final Pet pet : this.petList) {
            if (id == pet.id) {
                return pet;
            }
        }
        return null;
    }
}
