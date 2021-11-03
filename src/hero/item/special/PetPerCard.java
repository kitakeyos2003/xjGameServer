// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.item.bag.exception.BagException;
import hero.share.message.Warning;
import hero.share.ME2GameObject;
import hero.effect.service.EffectServiceImpl;
import hero.effect.dictionry.EffectDictionary;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseSpecialGoodsBag;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.service.GoodsDAO;
import hero.player.HeroPlayer;
import hero.effect.Effect;
import hero.item.SpecialGoods;

public class PetPerCard extends SpecialGoods {

    private int[][] PET_CARD_FUNCTION_LIST;
    public int surplusPoint;
    public int SkillID;
    private String oldDescription;
    private String nowDescription;
    private int location;
    private Effect effect;
    private boolean inUse;

    public void initData(final int _surplusPoint, final int _index) {
        this.surplusPoint = _surplusPoint;
        this.location = _index;
    }

    public void copyGoodsData(final SpecialGoods _goods) {
        this.setName(_goods.getName());
        this.setIconID(_goods.getIconID());
        this.setTrait(_goods.getTrait());
        if (_goods.useable()) {
            this.setUseable();
        }
        this.oldDescription = _goods.getDescription();
        this.nowDescription = "\n\u5269\u4f59:" + this.surplusPoint + "\u6b21";
        this.replaceDescription(this.oldDescription, this.nowDescription);
    }

    private void descriptionUpdate(final HeroPlayer _player) {
        this.nowDescription = "\n\u5269\u4f59:" + this.surplusPoint;
        this.replaceDescription(this.oldDescription, this.nowDescription);
        GoodsDAO.updatePetPer(_player.getUserID(), this.location, this.getID(), this.surplusPoint);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseSpecialGoodsBag(_player.getInventory().getSpecialGoodsBag(), _player.getShortcutKeyList()));
    }

    public PetPerCard(final int _id, final short nums) {
        super(_id, nums);
        this.PET_CARD_FUNCTION_LIST = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().pet_per_function;
        for (int i = 0; i < this.PET_CARD_FUNCTION_LIST.length; ++i) {
            if (_id == this.PET_CARD_FUNCTION_LIST[i][0]) {
                this.surplusPoint = this.PET_CARD_FUNCTION_LIST[i][1];
                this.SkillID = this.PET_CARD_FUNCTION_LIST[i][2];
            }
        }
        this.inUse = false;
        this.effect = EffectDictionary.getInstance().getEffectRef(this.SkillID);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean remove = false;
        boolean hava = EffectServiceImpl.getInstance().haveAlikeMount(_player, this.effect);
        if (!this.inUse && this.surplusPoint > 0 && !hava) {
            EffectServiceImpl.getInstance().appendSkillEffect(_player, _player, this.effect);
            _player.setMount(true);
            --this.surplusPoint;
            this.inUse = true;
        } else if (this.surplusPoint > 0 || _player.getMount()) {
            EffectServiceImpl.getInstance().appendSkillEffect(_player, _player, this.effect);
            _player.setMount(false);
            this.inUse = false;
        }
        this.location = _location;
        this.descriptionUpdate(_player);
        if (this.surplusPoint <= 0 && !this.inUse) {
            remove = true;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u7528\u5b8c\u4e86\u4e00\u5f20%fname".replaceAll("%fname", this.getName()), (byte) 0));
        }
        if (remove && this.disappearImmediatelyAfterUse()) {
            try {
                this.remove(_player, (short) this.location);
            } catch (BagException e) {
                e.printStackTrace();
            }
        }
        return remove;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.PET_PER;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
