// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.util.Iterator;
import java.io.IOException;
import yoyo.tools.YOYOOutputStream;
import hero.skill.Skill;
import java.util.ArrayList;

public class UI_SkillList {

    public static byte[] getBytes(final String[] _menuList, final ArrayList<Skill> _skillList) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeByte(_skillList.size());
            for (final Skill skill : _skillList) {
                output.writeInt(skill.id);
                output.writeUTF(skill.name);
                output.writeByte(skill.level);
                output.writeShort(skill.iconID);
                output.writeUTF(skill.description);
                output.writeInt(skill.learnFreight);
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
        return EUIType.SKILL_LIST;
    }
}
