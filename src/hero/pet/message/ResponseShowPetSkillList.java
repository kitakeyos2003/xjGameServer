// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import java.util.Iterator;
import hero.skill.PetPassiveSkill;
import hero.skill.PetActiveSkill;
import hero.pet.Pet;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseShowPetSkillList extends AbsResponseMessage {

    private Pet pet;

    public ResponseShowPetSkillList(final Pet pet) {
        this.pet = pet;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.pet.id);
        this.yos.writeInt(this.pet.petActiveSkillList.size());
        for (final PetActiveSkill skill : this.pet.petActiveSkillList) {
            this.yos.writeInt(skill.id);
            this.yos.writeUTF(skill.name);
            this.yos.writeInt(skill.level);
            this.yos.writeShort(skill.iconID);
            this.yos.writeByte(skill.getType().value());
            this.yos.writeUTF(skill.description);
            this.yos.writeInt(skill.coolDownTime);
            this.yos.writeInt(skill.reduceCoolDownTime);
            this.yos.writeByte(skill.isNeedTarget());
            this.yos.writeByte(skill.targetType.value());
            this.yos.writeByte(skill.targetDistance);
            this.yos.writeInt(skill.magicHarmHpValue);
            this.yos.writeInt(skill.physicsHarmValue);
            this.yos.writeByte(skill.rangeTargetNumber);
            this.yos.writeInt(skill.resumeHp);
            this.yos.writeInt(skill.resumeMp);
            this.yos.writeInt(skill.useMp);
            this.yos.writeUTF(skill.magicHarmType.getName());
            this.yos.writeShort(skill.releaseAnimationID);
            this.yos.writeShort(skill.activeAnimationID);
        }
        this.yos.writeInt(this.pet.petPassiveSkillList.size());
        for (final PetPassiveSkill skill2 : this.pet.petPassiveSkillList) {
            this.yos.writeInt(skill2.id);
            this.yos.writeUTF(skill2.name);
            this.yos.writeInt(skill2.level);
            this.yos.writeShort(skill2.iconID);
            this.yos.writeUTF(skill2.description);
        }
    }
}
