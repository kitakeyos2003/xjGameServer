// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class EnhanceAnswer extends AbsResponseMessage {

    private byte answerType;
    public static final byte ANSWER_TYPE_RIGHT = 1;
    public static final byte ANSWER_TYPE_WRONG = 0;

    public EnhanceAnswer(final byte _answerType) {
        this.answerType = _answerType;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.answerType);
    }
}
