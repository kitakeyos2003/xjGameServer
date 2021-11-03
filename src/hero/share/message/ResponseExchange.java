// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import hero.item.Weapon;
import hero.item.EquipmentInstance;
import hero.item.detail.EGoodsTrait;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseExchange extends AbsResponseMessage {

    private byte type;
    private int exchangeID;
    private String nickname;
    private int money;
    private String goodsName;
    private short goodsIcon;
    private short goodsNum;
    private EGoodsTrait trait;
    private String goodsDes;
    private boolean isEquipment;
    private EquipmentInstance instance;
    private short gridIndex;

    public ResponseExchange(final int _exchangeID, final String _nickname) {
        this.type = 1;
        this.exchangeID = _exchangeID;
        this.nickname = _nickname;
    }

    public ResponseExchange(final int _money) {
        this.type = 2;
        this.money = _money;
    }

    public ResponseExchange(final String _goodsName, final short _goodsIcon, final short _goodsNum, final EGoodsTrait _goodsTrait, final String _goodsDes) {
        this.type = 4;
        this.isEquipment = false;
        this.goodsName = _goodsName;
        this.goodsIcon = _goodsIcon;
        this.goodsNum = _goodsNum;
        this.trait = _goodsTrait;
        this.goodsDes = _goodsDes;
    }

    public ResponseExchange(final EquipmentInstance _instance) {
        this.type = 4;
        this.isEquipment = true;
        this.instance = _instance;
    }

    public ResponseExchange(final byte _type) {
        this.type = _type;
    }

    public ResponseExchange(final byte _type, final short _gridIndex) {
        this.type = _type;
        this.gridIndex = _gridIndex;
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
                this.yos.writeUTF(this.nickname);
                this.yos.writeInt(this.exchangeID);
                break;
            }
            case 2: {
                this.yos.writeInt(this.money);
                break;
            }
            case 4: {
                if (!this.isEquipment) {
                    this.yos.writeByte(0);
                    this.yos.writeUTF(this.goodsName);
                    this.yos.writeShort(this.goodsIcon);
                    this.yos.writeShort(this.goodsNum);
                    this.yos.writeByte(this.trait.value());
                    this.yos.writeUTF(this.goodsDes);
                    break;
                }
                this.yos.writeByte(1);
                this.yos.writeShort(this.instance.getArchetype().getIconID());
                StringBuffer name = new StringBuffer();
                name.append(this.instance.getArchetype().getName());
                int level = this.instance.getGeneralEnhance().getLevel();
                if (level > 0) {
                    name.append("+");
                    name.append(level);
                }
                int flash = this.instance.getGeneralEnhance().getFlash();
                if (flash > 0) {
                    name.append("(\u95ea");
                    name.append(flash);
                    name.append(")");
                }
                this.yos.writeUTF(name.toString());
                this.yos.writeShort(1);
                if (this.instance.getArchetype() instanceof Weapon) {
                    this.yos.writeByte(1);
                    this.yos.writeBytes(this.instance.getArchetype().getFixPropertyBytes());
                    this.yos.writeByte(this.instance.isBind());
                    if (this.instance.getArchetype().existSeal()) {
                        this.yos.writeByte(true);
                    } else {
                        this.yos.writeByte(false);
                    }
                    this.yos.writeShort(this.instance.getCurrentDurabilityPoint());
                    this.yos.writeInt(this.instance.getArchetype().getRetrievePrice());
                    this.yos.writeUTF(this.instance.getGeneralEnhance().getUpEndString());
                    this.yos.writeShort(this.instance.getGeneralEnhance().getFlashView()[0]);
                    this.yos.writeShort(this.instance.getGeneralEnhance().getFlashView()[1]);
                    this.yos.writeByte(this.instance.getGeneralEnhance().detail.length);
                    for (int j = 0; j < this.instance.getGeneralEnhance().detail.length; ++j) {
                        if (this.instance.getGeneralEnhance().detail[j][0] == 1) {
                            this.yos.writeByte(this.instance.getGeneralEnhance().detail[j][1] + 1);
                        } else {
                            this.yos.writeByte(0);
                        }
                    }
                    break;
                }
                this.yos.writeByte(2);
                this.yos.writeBytes(this.instance.getArchetype().getFixPropertyBytes());
                this.yos.writeByte(this.instance.isBind());
                this.yos.writeByte(this.instance.existSeal());
                this.yos.writeShort(this.instance.getCurrentDurabilityPoint());
                this.yos.writeInt(this.instance.getArchetype().getRetrievePrice());
                this.yos.writeUTF(this.instance.getGeneralEnhance().getUpEndString());
                this.yos.writeShort(this.instance.getGeneralEnhance().getArmorFlashView()[0]);
                this.yos.writeShort(this.instance.getGeneralEnhance().getArmorFlashView()[1]);
                this.yos.writeByte(this.instance.getGeneralEnhance().detail.length);
                for (int j = 0; j < this.instance.getGeneralEnhance().detail.length; ++j) {
                    if (this.instance.getGeneralEnhance().detail[j][0] == 1) {
                        this.yos.writeByte(this.instance.getGeneralEnhance().detail[j][1] + 1);
                    } else {
                        this.yos.writeByte(0);
                    }
                }
                break;
            }
            case 5: {
            }
            case 6: {
            }
            case 10: {
                this.yos.writeByte(-1);
                break;
            }
            case 11: {
                this.yos.writeShort(this.gridIndex);
                break;
            }
        }
    }
}
