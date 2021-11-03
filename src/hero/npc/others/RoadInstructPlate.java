// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.others;

import hero.npc.dict.RoadPlateDataDict;

public class RoadInstructPlate extends ME2OtherGameObject {

    private String content;

    public RoadInstructPlate(final RoadPlateDataDict.RoadPlateData _data) {
        super(_data.modelID);
        this.content = _data.instructContent;
    }

    public String getContent() {
        return this.content;
    }
}
