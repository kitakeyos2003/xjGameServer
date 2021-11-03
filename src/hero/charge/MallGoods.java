// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge;

import hero.item.detail.EGoodsTrait;

public class MallGoods {

    public int id;
    public String name;
    public byte type;
    public EGoodsTrait trait;
    public int price;
    public int[][] goodsList;
    public short icon;
    public String desc;
    public byte buyNumberPerTime;
    public static final byte TYPE_EQUIPMENT = 1;
    public static final byte TYPE_MEDICAMENT = 2;
    public static final byte TYPE_MATERIAL = 3;
    public static final byte TYPE_SKILL_BOOK = 4;
    public static final byte TYPE_PET = 5;
    public static final byte TYPE_BAG = 6;
    public static final byte TYPE_PET_EQUIP = 7;
    public static final byte TYPE_PET_GOODS = 8;

    public void setBuyNumberPerTime(final byte _buyNumberPerTime) {
        this.buyNumberPerTime = _buyNumberPerTime;
    }

    private static final byte findType(final String _typeDesc) {
        if (_typeDesc.equals("\u88c5\u5907")) {
            return 1;
        }
        if (_typeDesc.equals("\u836f\u6c34")) {
            return 2;
        }
        if (_typeDesc.equals("\u795e\u5668")) {
            return 3;
        }
        if (_typeDesc.equals("\u6280\u80fd\u4e66")) {
            return 4;
        }
        if (_typeDesc.equals("\u5ba0\u7269")) {
            return 5;
        }
        if (_typeDesc.equals("\u793c\u5305")) {
            return 6;
        }
        if (_typeDesc.equals("\u5ba0\u7269\u7269\u54c1")) {
            return 8;
        }
        if (_typeDesc.equals("\u5ba0\u7269\u88c5\u5907")) {
            return 7;
        }
        return 0;
    }
}
