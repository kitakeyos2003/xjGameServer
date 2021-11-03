// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import hero.item.EquipmentInstance;
import hero.micro.teach.MasterApprentice;
import hero.guild.Guild;
import hero.micro.service.MicroServiceImpl;
import hero.pet.message.PetChangeNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.skill.service.SkillServiceImpl;
import hero.pet.Pet;
import hero.pet.service.PetServiceImpl;
import hero.item.Weapon;
import hero.player.service.PlayerServiceImpl;
import hero.player.service.PlayerConfig;
import hero.item.Armor;
import hero.share.EVocationType;
import hero.micro.teach.TeachService;
import hero.guild.service.GuildServiceImpl;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class PlayerRefreshNotify extends AbsResponseMessage {

    private HeroPlayer emerger;

    public PlayerRefreshNotify(final HeroPlayer emerger) {
        this.emerger = emerger;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.emerger.getID());
        this.yos.writeUTF(this.emerger.getName());
        this.yos.writeShort(this.emerger.getLevel());
        this.yos.writeByte(this.emerger.getSex().value());
        this.yos.writeByte(this.emerger.getClan().getID());
        this.yos.writeUTF(this.emerger.spouse);
        Guild guild = GuildServiceImpl.getInstance().getGuild(this.emerger.getGuildID());
        if (guild != null) {
            this.yos.writeUTF(guild.getName());
            this.yos.writeUTF(GuildServiceImpl.getInstance().getMemberRank(this.emerger));
        } else {
            this.yos.writeUTF("");
            this.yos.writeUTF("");
        }
        MasterApprentice masterApprentice = TeachService.get(this.emerger.getUserID());
        if (masterApprentice != null) {
            this.yos.writeByte(1);
            this.yos.writeUTF((masterApprentice.masterName == null) ? "" : masterApprentice.masterName);
            this.yos.writeByte(masterApprentice.apprenticeNumber);
            for (int j = 0; j < masterApprentice.apprenticeNumber; ++j) {
                this.yos.writeUTF((masterApprentice.apprenticeList[j].name == null) ? "" : masterApprentice.apprenticeList[j].name);
            }
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeByte(this.emerger.getVocation().value());
        this.yos.writeInt(this.emerger.getHp());
        this.yos.writeInt(this.emerger.getActualProperty().getHpMax());
        this.yos.writeByte(this.emerger.getVocation().getType().getID());
        if (this.emerger.getVocation().getType() == EVocationType.PHYSICS) {
            this.yos.writeInt(this.emerger.getForceQuantity());
            this.yos.writeInt(100);
        } else {
            this.yos.writeInt(this.emerger.getMp());
            this.yos.writeInt(this.emerger.getActualProperty().getMpMax());
        }
        this.yos.writeByte(this.emerger.getCellX());
        this.yos.writeByte(this.emerger.getCellY());
        this.yos.writeByte(this.emerger.getDirection());
        EquipmentInstance ei = this.emerger.getBodyWear().getBosom();
        if (ei != null) {
            this.yos.writeShort(ei.getArchetype().getNeedLevel());
            this.yos.writeShort(ei.getArchetype().getImageID());
            this.yos.writeShort(ei.getArchetype().getAnimationID());
            this.yos.writeByte(((Armor) ei.getArchetype()).getDistinguish());
            this.yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[0]);
            this.yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[1]);
        } else {
            this.yos.writeShort(0);
            this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesImageID(this.emerger.getSex()));
            this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesAnimation(this.emerger.getSex()));
            this.yos.writeByte(0);
            this.yos.writeShort(-1);
            this.yos.writeShort(-1);
        }
        ei = this.emerger.getBodyWear().getHead();
        if (ei != null) {
            this.yos.writeShort(ei.getArchetype().getImageID());
            this.yos.writeShort(ei.getArchetype().getAnimationID());
            this.yos.writeByte(((Armor) ei.getArchetype()).getDistinguish());
        } else {
            this.yos.writeShort(-1);
            this.yos.writeShort(-1);
            this.yos.writeByte(0);
        }
        ei = this.emerger.getBodyWear().getWeapon();
        if (ei != null) {
            this.yos.writeByte(1);
            this.yos.writeShort(ei.getArchetype().getNeedLevel());
            this.yos.writeShort(((Weapon) ei.getArchetype()).getImageID());
            this.yos.writeShort(((Weapon) ei.getArchetype()).getAnimationID());
            this.yos.writeShort(((Weapon) ei.getArchetype()).getLightID());
            this.yos.writeShort(((Weapon) ei.getArchetype()).getLightAnimation());
            this.yos.writeShort(((Weapon) ei.getArchetype()).getWeaponType().getID());
            this.yos.writeShort(ei.getGeneralEnhance().getFlashView()[0]);
            this.yos.writeShort(ei.getGeneralEnhance().getFlashView()[1]);
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeByte(this.emerger.isVisible());
        HashMap<Integer, Pet> viewPetMap = PetServiceImpl.getInstance().getViewPetList(this.emerger.getUserID());
        if (viewPetMap != null && viewPetMap.size() > 0) {
            int viewpetnum = viewPetMap.size();
            this.yos.writeByte(viewpetnum);
            for (final Pet pet : viewPetMap.values()) {
                this.yos.writeByte(pet.isView);
                this.yos.writeInt(pet.id);
                this.yos.writeShort(pet.imageID);
                this.yos.writeShort(pet.animationID);
                this.yos.writeShort(pet.pk.getType());
                this.yos.writeShort(pet.fun);
                PetServiceImpl.getInstance().writePetSkillID(pet, this.yos);
                if (pet.pk.getStage() == 2 && pet.pk.getType() == 2) {
                    PetServiceImpl.getInstance().reCalculatePetProperty(pet);
                    SkillServiceImpl.getInstance().petReleasePassiveSkill(pet, 1);
                }
                MapSynchronousInfoBroadcast.getInstance().put(this.emerger.where(), new PetChangeNotify(this.emerger.getID(), (byte) 2, pet.imageID, pet.pk.getType()), true, this.emerger.getID());
            }
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeByte(this.emerger.isSelling());
        if (this.emerger.isSelling()) {
            this.yos.writeUTF(MicroServiceImpl.getInstance().getStore(this.emerger.getUserID()).name);
        }
    }
}
