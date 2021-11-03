// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ExperienceAddNotify extends AbsResponseMessage {

    private int experience;
    private int drawLocation;
    private int currentExp;
    private int currentExpShow;

    public ExperienceAddNotify(final int _experience, final int _drawLocation, final int _currentExp, final int _currentExpShow) {
        this.experience = _experience;
        this.drawLocation = _drawLocation;
        this.currentExp = _currentExp;
        this.currentExpShow = _currentExpShow;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.drawLocation);
        this.yos.writeInt(this.experience);
        this.yos.writeInt(this.currentExp);
        this.yos.writeInt(this.currentExpShow);
    }
}
