// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.storage;

import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;

public class WarehouseDict {

    private static WarehouseDict instance;
    private HashMap<String, Warehouse> warehouses;
    private ReentrantLock lock;

    private WarehouseDict() {
        this.warehouses = null;
        this.lock = new ReentrantLock();
        this.warehouses = new HashMap<String, Warehouse>();
    }

    public static WarehouseDict getInstance() {
        if (WarehouseDict.instance == null) {
            WarehouseDict.instance = new WarehouseDict();
        }
        return WarehouseDict.instance;
    }

    public Warehouse getWarehouseByNickname(final String _nickname) {
        try {
            this.lock.lock();
            Warehouse w = this.warehouses.get(_nickname);
            if (w == null) {
                byte lvl = WarehouseDB.selLvl(_nickname);
                w = new Warehouse(_nickname, lvl);
                WarehouseDB.selGoods(_nickname, w);
                this.warehouses.put(_nickname, w);
            }
            return w;
        } finally {
            this.lock.unlock();
        }
    }

    public void loadWarehouseByNickname(final String _nickname) {
        try {
            this.lock.lock();
            Warehouse w = this.warehouses.get(_nickname);
            if (w == null) {
                byte lvl = WarehouseDB.selLvl(_nickname);
                w = new Warehouse(_nickname, lvl);
                WarehouseDB.selGoods(_nickname, w);
                this.warehouses.put(_nickname, w);
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public void releaseWarehouseByNickname(final String _nickname) {
        try {
            this.lock.lock();
            Warehouse warehouse = this.warehouses.remove(_nickname);
            if (warehouse != null) {
                warehouse.clear();
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
}
