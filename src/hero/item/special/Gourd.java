// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class Gourd extends SpecialGoods {

    private int animaTypeNumber;
    private static final int[][] ANIMA_TYPE_NUMBER_LIST;

    static {
        ANIMA_TYPE_NUMBER_LIST = new int[][]{{50001, 10}, {50002, 15}, {50003, 20}, {50004, 25}, {50005, 30}, {50006, 50}};
    }

    public Gourd(final int _id, final short _stackNums) {
        super(_id, _stackNums);
        this.setMonsterTypeNumber();
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    public int getMonsterTypeNumber() {
        return this.animaTypeNumber;
    }

    public final short getAnimaMaxNumerPerType() {
        return 99;
    }

    private final void setMonsterTypeNumber() {
        int[][] anima_TYPE_NUMBER_LIST;
        for (int length = (anima_TYPE_NUMBER_LIST = Gourd.ANIMA_TYPE_NUMBER_LIST).length, i = 0; i < length; ++i) {
            int[] monsterTypeNumberTable = anima_TYPE_NUMBER_LIST[i];
            if (monsterTypeNumberTable[0] == this.getID()) {
                this.animaTypeNumber = monsterTypeNumberTable[1];
            }
        }
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.GOURD;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return false;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        return false;
    }
}
