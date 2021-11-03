// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill;

import hero.skill.detail.ESkillType;
import hero.pet.PetKind;

public abstract class PetSkill implements Cloneable {

    public int id;
    public String name;
    public int level;
    public byte getFrom;
    public int needLevel;
    public PetKind petKind;
    public short iconID;
    public String description;
    public boolean isNewSkill;
    public int _lowLevelSkillID;

    public PetSkill(final int id, final String name) {
        this.isNewSkill = true;
        this._lowLevelSkillID = -1;
        this.id = id;
        this.name = name;
    }

    public boolean isSameName(final PetSkill _otherSkill) {
        return this.name.equals(_otherSkill.name);
    }

    public PetSkill clone() throws CloneNotSupportedException {
        PetSkill skill = (PetSkill) super.clone();
        return skill;
    }

    public abstract ESkillType getType();
}
