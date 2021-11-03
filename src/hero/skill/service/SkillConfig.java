// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class SkillConfig extends AbsConfig {

    public String active_skill_data_path;
    public String touch_skill_data_path;
    public String enhance_skill_data_path;
    public String change_property_skill_data_path;
    public String player_skill_data_path;
    public String monster_skill_data_path;

    @Override
    public void init(final Element node) throws Exception {
        Element dataPathE = node.element("para");
        this.active_skill_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("active_skill_data_path");
        this.touch_skill_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("touch_skill_data_path");
        this.enhance_skill_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("enhance_skill_data_path");
        this.change_property_skill_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("change_property_skill_data_path");
        this.player_skill_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("player_skill_data_path");
        this.monster_skill_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("monster_skill_data_path");
    }
}
