// 
// Decompiled by Procyon v0.5.36
// 
package hero.social.message;

import java.io.IOException;
import hero.social.SocialObjectProxy;
import yoyo.core.packet.AbsResponseMessage;

public class AddSocialMemberNotify extends AbsResponseMessage {

    private SocialObjectProxy socialObjectProxy;

    public AddSocialMemberNotify(final SocialObjectProxy _socialObjectProxy) {
        this.socialObjectProxy = _socialObjectProxy;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.socialObjectProxy.socialRelationType.value());
        this.yos.writeInt(this.socialObjectProxy.userID);
        this.yos.writeUTF(this.socialObjectProxy.name);
        this.yos.writeByte(this.socialObjectProxy.vocation.value());
        this.yos.writeShort(this.socialObjectProxy.level);
        this.yos.writeByte(this.socialObjectProxy.sex.value());
    }
}
