// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class EffectConfig extends AbsConfig {

    public String static_effect_data_path;
    public String dynamic_effect_data_path;
    public String touch_effect_data_path;
    public String weapon_enhance_effect_data_path;

    @Override
    public void init(final Element node) throws Exception {
        Element dataPathE = node.element("para");
        this.static_effect_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("static_effect_data_path");
        this.dynamic_effect_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("dynamic_effect_data_path");
        this.touch_effect_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("touch_effect_data_path");
        this.weapon_enhance_effect_data_path = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("weapon_enhance_effect_data_path");
    }
}
