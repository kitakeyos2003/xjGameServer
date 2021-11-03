// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class RangeSkillWarning extends AbsResponseMessage {

    private int xLength;
    private int yLength;
    private boolean mobile;
    private int fixedRangeUpperLeftX;
    private int fixedRangeUpperLeftY;
    private int mobielRangeCenterTargetID;
    private int mobielRangeCenterTargetType;

    public RangeSkillWarning(final int _xLength, final int _yLength, final boolean _mobile, final int _mobielRangeCenterTargetType, final int _mobielRangeCenterTargetID, final int _fixedRangeUpperLeftX, final int _fixedRangeUpperLeftY) {
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.xLength);
        this.yos.writeByte(this.yLength);
        this.yos.writeByte(this.mobile);
        if (this.mobile) {
            this.yos.writeByte(this.mobielRangeCenterTargetType);
            this.yos.writeByte(this.mobielRangeCenterTargetID);
        } else {
            this.yos.writeByte(this.fixedRangeUpperLeftX);
            this.yos.writeByte(this.fixedRangeUpperLeftY);
        }
    }
}
