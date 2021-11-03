// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.message;

import java.io.IOException;
import hero.share.ME2GameObject;
import hero.effect.Effect;
import yoyo.core.packet.AbsResponseMessage;

public class AddEffectNotify extends AbsResponseMessage {

    private Effect effect;
    private ME2GameObject target;

    public AddEffectNotify(final ME2GameObject _target, final Effect _effect) {
        this.target = _target;
        this.effect = _effect;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.target.getObjectType().value());
        this.yos.writeInt(this.target.getID());
        this.yos.writeInt(this.effect.ID);
        this.yos.writeInt(this.effect.releaser.getID());
        this.yos.writeByte(this.effect.trait.value());
        this.yos.writeByte(this.effect.keepTimeType.value());
        this.yos.writeShort(this.effect.traceTime);
        this.yos.writeShort(this.effect.iconID);
        this.yos.writeUTF(this.effect.name);
        this.yos.writeUTF(this.effect.desc);
        this.yos.writeByte(this.effect.viewType);
        this.yos.writeShort(this.effect.imageID);
        this.yos.writeShort(this.effect.animationID);
    }
}
