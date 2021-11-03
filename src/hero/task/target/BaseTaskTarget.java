// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

public abstract class BaseTaskTarget implements Cloneable {

    private int ID;
    protected short[] transmitMapInfo;
    protected short currentNumber;
    protected String description;

    public BaseTaskTarget(final int _ID) {
        this.description = "";
        this.ID = _ID;
    }

    public int getID() {
        return this.ID;
    }

    public BaseTaskTarget clone() throws CloneNotSupportedException {
        return (BaseTaskTarget) super.clone();
    }

    public short getCurrentNumber() {
        return this.currentNumber;
    }

    public abstract ETastTargetType getType();

    public abstract boolean isCompleted();

    public abstract void complete();

    public void setTransmitMapInfo(final short[] _transmitMapInfo) {
        this.transmitMapInfo = _transmitMapInfo;
    }

    public short[] getTransmitMapInfo() {
        return this.transmitMapInfo;
    }

    public abstract boolean canTransmit();

    public abstract void setCurrentNumber(final short p0);

    public abstract String getDescripiton();
}
