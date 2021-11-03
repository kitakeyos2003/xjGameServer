// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.postbox;

import hero.item.dictionary.GoodsContents;
import java.util.Date;
import hero.item.EquipmentInstance;
import hero.item.SingleGoods;

public class Mail {

    private int id;
    private int receiverUserID;
    private String receiverName;
    private String senderName;
    private byte social;
    private byte contentType;
    private int money;
    private int gamePoint;
    private SingleGoods singleGoods;
    private short singleGoodsNumber;
    private String content;
    private String title;
    private EquipmentInstance equipment;
    private boolean readFinish;
    private boolean attachment;
    private Date date;
    public static final byte TYPE_OF_MONTY = 0;
    public static final byte TYPE_OF_GAME_POINT = 1;
    public static final byte TYPE_OF_SINGLE_GOODS = 2;
    public static final byte TYPE_OF_EQUIPMENT = 3;
    public static final byte TYPE_OF_TXT = 4;

    public Mail(final int _id, final int _receiver_uid, final String _receiver, final String _sender, final byte _type, final int _numeric, final String _content, final String _title, final Date _date, final byte _social) {
        this.readFinish = false;
        this.attachment = false;
        this.id = _id;
        this.receiverUserID = _receiver_uid;
        this.receiverName = _receiver;
        this.senderName = _sender;
        this.contentType = _type;
        this.content = _content;
        this.title = _title;
        this.date = _date;
        this.social = _social;
        if (this.content == null) {
            this.content = "";
        }
        switch (this.contentType) {
            case 0: {
                this.money = _numeric;
                this.attachment = true;
                break;
            }
            case 1: {
                this.gamePoint = _numeric;
                this.attachment = true;
                break;
            }
        }
    }

    public Mail(final int _id, final int _receiver_uid, final String _receiver, final String _sender, final byte _type, final int _singleGoodsID, final short _singleGoodsNumber, final EquipmentInstance _equipment, final String _content, final String _title, final Date _date, final byte _social) {
        this.readFinish = false;
        this.attachment = false;
        this.id = _id;
        this.receiverUserID = _receiver_uid;
        this.receiverName = _receiver;
        this.senderName = _sender;
        this.contentType = _type;
        this.content = _content;
        this.title = _title;
        this.date = _date;
        this.social = _social;
        if (this.content == null) {
            this.content = "";
        }
        switch (this.contentType) {
            case 2: {
                this.singleGoods = (SingleGoods) GoodsContents.getGoods(_singleGoodsID);
                this.singleGoodsNumber = _singleGoodsNumber;
                this.attachment = true;
                break;
            }
            case 3: {
                this.equipment = _equipment;
                this.attachment = true;
                break;
            }
        }
    }

    public int getID() {
        return this.id;
    }

    public int getReceiverUserID() {
        return this.receiverUserID;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public String getSender() {
        return this.senderName;
    }

    public byte getType() {
        return this.contentType;
    }

    public int getMoney() {
        return this.money;
    }

    public int getGamePoint() {
        return this.gamePoint;
    }

    public SingleGoods getSingleGoods() {
        return this.singleGoods;
    }

    public short getSingleGoodsNumber() {
        return this.singleGoodsNumber;
    }

    public Date getDate() {
        return this.date;
    }

    public EquipmentInstance getEquipment() {
        return this.equipment;
    }

    public String getContent() {
        return this.content;
    }

    public String getTitle() {
        return this.title;
    }

    public void readMail() {
        this.readFinish = true;
    }

    public boolean getReadFinish() {
        return this.readFinish;
    }

    public boolean attachmentMail() {
        return this.attachment;
    }

    public byte getSocial() {
        return this.social;
    }

    public void removeAttachment() {
        this.gamePoint = 0;
        this.money = 0;
        this.singleGoods = null;
        this.singleGoodsNumber = 0;
        this.equipment = null;
        this.attachment = false;
        this.readFinish = true;
        this.contentType = 4;
    }
}
