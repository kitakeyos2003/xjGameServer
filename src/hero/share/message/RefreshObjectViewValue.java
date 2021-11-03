// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import hero.share.EObjectType;
import yoyo.core.packet.AbsResponseMessage;

public class RefreshObjectViewValue extends AbsResponseMessage {

    private EObjectType objectType;
    private int objectID;
    private int hp;
    private int hpMax;
    private int mp;
    private int mpMax;
    private short surplusSkillPoint;

    public RefreshObjectViewValue(final ME2GameObject _object) {
        this.objectType = _object.getObjectType();
        this.objectID = _object.getID();
        this.hp = _object.getHp();
        this.hpMax = _object.getActualProperty().getHpMax();
        this.mp = _object.getMp();
        this.mpMax = _object.getActualProperty().getMpMax();
        this.surplusSkillPoint = -1;
        if (_object instanceof HeroPlayer) {
            this.surplusSkillPoint = ((HeroPlayer) _object).surplusSkillPoint;
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.objectType.value());
        this.yos.writeInt(this.objectID);
        this.yos.writeInt(this.hp);
        this.yos.writeInt(this.hpMax);
        this.yos.writeInt(this.mp);
        this.yos.writeInt(this.mpMax);
    }
}
