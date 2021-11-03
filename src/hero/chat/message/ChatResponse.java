// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.message;

import java.io.IOException;
import hero.share.DirtyStringDict;
import yoyo.core.packet.AbsResponseMessage;

public class ChatResponse extends AbsResponseMessage {

    byte type;
    String name;
    String dest;
    String content;
    int sessionID;
    int questionID;
    boolean showMiddle;

    public ChatResponse(final byte _type, final String _name, final String _content) {
        this.showMiddle = false;
        this.type = _type;
        this.name = _name;
        this.content = _content;
    }

    public ChatResponse(final byte _type, final String _name, final String _content, final boolean _showMiddle) {
        this.showMiddle = false;
        this.type = _type;
        this.name = _name;
        this.content = _content;
        this.showMiddle = _showMiddle;
    }

    public ChatResponse(final byte _type, final String _name, final String _dest, final String _content) {
        this.showMiddle = false;
        this.type = _type;
        this.name = _name;
        this.dest = _dest;
        this.content = _content;
    }

    public ChatResponse(final byte _type, final String _name, final int _sessionID, final int _questionID, final String _content) {
        this.showMiddle = false;
        this.type = _type;
        this.name = _name;
        this.sessionID = _sessionID;
        this.questionID = _questionID;
        this.content = _content;
    }

    @Override
    protected void write() throws IOException {
        if (this.type != 5) {
            this.content = DirtyStringDict.getInstance().clearDirtyChar(this.content);
        }
        try {
            this.yos.writeByte(this.type);
            if (this.name == null) {
                this.name = "";
            }
            this.yos.writeUTF(this.name);
            if (this.type == 0) {
                this.yos.writeUTF(this.dest);
            }
            this.yos.writeUTF(this.content);
            this.yos.writeByte(this.showMiddle);
            this.yos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
