// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MAOperateConfirm extends AbsResponseMessage {

    private byte confirmRequestType;
    private int initiatorUserID;
    private String initiatorName;

    public MAOperateConfirm(final byte _confirmRequestType, final int _initiatorUserID, final String _initiatorName) {
        this.confirmRequestType = _confirmRequestType;
        this.initiatorUserID = _initiatorUserID;
        this.initiatorName = _initiatorName;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.confirmRequestType);
        this.yos.writeInt(this.initiatorUserID);
        this.yos.writeUTF(this.initiatorName);
    }
}
