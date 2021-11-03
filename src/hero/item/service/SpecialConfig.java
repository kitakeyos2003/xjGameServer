// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.service;

import org.dom4j.Element;

public class SpecialConfig {

    public int[][] big_tonic;
    public int[][] pet_per_function;
    public int[][] pet_forever_function;
    public int[][] odds_enhance_list;
    public int number_transport;
    public int number_revive;
    public int number_guild_build;
    public int bag_expan_goods_id;
    public int[][] experience_book_time;
    public short experience_book_icon;

    public SpecialConfig(final Element node) {
        String[] temp = node.elementTextTrim("pet_per_function").split(";");
        this.pet_per_function = new int[temp.length][3];
        for (int i = 0; i < temp.length; ++i) {
            String[] group = temp[i].split(",");
            this.pet_per_function[i][0] = Integer.valueOf(group[0]);
            this.pet_per_function[i][1] = Integer.valueOf(group[1]);
            this.pet_per_function[i][2] = Integer.valueOf(group[2]);
        }
        temp = node.elementTextTrim("pet_forever_function").split(";");
        this.pet_forever_function = new int[temp.length][2];
        for (int i = 0; i < temp.length; ++i) {
            String[] group = temp[i].split(",");
            this.pet_forever_function[i][0] = Integer.valueOf(group[0]);
            this.pet_forever_function[i][1] = Integer.valueOf(group[1]);
        }
        temp = node.elementTextTrim("big_tonic").split(";");
        this.big_tonic = new int[temp.length][4];
        for (int i = 0; i < temp.length; ++i) {
            String[] group = temp[i].split(",");
            this.big_tonic[i][0] = Integer.valueOf(group[0]);
            this.big_tonic[i][1] = Integer.valueOf(group[1]);
            this.big_tonic[i][2] = Integer.valueOf(group[2]);
            this.big_tonic[i][3] = Integer.valueOf(group[3]);
        }
        temp = node.elementTextTrim("odds_enhance_list").split(";");
        this.odds_enhance_list = new int[temp.length][3];
        for (int i = 0; i < temp.length; ++i) {
            String[] group = temp[i].split(",");
            this.odds_enhance_list[i][0] = Integer.valueOf(group[0]);
            this.odds_enhance_list[i][1] = Integer.valueOf(group[1]);
            this.odds_enhance_list[i][2] = Integer.valueOf(group[2]);
        }
        temp = node.elementTextTrim("experience_book_time").split(";");
        this.experience_book_time = new int[temp.length][2];
        for (int i = 0; i < temp.length; ++i) {
            String[] group = temp[i].split(",");
            this.experience_book_time[i][0] = Integer.valueOf(group[0]);
            this.experience_book_time[i][1] = Integer.valueOf(group[1]);
        }
        this.experience_book_icon = Short.valueOf(node.elementTextTrim("experience_book_icon"));
        this.number_transport = Integer.valueOf(node.elementTextTrim("number_transport"));
        this.number_revive = Integer.valueOf(node.elementTextTrim("number_revive"));
        this.number_guild_build = Integer.valueOf(node.elementTextTrim("number_guild_build"));
        this.bag_expan_goods_id = Integer.parseInt(node.elementTextTrim("bag_expan_id"));
    }
}
