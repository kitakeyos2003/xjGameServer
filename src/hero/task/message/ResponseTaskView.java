// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.message;

import java.io.IOException;
import hero.skill.Skill;
import hero.item.Goods;
import java.util.Iterator;
import java.util.ArrayList;
import hero.task.Task;
import hero.skill.service.SkillServiceImpl;
import hero.task.Award;
import hero.task.target.BaseTaskTarget;
import hero.expressions.service.CEService;
import hero.share.service.LogWriter;
import hero.task.TaskInstance;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseTaskView extends AbsResponseMessage {

    private TaskInstance taskIns;
    private byte viewContent;
    private short playerLevel;

    public ResponseTaskView(final TaskInstance _taskIns, final byte _viewContent, final short playerLevel) {
        this.taskIns = _taskIns;
        this.viewContent = _viewContent;
        this.playerLevel = playerLevel;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        if (this.taskIns == null) {
            LogWriter.error("error:\u67e5\u770b\u4efb\u52a1\u7684\u65f6\u5019\u4f20\u5165\u4efb\u52a1\u5b9e\u4f8b\u53c2\u6570\u4e3aNULL,\u6267\u884c\u88ab\u7ec8\u6b62.", null);
            return;
        }
        Task task = this.taskIns.getArchetype();
        this.yos.writeByte(this.viewContent);
        this.yos.writeInt(task.getID());
        if (1 == this.viewContent) {
            this.yos.writeUTF(task.getViewDesc());
            this.yos.writeInt(task.getAward().money);
            this.yos.writeInt(CEService.taskExperience(this.playerLevel, task.getLevel(), task.getAward().experience));
            ArrayList<BaseTaskTarget> targetList = this.taskIns.getTargetList();
            this.yos.writeByte(targetList.size());
            for (final BaseTaskTarget target : targetList) {
                this.yos.writeInt(target.getID());
                this.yos.writeByte(target.isCompleted());
                this.yos.writeUTF(target.getDescripiton());
                this.yos.writeByte(target.canTransmit());
            }
        } else if (2 == this.viewContent) {
            ArrayList<Award.AwardGoodsUnit> awardGoodList = task.getAward().getOptionalGoodsList();
            if (awardGoodList != null && awardGoodList.size() > 0) {
                this.yos.writeByte(awardGoodList.size());
                Goods goods = null;
                for (final Award.AwardGoodsUnit awardGoodsUnit : awardGoodList) {
                    goods = awardGoodsUnit.goods;
                    this.yos.writeInt(goods.getID());
                    this.yos.writeUTF(goods.getName());
                    this.yos.writeByte(goods.getTrait().value());
                    this.yos.writeShort(goods.getIconID());
                    this.yos.writeUTF(goods.getDescription());
                    this.yos.writeByte(awardGoodsUnit.number);
                }
            } else {
                this.yos.writeByte(0);
            }
            awardGoodList = task.getAward().getBoundGoodsList();
            if (awardGoodList != null && awardGoodList.size() > 0) {
                this.yos.writeByte(awardGoodList.size());
                Goods goods = null;
                for (final Award.AwardGoodsUnit awardGoodsUnit : awardGoodList) {
                    goods = awardGoodsUnit.goods;
                    this.yos.writeUTF(goods.getName());
                    this.yos.writeByte(goods.getTrait().value());
                    this.yos.writeShort(goods.getIconID());
                    this.yos.writeUTF(goods.getDescription());
                    this.yos.writeByte(awardGoodsUnit.number);
                }
            } else {
                this.yos.writeByte(0);
            }
            int skillID = task.getAward().skillID;
            this.yos.writeInt(skillID);
            if (skillID > 0) {
                Skill skill = SkillServiceImpl.getInstance().getSkillModel(skillID);
                if (skill != null) {
                    this.yos.writeUTF(skill.name);
                    this.yos.writeShort(skill.iconID);
                    this.yos.writeUTF(skill.description);
                }
            }
        }
    }
}
