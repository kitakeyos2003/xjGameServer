// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.message;

import java.io.IOException;
import hero.effect.Effect;
import yoyo.core.packet.AbsResponseMessage;

public class RefreshEffectNotify extends AbsResponseMessage {

    private Effect effect;
    private int oldReleaserID;

    public RefreshEffectNotify(final Effect _effect, final int _oldReleaserID) {
        this.effect = _effect;
        this.oldReleaserID = _oldReleaserID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.effect.host.getObjectType().value());
        this.yos.writeInt(this.effect.host.getID());
        this.yos.writeInt(this.effect.ID);
        this.yos.writeInt(this.oldReleaserID);
        this.yos.writeInt(this.effect.releaser.getID());
        this.yos.writeByte(this.effect.trait.value());
        this.yos.writeShort(this.effect.currentCountTimes);
        this.yos.writeByte(this.effect.keepTimeType.value());
        this.yos.writeShort(this.effect.traceTime);
        this.yos.writeShort(this.effect.iconID);
        this.yos.writeUTF(this.effect.name);
        this.yos.writeByte(this.effect.viewType);
        this.yos.writeShort(this.effect.imageID);
        this.yos.writeShort(this.effect.animationID);
    }
}
