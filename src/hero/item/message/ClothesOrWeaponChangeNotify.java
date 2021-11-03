// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.message;

import java.io.IOException;
import hero.item.Armor;
import hero.item.Weapon;
import hero.player.service.PlayerServiceImpl;
import hero.player.service.PlayerConfig;
import hero.item.EqGoods;
import hero.item.detail.EBodyPartOfEquipment;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ClothesOrWeaponChangeNotify extends AbsResponseMessage {

    private HeroPlayer player;
    private EBodyPartOfEquipment equipmentType;
    private short imageID;
    private short animationID;
    private boolean isUnload;
    private byte enhanceLevel;
    private EqGoods eqGood;
    private short enhancePNG;
    private short enhanceANU;

    public ClothesOrWeaponChangeNotify(final HeroPlayer _player, final EBodyPartOfEquipment _equipmentType, final short _imageID, final short _animationID, final byte _enhanceLevel, final EqGoods _eqGood, final boolean _isUnload, final short _enhancePNG, final short _enhanceANU) {
        this.player = _player;
        this.equipmentType = _equipmentType;
        this.imageID = _imageID;
        this.animationID = _animationID;
        this.enhanceLevel = _enhanceLevel;
        this.eqGood = _eqGood;
        this.isUnload = _isUnload;
        this.enhancePNG = _enhancePNG;
        this.enhanceANU = _enhanceANU;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getID());
        this.yos.writeByte(this.equipmentType.value());
        this.yos.writeByte(this.isUnload);
        if (this.isUnload) {
            if (this.equipmentType == EBodyPartOfEquipment.BOSOM) {
                this.yos.writeShort(0);
                this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesImageID(this.player.getSex()));
                this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesAnimation(this.player.getSex()));
            }
        } else {
            if (this.equipmentType == EBodyPartOfEquipment.WEAPON) {
                this.yos.writeShort(((Weapon) this.eqGood).getLightID());
                this.yos.writeShort(((Weapon) this.eqGood).getLightAnimation());
                this.yos.writeShort(((Weapon) this.eqGood).getWeaponType().getID());
            } else {
                this.yos.writeByte(((Armor) this.eqGood).getDistinguish());
            }
            if (this.equipmentType == EBodyPartOfEquipment.WEAPON || this.equipmentType == EBodyPartOfEquipment.BOSOM || this.equipmentType == EBodyPartOfEquipment.HEAD) {
                short equipLevel = 0;
                if (this.eqGood instanceof Armor) {
                    equipLevel = (short) ((Armor) this.eqGood).getNeedLevel();
                } else if (this.eqGood instanceof Weapon) {
                    equipLevel = (short) ((Weapon) this.eqGood).getNeedLevel();
                }
                this.yos.writeShort(equipLevel);
                this.yos.writeShort(this.imageID);
                this.yos.writeShort(this.animationID);
            }
            this.yos.writeShort(this.enhancePNG);
            this.yos.writeShort(this.enhanceANU);
        }
    }
}
