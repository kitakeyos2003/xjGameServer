// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class PetConfig extends AbsConfig {

    public String pet_data_path;
    public String feed_data_path;
    public String pet_skill_data_path;
    public String pet_skill_effect_data_path;

    @Override
    public void init(final Element node) throws Exception {
        Element element = node.element("config");
        this.pet_data_path = String.valueOf(YOYOSystem.HOME) + element.elementTextTrim("pet_data_path");
        this.feed_data_path = String.valueOf(YOYOSystem.HOME) + element.elementTextTrim("feed_data_path");
        this.pet_skill_data_path = String.valueOf(YOYOSystem.HOME) + element.elementTextTrim("pet_skill_data_path");
        this.pet_skill_effect_data_path = String.valueOf(YOYOSystem.HOME) + element.elementTextTrim("pet_skill_effect_data_path");
    }
}
