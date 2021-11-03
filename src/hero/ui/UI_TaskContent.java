// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import hero.skill.Skill;
import java.util.Iterator;
import hero.item.Goods;
import java.util.ArrayList;
import java.io.IOException;
import hero.skill.service.SkillServiceImpl;
import hero.item.EqGoods;
import hero.item.Equipment;
import hero.task.Award;
import hero.expressions.service.CEService;
import yoyo.tools.YOYOOutputStream;
import hero.task.Task;

public class UI_TaskContent {

    public static EUIType getType() {
        return EUIType.TASK_CONTENT;
    }

    public static byte[] getBytes(final Task _task, final byte _receiveOfSubmit, final short playerLevel) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeByte(_receiveOfSubmit);
            output.writeShort(_task.getLevel());
            output.writeUTF(_task.getName());
            if (1 == _receiveOfSubmit) {
                output.writeUTF(_task.getReceiveDesc());
            } else {
                output.writeUTF(_task.getSubmitDesc());
            }
            output.writeInt(_task.getAward().money);
            output.writeInt(CEService.taskExperience(playerLevel, _task.getLevel(), _task.getAward().experience));
            ArrayList<Award.AwardGoodsUnit> awardGoodList = _task.getAward().getOptionalGoodsList();
            if (awardGoodList != null) {
                output.writeByte(awardGoodList.size());
                Goods goods = null;
                for (final Award.AwardGoodsUnit awardGoodsUnit : awardGoodList) {
                    goods = awardGoodsUnit.goods;
                    if (goods instanceof Equipment) {
                        output.writeByte(0);
                        EqGoods e = (EqGoods) goods;
                        output.writeInt(goods.getID());
                        output.writeShort(e.getIconID());
                        output.writeUTF(e.getName());
                        output.writeBytes(e.getFixPropertyBytes());
                        output.writeByte(0);
                        output.writeByte(0);
                        output.writeShort(e.getMaxDurabilityPoint());
                        output.writeByte(1);
                        output.writeInt(e.getSellPrice());
                        output.writeShort(e.getNeedLevel());
                    } else {
                        output.writeByte(1);
                        output.writeInt(goods.getID());
                        output.writeUTF(goods.getName());
                        output.writeByte(goods.getTrait().value());
                        output.writeShort(goods.getIconID());
                        output.writeUTF(goods.getDescription());
                        output.writeByte(awardGoodsUnit.number);
                        output.writeInt(goods.getSellPrice());
                        output.writeShort(goods.getNeedLevel());
                    }
                }
            } else {
                output.writeByte(0);
            }
            awardGoodList = _task.getAward().getBoundGoodsList();
            if (awardGoodList != null) {
                output.writeByte(awardGoodList.size());
                Goods goods = null;
                for (final Award.AwardGoodsUnit awardGoodsUnit : awardGoodList) {
                    goods = awardGoodsUnit.goods;
                    if (goods instanceof Equipment) {
                        output.writeByte(0);
                        EqGoods e = (EqGoods) goods;
                        output.writeShort(e.getIconID());
                        output.writeUTF(e.getName());
                        output.writeBytes(e.getFixPropertyBytes());
                        output.writeByte(0);
                        output.writeByte(0);
                        output.writeShort(e.getMaxDurabilityPoint());
                        output.writeByte(1);
                        output.writeInt(e.getSellPrice());
                        output.writeShort(e.getNeedLevel());
                    } else {
                        output.writeByte(1);
                        output.writeUTF(goods.getName());
                        output.writeByte(goods.getTrait().value());
                        output.writeShort(goods.getIconID());
                        output.writeUTF(goods.getDescription());
                        output.writeByte(awardGoodsUnit.number);
                        output.writeInt(goods.getSellPrice());
                        output.writeShort(goods.getNeedLevel());
                    }
                }
            } else {
                output.writeByte(0);
            }
            int skillID = _task.getAward().skillID;
            output.writeInt(skillID);
            if (skillID > 0) {
                Skill skill = SkillServiceImpl.getInstance().getSkillModel(skillID);
                if (skill != null) {
                    output.writeUTF(skill.name);
                    output.writeShort(skill.iconID);
                    output.writeUTF(skill.description);
                    output.writeShort(skill.learnerLevel);
                }
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }
}
