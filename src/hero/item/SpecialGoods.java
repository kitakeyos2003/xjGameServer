// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.item.bag.exception.BagException;
import yoyo.core.packet.AbsResponseMessage;
import hero.ui.message.ResponseSinglePackageChange;
import hero.item.bag.EBagType;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.service.GoodsDAO;
import hero.item.special.ESpecialGoodsType;
import hero.player.HeroPlayer;
import hero.item.detail.EGoodsType;

public abstract class SpecialGoods extends SingleGoods {

    private boolean isOnly;

    public SpecialGoods(final int _id, final short _stackNums) {
        super(_stackNums);
        this.setID(_id);
    }

    @Override
    public byte getSingleGoodsType() {
        return 4;
    }

    @Override
    public EGoodsType getGoodsType() {
        return EGoodsType.SPECIAL_GOODS;
    }

    public boolean isOnly() {
        return this.isOnly;
    }

    public void setOnly() {
        this.isOnly = true;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target) {
        return false;
    }

    public abstract boolean beUse(final HeroPlayer p0, final Object p1, final int p2);

    public abstract ESpecialGoodsType getType();

    public abstract boolean disappearImmediatelyAfterUse();

    public void remove(final HeroPlayer _player, final short _gridIndex) throws BagException {
        short[] gridChange = null;
        if (_gridIndex >= 0) {
            gridChange = _player.getInventory().getSpecialGoodsBag().remove(_gridIndex, this.getID(), 1);
        } else {
            gridChange = _player.getInventory().getSpecialGoodsBag().removeOne(this.getID());
        }
        if (gridChange != null) {
            if (gridChange[1] == 0) {
                GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(), gridChange[0], this.getID());
            } else {
                GoodsDAO.updateGridSingleGoodsNumberOfBag(_player.getUserID(), this.getID(), gridChange[1], gridChange[0]);
            }
            if (_gridIndex >= 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseSinglePackageChange(EBagType.SPECIAL_GOODS_BAG.getTypeValue(), gridChange));
            }
        }
    }
}
