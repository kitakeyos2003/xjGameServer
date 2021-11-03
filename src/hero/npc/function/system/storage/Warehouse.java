// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.storage;

import hero.item.EquipmentInstance;
import hero.item.service.GoodsDAO;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

public class Warehouse {

    public static final byte MAX_LEVEL = 8;
    public static final byte UP_SIZE = 8;
    public static final short DEFAULT_SIZE = 16;
    private String ownerNickname;
    private byte level;
    private ArrayList<WarehouseGoods> goodsList;
    private ReentrantLock lock;

    public Warehouse(final String _ownerNickname, final byte _level) {
        this.lock = new ReentrantLock();
        this.ownerNickname = _ownerNickname;
        this.level = _level;
        this.goodsList = new ArrayList<WarehouseGoods>();
        for (int maxSize = 16 + 8 * _level, i = 0; i < maxSize; ++i) {
            this.goodsList.add(null);
        }
    }

    protected void clear() {
        this.goodsList.clear();
    }

    public String getOwnerNickname() {
        return this.ownerNickname;
    }

    public byte getLevel() {
        return this.level;
    }

    public int getUpLevelMoney() {
        return (this.level + 1) * (this.level + 1) * 5000;
    }

    public int getMaxSize() {
        return this.goodsList.size();
    }

    public int getGoodsNum() {
        try {
            this.lock.lock();
            int num = 0;
            for (final WarehouseGoods goods : this.goodsList) {
                if (goods != null) {
                    ++num;
                }
            }
            return num;
        } finally {
            this.lock.unlock();
        }
    }

    public void upLevel() {
        try {
            this.lock.lock();
            ++this.level;
            for (int i = 0; i < 8; ++i) {
                this.goodsList.add(null);
            }
            WarehouseDB.updateLvl(this.level, this.ownerNickname);
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public boolean addWarehouseGoods(final int _goodsID, final short _goodsNum, final short _goodsType, final int _userID, final int _bagGridIndex, final boolean _isAutoBall) {
        try {
            this.lock.lock();
            for (int i = 0; i < this.goodsList.size(); ++i) {
                if (this.goodsList.get(i) == null) {
                    int indexID = 0;
                    if (_goodsType == 1) {
                        indexID = GoodsDAO.selectTonic(_userID, _bagGridIndex, _goodsID);
                    }
                    this.goodsList.set(i, new WarehouseGoods(_goodsID, _goodsNum, _goodsType, null, indexID));
                    WarehouseDB.insertGoods(this.ownerNickname, (byte) i, _goodsID, _goodsNum, _goodsType, indexID, _isAutoBall);
                    return true;
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean addWarehouseGoods(final EquipmentInstance _instance) {
        try {
            this.lock.lock();
            for (int i = 0; i < this.goodsList.size(); ++i) {
                if (this.goodsList.get(i) == null) {
                    int _goodsID = _instance.getInstanceID();
                    short _goodsNum = 1;
                    short _goodsType = 0;
                    this.goodsList.set(i, new WarehouseGoods(_goodsID, _goodsNum, _goodsType, _instance, 0));
                    WarehouseDB.insertGoods(this.ownerNickname, (byte) i, _goodsID, _goodsNum, _goodsType, 0, false);
                    return true;
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    protected void addWarehouseGoods(final byte _index, final int _goodsID, final short _goodsNum, final short _goodsType, final EquipmentInstance _instance, final int single_goods_id) {
        try {
            this.lock.lock();
            this.goodsList.set(_index, new WarehouseGoods(_goodsID, _goodsNum, _goodsType, _instance, single_goods_id));
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public WarehouseGoods getWarehouseGoods(final int _index) {
        try {
            this.lock.lock();
            WarehouseGoods goods = this.goodsList.get(_index);
            return goods;
        } finally {
            this.lock.unlock();
        }
    }

    public void removeWarehouseGoods(final int _index) {
        try {
            this.lock.lock();
            this.goodsList.set(_index, null);
            WarehouseDB.delGoods(this.ownerNickname, (byte) _index);
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public ArrayList<WarehouseGoods> getGoodsList() {
        return this.goodsList;
    }
}
