// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.share.EVocation;
import hero.share.MagicFastnessList;
import hero.item.detail.BodyPartOfEquipment;
import hero.item.detail.EGoodsType;
import hero.share.AccessorialOriginalAttribute;

public abstract class EqGoods extends Goods {

    public AccessorialOriginalAttribute atribute;

    public EqGoods(final short stackNums) {
        super(stackNums);
        this.atribute = new AccessorialOriginalAttribute();
    }

    @Override
    public EGoodsType getGoodsType() {
        return null;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    public abstract short getDurabilityConvertRate();

    public abstract int getMaxDurabilityPoint();

    public abstract BodyPartOfEquipment getWearBodyPart();

    public abstract byte[] getFixPropertyBytes();

    public abstract boolean existSeal();

    public abstract MagicFastnessList getMagicFastnessList();

    public abstract short getImageID();

    public abstract short getAnimationID();

    public abstract boolean canBeUse(final EVocation p0);

    public abstract byte getBindType();

    public abstract int getEquipmentType();
}
