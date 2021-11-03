// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.message;

import java.io.IOException;
import java.util.Iterator;
import hero.manufacture.Manufacture;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;

public class ManufNotify extends AbsResponseMessage {

    private List<Manufacture> manufList;

    public ManufNotify(final List<Manufacture> _manufList) {
        this.manufList = _manufList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.manufList.size());
        for (final Manufacture manuf : this.manufList) {
            this.yos.writeByte(manuf.getManufactureType().getID());
        }
    }
}
