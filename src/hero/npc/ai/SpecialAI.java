// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai;

import hero.npc.ai.data.AIDataDict;
import hero.npc.ai.data.SpecialWisdom;
import hero.npc.ai.data.SpecialAIData;
import hero.npc.Monster;

public class SpecialAI {

    private Monster dominator;
    private boolean hasExecuted;
    private float lastTraceHpPercent;
    private long timeThatLastExecuted;
    private SpecialAIData data;
    private SpecialWisdom wisdom;
    public static final byte USE_TIMES_OF_INTERVAL = 1;
    public static final byte USE_TIMES_OF_ONLY = 2;
    public static final byte INTERVAL_CONDITION_OF_TIME = 1;
    public static final byte INTERVAL_CONDITION_OF_HP_CONSUME = 2;
    public static final byte INTERVAL_CONDITION_OF_HATRED_TARGET_DIE = 3;
    public static final byte ONLY_CONDITION_OF_TRACE_HP = 1;
    public static final byte ONLY_CONDITION_OF_TRACE_MP = 2;
    public static final byte SUB_AI_TYPE_OF_CALL = 1;
    public static final byte SUB_AI_TYPE_OF_CHANGES = 2;
    public static final byte SUB_AI_TYPE_OF_DISAPPEAR = 3;
    public static final byte SUB_AI_TYPE_OF_RUN_AWAY = 4;
    public static final byte SUB_AI_TYPE_OF_SHOUT = 5;

    public SpecialAI(final Monster _dominator, final SpecialAIData _data) {
        this.dominator = _dominator;
        this.data = _data;
        this.wisdom = AIDataDict.getInstance().getSpecialWisdom(this.data);
    }

    public void execute() {
        if (1 == this.data.useTimesType) {
            if (this.data.intervalCondition == 1) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - this.timeThatLastExecuted >= this.data.intervalTime) {
                    this.wisdom.think(this.dominator);
                    this.timeThatLastExecuted = currentTime;
                }
            } else if (this.data.intervalCondition == 2) {
                if (this.lastTraceHpPercent - this.dominator.getHPPercent() >= this.data.hpConsumePercent) {
                    this.wisdom.think(this.dominator);
                    this.lastTraceHpPercent = this.dominator.getHPPercent();
                }
            } else if (this.data.intervalCondition == 3) {
                this.wisdom.think(this.dominator);
            }
        } else if (!this.hasExecuted) {
            if (this.data.onlyReleaseCondition == 1) {
                if (this.dominator.getHPPercent() <= this.data.hpTracePercent) {
                    this.wisdom.think(this.dominator);
                    this.hasExecuted = true;
                }
            } else if (this.data.onlyReleaseCondition == 2) {
                if (this.dominator.getMPPercent() <= this.data.mpTracePercent) {
                    this.wisdom.think(this.dominator);
                    this.hasExecuted = true;
                }
            } else {
                this.hasExecuted = true;
            }
        }
    }
}
