// 
// Decompiled by Procyon v0.5.36
// 
package hero.duel.message;

import java.io.IOException;
import hero.duel.service.DuelServiceImpl;
import hero.duel.service.PvpServerConfig;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseDuel extends AbsResponseMessage {

    private static final byte CONFIM = 1;
    private static final byte BEGIN = 2;
    private static final byte END = 3;
    public static final byte END_TYPE_OF_GENERIC = 0;
    public static final byte END_TYPE_OF_OFFLINE = 1;
    public static final byte END_TYPE_OF_TIMEOUT = 2;
    private byte type;
    private int objectID;
    private String nickname;
    private byte endType;

    public ResponseDuel(final int _objectID, final String _nickname) {
        this.type = 1;
        this.objectID = _objectID;
        this.nickname = _nickname;
    }

    public ResponseDuel(final int _objectID) {
        this.type = 2;
        this.objectID = _objectID;
    }

    public ResponseDuel(final byte _endType) {
        this.type = 3;
        this.endType = _endType;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.type);
        switch (this.type) {
            case 1: {
                this.yos.writeInt(this.objectID);
                this.yos.writeUTF(this.nickname);
                break;
            }
            case 2: {
                this.yos.writeInt(this.objectID);
                this.yos.writeByte(DuelServiceImpl.getInstance().getConfig().duel_time_alert_interval);
                this.yos.writeByte(DuelServiceImpl.getInstance().getConfig().duel_count_down);
                this.yos.writeShort(DuelServiceImpl.getInstance().getConfig().duel_sum_time);
                break;
            }
            case 3: {
                this.yos.writeByte(this.endType);
                break;
            }
        }
    }
}
