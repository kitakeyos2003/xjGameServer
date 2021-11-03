// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge;

public class FeePointInfo {

    public byte id;
    public String fpcode;
    public String name;
    public int price;
    public int presentPoint;
    public byte typeID;
    public String desc;
    public FPType type;

    public FeePointInfo clone() {
        FeePointInfo fpt = new FeePointInfo();
        fpt.id = this.id;
        fpt.fpcode = this.fpcode;
        fpt.name = this.name;
        fpt.price = this.price;
        fpt.presentPoint = this.presentPoint;
        fpt.typeID = this.typeID;
        fpt.desc = this.desc;
        fpt.type = this.type;
        return fpt;
    }
}
