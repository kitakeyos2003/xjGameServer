// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import hero.micro.teach.MasterApprentice;
import hero.guild.Guild;
import hero.item.EquipmentInstance;
import hero.share.service.ME2ObjectList;
import hero.npc.Monster;
import hero.npc.dict.MonsterImageConfDict;
import hero.npc.dict.MonsterImageDict;
import hero.npc.Npc;
import hero.npc.dict.NpcImageConfDict;
import hero.npc.dict.NpcImageDict;
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
import hero.map.Map;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMapGameObjectList extends AbsResponseMessage {

    private static Logger log;
    private Map map;
    private short clientType;

    static {
        ResponseMapGameObjectList.log = Logger.getLogger((Class) ResponseMapGameObjectList.class);
    }

    public ResponseMapGameObjectList(final short _clientType, final Map _map) {
        this.clientType = _clientType;
        this.map = _map;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        ME2ObjectList npcList = this.map.getNpcList();
        ME2ObjectList monsterList = this.map.getMonsterList();
        ME2ObjectList otherPlayerList = this.map.getPlayerList();
        this.yos.writeShort((short) otherPlayerList.size());
        HeroPlayer otherPlayer = null;
        Npc npc = null;
        Monster monster = null;
        EquipmentInstance ei = null;
        Guild guild = null;
        for (int i = 0; i < otherPlayerList.size(); ++i) {
            otherPlayer = (HeroPlayer) otherPlayerList.get(i);
            this.yos.writeInt(otherPlayer.getID());
            this.yos.writeUTF(otherPlayer.getName());
            this.yos.writeShort(otherPlayer.getLevel());
            this.yos.writeByte(otherPlayer.getSex().value());
            this.yos.writeByte(otherPlayer.getClan().getID());
            this.yos.writeUTF(otherPlayer.spouse);
            guild = GuildServiceImpl.getInstance().getGuild(otherPlayer.getGuildID());
            if (guild != null) {
                this.yos.writeUTF(guild.getName());
                this.yos.writeUTF(GuildServiceImpl.getInstance().getMemberRank(otherPlayer));
            } else {
                this.yos.writeUTF("");
                this.yos.writeUTF("");
            }
            MasterApprentice masterApprentice = TeachService.get(otherPlayer.getUserID());
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
            this.yos.writeByte(otherPlayer.getVocation().value());
            this.yos.writeInt(otherPlayer.getHp());
            this.yos.writeInt(otherPlayer.getActualProperty().getHpMax());
            this.yos.writeByte(otherPlayer.getVocation().getType().getID());
            this.yos.writeInt(otherPlayer.getMp());
            this.yos.writeInt(otherPlayer.getActualProperty().getMpMax());
            this.yos.writeByte(otherPlayer.getCellX());
            this.yos.writeByte(otherPlayer.getCellY());
            this.yos.writeByte(otherPlayer.getDirection());
            ei = otherPlayer.getBodyWear().getBosom();
            if (ei != null) {
                this.yos.writeShort(ei.getArchetype().getNeedLevel());
                this.yos.writeShort(ei.getArchetype().getImageID());
                this.yos.writeShort(ei.getArchetype().getAnimationID());
                this.yos.writeByte(((Armor) ei.getArchetype()).getDistinguish());
                this.yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[0]);
                this.yos.writeShort(ei.getGeneralEnhance().getArmorFlashView()[1]);
            } else {
                this.yos.writeShort(0);
                this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesImageID(otherPlayer.getSex()));
                this.yos.writeShort(PlayerServiceImpl.getInstance().getConfig().getDefaultClothesAnimation(otherPlayer.getSex()));
                this.yos.writeByte(0);
                this.yos.writeShort(-1);
                this.yos.writeShort(-1);
            }
            ei = otherPlayer.getBodyWear().getHead();
            if (ei != null) {
                this.yos.writeShort(ei.getArchetype().getImageID());
                this.yos.writeShort(ei.getArchetype().getAnimationID());
                this.yos.writeByte(((Armor) ei.getArchetype()).getDistinguish());
            } else {
                this.yos.writeShort(-1);
                this.yos.writeShort(-1);
                this.yos.writeByte(0);
            }
            ei = otherPlayer.getBodyWear().getWeapon();
            if (ei != null) {
                this.yos.writeByte(1);
                this.yos.writeShort(ei.getArchetype().getNeedLevel());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getImageID());
                this.yos.writeShort(ei.getArchetype().getAnimationID());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getLightID());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getLightAnimation());
                this.yos.writeShort(((Weapon) ei.getArchetype()).getWeaponType().getID());
                this.yos.writeShort(ei.getGeneralEnhance().getFlashView()[0]);
                this.yos.writeShort(ei.getGeneralEnhance().getFlashView()[1]);
            } else {
                this.yos.writeByte(0);
            }
            this.yos.writeByte(!otherPlayer.isDead());
            this.yos.writeByte(otherPlayer.isVisible());
            HashMap<Integer, Pet> viewPetMap = PetServiceImpl.getInstance().getViewPetList(otherPlayer.getUserID());
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
                    MapSynchronousInfoBroadcast.getInstance().put(otherPlayer.where(), new PetChangeNotify(otherPlayer.getID(), (byte) 2, pet.imageID, pet.pk.getType()), true, otherPlayer.getID());
                }
            } else {
                this.yos.writeByte(0);
            }
            this.yos.writeByte(otherPlayer.isSelling());
            if (otherPlayer.isSelling()) {
                this.yos.writeUTF(MicroServiceImpl.getInstance().getStore(otherPlayer.getUserID()).name);
            }
        }
        if (this.map.fixedNpcImageIDList != null) {
            this.yos.writeByte(this.map.fixedNpcImageIDList.size());
            for (final short imageID : this.map.fixedNpcImageIDList) {
                byte[] imageBytes = NpcImageDict.getInstance().getImageBytes(imageID);
                NpcImageConfDict.Config npcConfig = NpcImageConfDict.get(imageID);
                this.yos.writeShort(imageID);
                this.yos.writeShort(npcConfig.animationID);
                this.yos.writeByte(npcConfig.npcGrid);
                this.yos.writeShort(npcConfig.npcHeight);
                this.yos.writeByte(npcConfig.shadowSize);
                if (3 != this.clientType) {
                    this.yos.writeShort(imageBytes.length);
                    this.yos.writeBytes(imageBytes);
                }
            }
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeShort((short) npcList.size());
        for (int k = 0; k < npcList.size(); ++k) {
            npc = (Npc) npcList.get(k);
            this.yos.writeInt(npc.getID());
            this.yos.writeUTF(npc.getTitle());
            this.yos.writeUTF(npc.getName());
            this.yos.writeByte(npc.getClan().getID());
            this.yos.writeUTF(npc.getHello());
            if (npc.getScreamContent() != null) {
                this.yos.writeByte(1);
                this.yos.writeUTF(npc.getScreamContent());
            } else {
                this.yos.writeByte(0);
            }
            this.yos.writeByte(npc.canInteract());
            this.yos.writeByte(npc.getCellX());
            this.yos.writeByte(npc.getCellY());
            if (npc.where().fixedNpcImageIDList == null || !npc.where().fixedNpcImageIDList.contains(npc.getImageID())) {
                this.yos.writeByte(1);
                byte[] imageBytes2 = NpcImageDict.getInstance().getImageBytes(npc.getImageID());
                this.yos.writeShort(npc.getImageID());
                this.yos.writeShort(npc.getAnimationID());
                NpcImageConfDict.Config npcConfig2 = NpcImageConfDict.get(npc.getImageID());
                this.yos.writeByte(npcConfig2.npcGrid);
                this.yos.writeShort(npcConfig2.npcHeight);
                this.yos.writeByte(npcConfig2.shadowSize);
                if (3 != this.clientType) {
                    this.yos.writeShort(imageBytes2.length);
                    this.yos.writeBytes(imageBytes2);
                }
            } else {
                this.yos.writeByte(0);
            }
            this.yos.writeByte(npc.getImageType());
            this.yos.writeShort(npc.getImageID());
            this.yos.writeShort(npc.getAnimationID());
        }
        if (this.map.fixedMonsterImageIDList != null) {
            this.yos.writeByte(this.map.fixedMonsterImageIDList.size());
            for (final short imageID2 : this.map.fixedMonsterImageIDList) {
                byte[] imageBytes3 = MonsterImageDict.getInstance().getMonsterImageBytes(imageID2);
                MonsterImageConfDict.Config monsterConfig = MonsterImageConfDict.get(imageID2);
                this.yos.writeShort(imageID2);
                ResponseMapGameObjectList.log.info(("\u602a\u7269imageID:" + imageID2));
                this.yos.writeShort(monsterConfig.animationID);
                ResponseMapGameObjectList.log.info(("\u602a\u7269animationID:" + monsterConfig.animationID));
                this.yos.writeByte(monsterConfig.grid);
                this.yos.writeShort(monsterConfig.monsterHeight);
                this.yos.writeByte(monsterConfig.shadowSize);
                if (3 != this.clientType) {
                    this.yos.writeShort(imageBytes3.length);
                    this.yos.writeBytes(imageBytes3);
                }
            }
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeShort((short) monsterList.size());
        ResponseMapGameObjectList.log.info(("monsterList.size()=" + monsterList.size()));
        for (int k = 0; k < monsterList.size(); ++k) {
            monster = (Monster) monsterList.get(k);
            this.yos.writeInt(monster.getID());
            this.yos.writeUTF(monster.getName());
            this.yos.writeShort(monster.getLevel());
            this.yos.writeByte(monster.getClan().getID());
            this.yos.writeByte(monster.isActiveAttackType());
            this.yos.writeByte(monster.getVocation().value());
            this.yos.writeByte(monster.getMonsterLevel().value());
            this.yos.writeByte(monster.getObjectLevel().value());
            this.yos.writeByte((monster.getTakeSoulUserID() > 0) ? 1 : 0);
            this.yos.writeInt(monster.getHp());
            this.yos.writeInt(monster.getActualProperty().getHpMax());
            this.yos.writeInt(monster.getMp());
            this.yos.writeInt(monster.getActualProperty().getMpMax());
            this.yos.writeByte(monster.getCellX());
            this.yos.writeByte(monster.getCellY());
            this.yos.writeByte(monster.getDirection());
            if (monster.where().fixedMonsterImageIDList == null || !monster.where().fixedMonsterImageIDList.contains(monster.getImageID())) {
                this.yos.writeByte(1);
                this.yos.writeShort(monster.getImageID());
                this.yos.writeShort(monster.getAnimationID());
                MonsterImageConfDict.Config config = MonsterImageConfDict.get(monster.getImageID());
                this.yos.writeByte(config.grid);
                this.yos.writeShort(config.monsterHeight);
                this.yos.writeByte(config.shadowSize);
                if (3 != this.clientType) {
                    byte[] monsterImage = MonsterImageDict.getInstance().getMonsterImageBytes(monster.getImageID());
                    this.yos.writeShort(monsterImage.length);
                    this.yos.writeBytes(monsterImage);
                }
            } else {
                this.yos.writeByte(0);
            }
            this.yos.writeShort(monster.getImageID());
            this.yos.writeShort(monster.getAnimationID());
            this.yos.writeByte(monster.isVisible());
        }
        ResponseMapGameObjectList.log.info(("output size = " + String.valueOf(this.yos.size())));
    }
}
