// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

import hero.npc.dict.GearDataDict;

public class TaskTargetOpenGear extends BaseTaskTarget {

    public String gearModelID;

    public TaskTargetOpenGear(final int _ID, final String _gearNpcModelID) {
        super(_ID);
        this.gearModelID = _gearNpcModelID;
        GearDataDict.GearData data = GearDataDict.getInstance().getGearData(_gearNpcModelID);
        if (data != null) {
            String name = data.name;
            String optionDesc = data.optionDesc;
            if (optionDesc != null) {
                this.description = new StringBuffer(optionDesc).append(name).toString();
            }
        }
    }

    @Override
    public ETastTargetType getType() {
        return ETastTargetType.OPEN_GEAR;
    }

    @Override
    public boolean isCompleted() {
        return this.currentNumber == 1;
    }

    @Override
    public void complete() {
        this.currentNumber = 1;
    }

    @Override
    public String getDescripiton() {
        return new StringBuffer(this.description).append("\u3000").append(this.currentNumber).append("/").append(1).toString();
    }

    @Override
    public BaseTaskTarget clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void setCurrentNumber(final short _number) {
        this.currentNumber = _number;
        if (this.currentNumber > 1) {
            this.currentNumber = 1;
        }
    }

    @Override
    public boolean canTransmit() {
        return this.transmitMapInfo != null;
    }
}
