// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.ActiveSkill;
import hero.player.HeroPlayer;
import hero.share.EVocation;
import hero.skill.Skill;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class SkillUpgradeNotify extends AbsResponseMessage {

    private static Logger log;
    private Skill skill;
    private EVocation vocation;
    private short surplusSkillPoint;

    static {
        SkillUpgradeNotify.log = Logger.getLogger((Class) SkillUpgradeNotify.class);
    }

    public SkillUpgradeNotify(final Skill _skill, final HeroPlayer _player) {
        this.skill = _skill;
        this.vocation = _player.getVocation();
        this.surplusSkillPoint = _player.surplusSkillPoint;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        byte vocationType = 0;
        boolean isLoad = false;
        for (int i = 0; i < this.skill.learnerVocation.length; ++i) {
            if (this.skill.learnerVocation[i] == this.vocation) {
                vocationType = this.skill.learnerVocation[i].value();
                SkillUpgradeNotify.log.info((Object) (String.valueOf(this.vocation.getDesc()) + " \u6210\u529f\u52a0\u8f7d" + this.skill.learnerVocation[i].getDesc() + "\u804c\u4e1a\u7684[" + this.skill.name + "]"));
                isLoad = true;
            } else if (this.skill.learnerVocation[i] == this.vocation.getBasicVoction()) {
                vocationType = this.skill.learnerVocation[i].value();
                SkillUpgradeNotify.log.info((Object) (String.valueOf(this.vocation.getDesc()) + " \u6210\u529f\u52a0\u8f7d" + this.skill.learnerVocation[i].getDesc() + "\u804c\u4e1a\u7684[" + this.skill.name + "]"));
                isLoad = true;
            } else if (i == this.skill.learnerVocation.length - 1 && !isLoad) {
                SkillUpgradeNotify.log.info((Object) ("warn:" + this.vocation.getDesc() + "\u52a0\u8f7d[" + this.skill.name + "]\u5931\u8d25"));
            }
        }
        this.yos.writeByte(this.skill.getType().value());
        this.yos.writeUTF(this.skill.name);
        this.yos.writeInt(this.skill.id);
        this.yos.writeByte(this.skill.level);
        this.yos.writeByte(this.skill.skillRank);
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
        this.yos.writeShort(this.surplusSkillPoint);
        if (this.skill instanceof ActiveSkill) {
            ActiveSkill activeSkill = (ActiveSkill) this.skill;
            ActiveSkillUnit skillUnit = (ActiveSkillUnit) activeSkill.skillUnit;
            this.yos.writeInt(activeSkill.coolDownTime);
            this.yos.writeShort(skillUnit.releaseTime * 1000.0f);
            this.yos.writeByte(skillUnit.targetType.value());
            this.yos.writeByte(skillUnit.targetDistance);
            this.yos.writeShort(skillUnit.consumeHp);
            SkillUpgradeNotify.log.info((Object) ("\u4f7f\u7528\u6280\u80fd\u9700\u8981\u6d88\u8017\u7684\u751f\u547d\u503c:" + skillUnit.consumeHp));
            this.yos.writeShort(skillUnit.consumeMp);
            this.yos.writeByte(skillUnit.consumeFp);
            this.yos.writeByte(skillUnit.consumeGp);
            this.yos.writeShort(skillUnit.releaseImageID);
            this.yos.writeShort(skillUnit.releaseAnimationID);
            this.yos.writeByte(skillUnit.animationAction);
            this.yos.writeByte(skillUnit.tierRelation);
            this.yos.writeByte(skillUnit.releaseHeightRelation);
            this.yos.writeByte(skillUnit.isDirection);
        }
    }
}
