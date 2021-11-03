// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class Warning extends AbsResponseMessage {

    private static Logger log;
    private String warningContent;
    private byte uiType;
    private short time;
    private boolean fullScreen;
    private byte event;
    private int signID;
    private short returnCommandCode;
    public static final byte UI_STRING_TIP = 0;
    public static final byte UI_TOOLTIP_TIP = 1;
    public static final byte UI_TOOLTIP_AND_EVENT_TIP = 2;
    public static final byte UI_COMPLEX_TIP = 3;
    public static final byte UI_TOOLTIP_CONFIM_CANCEL_TIP = 4;
    public static final byte SUBFUNCTION_UI_POPUP_REVIVE_CHARGE = 0;
    public static final byte SUBFUNCTION_UI_POPUP_COMM_CHARGE = 1;
    public static final byte SUBFUNCTION_UI_TASK_PUSH_COMM = 2;
    public static final byte SUBFUNCTION_UI_TASK_PUSH_COMM_CONFIRM = 3;
    public static final byte SUBFUNCTION_UI_TASK_PUSH_COMM_SPECIAL = 4;
    public static final byte SUBFUNCTION_UI_CHARGEUP = 5;
    public static final byte EVENT_SERVER_TRANSFER_NPC = 6;
    public static final byte EVENT_SERVER_TRANSFER_MAP = 7;
    public static final byte EVENT_SERVER_NEED_RETURN = 8;

    static {
        Warning.log = Logger.getLogger((Class) Warning.class);
    }

    public Warning(final String _content, final byte _uiType, final short _commandCode) {
        this.fullScreen = false;
        this.warningContent = _content;
        this.uiType = _uiType;
        this.returnCommandCode = _commandCode;
        this.event = 8;
    }

    public Warning(final String _content, final byte _uiType, final byte _event, final int _signID, final int _time) {
        this.fullScreen = false;
        this.warningContent = _content;
        this.uiType = _uiType;
        this.event = _event;
        this.signID = _signID;
        this.time = (short) _time;
    }

    public Warning(final String _content, final byte _uiType, final byte _event) {
        this.fullScreen = false;
        this.warningContent = _content;
        this.uiType = _uiType;
        this.event = _event;
    }

    public Warning(final String _content, final byte _uiType) {
        this.fullScreen = false;
        this.warningContent = _content;
        this.uiType = _uiType;
    }

    public Warning(final String _content) {
        this.fullScreen = false;
        this.uiType = 0;
        this.warningContent = _content;
    }

    public void fullScreenShow() {
        this.fullScreen = true;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.uiType);
        this.yos.writeUTF(this.warningContent);
        this.yos.writeByte(this.fullScreen);
        if (this.uiType == 2) {
            this.yos.writeByte(this.event);
        } else if (this.uiType == 3) {
            this.yos.writeByte(this.event);
            this.yos.writeInt(this.signID);
            this.yos.writeShort(this.time);
        } else if (this.uiType == 4) {
            this.yos.writeByte((byte) 8);
            this.yos.writeShort(this.returnCommandCode);
        }
    }
}
