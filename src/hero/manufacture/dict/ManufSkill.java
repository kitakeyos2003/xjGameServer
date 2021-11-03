// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.dict;

import java.util.Random;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import hero.manufacture.Odd;

public class ManufSkill {

    public int id;
    public String name;
    public byte type;
    public short icon;
    public boolean npcStudy;
    public byte category;
    public int money;
    public byte needLevel;
    public short needSkillPoint;
    public final byte getPoint = 1;
    public int abruptID;
    public String desc;
    public int[] stagesNeedPoint;
    public byte[] stagesGetPointOdd;
    public int[] needGoodsID;
    public short[] needGoodsNum;
    public int[] getGoodsID;
    public short[] getGoodsNum;
    public byte[] getGoodsOdd;
    private Odd[] getGoodsOddList;
    private static final int MAX_SKILL_POINT = 400;

    public ManufSkill() {
        this.needLevel = 1;
        this.stagesNeedPoint = new int[3];
        this.stagesGetPointOdd = new byte[3];
        this.needGoodsID = new int[8];
        this.needGoodsNum = new short[8];
        this.getGoodsID = new int[3];
        this.getGoodsNum = new short[3];
        this.getGoodsOdd = new byte[3];
        this.getGoodsOddList = new Odd[3];
    }

    public Odd[] getGetGoodsOddList() {
        return this.getGoodsOddList;
    }

    public void setGetGoodsOddList(final List<Odd> _getGoodsOddList) {
        OddCompare compare = new OddCompare();
        Collections.sort(_getGoodsOddList, compare);
        this.getGoodsOddList[0] = _getGoodsOddList.get(0);
        this.getGoodsOddList[1] = _getGoodsOddList.get(1);
        this.getGoodsOddList[2] = _getGoodsOddList.get(2);
    }

    public boolean canAddPoint(final int point) {
        if (point >= 400) {
            return false;
        }
        Random r = new Random();
        int odd = r.nextInt(100);
        byte stage = this.getStage(point);
        return stage <= 3 && this.stagesGetPointOdd[stage - 1] > odd;
    }

    private byte getStage(final int point) {
        if (point <= this.stagesNeedPoint[0]) {
            return 1;
        }
        if (point <= this.stagesNeedPoint[1]) {
            return 2;
        }
        if (point <= this.stagesNeedPoint[2]) {
            return 3;
        }
        if (point > this.stagesNeedPoint[2]) {
            return 4;
        }
        return 1;
    }
}
