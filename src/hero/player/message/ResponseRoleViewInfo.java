// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

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
import hero.micro.teach.TeachService;
import hero.guild.service.GuildServiceImpl;
import hero.player.HeroPlayer;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseRoleViewInfo extends AbsResponseMessage {

    private static Logger log;
    private HeroPlayer player;
    private boolean isNovice;

    static {
        ResponseRoleViewInfo.log = Logger.getLogger((Class) ResponseRoleViewInfo.class);
    }

    public ResponseRoleViewInfo(final HeroPlayer _player, final boolean _isNovice) {
        this.player = _player;
        this.isNovice = _isNovice;
    }

    public ResponseRoleViewInfo(final HeroPlayer _player) {
        this.player = _player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getID());
        this.yos.writeUTF(this.player.getName());
        this.yos.writeShort(this.player.getLevel());
        this.yos.writeInt(this.player.getMoney());
        this.yos.writeByte(this.player.getSex().value());
        this.yos.writeByte(this.player.getClan().getID());
        Guild tmp = GuildServiceImpl.getInstance().getGuild(this.player.getGuildID());
        if (tmp != null) {
            this.yos.writeUTF(tmp.getName());
            this.yos.writeUTF(GuildServiceImpl.getInstance().getMemberRank(this.player));
        } else {
            this.yos.writeUTF("");
            this.yos.writeUTF("");
        }
        MasterApprentice masterApprentice = TeachService.get(this.player.getUserID());
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
        this.yos.writeByte(this.player.getVocation().value());
        this.yos.writeInt(this.player.getHp());
        this.yos.writeInt(this.player.getActualProperty().getHpMax());
        this.yos.writeByte(this.player.getVocation().getType().getID());
        this.yos.writeInt(this.player.getMp());
        this.yos.writeInt(this.player.getActualProperty().getMpMax());
        this.yos.writeInt(this.player.getExp());
        this.yos.writeInt(this.player.getUpgradeNeedExp());
        this.yos.writeInt(this.player.getExpShow());
        this.yos.writeInt(this.player.getUpgradeNeedExpShow());
        this.yos.writeShort(this.player.getActualAttackImmobilityTime());
        this.yos.writeByte(this.player.getAttackRange());
        EquipmentInstance ei = this.player.getBodyWear().getBosom();
        if (ei != null) {
            this.yos.writeShort(ei.getArchetype().getImageID());
            ResponseRoleViewInfo.log.debug((Object) ("1 ei [" + ei.getArchetype().getID() + "] imageID=" + ei.getArchetype().getImageID()));
            this.yos.writeShort(ei.getArchetype().getAnimationID());
            this.yos.writeByte(((Armor) ei.getArchetype()).getDistinguish());
            this.yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[0]);
            this.yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[1]);
        } else {
            this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesImageID(this.player.getSex()));
            this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesAnimation(this.player.getSex()));
            this.yos.writeByte(0);
            this.yos.writeShort(-1);
            this.yos.writeShort(-1);
        }
        ei = this.player.getBodyWear().getHead();
        if (ei != null) {
            this.yos.writeShort(ei.getArchetype().getImageID());
            this.yos.writeShort(ei.getArchetype().getAnimationID());
            ResponseRoleViewInfo.log.debug((Object) ("2 ei [" + ei.getArchetype().getID() + "] imageID=" + ei.getArchetype().getImageID()));
            this.yos.writeByte(((Armor) ei.getArchetype()).getDistinguish());
        } else {
            this.yos.writeShort(-1);
            this.yos.writeShort(-1);
            this.yos.writeByte(0);
        }
        ei = this.player.getBodyWear().getWeapon();
        if (ei != null) {
            this.yos.writeByte(1);
            this.yos.writeShort(((Weapon) ei.getArchetype()).getImageID());
            this.yos.writeShort(((Weapon) ei.getArchetype()).getAnimationID());
            this.yos.writeShort(ei.getGeneralEnhance().getFlashView()[0]);
            this.yos.writeShort(ei.getGeneralEnhance().getFlashView()[1]);
            this.yos.writeShort(((Weapon) ei.getArchetype()).getLightID());
            this.yos.writeShort(((Weapon) ei.getArchetype()).getLightAnimation());
            this.yos.writeShort(((Weapon) ei.getArchetype()).getWeaponType().getID());
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeByte(this.isNovice);
        if (!this.isNovice) {
            HashMap<Integer, Pet> viewPetMap = PetServiceImpl.getInstance().getViewPetList(this.player.getUserID());
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
                    if (!pet.isView && pet.viewStatus == 1) {
                        PetServiceImpl.getInstance().hatchPet(this.player, pet);
                    }
                    PetServiceImpl.getInstance().writePetSkillID(pet, this.yos);
                    if (pet.pk.getStage() == 2 && pet.pk.getType() == 2) {
                        PetServiceImpl.getInstance().reCalculatePetProperty(pet);
                        SkillServiceImpl.getInstance().petReleasePassiveSkill(pet, 1);
                    }
                    MapSynchronousInfoBroadcast.getInstance().put(this.player.where(), new PetChangeNotify(this.player.getID(), (byte) 2, pet.imageID, pet.pk.getType()), true, this.player.getID());
                }
            } else {
                this.yos.writeByte(0);
            }
        }
        this.yos.writeByte(this.player.isSelling());
        if (this.player.isSelling()) {
            this.yos.writeUTF(MicroServiceImpl.getInstance().getStore(this.player.getUserID()).name);
        }
        this.yos.writeUTF(this.player.spouse);
        this.yos.writeShort(this.player.surplusSkillPoint);
        ResponseRoleViewInfo.log.info((Object) ("output size = " + String.valueOf(this.yos.size())));
    }
}
