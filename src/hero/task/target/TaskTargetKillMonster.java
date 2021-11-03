// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

import hero.npc.service.NotPlayerServiceImpl;

public class TaskTargetKillMonster extends BaseTaskTarget implements INumberTypeTarget {

    public String monsterModelID;
    public short number;

    public TaskTargetKillMonster(final int _ID, final String _monsterModelID, final short _number) {
        super(_ID);
        this.monsterModelID = _monsterModelID;
        this.number = _number;
        String monsterName = NotPlayerServiceImpl.getInstance().getNotPlayerNameByModelID(_monsterModelID);
        if (monsterName != null) {
            this.description = new StringBuffer("\u6740\u6b7b").append(monsterName).toString();
        }
    }

    @Override
    public ETastTargetType getType() {
        return ETastTargetType.KILL_MONSTER;
    }

    @Override
    public boolean isCompleted() {
        return this.currentNumber >= this.number;
    }

    @Override
    public void numberChanged(final int _changeNumber) {
        this.currentNumber += (short) _changeNumber;
        if (this.currentNumber >= this.number) {
            this.currentNumber = this.number;
        }
    }

    @Override
    public String getDescripiton() {
        return new StringBuffer(this.description).append("\u3000").append(this.currentNumber).append("/").append(this.number).toString();
    }

    @Override
    public BaseTaskTarget clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void setCurrentNumber(final short _number) {
        this.currentNumber = _number;
        if (this.currentNumber > this.number) {
            this.currentNumber = this.number;
        }
    }

    @Override
    public void complete() {
        this.currentNumber = this.number;
    }

    @Override
    public boolean canTransmit() {
        return this.transmitMapInfo != null;
    }
}
