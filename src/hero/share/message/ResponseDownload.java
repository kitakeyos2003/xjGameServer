// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseDownload extends AbsResponseMessage {

    private static Logger log;
    private String fileURL;
    private byte[] file;

    static {
        ResponseDownload.log = Logger.getLogger((Class) ResponseDownload.class);
    }

    public ResponseDownload(final String _url, final byte[] _file) {
        this.fileURL = _url;
        this.file = _file;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.fileURL);
        this.yos.writeShort(this.file.length);
        this.yos.writeBytes(this.file);
    }
}
