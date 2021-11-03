// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.manufacture.dict.ManufSkill;
import hero.gather.dict.Refined;
import hero.log.service.LogServiceImpl;
import hero.manufacture.service.ManufactureServerImpl;
import hero.manufacture.dict.ManufSkillDict;
import hero.share.service.LogWriter;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.gather.service.GatherServerImpl;
import hero.gather.dict.RefinedDict;
import hero.player.HeroPlayer;
import hero.manufacture.ManufactureType;
import hero.item.SpecialGoods;

public class Drawings extends SpecialGoods {

    private ManufactureType needManufactureType;
    private int getSkillItemID;

    public Drawings(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.DRAWINGS;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    public void setNeedManufactureType(final String _manufactureTypeDesc) {
        this.needManufactureType = ManufactureType.get(_manufactureTypeDesc);
    }

    public ManufactureType getNeedManufactureType() {
        return this.needManufactureType;
    }

    public void setManufactureItemID(final int _manufactureItemID) {
        this.getSkillItemID = _manufactureItemID;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean res = false;
        if (this.getSkillItemID >= 40001) {
            Refined skillItem = RefinedDict.getInstance().getRefinedByID(this.getSkillItemID);
            if (skillItem != null) {
                res = GatherServerImpl.getInstance().addRefinedItem(_player, skillItem, GetTypeOfSkillItem.LEARN);
            } else {
                LogWriter.println("\u4e0d\u5b58\u5728\u7684\u6280\u80fd\u6761\u76ee\uff1a" + this.getSkillItemID);
            }
        } else {
            ManufSkill skillItem2 = ManufSkillDict.getInstance().getManufSkillByID(this.getSkillItemID);
            if (skillItem2 != null) {
                res = ManufactureServerImpl.getInstance().addManufSkillItem(_player, skillItem2, GetTypeOfSkillItem.LEARN);
            } else {
                LogWriter.println("\u4e0d\u5b58\u5728\u7684\u6280\u80fd\u6761\u76ee\uff1a" + this.getSkillItemID);
            }
        }
        if (res) {
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        }
        return false;
    }
}
