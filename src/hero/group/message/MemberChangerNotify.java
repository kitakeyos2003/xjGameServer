// 
// Decompiled by Procyon v0.5.36
// 
package hero.group.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MemberChangerNotify extends AbsResponseMessage {

    private byte changerType;
    private int memberUserID;
    private int value;
    public static final byte CHANGER_OF_SUB_GROUP = 1;
    public static final byte CHANGER_OF_RANK = 2;
    public static final byte CHANGER_OF_ONLINE_STATUS = 3;
    public static final byte CHANGER_OF_LEVEL = 4;
    public static final byte CHANGER_OF_VOCATION = 5;

    public MemberChangerNotify(final byte _changerType, final int _memberUserID, final int _value) {
        this.changerType = _changerType;
        this.memberUserID = _memberUserID;
        this.value = _value;
    }

    public MemberChangerNotify(final byte _changerType, final int _memberUserID, final boolean _onlineStatus) {
        this.changerType = _changerType;
        this.memberUserID = _memberUserID;
        this.value = (_onlineStatus ? 1 : 0);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.changerType);
        this.yos.writeInt(this.memberUserID);
        this.yos.writeShort(this.value);
    }
}
