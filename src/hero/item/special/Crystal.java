// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.NotifyPopEnhanceUI;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import java.util.Arrays;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.SpecialGoods;

public class Crystal extends SpecialGoods {

    private byte equipmentLevelLower;
    private byte equipmentLevelLimit;
    private boolean isUltimaNeed;
    private byte useType;
    private byte crystalLevel;
    private boolean isUltimate;
    public static int[][] CORRESPONDING_TARGET_GOURD_LIST;
    public static int[] STONE_PWEDOEATE_LIST;
    public static int[] STONE_WRECK_LIST;

    static {
        Crystal.CORRESPONDING_TARGET_GOURD_LIST = new int[][]{{340007, 1, 19, 0, 0}, {340008, 1, 19, 1, 0}, {340009, 1, 19, 2, 0}, {340010, 20, 39, 0, 0}, {340011, 20, 39, 1, 0}, {340012, 20, 39, 2, 0}, {340013, 40, 59, 0, 0}, {340014, 40, 59, 1, 0}, {340015, 40, 59, 2, 0}, {340016, 60, 61, 0, 0}, {340017, 60, 61, 1, 0}, {340018, 60, 61, 2, 0}, {340019, 1, 120, 3, 1}};
        Crystal.STONE_PWEDOEATE_LIST = new int[]{340001};
        Crystal.STONE_WRECK_LIST = new int[]{340002};
    }

    public Crystal(final int _id, final short _stackNums) {
        super(_id, _stackNums);
        this.useType = 1;
        int[] stone_PWEDOEATE_LIST;
        for (int length = (stone_PWEDOEATE_LIST = Crystal.STONE_PWEDOEATE_LIST).length, j = 0; j < length; ++j) {
            int sid = stone_PWEDOEATE_LIST[j];
            if (_id == sid) {
                this.useType = 0;
                break;
            }
        }
        int[] stone_WRECK_LIST;
        for (int length2 = (stone_WRECK_LIST = Crystal.STONE_WRECK_LIST).length, k = 0; k < length2; ++k) {
            int eid = stone_WRECK_LIST[k];
            if (_id == eid) {
                this.useType = 2;
                break;
            }
        }
        this.isUltimaNeed = false;
        for (int i = 0; i < Crystal.CORRESPONDING_TARGET_GOURD_LIST.length; ++i) {
            if (_id == Crystal.CORRESPONDING_TARGET_GOURD_LIST[i][0]) {
                this.crystalLevel = (byte) Crystal.CORRESPONDING_TARGET_GOURD_LIST[i][3];
                if (Crystal.CORRESPONDING_TARGET_GOURD_LIST[i][4] == 1) {
                    this.isUltimaNeed = true;
                }
            }
        }
        int[][] corresponding_TARGET_GOURD_LIST;
        int length3 = (corresponding_TARGET_GOURD_LIST = Crystal.CORRESPONDING_TARGET_GOURD_LIST).length;
        int l = 0;
        while (l < length3) {
            int[] equipmentLimit = corresponding_TARGET_GOURD_LIST[l];
            if (equipmentLimit[0] == this.getID()) {
                this.useType = 1;
                this.equipmentLevelLower = (byte) equipmentLimit[1];
                this.equipmentLevelLimit = (byte) equipmentLimit[2];
                if (equipmentLimit[4] == 0) {
                    this.isUltimaNeed = false;
                    break;
                }
                this.isUltimaNeed = true;
                break;
            } else {
                ++l;
            }
        }
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.CRYSTAL;
    }

    public byte getUseType() {
        return this.useType;
    }

    public boolean getIsUltimaNeed() {
        return this.isUltimaNeed;
    }

    public byte getCrystalLevel() {
        return this.crystalLevel;
    }

    public int getEnhanceOdds(final int _random) {
        int resultEnhance = 0;
        int result = 0;
        int a = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().odds_enhance_list[this.crystalLevel][0];
        int b = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().odds_enhance_list[this.crystalLevel][1];
        int c = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().odds_enhance_list[this.crystalLevel][2];
        int[] list = {a, b, c};
        Arrays.sort(list);
        if (_random <= list[0]) {
            result = list[0];
        } else if (_random <= list[1]) {
            result = list[1];
        } else {
            result = list[2];
        }
        if (result == a) {
            resultEnhance = 1;
        } else if (result == b) {
            resultEnhance = 2;
        } else {
            resultEnhance = 3;
        }
        return resultEnhance;
    }

    public boolean conformLevel(final int _equipmentLevel) {
        return _equipmentLevel >= this.equipmentLevelLower && _equipmentLevel <= this.equipmentLevelLimit;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return this.useType == 2;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _locationOfBag) {
        if (this.useType != 2) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyPopEnhanceUI(this.getID(), _locationOfBag));
        }
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }
}
