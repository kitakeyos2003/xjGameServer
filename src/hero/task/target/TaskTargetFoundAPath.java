// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

import hero.map.Map;
import hero.share.service.Tip;
import hero.map.service.MapServiceImpl;

public class TaskTargetFoundAPath extends BaseTaskTarget {

    public short mapID;
    public short x;
    public short y;
    public short mistakeRang;

    public TaskTargetFoundAPath(final int _ID, final short _mapID, final short _x, final short _y, final short _range) {
        super(_ID);
        this.mapID = _mapID;
        this.x = _x;
        this.y = _y;
        this.mistakeRang = _range;
        Map map = MapServiceImpl.getInstance().getNormalMapByID(this.mapID);
        if (map != null) {
            int centerX = map.getWidth() / 2;
            int centerY = map.getHeight() / 2;
            if (this.x < centerX) {
                if (this.y < centerY) {
                    this.description = Tip.TIP_TASK_LOCATION_LIST[0];
                } else {
                    this.description = Tip.TIP_TASK_LOCATION_LIST[1];
                }
            } else if (this.y < centerY) {
                this.description = Tip.TIP_TASK_LOCATION_LIST[2];
            } else {
                this.description = Tip.TIP_TASK_LOCATION_LIST[3];
            }
            this.description = new StringBuffer("\u63a2\u7d22").append(map.getName()).append("\u7684").append(this.description).toString();
        }
    }

    @Override
    public ETastTargetType getType() {
        return ETastTargetType.FOUND_A_PATH;
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
