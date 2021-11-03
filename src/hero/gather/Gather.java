// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather;

import java.util.Iterator;
import hero.item.special.Gourd;
import hero.gather.service.GatherServerImpl;
import java.util.ArrayList;

public class Gather {

    private boolean save;
    private byte lvl;
    private int point;
    private ArrayList<Integer> refinedSkillIDList;
    private ArrayList<MonsterSoul> souls;
    public static final byte MAX_LVL = 5;

    public Gather() {
        this.lvl = 1;
        this.souls = new ArrayList<MonsterSoul>();
        this.refinedSkillIDList = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getRefinedList() {
        return this.refinedSkillIDList;
    }

    public void addRefinedID(final int _refinedID) {
        this.refinedSkillIDList.add(_refinedID);
    }

    public void setSave(final boolean _change) {
        this.save = _change;
    }

    public boolean isSave() {
        return this.save;
    }

    public boolean lvlUp() {
        if (this.lvl < 5) {
            ++this.lvl;
            return true;
        }
        return false;
    }

    public byte getLvl() {
        return this.lvl;
    }

    public void setLvl(final byte _lvl) {
        this.lvl = _lvl;
    }

    public void setPoint(final int _point) {
        this.point = _point;
    }

    public boolean addPoint(final int _point) {
        if (this.point < GatherServerImpl.POINT_LIMIT[this.lvl - 1]) {
            this.point += _point;
            if (this.point > GatherServerImpl.POINT_LIMIT[this.lvl - 1]) {
                this.point = GatherServerImpl.POINT_LIMIT[this.lvl - 1];
            }
            return true;
        }
        return false;
    }

    public int getPoint() {
        return this.point;
    }

    public boolean addMosnterSoul(final int _soulID, final Gourd _gourd) {
        for (final MonsterSoul s : this.souls) {
            if (s.soulID == _soulID) {
                if (s.num < _gourd.getAnimaMaxNumerPerType()) {
                    MonsterSoul monsterSoul = s;
                    ++monsterSoul.num;
                    this.save = true;
                }
                return true;
            }
        }
        if (this.souls.size() < _gourd.getMonsterTypeNumber()) {
            this.souls.add(new MonsterSoul(_soulID));
            return this.save = true;
        }
        return false;
    }

    public void loadMonsterSoul(final MonsterSoul _soul) {
        this.souls.add(_soul);
    }

    public void releaseMonsterSoul(final byte _index) {
        if (this.souls.size() > _index) {
            this.souls.remove(_index);
            this.save = true;
        }
    }

    public void releaseMonsterSoul(final int _soulID, short _num) {
        this.save = true;
        for (int i = 0; i < this.souls.size(); ++i) {
            MonsterSoul s = this.souls.get(i);
            if (s.soulID == _soulID) {
                if (s.num > _num) {
                    MonsterSoul monsterSoul = s;
                    monsterSoul.num -= (byte) _num;
                    return;
                }
                if (s.num == _num) {
                    this.souls.remove(i);
                    return;
                }
                _num -= s.num;
                this.souls.remove(i);
                --i;
            }
        }
    }

    public void clear() {
        this.save = true;
        this.souls.clear();
    }

    public ArrayList<MonsterSoul> getMonsterSoul() {
        return this.souls;
    }

    public boolean enough(final int _soulID, final int _num) {
        int _count = 0;
        for (final MonsterSoul s : this.souls) {
            if (s.soulID == _soulID) {
                _count += s.num;
            }
        }
        return _count >= _num;
    }

    public int getNumBySoulID(final int _soulID) {
        int _count = 0;
        for (final MonsterSoul s : this.souls) {
            if (s.soulID == _soulID) {
                _count += s.num;
            }
        }
        return _count;
    }

    public boolean isStudyedRefinedID(final int _refinedID) {
        for (final int _r : this.refinedSkillIDList) {
            if (_r == _refinedID) {
                return true;
            }
        }
        return false;
    }
}
