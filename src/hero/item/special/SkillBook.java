// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.skill.service.SkillServiceImpl;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class SkillBook extends SpecialGoods {

    private int skillID;

    public SkillBook(final int _id, final short _stackNums) {
        super(_id, _stackNums);
    }

    public void setSkillID(final int _skillID) {
        this.skillID = _skillID;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean res = SkillServiceImpl.getInstance().comprehendSkill(_player, this.skillID);
        if (res) {
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        }
        return res;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.SKILL_BOOK;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
