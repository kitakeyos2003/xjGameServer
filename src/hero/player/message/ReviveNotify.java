// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ReviveNotify extends AbsResponseMessage {

    private int playrID;
    private int hp;
    private int maxHp;
    private int mpOrForceQuantity;
    private int maxMpOrPhisicsQuantity;
    private byte locationX;
    private byte locationY;

    public ReviveNotify(final int _playrID, final int _hp, final int _maxHp, final int _mpOrForceQuantity, final int _maxMpOrPhisicsQuantity, final byte _locationX, final byte _locationY) {
        this.playrID = _playrID;
        this.hp = _hp;
        this.maxHp = _maxHp;
        this.mpOrForceQuantity = _mpOrForceQuantity;
        this.maxMpOrPhisicsQuantity = _maxMpOrPhisicsQuantity;
        this.locationX = _locationX;
        this.locationY = _locationY;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.playrID);
        this.yos.writeInt(this.hp);
        this.yos.writeInt(this.maxHp);
        this.yos.writeInt(this.mpOrForceQuantity);
        this.yos.writeInt(this.maxMpOrPhisicsQuantity);
        this.yos.writeByte(this.locationX);
        this.yos.writeByte(this.locationY);
    }
}
