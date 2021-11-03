// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.share.ME2GameObject;
import hero.effect.service.EffectServiceImpl;
import hero.effect.dictionry.EffectDictionary;
import hero.player.HeroPlayer;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.effect.Effect;
import hero.item.SpecialGoods;

public class PetForeverCard extends SpecialGoods {

    private Effect effect;
    private static final int[][] PET_CARD_FUNCTION_LIST;

    static {
        PET_CARD_FUNCTION_LIST = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().pet_forever_function;
    }

    public PetForeverCard(final int _id, final short nums) {
        super(_id, nums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        int SkillID = 0;
        for (int i = 0; i < PetForeverCard.PET_CARD_FUNCTION_LIST.length; ++i) {
            if (this.getID() == PetForeverCard.PET_CARD_FUNCTION_LIST[i][0]) {
                SkillID = PetForeverCard.PET_CARD_FUNCTION_LIST[i][1];
            }
        }
        this.effect = EffectDictionary.getInstance().getEffectRef(SkillID);
        EffectServiceImpl.getInstance().appendSkillEffect(_player, _player, this.effect);
        return false;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return false;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.PET_FOREVER;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }
}
