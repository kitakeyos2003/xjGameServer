// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.dict;

public class Refined {

    public int id;
    public String name;
    public short icon;
    public boolean npcStudy;
    public byte category;
    public byte needLvl;
    public int money;
    public int point;
    public int abruptID;
    public String desc;
    public int[] needSoulID;
    public short[] needSoulNum;
    public int[] getGoodsID;
    public short[] getGoodsNum;
    public int needGourd;

    public Refined() {
        this.needSoulID = new int[8];
        this.needSoulNum = new short[8];
        this.getGoodsID = new int[3];
        this.getGoodsNum = new short[3];
    }
}
