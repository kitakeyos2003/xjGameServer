// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.item.bag.exception.BagException;
import hero.log.service.LogServiceImpl;
import hero.item.SingleGoods;
import hero.ui.message.NotifyAddGoods2SinglePackage;
import hero.item.Goods;
import yoyo.core.packet.AbsResponseMessage;
import hero.ui.message.ResponseSinglePackageChange;
import hero.item.bag.EBagType;
import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.dictionary.SpecialGoodsDict;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class Rattan extends SpecialGoods {

    private int targetGourdID;
    private int resultGourdID;
    private static final int[][] CORRESPONDING_TARGET_GOURD_LIST;

    static {
        CORRESPONDING_TARGET_GOURD_LIST = new int[][]{{50011, 50001, 50002}, {50012, 50002, 50003}, {50013, 50003, 50004}, {50014, 50004, 50005}};
    }

    public Rattan(final int _id, final short _stackNums) {
        super(_id, _stackNums);
        this.setTargetGourdID();
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    public int getTargetGourdID() {
        return this.targetGourdID;
    }

    public int getResultGourdID() {
        return this.resultGourdID;
    }

    private final void setTargetGourdID() {
        int[][] corresponding_TARGET_GOURD_LIST;
        for (int length = (corresponding_TARGET_GOURD_LIST = Rattan.CORRESPONDING_TARGET_GOURD_LIST).length, i = 0; i < length; ++i) {
            int[] correspondingGourdTable = corresponding_TARGET_GOURD_LIST[i];
            if (correspondingGourdTable[0] == this.getID()) {
                this.targetGourdID = correspondingGourdTable[1];
                this.resultGourdID = correspondingGourdTable[2];
            }
        }
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.RATTAN;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        try {
            if (_player.getInventory().getSpecialGoodsBag().getGoodsNumber(this.targetGourdID) == 1) {
                SpecialGoods resultGoods = SpecialGoodsDict.getInstance().getSpecailGoods(this.resultGourdID);
                if (resultGoods != null) {
                    int gridIndexTargetGourd = _player.getInventory().getSpecialGoodsBag().getFirstGridIndex(this.targetGourdID);
                    if (GoodsServiceImpl.getInstance().diceSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), gridIndexTargetGourd, this.targetGourdID, CauseLog.RATTAN)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseSinglePackageChange(EBagType.SPECIAL_GOODS_BAG.getTypeValue(), new short[]{(short) gridIndexTargetGourd, 0}));
                        short[] resultGourdInfo = GoodsServiceImpl.getInstance().addGoods2Package(_player, resultGoods, 1, CauseLog.RATTAN);
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyAddGoods2SinglePackage(EBagType.SPECIAL_GOODS_BAG.getTypeValue(), resultGourdInfo, resultGoods, _player.getShortcutKeyList()));
                        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
                        return true;
                    }
                }
            }
        } catch (BagException be) {
            be.printStackTrace();
        }
        return false;
    }
}
