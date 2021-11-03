// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class EquipmentEnhanceChangeNotify extends AbsResponseMessage {

    private static Logger log;
    private byte bodypartIndex;
    private int equipmentInsID;
    private byte[][] enhanceData;
    private short flashPNG;
    private short flashANU;
    private byte body;
    private String changeValue;
    private byte isDamage;

    static {
        EquipmentEnhanceChangeNotify.log = Logger.getLogger((Class) EquipmentEnhanceChangeNotify.class);
    }

    public EquipmentEnhanceChangeNotify(final int _equipmentInsID, final byte[][] _enhanceData, final short _flashPNG, final short _flashANU, final int _body, final String _changeValue) {
        this.equipmentInsID = _equipmentInsID;
        this.enhanceData = _enhanceData;
        this.flashPNG = _flashPNG;
        this.flashANU = _flashANU;
        this.body = (byte) _body;
        this.changeValue = _changeValue;
        this.isDamage = 0;
    }

    public EquipmentEnhanceChangeNotify(final int _equipmentInsID, final byte[][] _enhanceData, final short _flashPNG, final short _flashANU, final int _body, final String _changeValue, final byte _isDamage) {
        this.equipmentInsID = _equipmentInsID;
        this.enhanceData = _enhanceData;
        this.flashPNG = _flashPNG;
        this.flashANU = _flashANU;
        this.body = (byte) _body;
        this.changeValue = _changeValue;
        this.isDamage = _isDamage;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.isDamage);
        this.yos.writeInt(this.equipmentInsID);
        this.yos.writeByte(this.body);
        this.yos.writeShort(this.flashPNG);
        this.yos.writeShort(this.flashANU);
        this.yos.writeUTF(this.changeValue);
        EquipmentEnhanceChangeNotify.log.info((Object) ("changeValue-->" + this.changeValue));
        this.yos.writeByte(this.enhanceData.length);
        for (int i = 0; i < this.enhanceData.length; ++i) {
            if (this.enhanceData[i][0] == 1) {
                this.yos.writeByte(this.enhanceData[i][1] + 1);
            } else {
                this.yos.writeByte(0);
            }
        }
    }
}
