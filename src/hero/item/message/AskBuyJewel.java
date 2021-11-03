// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class AskBuyJewel extends AbsResponseMessage {

    private int currPoint;

    public AskBuyJewel(final int _currPoint) {
        this.currPoint = _currPoint;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.currPoint);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
