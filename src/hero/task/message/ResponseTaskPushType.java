// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseTaskPushType extends AbsResponseMessage {

    private static Logger log;
    private byte feeType;
    private byte limit;
    private String smsServer;
    private String smsContent;
    private String smsSeparator;
    private int smsCount;
    private String transID;
    private int smsFee;
    private String pCode;
    private byte proxyID;

    static {
        ResponseTaskPushType.log = Logger.getLogger((Class) ResponseTaskPushType.class);
    }

    public ResponseTaskPushType(final int _feeType, final int _limit) {
        this.feeType = (byte) _feeType;
        this.limit = (byte) _limit;
    }

    public ResponseTaskPushType(final int _feeType, final int _limit, final String _pCode, final byte _proxyID, final String _transID) {
        this.feeType = (byte) _feeType;
        this.limit = (byte) _limit;
        this.pCode = _pCode;
        this.proxyID = _proxyID;
        this.transID = _transID;
    }

    public ResponseTaskPushType(final int _feeType, final int _limit, final String _smsServer, final String _smsContent, final String _transID, final int _smsCount, final int _smsFee) {
        this.feeType = (byte) _feeType;
        this.limit = (byte) _limit;
        this.smsServer = _smsServer;
        this.smsContent = _smsContent;
        this.transID = _transID;
        this.smsCount = _smsCount;
        this.smsFee = _smsFee;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.feeType);
        ResponseTaskPushType.log.info((Object) ("\u4efb\u52a1\u8ba1\u8d39\u4e0b\u53d1\u8ba1\u8d39\u65b9\u5f0f\uff1a" + this.feeType));
        if (this.feeType == 1) {
            String contentTitle = String.valueOf(this.smsContent) + "#" + this.transID + "_" + String.valueOf(this.smsFee);
            String contentTail = "#ck";
            if (this.smsCount > 1) {
                contentTitle = String.valueOf(contentTitle) + "_";
            }
            this.yos.writeUTF(this.smsServer);
            this.yos.writeUTF(contentTitle);
            this.yos.writeUTF(contentTail);
            this.yos.writeUTF(this.transID);
            this.yos.writeByte(this.smsCount);
            ResponseTaskPushType.log.info((Object) ("\u77ed\u4fe1\u8ba1\u8d39\u65b9\u5f0f\uff1a" + contentTitle));
        } else if (this.feeType == 2) {
            this.yos.writeUTF(this.pCode);
            this.yos.writeByte(this.proxyID);
            this.yos.writeUTF(this.transID);
            ResponseTaskPushType.log.info((Object) ("\u7f51\u6e38\u8ba1\u8d39\u65b9\u5f0f\uff1a" + this.pCode));
        }
    }
}
