// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class NodifyMedicamentCDTime extends AbsResponseMessage {

    int goodID;

    public NodifyMedicamentCDTime(final int _id) {
        this.goodID = _id;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.goodID);
    }
}
