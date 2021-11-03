// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.letter;

public class Letter implements Comparable {

    public int letterID;
    public byte type;
    public String title;
    public String senderName;
    public int receiverUserID;
    public String receiverName;
    public String content;
    public long sendTime;
    public boolean isRead;
    public boolean isSave;
    public static final byte SYSTEM_TYPE = 0;
    public static final byte GENERIC_TYPE = 1;

    public Letter(final int _letterID, final String _title, final String _sender, final int _receiver_uid, final String _receiver, final String _content) {
        this.type = 1;
        this.letterID = _letterID;
        this.title = _title;
        this.senderName = _sender;
        this.receiverUserID = _receiver_uid;
        this.receiverName = _receiver;
        this.content = _content;
        this.sendTime = System.currentTimeMillis();
        this.isRead = false;
        this.isSave = false;
    }

    public Letter(final byte _type, final int _letterID, final String _title, final String _sender, final int _receiver_uid, final String _receiver, final String _content) {
        this.type = _type;
        this.letterID = _letterID;
        this.title = _title;
        this.senderName = _sender;
        this.receiverUserID = _receiver_uid;
        this.receiverName = _receiver;
        this.content = _content;
        this.sendTime = System.currentTimeMillis();
        this.isRead = false;
        this.isSave = false;
    }

    public Letter() {
    }

    @Override
    public int compareTo(final Object o) {
        Letter l = (Letter) o;
        if (l.type < l.type) {
            return 1;
        }
        return 0;
    }
}
