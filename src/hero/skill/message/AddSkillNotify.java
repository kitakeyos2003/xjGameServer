// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import hero.item.Weapon;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.ActiveSkill;
import hero.player.HeroPlayer;
import hero.share.EVocation;
import hero.skill.Skill;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class AddSkillNotify extends AbsResponseMessage {

    private static Logger log;
    private Skill skill;
    private EVocation vocation;

    static {
        AddSkillNotify.log = Logger.getLogger((Class) AddSkillNotify.class);
    }

    public AddSkillNotify(final Skill _skill, final HeroPlayer _player) {
        this.skill = _skill;
        this.vocation = _player.getVocation();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        byte vocationType = 0;
        for (int i = 0; i < this.skill.learnerVocation.length; ++i) {
            if (this.skill.learnerVocation[i] == this.vocation) {
                vocationType = this.vocation.value();
                break;
            }
            vocationType = this.vocation.getBasicVoction().value();
        }
        this.yos.writeByte(this.skill.getType().value());
        this.yos.writeInt(this.skill.id);
        this.yos.writeUTF(this.skill.name);
        this.yos.writeByte(this.skill.level);
        this.yos.writeByte(this.skill.skillRank);
        this.yos.writeShort(this.skill.iconID);
        this.yos.writeUTF(this.skill.description);
        if (this.skill.next != null) {
            this.yos.writeByte(1);
            this.yos.writeUTF(this.skill.next.description);
        } else {
            this.yos.writeByte(0);
        }
        this.yos.writeShort(this.skill.skillPoints);
        if (this.skill.next != null) {
            this.yos.writeInt(this.skill.learnFreight);
        } else {
            this.yos.writeInt(0);
        }
        this.yos.writeByte(vocationType);
        if (this.skill instanceof ActiveSkill) {
            ActiveSkill activeSkill = (ActiveSkill) this.skill;
            ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
            this.yos.writeByte(skillUnit.activeSkillType.value());
            this.yos.writeShort(activeSkill.coolDownID);
            this.yos.writeInt(activeSkill.coolDownTime);
            this.yos.writeInt(activeSkill.reduceCoolDownTime);
            this.yos.writeShort(skillUnit.releaseTime * 1000.0f);
            if (this.skill.needWeaponType == null) {
                this.yos.writeByte(1);
                this.yos.writeByte(0);
            } else {
                byte size = (byte) this.skill.needWeaponType.size();
                this.yos.writeByte(size);
                for (int j = 0; j < size; ++j) {
                    this.yos.writeShort(this.skill.needWeaponType.get(j).getID());
                }
            }
            this.yos.writeByte(skillUnit.isNeedTarget());
            this.yos.writeByte(skillUnit.targetType.value());
            this.yos.writeByte(skillUnit.targetDistance);
            this.yos.writeShort(skillUnit.consumeHp);
            this.yos.writeShort(skillUnit.consumeMp);
            this.yos.writeByte(skillUnit.consumeFp);
            this.yos.writeByte(skillUnit.consumeGp);
            this.yos.writeShort(skillUnit.releaseImageID);
            AddSkillNotify.log.info((Object) ("0x1002 is skill releaseImageID:" + skillUnit.releaseImageID));
            this.yos.writeShort(skillUnit.releaseAnimationID);
            AddSkillNotify.log.info((Object) ("0x1002 is skill releaseAnimationID:" + skillUnit.releaseAnimationID));
            this.yos.writeByte(skillUnit.animationAction);
            this.yos.writeByte(skillUnit.tierRelation);
            this.yos.writeByte(skillUnit.releaseHeightRelation);
        }
    }
}
