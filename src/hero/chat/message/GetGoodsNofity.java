// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class GetGoodsNofity extends AbsResponseMessage {

    String name;
    String content;
    int traitRGB;
    int num;

    public GetGoodsNofity(final String _content, final String _name, final int _rgb, final int _num) {
        this.content = _content;
        this.name = _name;
        this.traitRGB = _rgb;
        this.num = _num;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.content);
        this.yos.writeUTF(this.name);
        this.yos.writeInt(this.traitRGB);
        this.yos.writeByte(this.num);
    }
}
