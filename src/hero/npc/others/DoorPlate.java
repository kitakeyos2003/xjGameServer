// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.others;

import hero.npc.dict.DoorPlateDataDict;

public class DoorPlate extends ME2OtherGameObject {

    private String tip;

    public DoorPlate(final DoorPlateDataDict.DoorPlateData _data) {
        super(_data.modelID);
        this.tip = _data.tip;
    }

    public String getTip() {
        return this.tip;
    }
}
