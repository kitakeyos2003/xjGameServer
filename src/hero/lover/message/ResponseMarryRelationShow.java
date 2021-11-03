// 
// Decompiled by Procyon v0.5.36
// 
package hero.lover.message;

import java.io.IOException;
import hero.item.EquipmentInstance;
import hero.item.Weapon;
import hero.player.service.PlayerConfig;
import hero.item.Armor;
import hero.item.service.GoodsDAO;
import hero.player.service.PlayerServiceImpl;
import hero.guild.service.GuildServiceImpl;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMarryRelationShow extends AbsResponseMessage {

    private byte relation;
    private HeroPlayer player;

    public ResponseMarryRelationShow(final byte relation, final HeroPlayer _player) {
        this.relation = relation;
        this.player = _player;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.relation);
        if (this.relation > 0) {
            this.yos.writeUTF(this.player.getName());
            this.yos.writeShort(this.player.getLevel());
            this.yos.writeByte(this.player.getSex().value());
            this.yos.writeByte(this.player.getClan().getID());
            this.yos.writeByte(this.player.getVocation().value());
            this.yos.writeUTF(GuildServiceImpl.getInstance().getGuildName(this.player));
            this.yos.writeInt(this.player.getLoverValue());
            this.yos.writeInt(this.player.loverDays(this.player));
            this.yos.writeInt(PlayerServiceImpl.getInstance().getPlayerLoverValueOrder(this.player.getUserID()));
            if (this.relation == 2) {
                this.yos.writeUTF(this.player.loverLever.getName());
            } else {
                this.yos.writeUTF("\u604b\u4eba");
            }
            if (!this.player.isEnable()) {
                GoodsDAO.loadPlayerWearGoods(this.player);
            }
            EquipmentInstance ei = this.player.getBodyWear().getBosom();
            if (ei != null) {
                this.yos.writeShort(ei.getArchetype().getNeedLevel());
                this.yos.writeShort(ei.getArchetype().getImageID());
                this.yos.writeShort(ei.getArchetype().getAnimationID());
                this.yos.writeByte(((Armor) ei.getArchetype()).getDistinguish());
            } else {
                this.yos.writeShort(0);
                this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesImageID(this.player.getSex()));
                this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesAnimation(this.player.getSex()));
                this.yos.writeByte(0);
            }
            ei = this.player.getBodyWear().getHead();
            if (ei != null) {
                this.yos.writeShort(ei.getArchetype().getImageID());
                this.yos.writeShort(ei.getArchetype().getAnimationID());
                this.yos.writeByte(((Armor) ei.getArchetype()).getDistinguish());
            } else {
                this.yos.writeShort(-1);
                this.yos.writeShort(-1);
                this.yos.writeByte(0);
            }
            ei = this.player.getBodyWear().getWeapon();
            if (ei != null) {
                this.yos.writeByte(1);
                this.yos.writeShort(ei.getArchetype().getNeedLevel());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getImageID());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getAnimationID());
                this.yos.writeByte(ei.getGeneralEnhance().getLevel());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getLightID());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getLightAnimation());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getWeaponType().getID());
            } else {
                this.yos.writeByte(0);
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
