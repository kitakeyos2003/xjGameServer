// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.unit;

public abstract class SkillUnit implements Cloneable {

    public int id;
    public String name;
    public int additionalSEID;

    public SkillUnit(final int _id) {
        this.id = _id;
    }

    public SkillUnit clone() throws CloneNotSupportedException {
        return (SkillUnit) super.clone();
    }
}
