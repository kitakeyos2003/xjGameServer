// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.message;

import java.io.IOException;
import java.util.Iterator;
import hero.skill.PetActiveSkill;
import hero.skill.detail.ESkillType;
import hero.skill.PetSkill;
import java.util.List;
import hero.pet.Pet;
import yoyo.core.packet.AbsResponseMessage;

public class PetLearnSkillNotify extends AbsResponseMessage {

    Pet pet;
    List<PetSkill> learnPetSkillList;

    public PetLearnSkillNotify(final Pet pet, final List<PetSkill> learnPetSkillList) {
        this.pet = pet;
        this.learnPetSkillList = learnPetSkillList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.pet.id);
        this.yos.writeInt(this.learnPetSkillList.size());
        for (final PetSkill skill : this.learnPetSkillList) {
            this.yos.writeInt(skill.id);
            this.yos.writeUTF(skill.name);
            this.yos.writeInt(skill.level);
            this.yos.writeShort(skill.iconID);
            if (skill.getType() == ESkillType.ACTIVE) {
                PetActiveSkill activeSkill = (PetActiveSkill) skill;
                this.yos.writeShort(activeSkill.releaseAnimationID);
                this.yos.writeShort(activeSkill.activeAnimationID);
            } else {
                this.yos.writeShort(0);
                this.yos.writeShort(0);
            }
        }
    }
}
