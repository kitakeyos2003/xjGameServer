// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class DungeonConfig extends AbsConfig {

    public String dungeon_data_path;
    public byte history_refresh_time;
    public byte raid_history_refresh_week;
    public byte difficult_addition_level;

    @Override
    public void init(final Element node) throws Exception {
        Element element = node.element("config");
        this.dungeon_data_path = String.valueOf(YOYOSystem.HOME) + element.elementTextTrim("dungeon_data_path");
        this.history_refresh_time = Byte.parseByte(element.elementTextTrim("history_refresh_time"));
        this.raid_history_refresh_week = Byte.parseByte(element.elementTextTrim("raid_history_refresh_week"));
        this.difficult_addition_level = Byte.parseByte(element.elementTextTrim("difficult_addition_level"));
    }
}
