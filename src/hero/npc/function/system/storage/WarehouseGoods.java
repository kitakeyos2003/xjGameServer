// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.storage;

import hero.item.EquipmentInstance;

public class WarehouseGoods {

    public int goodsID;
    public short goodsNum;
    public short goodsType;
    public int indexID;
    public EquipmentInstance instance;

    public WarehouseGoods(final int _goodsID, final short _goodsNum, final short _goodsType, final EquipmentInstance _instance, final int _indexID) {
        this.goodsID = _goodsID;
        this.goodsNum = _goodsNum;
        this.goodsType = _goodsType;
        this.instance = _instance;
        this.indexID = _indexID;
    }
}
