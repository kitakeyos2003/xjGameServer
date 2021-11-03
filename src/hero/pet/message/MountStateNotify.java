// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class MountStateNotify extends AbsResponseMessage {

    private int userID;
    private byte stateMount;
    private short pngMount;
    private short anuMount;
    private String name;
    private short level;
    public static final byte MOUNT_STATE_UP = 0;
    public static final byte MOUNT_STATE_DOWN = 1;

    public MountStateNotify(final int _userID, final byte _stateMount, final short _pngMount, final short _anuMount, final String _name, final int _level) {
        this.stateMount = _stateMount;
        this.pngMount = _pngMount;
        this.anuMount = _anuMount;
        this.userID = _userID;
        this.name = _name;
        this.level = (short) _level;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.userID);
        this.yos.writeUTF(this.name);
        this.yos.writeShort(this.level);
        this.yos.writeByte(this.stateMount);
        this.yos.writeShort(this.pngMount);
        this.yos.writeShort(this.anuMount);
    }
}
