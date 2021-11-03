// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GmReplyResponse extends AbsResponseMessage {

    private String gmName;
    private int sessionID;
    private int questionID;
    private String content;

    public GmReplyResponse(final String _gmName, final int _sid, final int _questionID, final String _content) {
        this.gmName = _gmName;
        this.sessionID = _sid;
        this.questionID = _questionID;
        this.content = _content;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        try {
            this.yos.writeUTF(this.gmName);
            this.yos.writeInt(this.sessionID);
            this.yos.writeInt(this.questionID);
            this.yos.writeUTF(this.content);
            this.yos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
