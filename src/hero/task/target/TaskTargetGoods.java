// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

import hero.item.SingleGoods;

public class TaskTargetGoods extends BaseTaskTarget implements INumberTypeTarget {

    public SingleGoods goods;
    public short number;

    public TaskTargetGoods(final int _ID, final SingleGoods _goods, final short _number) {
        super(_ID);
        this.goods = _goods;
        this.number = _number;
        this.description = this.goods.getName();
    }

    @Override
    public ETastTargetType getType() {
        return ETastTargetType.GOODS;
    }

    @Override
    public boolean isCompleted() {
        return this.currentNumber >= this.number;
    }

    @Override
    public String getDescripiton() {
        return new StringBuffer(this.description).append("\u3000").append(this.currentNumber).append("/").append(this.number).toString();
    }

    @Override
    public void numberChanged(final int _changeNumber) {
        this.currentNumber += (short) _changeNumber;
        if (this.currentNumber >= this.number) {
            this.currentNumber = this.number;
        } else if (this.currentNumber < 0) {
            this.currentNumber = 0;
        }
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
