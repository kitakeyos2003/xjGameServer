// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMapMiniImage extends AbsResponseMessage {

    private short clientType;
    private byte[] mapImage;
    private int imageID;

    public ResponseMapMiniImage(final short _clientType, final int _imageID, final byte[] _mapImage) {
        this.clientType = _clientType;
        this.imageID = _imageID;
        this.mapImage = _mapImage;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.imageID);
        if (3 != this.clientType) {
            this.yos.writeShort((short) this.mapImage.length);
            this.yos.writeBytes(this.mapImage);
        }
    }
}
