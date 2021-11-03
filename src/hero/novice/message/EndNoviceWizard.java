// 
// Decompiled by Procyon v0.5.36
// 
package hero.novice.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class EndNoviceWizard extends AbsResponseMessage {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
    }
}
