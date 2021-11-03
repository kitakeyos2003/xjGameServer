// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ChangeAllSkillReleaseTimePercent extends AbsResponseMessage {

    private byte numerator;

    public ChangeAllSkillReleaseTimePercent(final float _percent) {
        this.numerator = (byte) (_percent * 100.0f);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.numerator);
    }
}
