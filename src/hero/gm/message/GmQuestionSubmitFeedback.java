// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GmQuestionSubmitFeedback extends AbsResponseMessage {

    public static final byte FAIL = 0;
    public static final byte OK = 1;
    private byte feedback;

    public GmQuestionSubmitFeedback(final byte _feedback) {
        this.feedback = _feedback;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        try {
            this.yos.writeByte(this.feedback);
            this.yos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
