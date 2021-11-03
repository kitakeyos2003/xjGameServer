// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.pet.service.PetServiceImpl;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class PetSkillBook extends SpecialGoods {

    private int skillID;

    public PetSkillBook(final int id, final short stackNums) {
        super(id, stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object target, final int location) {
        Pet pet = (Pet) target;
        boolean r = PetServiceImpl.getInstance().petLearnSkillFromSkillBook(_player, pet, this.skillID);
        if (r) {
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        }
        return r;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public byte getSingleGoodsType() {
        return 5;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.PET_SKILL_BOOK;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    public int getSkillID() {
        return this.skillID;
    }

    public void setSkillID(final int skillID) {
        this.skillID = skillID;
    }
}
