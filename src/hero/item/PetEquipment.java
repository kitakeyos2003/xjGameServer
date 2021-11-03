// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.item.detail.BodyPartOfEquipment;
import hero.item.detail.EGoodsType;
import hero.share.EMagic;
import hero.share.MagicFastnessList;
import hero.item.detail.EPetBodyPartOfEquipment;

public abstract class PetEquipment extends EqGoods {

    protected EPetBodyPartOfEquipment bodyPart;
    protected byte durabilityConvertRate;
    protected int maxDurabilityPoint;
    protected boolean repairable;
    private short imageID;
    protected float immobilityTime;
    protected boolean existSeal;
    protected byte bindType;
    private MagicFastnessList magicFastnessList;
    protected byte[] fixProperty;
    public static final int DURA_REDUCE_PARA_OF_WEAPON = 60;
    public static final int DURA_REDUCE_PARA_OF_ARMOR = 40;
    public static final int BAD_NAME_VIEW_COLOR = 6052180;
    public static final int TYPE_WEAPON = 1;
    public static final int TYPE_ARMOR = 2;
    public static final byte BIND_TYPE_OF_NOT = 1;
    public static final byte BIND_TYPE_OF_WEAR = 2;
    public static final byte BIND_TYPE_OF_PICK = 3;

    @Override
    public short getImageID() {
        return this.imageID;
    }

    public void setImageID(final short _imageID) {
        this.imageID = _imageID;
    }

    @Override
    public short getDurabilityConvertRate() {
        return this.durabilityConvertRate;
    }

    public void setMaxDurabilityPoint(final int _durabilityPoint) {
        this.maxDurabilityPoint = _durabilityPoint;
    }

    @Override
    public int getMaxDurabilityPoint() {
        return this.maxDurabilityPoint;
    }

    public void setRepairable(final boolean _yesOrNo) {
        this.repairable = _yesOrNo;
    }

    public boolean repairable() {
        return this.repairable;
    }

    @Override
    public boolean exchangeable() {
        return this.bindType != 3;
    }

    public void setImmobilityTime(final float _immobilityTime) {
        this.immobilityTime = _immobilityTime;
    }

    public float getImmobilityTime() {
        return this.immobilityTime;
    }

    @Override
    public EPetBodyPartOfEquipment getWearBodyPart() {
        return this.bodyPart;
    }

    public void setWearBodyPart(final EPetBodyPartOfEquipment _bodyPart) {
        this.bodyPart = _bodyPart;
    }

    public void setMagicFastness(final EMagic _magic, final int _value) {
        if (_value <= 0) {
            return;
        }
        if (this.magicFastnessList == null) {
            this.magicFastnessList = new MagicFastnessList();
        }
        this.magicFastnessList.add(_magic, _value);
    }

    @Override
    public MagicFastnessList getMagicFastnessList() {
        return this.magicFastnessList;
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    @Override
    public abstract int getEquipmentType();

    public void setSeal() {
        this.existSeal = true;
    }

    @Override
    public boolean existSeal() {
        return this.existSeal;
    }

    public void setBindType(final byte _type) {
        this.bindType = _type;
    }

    @Override
    public byte getBindType() {
        return this.bindType;
    }

    public void setFixPropertyBytes(final byte[] _fixProperty) {
        this.fixProperty = _fixProperty;
    }

    @Override
    public byte[] getFixPropertyBytes() {
        return this.fixProperty;
    }

    public PetEquipment() {
        super((short) 1);
        this.bindType = 1;
    }

    @Override
    public EGoodsType getGoodsType() {
        return EGoodsType.PET_EQUIQ_GOODS;
    }
}
