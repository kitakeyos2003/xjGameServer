// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai.data;

import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.MonsterChangesNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.Monster;
import hero.share.EMagic;

public class Changes extends SpecialWisdom {

    public int id;
    public String shoutContent;
    public int strength;
    public int agility;
    public int inte;
    public int spirit;
    public int lucky;
    public int defense;
    public int minAttack;
    public int maxAttack;
    public EMagic magicType;
    public int minDamageValue;
    public int maxDamageValue;
    public int newHp;
    public int sanctityFastness;
    public int umbraFastness;
    public int fireFastness;
    public int waterFastness;
    public int soilFastness;
    public byte endType;
    public int keepTime;
    public float reduceHpPercent;
    public SkillAIData[] skillAIDataList;
    public short imageID;
    public static final byte END_CONDITION_TYPE_OF_TIME = 1;
    public static final byte END_CONDITION_TYPE_OF_HP = 2;

    @Override
    public byte getType() {
        return 2;
    }

    @Override
    public void think(final Monster _dominator) {
        NotPlayerServiceImpl.getInstance().processMonsterChanges(_dominator, this);
        _dominator.setChangesStatus(true);
        _dominator.getAI().currentChangesData = this;
        _dominator.getAI().traceDisappearTime = this.keepTime;
        _dominator.getAI().traceHpWhenChanges = _dominator.getHp();
        _dominator.getAI().currentSkillAIList = AIDataDict.getInstance().buildSkillAIList(this.skillAIDataList, _dominator.getHPPercent());
        MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(), new MonsterChangesNotify(_dominator), false, 0);
    }

    public boolean endChanges(final Monster _dominator, final long _timeClockWhenChanges, final int _traceHpWhenChanges) {
        if (1 == this.endType) {
            if (System.currentTimeMillis() - _timeClockWhenChanges >= this.keepTime) {
                this.changesArchetype(_dominator, _traceHpWhenChanges);
            }
        } else if (_traceHpWhenChanges / _dominator.getActualProperty().getHpMax() - _dominator.getHPPercent() >= this.reduceHpPercent) {
            this.changesArchetype(_dominator, _traceHpWhenChanges);
        }
        return false;
    }

    private void changesArchetype(final Monster _dominator, final int _traceHpWhenChanges) {
        NotPlayerServiceImpl.getInstance().processMonsterChangesToArchetype(_dominator, _traceHpWhenChanges, this);
        _dominator.setChangesStatus(false);
        _dominator.getAI().currentSkillAIList = _dominator.getAI().skillAIList;
        MapSynchronousInfoBroadcast.getInstance().put(_dominator.where(), new MonsterChangesNotify(_dominator), false, 0);
    }
}
