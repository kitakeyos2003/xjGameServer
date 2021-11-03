// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.pet.service.PetServiceImpl;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class PetDicard extends SpecialGoods {

    public PetDicard(final int id, final short stackNums) {
        super(id, stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object target, final int location) {
        Pet pet = (Pet) target;
        if (pet == null) {
            return false;
        }
        PetServiceImpl.getInstance().dicardPoint(_player, pet);
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.PET_DICARD;
    }

    @Override
    public byte getSingleGoodsType() {
        return 5;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
