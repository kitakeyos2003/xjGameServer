// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.bag;

import hero.item.Goods;
import hero.item.service.GoodsServiceImpl;
import hero.item.EquipmentInstance;
import hero.item.bag.exception.BagException;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.SingleGoods;
import org.apache.log4j.Logger;

public class Inventory {

    private static Logger log;
    private int masterUserID;
    private EquipmentBag equpmentBag;
    private SingleGoodsBag medicamentBag;
    private SingleGoodsBag materialBag;
    private SingleGoodsBag taskToolBag;
    private SingleGoodsBag specialGoodsBag;
    private PetEquipmentBag petEquipmentBag;
    private PetContainer petContainer;
    private SingleGoodsBag petGoodsBag;
    public static final byte GENERIC_BAG_GRID_SIZE = 16;
    public static final byte MATERIAL_BAG_GRID_SIZE = 16;
    public static final byte TOOL_GOODS_BAG_GRID_SIZE = 16;
    public static final byte STEP_GRID_SIZE = 8;
    public static final byte BAG_MAX_SIZE = 40;

    static {
        Inventory.log = Logger.getLogger((Class) Inventory.class);
    }

    public Inventory(final int _masterUserID) {
        this.masterUserID = _masterUserID;
        this.equpmentBag = new EquipmentBag((short) 16);
        this.medicamentBag = new SingleGoodsBag((short) 16);
        this.materialBag = new SingleGoodsBag((short) 16);
        this.specialGoodsBag = new SingleGoodsBag((short) 16);
        this.taskToolBag = new SingleGoodsBag((short) 16);
        this.petEquipmentBag = new PetEquipmentBag((short) 16);
        this.petContainer = new PetContainer((short) 16);
        this.petGoodsBag = new SingleGoodsBag((short) 16);
    }

    public Inventory(final int _masterUserID, final byte[] _bagSizeList) {
        this.masterUserID = _masterUserID;
        this.equpmentBag = new EquipmentBag(_bagSizeList[0]);
        (this.medicamentBag = new SingleGoodsBag(_bagSizeList[1])).initMaster(this.masterUserID);
        this.materialBag = new SingleGoodsBag(_bagSizeList[2]);
        (this.specialGoodsBag = new SingleGoodsBag(_bagSizeList[3])).initMaster(this.masterUserID);
        this.taskToolBag = new SingleGoodsBag((short) 16);
        this.petEquipmentBag = new PetEquipmentBag(_bagSizeList[4]);
        this.petContainer = new PetContainer(_bagSizeList[5]);
        this.petGoodsBag = new SingleGoodsBag(_bagSizeList[6]);
    }

    public int getOwnerUserID() {
        return this.masterUserID;
    }

    public short[] addSingleGoods(final SingleGoods _goods, final int _nums) throws BagException {
        short[] addResult = null;
        EGoodsType goodsType = GoodsContents.getGoodsType(_goods.getID());
        Inventory.log.debug((Object) ("add singleGoods type = " + goodsType));
        switch (goodsType) {
            case MATERIAL: {
                addResult = this.materialBag.add(_goods, _nums);
                break;
            }
            case MEDICAMENT: {
                addResult = this.medicamentBag.add(_goods, _nums);
                break;
            }
            case TASK_TOOL: {
                addResult = this.taskToolBag.add(_goods, _nums);
                break;
            }
            case SPECIAL_GOODS: {
                addResult = this.specialGoodsBag.add(_goods, _nums);
                break;
            }
            case PET_GOODS: {
                addResult = this.petGoodsBag.add(_goods, _nums);
                break;
            }
        }
        return addResult;
    }

    public int addEquipmentIns(final EquipmentInstance _equipmentInstance) throws BagException {
        _equipmentInstance.changeOwnerUserID(this.masterUserID);
        Inventory.log.debug((Object) ("\u6dfb\u52a0\u88c5\u5907\u5b9e\u4f8b equipment owner type = " + _equipmentInstance.getOwnerType()));
        if (_equipmentInstance.getOwnerType() == 2) {
            return this.addPetEquipment(_equipmentInstance);
        }
        return this.equpmentBag.add(_equipmentInstance);
    }

    public int addPetEquipment(final EquipmentInstance _equipmentInstance) throws BagException {
        _equipmentInstance.changeOwnerUserID(this.masterUserID);
        return this.petEquipmentBag.add(_equipmentInstance);
    }

    public EquipmentBag getEquipmentBag() {
        return this.equpmentBag;
    }

    public short[] addSingleGoods(final int _goodsID, final int _nums) throws BagException {
        Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(_goodsID);
        if (EGoodsType.EQUIPMENT != goods.getGoodsType() && EGoodsType.PET_EQUIQ_GOODS != goods.getGoodsType()) {
            return this.addSingleGoods((SingleGoods) goods, _nums);
        }
        return null;
    }

    public SingleGoodsBag getMedicamentBag() {
        return this.medicamentBag;
    }

    public SingleGoodsBag getMaterialBag() {
        return this.materialBag;
    }

    public SingleGoodsBag getTaskToolBag() {
        return this.taskToolBag;
    }

    public SingleGoodsBag getSpecialGoodsBag() {
        return this.specialGoodsBag;
    }

    public PetEquipmentBag getPetEquipmentBag() {
        return this.petEquipmentBag;
    }

    public PetContainer getPetContainer() {
        return this.petContainer;
    }

    public SingleGoodsBag getPetGoodsBag() {
        return this.petGoodsBag;
    }
}
