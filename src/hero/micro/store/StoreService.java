// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.store;

import hero.player.service.PlayerServiceImpl;
import hero.micro.store.message.OtherStoreGoodsList;
import hero.micro.service.MicroServiceImpl;
import hero.micro.store.message.GridGoodsChangesNotify;
import hero.item.bag.Inventory;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.micro.store.message.StoreStatusChanged;
import hero.item.EquipmentInstance;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.detail.EGoodsType;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class StoreService {

    private static Logger log;
    private static FastMap<Integer, PersionalStore> storeTable;

    static {
        StoreService.log = Logger.getLogger((Class) StoreService.class);
        StoreService.storeTable = (FastMap<Integer, PersionalStore>) new FastMap();
    }

    private StoreService() {
    }

    public static void login(final HeroPlayer player) {
        PersionalStore store = get(player.getUserID());
        if (store != null && (store.opened || player.isSelling())) {
            StoreService.log.debug(("\u8fdb\u5165\u6e38\u620f\uff0c\u6446\u644a\u72b6\u6001 = " + store.opened + ", player storestatus = " + player.isSelling()));
            clear(player.getUserID());
        }
        takeOffAll(player);
    }

    public static void clear(final int _userID) {
        StoreService.storeTable.remove(_userID);
    }

    public static PersionalStore get(final int _userID) {
        return (PersionalStore) StoreService.storeTable.get(_userID);
    }

    public static synchronized void openStore(final HeroPlayer _player, final String _storeName, final int[][] _newGoodsDataList) {
        PersionalStore store = (PersionalStore) StoreService.storeTable.get(_player.getUserID());
        if (store != null) {
            if (store.opened) {
                return;
            }
        } else {
            if (_newGoodsDataList == null) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u4e0a\u8d27\uff1f\u60f3\u8981\u7a7a\u624b\u5957\u767d\u72fc\u5417\uff1f"));
                return;
            }
            store = new PersionalStore();
            StoreService.storeTable.put(_player.getUserID(), store);
        }
        store.setName(_storeName);
        if (_newGoodsDataList != null) {
            try {
                Inventory inventory = _player.getInventory();
                for (final int[] goodsForSaleData : _newGoodsDataList) {
                    byte goodsType = (byte) goodsForSaleData[0];
                    byte bagGrid = (byte) goodsForSaleData[1];
                    int goodsID = goodsForSaleData[2];
                    byte number = (byte) goodsForSaleData[3];
                    byte storeBagGrid = (byte) goodsForSaleData[4];
                    int salePrice = goodsForSaleData[5];
                    StoreService.log.debug(("open store add goods type=" + goodsType + ",bagGrid=" + bagGrid + ",goodsID=" + goodsID + ",number=" + number + ",storeBagGrid=" + storeBagGrid + ",salePrice=" + salePrice));
                    switch (EGoodsType.getGoodsType((byte) goodsForSaleData[0])) {
                        case EQUIPMENT: {
                            EquipmentInstance equipment = GoodsServiceImpl.getInstance().removeEquipmentOfBag(_player, inventory.getEquipmentBag(), bagGrid, CauseLog.STORE);
                            if (equipment != null && equipment.getInstanceID() == goodsID) {
                                store.add(goodsType, storeBagGrid, 0, number, equipment, salePrice);
                                break;
                            }
                            break;
                        }
                        case MATERIAL: {
                            if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, inventory.getMaterialBag(), bagGrid, goodsID, number, CauseLog.STORE)) {
                                store.add(goodsType, storeBagGrid, goodsID, number, null, salePrice);
                                break;
                            }
                            break;
                        }
                        case MEDICAMENT: {
                            if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, inventory.getMedicamentBag(), bagGrid, goodsID, number, CauseLog.STORE)) {
                                store.add(goodsType, storeBagGrid, goodsID, number, null, salePrice);
                                break;
                            }
                            break;
                        }
                        case SPECIAL_GOODS: {
                            if (GoodsServiceImpl.getInstance().reduceSingleGoods(_player, inventory.getSpecialGoodsBag(), bagGrid, goodsID, number, CauseLog.STORE)) {
                                store.add(goodsType, storeBagGrid, goodsID, number, null, salePrice);
                                break;
                            }
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
            }
            StoreDAO.insertGoods2Store(_player.getUserID(), _newGoodsDataList);
        } else if (store.goodsNumber == 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u4e0a\u8d27\uff1f\u60f3\u8981\u7a7a\u624b\u5957\u767d\u72fc\u5417\uff1f"));
            return;
        }
        _player.setSellStatus(store.opened = true);
        StoreStatusChanged notify = new StoreStatusChanged(_player.getID(), true, _storeName);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), notify);
        MapSynchronousInfoBroadcast.getInstance().put(_player.where(), notify, true, _player.getID());
    }

    public static void closeStore(final HeroPlayer _player) {
        StoreService.log.info("@@ closeStore ...");
        PersionalStore store = (PersionalStore) StoreService.storeTable.get(_player.getUserID());
        if (store != null && store.opened) {
            StoreService.log.info("#@#@ strat close store....");
            _player.setSellStatus(store.opened = false);
            store.takeOffAll(_player);
            store.removeAllEnterPlayer();
            StoreService.storeTable.remove(_player.getUserID());
            StoreStatusChanged notify = new StoreStatusChanged(_player.getID(), false);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), notify);
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(), notify, true, _player.getID());
        }
    }

    public static void takeOffAll(final HeroPlayer _player) {
        _player.setSellStatus(false);
        PersionalStore store = (PersionalStore) StoreService.storeTable.get(_player.getUserID());
        if (store != null) {
            store.opened = false;
            StoreService.log.debug(("take off all before goodsnumber = " + store.goodsNumber));
            if (store.goodsNumber > 0) {
                store.takeOffAll(_player);
            }
            StoreStatusChanged notify = new StoreStatusChanged(_player.getID(), false);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), notify);
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(), notify, true, _player.getID());
        } else {
            store = StoreDAO.loadStore(_player.getUserID());
            if (store != null) {
                store.opened = false;
                StoreService.log.debug(("take off all before goodsnumber = " + store.goodsNumber));
                if (store.goodsNumber > 0) {
                    store.takeOffAll(_player);
                }
            }
        }
    }

    public static void removeGoods(final HeroPlayer _player, final byte _gridIndex) {
        PersionalStore store = (PersionalStore) StoreService.storeTable.get(_player.getUserID());
        if (store != null && !store.opened) {
            PersionalStore.GoodsForSale goodsForSale = store.goodsList[_gridIndex];
            if (goodsForSale != null) {
                if (goodsForSale.goodsType == EGoodsType.EQUIPMENT.value()) {
                    if (GoodsServiceImpl.getInstance().addEquipmentInstance2Bag(_player, goodsForSale.equipment, CauseLog.STORE) != null && store.remove(_gridIndex, goodsForSale) && StoreDAO.removeFromStore(_player.getUserID(), _gridIndex)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GridGoodsChangesNotify(true, _gridIndex, (byte) 2));
                    }
                } else if (GoodsServiceImpl.getInstance().addGoods2Package(_player, goodsForSale.singleGoods, goodsForSale.number, CauseLog.STORE) != null && store.remove(_gridIndex, goodsForSale) && StoreDAO.removeFromStore(_player.getUserID(), _gridIndex)) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GridGoodsChangesNotify(true, _gridIndex, (byte) 2));
                }
                if (store.goodsNumber == 0) {
                    StoreService.storeTable.remove(_player.getUserID());
                }
            }
        }
    }

    public static void modifyPrice(final HeroPlayer _player, final byte _gridIndex, final int _newPrice) {
        if (_newPrice > 0 && _newPrice < 1000000000) {
            PersionalStore store = (PersionalStore) StoreService.storeTable.get(_player.getUserID());
            if (store != null && !store.opened) {
                PersionalStore.GoodsForSale goodsForSale = store.goodsList[_gridIndex];
                if (goodsForSale != null) {
                    int goodsID;
                    if (goodsForSale.goodsType == EGoodsType.EQUIPMENT.value()) {
                        goodsID = goodsForSale.equipment.getInstanceID();
                    } else {
                        goodsID = goodsForSale.singleGoods.getID();
                    }
                    if (StoreDAO.changePrice(_player.getUserID(), _gridIndex, goodsID, _newPrice)) {
                        goodsForSale.salePrice = _newPrice;
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GridGoodsChangesNotify(true, _gridIndex, (byte) 1, _newPrice));
                    }
                }
            }
        }
    }

    public static void buy(final HeroPlayer _buyer, final HeroPlayer _seller, final byte _gridIndex, final int _goodsID) {
        PersionalStore store = MicroServiceImpl.getInstance().getStore(_seller.getUserID());
        if (store == null) {
            ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(), new Warning("\u5546\u5e97\u4e0d\u5b58\u5728"));
        } else {
            PersionalStore.GoodsForSale goods = store.goodsList[_gridIndex];
            if (goods != null) {
                if (_buyer.getMoney() < goods.salePrice) {
                    ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f"));
                    return;
                }
                if (goods.goodsType == EGoodsType.EQUIPMENT.value() || goods.goodsType == EGoodsType.PET_EQUIQ_GOODS.value()) {
                    if (_goodsID != goods.equipment.getInstanceID()) {
                        ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(), new Warning("\u5546\u54c1\u4e0d\u5b58\u5728"));
                        return;
                    }
                    if (GoodsServiceImpl.getInstance().addEquipmentInstance2Bag(_buyer, goods.equipment, CauseLog.STORE) == null) {
                        return;
                    }
                    if (!store.remove(_gridIndex, goods) && StoreDAO.removeFromStore(_seller.getUserID(), _gridIndex)) {
                        return;
                    }
                    for (final HeroPlayer other : store.getEnterPlayerList()) {
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new OtherStoreGoodsList(MicroServiceImpl.getInstance().getStore(_seller.getUserID())));
                    }
                } else {
                    if (_goodsID != goods.singleGoods.getID()) {
                        ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(), new Warning("\u5546\u54c1\u4e0d\u5b58\u5728"));
                        return;
                    }
                    if (GoodsServiceImpl.getInstance().addGoods2Package(_buyer, goods.singleGoods, goods.number, CauseLog.STORE) == null) {
                        return;
                    }
                    if (!store.remove(_gridIndex, goods) && StoreDAO.removeFromStore(_seller.getUserID(), _gridIndex)) {
                        return;
                    }
                    for (final HeroPlayer other : store.getEnterPlayerList()) {
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new OtherStoreGoodsList(MicroServiceImpl.getInstance().getStore(_seller.getUserID())));
                    }
                }
                ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(), new GridGoodsChangesNotify(false, _gridIndex, (byte) 2));
                ResponseMessageQueue.getInstance().put(_seller.getMsgQueueIndex(), new GridGoodsChangesNotify(true, _gridIndex, (byte) 2));
                if (!PlayerServiceImpl.getInstance().addMoney(_seller, goods.salePrice, 1.0f, 2, "\u4e2a\u4eba\u5546\u5e97\u51fa\u552e")) {
                    return;
                }
                PlayerServiceImpl.getInstance().addMoney(_buyer, -goods.salePrice, 1.0f, 2, "\u4e2a\u4eba\u5546\u5e97\u8d2d\u4e70");
                if (store.goodsNumber == 0) {
                    store.removeAllEnterPlayer();
                    StoreService.storeTable.remove(_seller.getUserID());
                    _seller.setSellStatus(false);
                    ResponseMessageQueue.getInstance().put(_seller.getMsgQueueIndex(), new Warning("\u5356\u5b8c\u4e86,\u9a6c\u4e0a\u6253\u70ca\u55bd"));
                    StoreStatusChanged msg = new StoreStatusChanged(_seller.getID(), false);
                    ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(), msg);
                    MapSynchronousInfoBroadcast.getInstance().put(_seller.where(), msg, true, _buyer.getID());
                }
            } else {
                ResponseMessageQueue.getInstance().put(_buyer.getMsgQueueIndex(), new Warning("\u5546\u54c1\u4e0d\u5b58\u5728"));
            }
        }
    }
}
