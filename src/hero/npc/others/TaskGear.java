// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.others;

import hero.npc.dict.NpcImageDict;
import hero.npc.dict.GearDataDict;

public class TaskGear extends ME2OtherGameObject {

    private String name;
    private String description;
    private String optionDesc;
    private int taskIDAbout;
    private byte[] image;

    public TaskGear(final GearDataDict.GearData _gearData) {
        super(_gearData.modelID, _gearData.imageID);
        this.name = _gearData.name;
        this.description = _gearData.description;
        this.optionDesc = _gearData.optionDesc;
        this.taskIDAbout = _gearData.taskID;
        this.image = NpcImageDict.getInstance().getImageBytes(this.getImageID());
    }

    public String getName() {
        return this.name;
    }

    public int getTaskIDAbout() {
        return this.taskIDAbout;
    }

    public String getDesc() {
        return this.description;
    }

    public String getOptionDesc() {
        return this.optionDesc;
    }

    public byte[] getImage() {
        return this.image;
    }
}
