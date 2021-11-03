// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

import hero.npc.service.NotPlayerServiceImpl;

public class TaskTargetEscortNpc extends BaseTaskTarget {

    public String npcModelID;
    public int countTime;
    public int mapID;
    public short x;
    public short y;
    public short mistakeRang;

    public TaskTargetEscortNpc(final int _ID, final String _npcModelID, final int _countTime, final int _mapID, final short _x, final short _y, final short _range) {
        super(_ID);
        this.npcModelID = _npcModelID;
        this.countTime = _countTime;
        this.mapID = _mapID;
        this.x = _x;
        this.y = _y;
        this.mistakeRang = _range;
        String npcName = NotPlayerServiceImpl.getInstance().getNotPlayerNameByModelID(_npcModelID);
        if (npcName != null) {
            this.description = new StringBuffer("\u5728").append(this.countTime / 60000).append("\u5206\u949f\u5185\u5b8c\u6210").append("\u62a4\u9001").append(npcName).append(this.description).toString();
        }
    }

    @Override
    public ETastTargetType getType() {
        return ETastTargetType.ESCORT_NPC;
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
        return this.description;
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
        return false;
    }
}
