// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import hero.manufacture.CategoryManager;
import hero.manufacture.Manufacture;
import hero.manufacture.dict.ManufSkill;
import hero.item.Goods;
import java.util.Iterator;
import java.io.IOException;
import hero.item.dictionary.GoodsContents;
import hero.gather.RefinedCategory;
import yoyo.tools.YOYOOutputStream;
import hero.gather.dict.Refined;
import java.util.ArrayList;

public class UI_AssistSkillList {

    public static byte[] getRefinedBytes(final String[] _menuList, final ArrayList<Refined> _list) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            String[] _str = RefinedCategory.getCategorys();
            output.writeByte(_str.length);
            String[] array;
            for (int length = (array = _str).length, i = 0; i < length; ++i) {
                String _s = array[i];
                output.writeUTF(_s);
            }
            output.writeShort(_list.size());
            for (final Refined _refined : _list) {
                output.writeInt(_refined.id);
                output.writeShort(_refined.icon);
                output.writeUTF(_refined.name);
                int goodsID = _refined.getGoodsID[1];
                Goods goods = GoodsContents.getGoods(goodsID);
                output.writeUTF(goods.getName());
                output.writeShort(goods.getIconID());
                output.writeByte(goods.getTrait().value());
                output.writeByte(_refined.category);
                output.writeInt(_refined.money);
            }
            output.writeByte(_menuList.length);
            for (final String menu : _menuList) {
                output.writeUTF(menu);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static byte[] getManufSkillBytes(final String[] _menuList, final ArrayList<ManufSkill> _list, final Manufacture _manuf) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            String[] _str = CategoryManager.getCategoryStrByManufactureType(_manuf.getManufactureType());
            output.writeByte(_str.length);
            String[] array;
            for (int length = (array = _str).length, i = 0; i < length; ++i) {
                String _s = array[i];
                output.writeUTF(_s);
            }
            output.writeShort(_list.size());
            for (final ManufSkill _mSkill : _list) {
                output.writeInt(_mSkill.id);
                output.writeShort(_mSkill.icon);
                output.writeUTF(_mSkill.name);
                int goodsID = _mSkill.getGoodsID[1];
                Goods goods = GoodsContents.getGoods(goodsID);
                output.writeUTF(goods.getName());
                output.writeShort(goods.getIconID());
                output.writeByte(goods.getTrait().value());
                output.writeByte(_mSkill.category);
                output.writeInt(_mSkill.money);
            }
            output.writeByte(_menuList.length);
            for (final String menu : _menuList) {
                output.writeUTF(menu);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static EUIType getType() {
        return EUIType.ASSIST_SKILL_LIST;
    }
}
