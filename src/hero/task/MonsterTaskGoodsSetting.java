// 
// Decompiled by Procyon v0.5.36
// 
package hero.task;

import java.util.Random;

public class MonsterTaskGoodsSetting {

    private static final Random random;
    public int goodsID;
    public int taskID;
    public float odds;
    public short maxNumberPerTime;

    static {
        random = new Random();
    }

    public MonsterTaskGoodsSetting(final int _goodsID, final int _taskID, final float _odds, final short _maxNumberPerTime) {
        this.goodsID = _goodsID;
        this.taskID = _taskID;
        this.odds = _odds;
        this.maxNumberPerTime = _maxNumberPerTime;
    }

    public int getDropNumber() {
        float randomOdds = MonsterTaskGoodsSetting.random.nextFloat();
        if (randomOdds <= this.odds) {
            return MonsterTaskGoodsSetting.random.nextInt(this.maxNumberPerTime) + 1;
        }
        return 0;
    }
}
