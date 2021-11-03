// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseTransactPet extends AbsResponseMessage {

    int userID;
    long petID;
    int res;

    public ResponseTransactPet(final int userID, final long petID, final int res) {
        this.userID = userID;
        this.petID = petID;
        this.res = res;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        String msg = "\u5ba0\u7269\u4ea4\u6613\u6210\u529f";
        if (this.res != 1) {
            msg = "\u5ba0\u7269\u4ea4\u6613\u5931\u8d25";
        }
        this.yos.writeInt(this.userID);
        this.yos.writeInt((float) this.petID);
        this.yos.writeByte(this.res);
        this.yos.writeUTF(msg);
    }
}
