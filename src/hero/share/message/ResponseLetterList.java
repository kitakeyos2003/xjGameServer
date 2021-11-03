// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.util.Calendar;
import java.io.IOException;
import java.util.Iterator;
import hero.share.letter.Letter;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseLetterList extends AbsResponseMessage {

    private static Logger log;
    private ArrayList<Letter> letterList;

    static {
        ResponseLetterList.log = Logger.getLogger((Class) ResponseLetterList.class);
    }

    public ResponseLetterList(final ArrayList<Letter> _letterList) {
        this.letterList = _letterList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        if (this.letterList == null) {
            this.yos.writeByte(0);
            return;
        }
        ResponseLetterList.log.debug((Object) ("response letter size = " + this.letterList.size()));
        this.yos.writeByte(this.letterList.size());
        for (final Letter l : this.letterList) {
            ResponseLetterList.log.debug((Object) ("letter id=" + l.letterID + ", title : " + l.title));
            this.yos.writeByte(l.type);
            this.yos.writeInt(l.letterID);
            this.yos.writeUTF(l.title);
            this.yos.writeUTF(l.senderName);
            this.yos.writeUTF(l.content);
            this.yos.writeUTF(this.getTime(l.sendTime));
            this.yos.writeByte(l.isRead ? 1 : 0);
            this.yos.writeByte((byte) (l.isSave ? 100 : this.lastDay(l.sendTime)));
        }
    }

    private byte lastDay(final long time) {
        long nowTime = System.currentTimeMillis();
        byte day = (byte) ((604800000L - (nowTime - time)) / 86400000L);
        if (day < 1) {
            day = 1;
        }
        return day;
    }

    private String getTime(final long time) {
        StringBuffer buf = new StringBuffer();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        buf.append(String.valueOf(c.get(1)) + "/");
        buf.append(String.valueOf(c.get(2) + 1) + "/");
        buf.append(c.get(5));
        buf.delete(0, 2);
        return buf.toString();
    }
}
