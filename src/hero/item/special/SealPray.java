// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class SealPray extends SpecialGoods {

    private byte equipmentLevelLower;
    private byte equipmentLevelLimit;
    private static final int[][] PRAY_TARGET_LEVEL_LIST;

    static {
        PRAY_TARGET_LEVEL_LIST = new int[][]{{52011, 1, 40}, {52012, 41, 70}, {52013, 71, 100}};
    }

    public SealPray(final int _id, final short _stackNums) {
        super(_id, _stackNums);
        int[][] pray_TARGET_LEVEL_LIST;
        for (int length = (pray_TARGET_LEVEL_LIST = SealPray.PRAY_TARGET_LEVEL_LIST).length, i = 0; i < length; ++i) {
            int[] equipmentLevelList = pray_TARGET_LEVEL_LIST[i];
            if (equipmentLevelList[0] == this.getID()) {
                this.equipmentLevelLower = (byte) equipmentLevelList[1];
                this.equipmentLevelLimit = (byte) equipmentLevelList[2];
                break;
            }
        }
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.SEAL_PRAY;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    public boolean isValidatePray(final int _equipmentLevel) {
        return _equipmentLevel >= this.equipmentLevelLower && _equipmentLevel <= this.equipmentLevelLimit;
    }

    @Override
    public boolean useable() {
        return false;
    }

    public static final int getValidatePrayID(final int _equipmentLevel) {
        int[][] pray_TARGET_LEVEL_LIST;
        for (int length = (pray_TARGET_LEVEL_LIST = SealPray.PRAY_TARGET_LEVEL_LIST).length, i = 0; i < length; ++i) {
            int[] equipmentLevelList = pray_TARGET_LEVEL_LIST[i];
            if (_equipmentLevel >= equipmentLevelList[1] && _equipmentLevel <= equipmentLevelList[2]) {
                return equipmentLevelList[0];
            }
        }
        return 0;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean res = true;
        if (res) {
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        }
        return res;
    }
}
