// 
// Decompiled by Procyon v0.5.36
// 
package hero.novice.service;

import hero.share.EVocationType;
import hero.share.EVocation;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class NoviceConfig extends AbsConfig {

    public short novice_map_id;
    public short novice_map_born_x;
    public short novice_map_born_y;
    public short novice_task_experience;
    public short novice_task_money;
    public short novice_monster_experience;
    public short novice_monster_money;
    public short level_when_complete_novice_teaching;
    public int[] novice_li_shi_init_award_list;
    public int[] novice_chi_hou_init_award_list;
    public int[] novice_fa_shi_init_award_list;
    public int[] novice_wu_yi_init_award_list;
    public boolean is_novice_award;
    public short novice_award_skill_point;
    public short novice_award_level;
    public int novice_award_money;
    public int[][] novice_award_item;
    public int award_equipment_id;

    @Override
    public void init(final Element node) throws Exception {
        Element element = node.element("config");
        this.novice_map_id = Short.parseShort(element.elementTextTrim("novice_map_id"));
        this.novice_map_born_x = Short.parseShort(element.elementTextTrim("novice_map_born_x"));
        this.novice_map_born_y = Short.parseShort(element.elementTextTrim("novice_map_born_y"));
        this.novice_task_experience = Short.parseShort(element.elementTextTrim("novice_task_experience"));
        this.novice_task_money = Short.parseShort(element.elementTextTrim("novice_task_money"));
        this.novice_monster_experience = Short.parseShort(element.elementTextTrim("novice_monster_experience"));
        this.novice_monster_money = Short.parseShort(element.elementTextTrim("novice_monster_money"));
        this.level_when_complete_novice_teaching = Short.parseShort(element.elementTextTrim("level_when_complete_novice_teaching"));
        this.award_equipment_id = Short.parseShort(element.elementTextTrim("award_equipment_id"));
        String tempString = "";
        this.is_novice_award = Boolean.valueOf(element.elementTextTrim("is_novice_award"));
        String[] temp = null;
        tempString = element.elementTextTrim("novice_li_shi_init_award_list");
        if (tempString != null && !tempString.equals("")) {
            temp = element.elementTextTrim("novice_li_shi_init_award_list").split(",");
            this.novice_li_shi_init_award_list = new int[temp.length];
            for (int i = 0; i < temp.length; ++i) {
                this.novice_li_shi_init_award_list[i] = Integer.valueOf(temp[i]);
            }
        }
        tempString = element.elementTextTrim("novice_chi_hou_init_award_list");
        if (tempString != null && !tempString.equals("")) {
            temp = tempString.split(",");
            this.novice_chi_hou_init_award_list = new int[temp.length];
            for (int i = 0; i < temp.length; ++i) {
                this.novice_chi_hou_init_award_list[i] = Integer.valueOf(temp[i]);
            }
        }
        tempString = element.elementTextTrim("novice_fa_shi_init_award_list");
        if (tempString != null && !tempString.equals("")) {
            temp = tempString.split(",");
            this.novice_fa_shi_init_award_list = new int[temp.length];
            for (int i = 0; i < temp.length; ++i) {
                this.novice_fa_shi_init_award_list[i] = Integer.valueOf(temp[i]);
            }
        }
        tempString = element.elementTextTrim("novice_wu_yi_init_award_list");
        if (tempString != null && !tempString.equals("")) {
            temp = tempString.split(",");
            this.novice_wu_yi_init_award_list = new int[temp.length];
            for (int i = 0; i < temp.length; ++i) {
                this.novice_wu_yi_init_award_list[i] = Integer.valueOf(temp[i]);
            }
        }
        this.novice_award_skill_point = Short.valueOf(element.elementTextTrim("novice_award_skill_point"));
        this.novice_award_level = Short.valueOf(element.elementTextTrim("novice_award_level"));
        this.novice_award_money = Integer.valueOf(element.elementTextTrim("novice_award_money"));
        tempString = element.elementTextTrim("novice_award_item");
        if (tempString != null && !tempString.equals("")) {
            temp = tempString.split(";");
            this.novice_award_item = new int[temp.length][2];
            for (int i = 0; i < temp.length; ++i) {
                this.novice_award_item[i][0] = Integer.valueOf(temp[i].split(",")[0]);
                this.novice_award_item[i][1] = Integer.valueOf(temp[i].split(",")[1]);
            }
        }
    }

    public int[] getInitAwardList(final EVocation _vocation) {
        int[] goodsList = null;
        if (this.is_novice_award) {
            if (EVocationType.PHYSICS == _vocation.getType()) {
                goodsList = this.novice_li_shi_init_award_list;
            } else if (EVocationType.RANGER == _vocation.getType()) {
                goodsList = this.novice_chi_hou_init_award_list;
            } else if (EVocationType.MAGIC == _vocation.getType()) {
                goodsList = this.novice_fa_shi_init_award_list;
            } else {
                goodsList = this.novice_wu_yi_init_award_list;
            }
        }
        return goodsList;
    }
}
