// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.message;

import java.io.IOException;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseTransID extends AbsResponseMessage {

    private static Logger log;
    private String tranID;
    private int userID;
    private int accountID;
    private byte gameID;
    private short serverID;

    static {
        ResponseTransID.log = Logger.getLogger((Class) ResponseTransID.class);
    }

    public ResponseTransID(final String _tranID, final int _userID, final int _accountID, final byte _gameID, final short _serverID) {
        this.tranID = _tranID;
        this.userID = _userID;
        this.accountID = _accountID;
        this.gameID = _gameID;
        this.serverID = _serverID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.tranID);
        this.yos.writeInt(this.userID);
        this.yos.writeInt(this.accountID);
        this.yos.writeByte(this.gameID);
        this.yos.writeShort(this.serverID);
    }
}
