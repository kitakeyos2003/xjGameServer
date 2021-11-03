// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.store.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GridGoodsChangesNotify extends AbsResponseMessage {

    private boolean isSelf;
    private byte gridIndex;
    private byte changeTypeOfContent;
    private int newPrice;
    public static final byte CONTENT_OF_PRICE = 1;
    public static final byte CONTENT_OF_REMOVE = 2;

    public GridGoodsChangesNotify(final boolean _isSelf, final byte _gridIndex, final byte _changeTypeOfContent) {
        this.isSelf = _isSelf;
        this.gridIndex = _gridIndex;
        this.changeTypeOfContent = _changeTypeOfContent;
    }

    public GridGoodsChangesNotify(final boolean _isSelf, final byte _gridIndex, final byte _changeTypeOfContent, final int _newPrice) {
        this.isSelf = _isSelf;
        this.gridIndex = _gridIndex;
        this.changeTypeOfContent = _changeTypeOfContent;
        this.newPrice = _newPrice;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.isSelf);
        this.yos.writeByte(this.gridIndex);
        this.yos.writeByte(this.changeTypeOfContent);
        if (1 == this.changeTypeOfContent) {
            this.yos.writeInt(this.newPrice);
        }
    }
}
