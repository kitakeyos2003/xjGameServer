// 
// Decompiled by Procyon v0.5.36
// 
package hero.social.message;

import java.io.IOException;
import java.util.Iterator;
import hero.social.SocialObjectProxy;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseSocialRelationdList extends AbsResponseMessage {

    private static Logger log;
    private byte socialRelationType;
    ArrayList<SocialObjectProxy> list;

    static {
        ResponseSocialRelationdList.log = Logger.getLogger((Class) ResponseSocialRelationdList.class);
    }

    public ResponseSocialRelationdList(final byte _socialRelationType, final ArrayList<SocialObjectProxy> _list) {
        this.socialRelationType = _socialRelationType;
        this.list = _list;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        ResponseSocialRelationdList.log.debug((Object) ("\u4e0b\u53d1\u793e\u4ea4\u5217\u8868\uff0c\u7c7b\u578b=" + this.socialRelationType));
        this.yos.writeByte(this.socialRelationType);
        if (this.list != null && this.list.size() > 0) {
            ResponseSocialRelationdList.log.debug((Object) ("social list size = " + this.list.size()));
            this.yos.writeShort(this.list.size());
            for (final SocialObjectProxy socialObjectProxy : this.list) {
                ResponseSocialRelationdList.log.debug((Object) ("userid=" + socialObjectProxy.userID + ",name=" + socialObjectProxy.name + ",isOnline=" + socialObjectProxy.isOnline));
                this.yos.writeInt(socialObjectProxy.userID);
                this.yos.writeUTF(socialObjectProxy.name);
                this.yos.writeByte(socialObjectProxy.isOnline);
                if (socialObjectProxy.isOnline) {
                    ResponseSocialRelationdList.log.debug((Object) "user isonline...");
                    this.yos.writeByte(socialObjectProxy.vocation.value());
                    this.yos.writeShort(socialObjectProxy.level);
                    this.yos.writeByte(socialObjectProxy.sex.value());
                    ResponseSocialRelationdList.log.debug((Object) ("vocation=" + socialObjectProxy.vocation.value() + ",level=" + socialObjectProxy.level + ",sex=" + socialObjectProxy.sex.value()));
                } else {
                    this.yos.writeByte(0);
                    this.yos.writeShort(0);
                    ResponseSocialRelationdList.log.debug((Object) "user offline...");
                }
            }
        } else {
            this.yos.writeShort(0);
        }
        ResponseSocialRelationdList.log.debug((Object) "\u4e0b\u53d1\u793e\u4ea4\u5217\u8868 end ...");
    }
}
