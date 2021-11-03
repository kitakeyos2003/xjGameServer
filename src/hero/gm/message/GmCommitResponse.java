// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GmCommitResponse extends AbsResponseMessage {

    private int questionID;

    public GmCommitResponse(final int _questionID) {
        this.questionID = -1;
        this.questionID = _questionID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        try {
            this.yos.writeInt(this.questionID);
            this.yos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
