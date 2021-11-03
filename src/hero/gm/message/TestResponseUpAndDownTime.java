// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class TestResponseUpAndDownTime extends AbsResponseMessage {

    private int key;
    private long uptime;
    private long upsubtime;

    public TestResponseUpAndDownTime(final int _key, final long _uptime, final long _upsubtime) {
        this.uptime = _uptime;
        this.key = _key;
        this.upsubtime = _upsubtime;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.key);
        this.yos.writeLong(this.uptime);
        this.yos.writeLong(this.upsubtime);
        this.yos.writeLong(System.currentTimeMillis());
        this.yos.writeUTF("\u8fd9\u662f\u6d4b\u8bd5\u65f6\u95f4\u7684\u62a5\u6587\u3002\u3002\u3002");
    }
}
