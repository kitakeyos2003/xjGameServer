// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture;

import java.util.ArrayList;

public class Manufacture {

    private boolean save;
    private byte lvl;
    private int point;
    private ManufactureType type;
    private ArrayList<Integer> manufSkillIDList;
    public static final byte MAX_LVL = 5;

    public Manufacture(final ManufactureType _type) {
        this.lvl = 1;
        this.type = _type;
        this.manufSkillIDList = new ArrayList<Integer>();
    }

    public ManufactureType getManufactureType() {
        return this.type;
    }

    public void setSave(final boolean _change) {
        this.save = _change;
    }

    public boolean isSave() {
        return this.save;
    }

    public void lvlUp() {
        ++this.lvl;
        this.save = true;
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

    public void addPoint(final int _point) {
        this.point += _point;
        this.save = true;
    }

    public int getPoint() {
        return this.point;
    }

    public void addManufID(final int _manufID) {
        this.manufSkillIDList.add(_manufID);
    }

    public ArrayList<Integer> getManufIDList() {
        return this.manufSkillIDList;
    }

    public boolean isStudyedManufSkillID(final int _manufSkillID) {
        return this.manufSkillIDList.contains(_manufSkillID);
    }
}
