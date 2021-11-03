// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai.data;

import hero.skill.ActiveSkill;

public class SkillAIData {

    public int id;
    public byte useTimesType;
    public byte intervalCondition;
    public int intervalTime;
    public float hpConsumePercent;
    public float odds;
    public byte onlyReleaseCondition;
    public int timeOfFighting;
    public float hpTracePercent;
    public float mpTracePercent;
    public byte targetSettingCondition;
    public byte sequenceOfSettingCondition;
    public int releaseDelay;
    public String shoutContentWhenRelease;
    public ActiveSkill skill;
}
