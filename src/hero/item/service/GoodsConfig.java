// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class GoodsConfig extends AbsConfig {

    private SpecialConfig specialConfig;
    public String weapon_data_path;
    public String armor_data_path;
    public String medicament_data_path;
    public String material_data_path;
    public String gift_bag_data_path;
    public String special_goods_data_path;
    public String task_goods_data_path;
    public String exchange_goods_data_path;
    public String world_legacy_equip_data_path;
    public String world_legacy_material_data_path;
    public String world_legacy_medicament_data_path;
    public String change_vocation_tool_data_path;
    public short[][] shine_flash_view;
    public short[][] armor_shine_flash_view;
    public short[][] yet_set_jewel;
    public String suite_equipment_data_path;
    public int[] perforate_money_list;
    public int[] enhance_money_list;
    public int[] wreck_money_list;
    public String describe_string;
    public String describe_enhance_string;
    public String material_bag_tab_name;
    public String medicament_bag_tab_name;
    public String task_tool_bag_tab_name;
    public String special_bag_tab_name;
    public String equipment_bag_tab_name;

    @Override
    public void init(final Element node) throws Exception {
        Element paraElement = node.element("para");
        this.specialConfig = new SpecialConfig(node.element("specialConfig"));
        Element defaultConfig = node.element("defaultConfig");
        this.weapon_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("weapon_data_path");
        this.armor_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("armor_data_path");
        this.medicament_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("medicament_data_path");
        this.material_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("material_data_path");
        this.task_goods_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("task_goods_data_path");
        this.exchange_goods_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("exchange_goods_data_path");
        this.special_goods_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("special_goods_data_path");
        this.change_vocation_tool_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("change_vocation_tool_data_path");
        this.world_legacy_equip_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("world_legacy_equip_data_path");
        this.world_legacy_material_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("world_legacy_material_data_path");
        this.world_legacy_medicament_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("world_legacy_medicament_data_path");
        this.suite_equipment_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("suite_equipment_data_path");
        this.gift_bag_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("gift_bag_data_path");
        this.shine_flash_view = new short[6][2];
        for (int i = 0; i < this.shine_flash_view.length; ++i) {
            String[] temp = defaultConfig.elementTextTrim("shine_flash_view_" + (i + 1)).split(",");
            this.shine_flash_view[i][0] = Short.valueOf(temp[0]);
            this.shine_flash_view[i][1] = Short.valueOf(temp[1]);
        }
        this.armor_shine_flash_view = new short[6][2];
        for (int i = 0; i < this.armor_shine_flash_view.length; ++i) {
            String[] temp = defaultConfig.elementTextTrim("armor_shine_flash_view_" + (i + 1)).split(",");
            this.armor_shine_flash_view[i][0] = Short.valueOf(temp[0]);
            this.armor_shine_flash_view[i][1] = Short.valueOf(temp[1]);
        }
        this.yet_set_jewel = new short[3][2];
        for (int i = 0; i < this.yet_set_jewel.length; ++i) {
            String[] temp = defaultConfig.elementTextTrim("yet_set_jewel_" + (i + 1)).split(",");
            this.yet_set_jewel[i][0] = Short.valueOf(temp[0]);
            this.yet_set_jewel[i][1] = Short.valueOf(temp[1]);
        }
        this.perforate_money_list = new int[12];
        String[] temp2 = defaultConfig.elementTextTrim("perforate_money_list").split(",");
        for (int j = 0; j < this.perforate_money_list.length; ++j) {
            this.perforate_money_list[j] = Integer.valueOf(temp2[j]);
        }
        this.enhance_money_list = new int[12];
        temp2 = defaultConfig.elementTextTrim("enhance_money_list").split(",");
        for (int j = 0; j < this.enhance_money_list.length; ++j) {
            this.enhance_money_list[j] = Integer.valueOf(temp2[j]);
        }
        this.wreck_money_list = new int[12];
        temp2 = defaultConfig.elementTextTrim("wreck_money_list").split(",");
        for (int j = 0; j < this.wreck_money_list.length; ++j) {
            this.wreck_money_list[j] = Integer.valueOf(temp2[j]);
        }
        this.describe_string = defaultConfig.elementTextTrim("describe_string");
        this.describe_enhance_string = defaultConfig.elementTextTrim("describe_enhance_string");
        this.material_bag_tab_name = defaultConfig.elementTextTrim("material_bag_tab_name");
        this.medicament_bag_tab_name = defaultConfig.elementTextTrim("medicament_bag_tab_name");
        this.task_tool_bag_tab_name = defaultConfig.elementTextTrim("task_tool_bag_tab_name");
        this.special_bag_tab_name = defaultConfig.elementTextTrim("special_bag_tab_name");
        this.equipment_bag_tab_name = defaultConfig.elementTextTrim("equipment_bag_tab_name");
    }

    public SpecialConfig getSpecialConfig() {
        return this.specialConfig;
    }
}
