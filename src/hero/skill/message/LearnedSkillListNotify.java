// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import java.util.Iterator;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.HeavenBook;
import hero.item.Weapon;
import hero.skill.unit.ActiveSkillUnit;
import hero.player.HeroPlayer;
import hero.share.EVocation;
import hero.skill.PassiveSkill;
import hero.skill.ActiveSkill;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class LearnedSkillListNotify extends AbsResponseMessage {

    private static Logger log;
    ArrayList<ActiveSkill> activeSkillList;
    ArrayList<PassiveSkill> passiveSkillList;
    private EVocation vocation;
    private int[] heaven_book_ids;

    static {
        LearnedSkillListNotify.log = Logger.getLogger((Class) LearnedSkillListNotify.class);
    }

    public LearnedSkillListNotify(final HeroPlayer _player) {
        this.activeSkillList = _player.activeSkillList;
        this.passiveSkillList = _player.passiveSkillList;
        this.vocation = _player.getVocation();
        this.heaven_book_ids = _player.heaven_book_ids;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.activeSkillList.size());
        byte vocationType = 0;
        for (final ActiveSkill skill : this.activeSkillList) {
            boolean isLoad = false;
            for (int i = 0; i < skill.learnerVocation.length; ++i) {
                if (skill.learnerVocation[i] == this.vocation) {
                    vocationType = skill.learnerVocation[i].value();
                    LearnedSkillListNotify.log.info((Object) (String.valueOf(this.vocation.getDesc()) + " \u6210\u529f\u52a0\u8f7d" + skill.learnerVocation[i].getDesc() + "\u804c\u4e1a\u7684[" + skill.name + "]"));
                    isLoad = true;
                } else if (skill.learnerVocation[i] == this.vocation.getBasicVoction()) {
                    vocationType = skill.learnerVocation[i].value();
                    LearnedSkillListNotify.log.info((Object) (String.valueOf(this.vocation.getDesc()) + " \u6210\u529f\u52a0\u8f7d" + skill.learnerVocation[i].getDesc() + "\u804c\u4e1a\u7684[" + skill.name + "]"));
                    isLoad = true;
                } else if (i == skill.learnerVocation.length - 1 && !isLoad) {
                    LearnedSkillListNotify.log.info((Object) ("warn:" + this.vocation.getDesc() + "\u52a0\u8f7d[" + skill.name + "]\u5931\u8d25"));
                }
            }
            ActiveSkillUnit skillUnit = (ActiveSkillUnit) skill.skillUnit;
            this.yos.writeInt(skill.id);
            this.yos.writeUTF(skill.name);
            this.yos.writeByte(skill.level);
            this.yos.writeByte(skill.skillRank);
            this.yos.writeShort(skill.iconID);
            this.yos.writeUTF(skill.description);
            if (skill.next != null) {
                this.yos.writeByte(1);
                this.yos.writeUTF(skill.next.description);
            } else {
                this.yos.writeByte(0);
            }
            this.yos.writeShort(skill.skillPoints);
            if (skill.next != null) {
                this.yos.writeInt(skill.learnFreight);
            } else {
                this.yos.writeInt(0);
            }
            this.yos.writeByte(vocationType);
            this.yos.writeByte(skillUnit.activeSkillType.value());
            this.yos.writeShort(skill.coolDownID);
            this.yos.writeInt(skill.coolDownTime);
            this.yos.writeInt(skill.reduceCoolDownTime);
            this.yos.writeShort(skillUnit.releaseTime * 1000.0f);
            if (skillUnit.releaseTime > 0.0f) {
                System.out.println("\u72c2\u66b4\u524d\u901f\u5ea6\u5217\u8868:" + skillUnit.releaseTime);
            }
            if (skill.needWeaponType == null) {
                this.yos.writeByte(1);
                this.yos.writeShort(0);
            } else {
                byte size = (byte) skill.needWeaponType.size();
                this.yos.writeByte(size);
                for (int j = 0; j < size; ++j) {
                    this.yos.writeShort(skill.needWeaponType.get(j).getID());
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
            this.yos.writeShort(skillUnit.releaseAnimationID);
            this.yos.writeByte(skillUnit.animationAction);
            this.yos.writeByte(skillUnit.tierRelation);
            this.yos.writeByte(skillUnit.releaseHeightRelation);
            this.yos.writeByte(skillUnit.isDirection);
        }
        this.yos.writeShort(this.passiveSkillList.size());
        for (final PassiveSkill skill2 : this.passiveSkillList) {
            boolean isLoad = false;
            for (int i = 0; i < skill2.learnerVocation.length; ++i) {
                if (skill2.learnerVocation[i] == this.vocation) {
                    vocationType = skill2.learnerVocation[i].value();
                    LearnedSkillListNotify.log.info((Object) (String.valueOf(this.vocation.getDesc()) + " \u6210\u529f\u52a0\u8f7d" + skill2.learnerVocation[i].getDesc() + "\u804c\u4e1a\u7684[" + skill2.name + "]"));
                    isLoad = true;
                } else if (skill2.learnerVocation[i] == this.vocation.getBasicVoction()) {
                    vocationType = skill2.learnerVocation[i].value();
                    LearnedSkillListNotify.log.info((Object) (String.valueOf(this.vocation.getDesc()) + " \u6210\u529f\u52a0\u8f7d" + skill2.learnerVocation[i].getDesc() + "\u804c\u4e1a\u7684[" + skill2.name + "]"));
                    isLoad = true;
                } else if (i == skill2.learnerVocation.length - 1 && !isLoad) {
                    LearnedSkillListNotify.log.info((Object) ("warn:" + this.vocation.getDesc() + "\u52a0\u8f7d[" + skill2.name + "]\u5931\u8d25"));
                }
            }
            this.yos.writeInt(skill2.id);
            this.yos.writeUTF(skill2.name);
            this.yos.writeByte(skill2.level);
            this.yos.writeByte(skill2.skillRank);
            this.yos.writeShort(skill2.iconID);
            this.yos.writeUTF(skill2.description);
            if (skill2.next != null) {
                this.yos.writeByte(1);
                this.yos.writeUTF(skill2.next.description);
            } else {
                this.yos.writeByte(0);
            }
            this.yos.writeShort(skill2.skillPoints);
            if (skill2.next != null) {
                this.yos.writeInt(skill2.learnFreight);
            } else {
                this.yos.writeInt(0);
            }
            this.yos.writeByte(vocationType);
        }
        this.yos.writeByte(this.heaven_book_ids.length);
        int[] heaven_book_ids;
        for (int length = (heaven_book_ids = this.heaven_book_ids).length, k = 0; k < length; ++k) {
            int heaven_book_id = heaven_book_ids[k];
            this.yos.writeInt(heaven_book_id);
            if (heaven_book_id > 0) {
                HeavenBook book = (HeavenBook) GoodsServiceImpl.getInstance().getGoodsByID(heaven_book_id);
                this.yos.writeShort(book.getIconID());
                this.yos.writeByte(book.getTrait().value());
                this.yos.writeShort(book.getSkillPoint());
                this.yos.writeUTF(book.getName());
                this.yos.writeUTF(book.getDescription());
            }
        }
    }
}
