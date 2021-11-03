// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.service;

import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class FightConfig extends AbsConfig {

    public static int DISENGAGE_FIGHT_TIME;
    public short[] sword_attack_target_animation;
    public short[] dagger_attack_target_animation;
    public short[] bow_attack_target_animation;
    public short[] hammer_attack_target_animation;
    public short[] staff_attack_target_animation;

    static {
        FightConfig.DISENGAGE_FIGHT_TIME = 15000;
    }

    @Override
    public void init(final Element node) throws Exception {
        Element paraElement = node.element("para");
        String[] sword = paraElement.elementTextTrim("sword_attack_target_animation").split(",");
        String[] dagger = paraElement.elementTextTrim("dagger_attack_target_animation").split(",");
        String[] bow = paraElement.elementTextTrim("bow_attack_target_animation").split(",");
        String[] hammer = paraElement.elementTextTrim("hammer_attack_target_animation").split(",");
        String[] staff = paraElement.elementTextTrim("staff_attack_target_animation").split(",");
        FightConfig.DISENGAGE_FIGHT_TIME = Integer.valueOf(paraElement.elementTextTrim("disengage_fight_seconds")) * 1000;
        (this.sword_attack_target_animation = new short[2])[0] = Short.valueOf(sword[0]);
        this.sword_attack_target_animation[1] = Short.valueOf(sword[1]);
        (this.dagger_attack_target_animation = new short[2])[0] = Short.valueOf(dagger[0]);
        this.dagger_attack_target_animation[1] = Short.valueOf(dagger[1]);
        (this.bow_attack_target_animation = new short[2])[0] = Short.valueOf(bow[0]);
        this.bow_attack_target_animation[1] = Short.valueOf(bow[1]);
        (this.hammer_attack_target_animation = new short[2])[0] = Short.valueOf(hammer[0]);
        this.hammer_attack_target_animation[1] = Short.valueOf(hammer[1]);
        (this.staff_attack_target_animation = new short[2])[0] = Short.valueOf(staff[0]);
        this.staff_attack_target_animation[1] = Short.valueOf(staff[1]);
    }
}
