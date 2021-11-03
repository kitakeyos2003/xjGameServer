// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MoneyChangeNofity extends AbsResponseMessage {

    private int money;
    private int drawLocation;

    public MoneyChangeNofity(final int _money, final int _drawLocation) {
        this.money = _money;
        this.drawLocation = _drawLocation;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.drawLocation);
        this.yos.writeInt(this.money);
    }
}
