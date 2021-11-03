// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.service;

import hero.item.special.ESpecialGoodsType;
import hero.item.SpecialGoods;
import hero.item.special.HeavenBook;
import java.util.List;
import hero.item.message.SendLegacyBoxList;
import hero.item.legacy.RaidPickerBox;
import hero.item.legacy.PersonalPickerBox;
import hero.item.legacy.MonsterLegacyBox;
import hero.map.Map;
import hero.item.detail.EGoodsTrait;
import hero.expressions.service.CEService;
import java.util.Iterator;
import hero.item.message.RefreshEquipmentDurabilityPoint;
import hero.share.EObjectType;
import hero.share.ME2GameObject;
import hero.item.message.SendBagSize;
import hero.log.service.ServiceType;
import hero.charge.service.ChargeServiceImpl;
import hero.item.special.BagExpan;
import hero.npc.function.system.storage.Warehouse;
import hero.item.message.UpgradeBagAnswer;
import hero.share.service.LogWriter;
import hero.ui.message.ResponseEuipmentPackageChange;
import hero.item.bag.EBagType;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.SingleGoods;
import hero.item.PetEquipment;
import hero.chat.service.ChatQueue;
import hero.item.EqGoods;
import hero.item.Equipment;
import hero.item.detail.EGoodsType;
import java.util.ArrayList;
import hero.item.dictionary.GoodsContents;
import hero.item.bag.SingleGoodsBag;
import hero.pet.Pet;
import hero.item.bag.exception.BagException;
import hero.item.Goods;
import hero.log.service.FlowLog;
import hero.log.service.LogServiceImpl;
import hero.log.service.CauseLog;
import hero.log.service.LoctionLog;
import hero.item.EquipmentInstance;
import hero.item.bag.EquipmentContainer;
import hero.npc.function.system.storage.WarehouseDict;
import hero.player.HeroPlayer;
import hero.item.bag.Inventory;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import hero.item.legacy.MonsterLegacyManager;
import hero.item.dictionary.ChangeVocationToolsDict;
import hero.item.legacy.WorldLegacyDict;
import hero.item.dictionary.ExchangeGoodsDict;
import hero.item.dictionary.TaskGoodsDict;
import hero.item.dictionary.SpecialGoodsDict;
import hero.item.dictionary.MaterialDict;
import hero.item.dictionary.MedicamentDict;
import hero.item.dictionary.ArmorDict;
import hero.item.dictionary.WeaponDict;
import hero.item.dictionary.SuiteEquipmentDataDict;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class GoodsServiceImpl extends AbsServiceAdaptor<GoodsConfig> {

    private static Logger log;
    private static GoodsServiceImpl instance;
    private int USABLE_SPECAIL_GOODS_ID;
    private static final short BASE_UPGRADE_POINT = 20;

    static {
        GoodsServiceImpl.log = Logger.getLogger((Class) GoodsServiceImpl.class);
    }

    private GoodsServiceImpl() {
        this.config = new GoodsConfig();
        this.USABLE_SPECAIL_GOODS_ID = 1000;
    }

    public static GoodsServiceImpl getInstance() {
        if (GoodsServiceImpl.instance == null) {
            GoodsServiceImpl.instance = new GoodsServiceImpl();
        }
        return GoodsServiceImpl.instance;
    }

    @Override
    protected void start() {
        SuiteEquipmentDataDict.getInstance().load(((GoodsConfig) this.config).suite_equipment_data_path);
        WeaponDict.getInstance().load(((GoodsConfig) this.config).weapon_data_path);
        ArmorDict.getInstance().load(((GoodsConfig) this.config).armor_data_path);
        MedicamentDict.getInstance().load(((GoodsConfig) this.config).medicament_data_path);
        MaterialDict.getInstance().load(((GoodsConfig) this.config).material_data_path);
        SpecialGoodsDict.getInstance().load(((GoodsConfig) this.config).special_goods_data_path);
        SpecialGoodsDict.getInstance().loadGiftBagData(((GoodsConfig) this.config).gift_bag_data_path);
        TaskGoodsDict.getInstance().load(((GoodsConfig) this.config).task_goods_data_path);
        ExchangeGoodsDict.getInstance().load(((GoodsConfig) this.config).exchange_goods_data_path);
        WorldLegacyDict.getInstance().load((GoodsConfig) this.config);
        ChangeVocationToolsDict.getInstance().load(((GoodsConfig) this.config).change_vocation_tool_data_path);
        MonsterLegacyManager.getInstance().startMonitor();
        GoodsDAO.load();
    }

    public short[] getYetSetJewel(final byte _level) {
        short[] view = {-1, -1};
        if (_level == 0) {
            return view;
        }
        view = ((GoodsConfig) this.config).yet_set_jewel[_level - 1];
        return view;
    }

    public void setUseableSpecailID(final int _id) {
        this.USABLE_SPECAIL_GOODS_ID = _id;
    }

    public int getUseableSpecailID() {
        return ++this.USABLE_SPECAIL_GOODS_ID;
    }

    public short[] getFlashView(final byte _flashLevel) {
        short[] view = new short[2];
        if (_flashLevel == 0) {
            return view;
        }
        view = ((GoodsConfig) this.config).shine_flash_view[_flashLevel - 1];
        return view;
    }

    public short[] getArmorFlashView(final byte _flashLevel) {
        short[] view = new short[2];
        if (_flashLevel == 0) {
            return view;
        }
        view = ((GoodsConfig) this.config).armor_shine_flash_view[_flashLevel - 1];
        return view;
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        GoodsServiceImpl.log.debug(("userid= " + _session.userID + " ,goodsserviceImpl player = " + player));
        if (player.getInventory() == null) {
            player.setInventory(new Inventory(player.getUserID(), player.bagSizes));
            GoodsDAO.loadPlayerGoods(player);
        }
    }

    @Override
    public void sessionFree(final Session _session) {
        if (_session.nickName != null) {
            WarehouseDict.getInstance().releaseWarehouseByNickname(_session.nickName);
        }
    }

    public boolean diceEquipmentOfBag(final HeroPlayer _player, final EquipmentContainer _package, final EquipmentInstance _ei, final LoctionLog _loction, final CauseLog _cause) throws BagException {
        boolean result = -1 != _package.remove(_ei) && GoodsDAO.diceEquipment(_ei.getInstanceID());
        if (result) {
            LogServiceImpl.getInstance().goodsChangeLog(_player, _ei.getArchetype(), 1, _loction, FlowLog.LOSE, _cause);
        }
        return result;
    }

    public boolean diceEquipmentOfBag(final Pet pet, final HeroPlayer player, final EquipmentContainer _package, final EquipmentInstance _ei, final LoctionLog _loction, final CauseLog _cause) throws BagException {
        boolean result = -1 != _package.remove(_ei) && GoodsDAO.diceEquipment(_ei.getInstanceID());
        if (result) {
            LogServiceImpl.getInstance().goodsChangeLog(player, _ei.getArchetype(), 1, _loction, FlowLog.LOSE, _cause);
        }
        return result;
    }

    public int removeEquipmentOfBag(final HeroPlayer _player, final EquipmentContainer _package, final EquipmentInstance _ei, final CauseLog _cause) throws BagException {
        if (GoodsDAO.removeEquipmentOfBag(_ei.getInstanceID())) {
            int index = _package.remove(_ei);
            if (index >= 0) {
                LogServiceImpl.getInstance().goodsChangeLog(_player, _ei.getArchetype(), 1, LoctionLog.BAG, FlowLog.LOSE, _cause);
            }
            return index;
        }
        return -1;
    }

    public EquipmentInstance removeEquipmentOfBag(final HeroPlayer _player, final EquipmentContainer _package, final int _bagGrid, final CauseLog _cause) throws BagException {
        EquipmentInstance equipment = _package.remove(_bagGrid);
        if (equipment != null && GoodsDAO.removeEquipmentOfBag(equipment.getInstanceID())) {
            LogServiceImpl.getInstance().goodsChangeLog(_player, equipment.getArchetype(), 1, LoctionLog.BAG, FlowLog.LOSE, _cause);
            return equipment;
        }
        return null;
    }

    public boolean reduceSingleGoods(final HeroPlayer _player, final SingleGoodsBag _package, final int _gridIndex, final Goods _goods, final int _number, final CauseLog _cause) throws BagException {
        short[] change = _package.remove(_gridIndex, _goods.getID(), _number);
        boolean result = false;
        if (change != null) {
            if (change[1] == 0) {
                result = GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(), (short) _gridIndex, _goods.getID());
            } else {
                result = GoodsDAO.updateGridSingleGoodsNumberOfBag(_player.getUserID(), _goods.getID(), change[1], (short) _gridIndex);
            }
        }
        if (result) {
            LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number, LoctionLog.BAG, FlowLog.LOSE, _cause);
        }
        return result;
    }

    public boolean reduceSingleGoods(final HeroPlayer _player, final SingleGoodsBag _package, final int _gridIndex, final int _goodsID, final int _number, final CauseLog _cause) throws BagException {
        Goods goods = GoodsContents.getGoods(_goodsID);
        return this.reduceSingleGoods(_player, _package, _gridIndex, goods, _number, _cause);
    }

    public boolean deleteSingleGoods(final HeroPlayer _player, final SingleGoodsBag _package, final Goods _goods, final int _number, final CauseLog _cause) throws BagException {
        ArrayList<int[]> result = _package.remove(_goods.getID(), (short) _number);
        boolean[] flag = new boolean[result.size()];
        for (int i = 0; i < result.size(); ++i) {
            int[] change = result.get(i);
            if (change[1] == 0) {
                flag[i] = GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(), (short) change[0], _goods.getID());
            } else {
                flag[i] = GoodsDAO.updateGridSingleGoodsNumberOfBag(_player.getUserID(), _goods.getID(), change[1], (short) change[0]);
            }
        }
        boolean[] array;
        for (int length = (array = flag).length, j = 0; j < length; ++j) {
            boolean f = array[j];
            if (!f) {
                return false;
            }
        }
        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number, LoctionLog.BAG, FlowLog.LOSE, _cause);
        return true;
    }

    public boolean deleteSingleGoods(final HeroPlayer _player, final SingleGoodsBag _package, final int _goodsID, final int _number, final CauseLog _cause) throws BagException {
        Goods goods = GoodsContents.getGoods(_goodsID);
        return this.deleteSingleGoods(_player, _package, goods, _number, _cause);
    }

    public boolean deleteSingleGoods(final HeroPlayer _player, final SingleGoodsBag _package, final int _goodsID, final CauseLog _cause) throws BagException {
        Goods goods = GoodsContents.getGoods(_goodsID);
        return this.deleteSingleGoods(_player, _package, goods, _cause);
    }

    public boolean deleteOne(final HeroPlayer _player, final SingleGoodsBag _package, final int _goodsID, final CauseLog _cause) throws BagException {
        short[] change = _package.removeOne(_goodsID);
        if (change == null) {
            return false;
        }
        Goods goods = GoodsContents.getGoods(_goodsID);
        LogServiceImpl.getInstance().goodsChangeLog(_player, goods, 1, LoctionLog.BAG, FlowLog.LOSE, _cause);
        if (change[1] == 0) {
            return GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(), change[0], _goodsID);
        }
        return GoodsDAO.updateGridSingleGoodsNumberOfBag(_player.getUserID(), _goodsID, change[1], change[0]);
    }

    public static EquipmentInstance buildEquipmentInstance(final int _userID, final int _equipmentID) {
        EquipmentInstance ei = EquipmentFactory.getInstance().build(_userID, _userID, _equipmentID);
        if (ei != null) {
            GoodsDAO.buildEquipmentInstance(ei);
        }
        return ei;
    }

    public boolean deleteSingleGoods(final HeroPlayer _player, final SingleGoodsBag _package, final Goods _goods, final CauseLog _cause) throws BagException {
        int num = _package.remove(_goods.getID());
        if (GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(), _goods.getID())) {
            LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, num, LoctionLog.BAG, FlowLog.LOSE, _cause);
            return true;
        }
        return false;
    }

    public boolean diceSingleGoods(final HeroPlayer _player, final SingleGoodsBag _package, final int _gridIndex, final int _goodsID, final CauseLog _cause) throws BagException {
        if (GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(), (short) _gridIndex, _goodsID)) {
            Goods goods = GoodsContents.getGoods(_goodsID);
            int[] items = _package.getItemData(_gridIndex);
            int _num = 0;
            if (items != null) {
                _num = items[1];
            }
            LogServiceImpl.getInstance().goodsChangeLog(_player, goods, _num, LoctionLog.BAG, FlowLog.LOSE, _cause);
            return _package.remove(_gridIndex, _goodsID);
        }
        return false;
    }

    public short[] addGoods2PackageByTask(final HeroPlayer _player, final Goods _goods, final int _number, final CauseLog _cause) {
        try {
            if (_goods != null && _number > 0) {
                GoodsServiceImpl.log.debug((" \u6dfb\u52a0\u7269\u54c1 addGoods2Package  goodstype= " + _goods.getGoodsType()));
                if (_goods.getGoodsType() == EGoodsType.EQUIPMENT) {
                    GoodsServiceImpl.log.debug(" \u7ed9\u73a9\u5bb6\u6dfb\u52a0\u88c5\u5907");
                    Equipment e = (Equipment) _goods;
                    EquipmentInstance ei = EquipmentFactory.getInstance().build(_player.getUserID(), _player.getUserID(), e);
                    if (e.getBindType() == 2) {
                        ei.bind();
                    }
                    int gridInex = _player.getInventory().addEquipmentIns(ei);
                    if (-1 != gridInex) {
                        GoodsDAO.buildEquipment2Bag(_player.getUserID(), ei, gridInex);
                        ChatQueue.getInstance().addGoodsMsg(_player, "\u83b7\u5f97\u4e86", _goods.getName(), _goods.getTrait().getViewRGB(), 1);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number, LoctionLog.BAG, FlowLog.GET, _cause);
                        return new short[]{(short) gridInex, 1};
                    }
                } else if (_goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS) {
                    GoodsServiceImpl.log.debug(" \u7ed9\u5ba0\u7269\u6dfb\u52a0\u88c5\u5907");
                    PetEquipment e2 = (PetEquipment) _goods;
                    EquipmentInstance ei = EquipmentFactory.getInstance().build(_player.getUserID(), _player.getUserID(), e2);
                    int gridInex = _player.getInventory().addEquipmentIns(ei);
                    if (-1 != gridInex) {
                        GoodsDAO.buildEquipment2Bag(_player.getUserID(), ei, gridInex);
                        ChatQueue.getInstance().addGoodsMsg(_player, "\u83b7\u5f97\u4e86", _goods.getName(), _goods.getTrait().getViewRGB(), 1);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number, LoctionLog.BAG, FlowLog.GET, _cause);
                        return new short[]{(short) gridInex, 1};
                    }
                } else {
                    SingleGoods sg = (SingleGoods) _goods;
                    short[] change = _player.getInventory().addSingleGoods(sg, _number);
                    if (change != null) {
                        if (change[1] > _number) {
                            GoodsDAO.updateGridSingleGoodsNumberOfBag(_player.getUserID(), _goods.getID(), change[1], change[0]);
                        } else if (change[1] == _number) {
                            GoodsDAO.addSingleGoods(_player.getUserID(), ((SingleGoods) _goods).getSingleGoodsType(), _goods.getID(), change[1], change[0]);
                        }
                        ChatQueue.getInstance().addGoodsMsg(_player, "\u83b7\u5f97\u4e86", _goods.getName(), _goods.getTrait().getViewRGB(), _number);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number, LoctionLog.BAG, FlowLog.GET, _cause);
                        return change;
                    }
                }
            }
        } catch (BagException e3) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(e3.getMessage(), (byte) 0));
        }
        return null;
    }

    public short[] addGoods2Package(final HeroPlayer _player, final Goods _goods, final int _number, final CauseLog _cause) {
        try {
            if (_goods != null && _number > 0) {
                GoodsServiceImpl.log.debug((" \u6dfb\u52a0\u7269\u54c1 addGoods2Package  goodstype= " + _goods.getGoodsType()));
                if (_goods.getGoodsType() == EGoodsType.EQUIPMENT) {
                    GoodsServiceImpl.log.debug(" \u7ed9\u73a9\u5bb6\u6dfb\u52a0\u88c5\u5907");
                    Equipment e = (Equipment) _goods;
                    EquipmentInstance ei = EquipmentFactory.getInstance().build(_player.getUserID(), _player.getUserID(), e);
                    int gridInex = _player.getInventory().addEquipmentIns(ei);
                    if (-1 != gridInex) {
                        GoodsDAO.buildEquipment2Bag(_player.getUserID(), ei, gridInex);
                        ChatQueue.getInstance().addGoodsMsg(_player, "\u83b7\u5f97\u4e86", _goods.getName(), _goods.getTrait().getViewRGB(), 1);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number, LoctionLog.BAG, FlowLog.GET, _cause);
                        return new short[]{(short) gridInex, 1};
                    }
                } else if (_goods.getGoodsType() == EGoodsType.PET_EQUIQ_GOODS) {
                    GoodsServiceImpl.log.debug(" \u7ed9\u5ba0\u7269\u6dfb\u52a0\u88c5\u5907");
                    PetEquipment e2 = (PetEquipment) _goods;
                    EquipmentInstance ei = EquipmentFactory.getInstance().build(_player.getUserID(), _player.getUserID(), e2);
                    int gridInex = _player.getInventory().addEquipmentIns(ei);
                    if (-1 != gridInex) {
                        GoodsDAO.buildEquipment2Bag(_player.getUserID(), ei, gridInex);
                        ChatQueue.getInstance().addGoodsMsg(_player, "\u83b7\u5f97\u4e86", _goods.getName(), _goods.getTrait().getViewRGB(), 1);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number, LoctionLog.BAG, FlowLog.GET, _cause);
                        return new short[]{(short) gridInex, 1};
                    }
                } else {
                    SingleGoods sg = (SingleGoods) _goods;
                    short[] change = _player.getInventory().addSingleGoods(sg, _number);
                    if (change != null) {
                        if (change[1] > _number) {
                            GoodsDAO.updateGridSingleGoodsNumberOfBag(_player.getUserID(), _goods.getID(), change[1], change[0]);
                        } else if (change[1] == _number) {
                            GoodsDAO.addSingleGoods(_player.getUserID(), ((SingleGoods) _goods).getSingleGoodsType(), _goods.getID(), change[1], change[0]);
                        }
                        GoodsServiceImpl.log.debug(("add special goods name = " + sg.getName()));
                        ChatQueue.getInstance().addGoodsMsg(_player, "\u83b7\u5f97\u4e86", _goods.getName(), _goods.getTrait().getViewRGB(), _number);
                        LogServiceImpl.getInstance().goodsChangeLog(_player, _goods, _number, LoctionLog.BAG, FlowLog.GET, _cause);
                        return change;
                    }
                }
            }
        } catch (BagException e3) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(e3.getMessage(), (byte) 0));
            GoodsServiceImpl.log.debug("addGoods2Package error : ", (Throwable) e3);
        }
        return null;
    }

    public short[] addGoods2Package(final HeroPlayer _player, final int _goodsID, final int _number, final CauseLog _cause) {
        Goods goods = GoodsContents.getGoods(_goodsID);
        return this.addGoods2Package(_player, goods, _number, _cause);
    }

    public void changeSingleGoodsOwner(final SingleGoods _goods, final HeroPlayer _master, final SingleGoodsBag _bag, final int _bagGridIndex, final int _number, final HeroPlayer _newMaster, final CauseLog _cause) {
        try {
            this.reduceSingleGoods(_master, _bag, _bagGridIndex, _goods, _number, _cause);
            this.addGoods2Package(_newMaster, _goods, _number, _cause);
            ChatQueue.getInstance().addGoodsMsg(_newMaster, "\u83b7\u5f97\u4e86", _goods.getName(), _goods.getTrait().getViewRGB(), _number);
        } catch (BagException ex) {
        }
    }

    public boolean changeGoodsOwner(final EquipmentInstance _equipmentIns, final HeroPlayer _master, final HeroPlayer _newMaster, final CauseLog _cause) {
        try {
            if (-1 != _master.getInventory().getEquipmentBag().remove(_equipmentIns)) {
                int gridIndex = _newMaster.getInventory().addEquipmentIns(_equipmentIns);
                ChatQueue.getInstance().addGoodsMsg(_newMaster, "\u83b7\u5f97\u4e86", _equipmentIns.getArchetype().getName(), _equipmentIns.getArchetype().getTrait().getViewRGB(), 1);
                LogServiceImpl.getInstance().goodsChangeLog(_master, _equipmentIns.getArchetype(), 1, LoctionLog.BAG, FlowLog.LOSE, _cause);
                LogServiceImpl.getInstance().goodsChangeLog(_newMaster, _equipmentIns.getArchetype(), 1, LoctionLog.BAG, FlowLog.GET, _cause);
                return GoodsDAO.changeEquipmentOwner(_newMaster.getUserID(), _equipmentIns.getInstanceID(), gridIndex);
            }
        } catch (BagException pe) {
            pe.printStackTrace();
        }
        return false;
    }

    public short[] addEquipmentInstance2Bag(final HeroPlayer _player, final EquipmentInstance _ei, final CauseLog _cause) {
        try {
            if (_ei != null) {
                int gridIndex = _player.getInventory().addEquipmentIns(_ei);
                if (-1 != gridIndex) {
                    GoodsDAO.addEquipment2Bag(_player.getUserID(), _ei, gridIndex);
                    ChatQueue.getInstance().addGoodsMsg(_player, "\u4f60\u83b7\u5f97\u4e86", _ei.getArchetype().getName(), _ei.getArchetype().getTrait().getViewRGB(), 1);
                    LogServiceImpl.getInstance().goodsChangeLog(_player, _ei.getArchetype(), 1, LoctionLog.BAG, FlowLog.GET, _cause);
                    return new short[]{(short) gridIndex, 1};
                }
            }
        } catch (BagException e) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(e.getMessage(), (byte) 0));
        }
        return null;
    }

    public void addEquipmentInstance2Body(final HeroPlayer _player, final EquipmentInstance _ei, final CauseLog _cause) {
        try {
            if (_ei != null) {
                _player.getBodyWear().wear(_ei);
                GoodsDAO.buildEquipment2Body(_player.getUserID(), _ei);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseEuipmentPackageChange(EBagType.BODY_WEAR.getTypeValue(), _player.getBodyWear().indexOf(_ei), _ei));
                ChatQueue.getInstance().addGoodsMsg(_player, "\u4f60\u83b7\u5f97\u4e86", _ei.getArchetype().getName(), _ei.getArchetype().getTrait().getViewRGB(), 1);
                LogServiceImpl.getInstance().goodsChangeLog(_player, _ei.getArchetype(), 1, LoctionLog.BODY, FlowLog.GET, _cause);
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }

    public Goods getGoodsByID(final int _goodsID) {
        return GoodsContents.getGoods(_goodsID);
    }

    @Override
    public void clean(final int _userID) {
    }

    public void searchUpgradeBag(final HeroPlayer _player, final byte _bagType) {
        int upgradeTimes = 0;
        String bagName = "";
        Inventory inventory = _player.getInventory();
        EBagType bagType = EBagType.getBagType(_bagType);
        switch (bagType) {
            case TASK_TOOL_BAG: {
                upgradeTimes = (inventory.getTaskToolBag().getSize() - 16) / 8 + 1;
                bagName = getInstance().getConfig().task_tool_bag_tab_name;
                break;
            }
            case EQUIPMENT_BAG: {
                upgradeTimes = (inventory.getEquipmentBag().getSize() - 16) / 8 + 1;
                bagName = getInstance().getConfig().equipment_bag_tab_name;
                break;
            }
            case MEDICAMENT_BAG: {
                upgradeTimes = (inventory.getMedicamentBag().getSize() - 16) / 8 + 1;
                bagName = getInstance().getConfig().medicament_bag_tab_name;
                break;
            }
            case MATERIAL_BAG: {
                upgradeTimes = (inventory.getMaterialBag().getSize() - 16) / 8 + 1;
                bagName = getInstance().getConfig().material_bag_tab_name;
                break;
            }
            case SPECIAL_GOODS_BAG: {
                upgradeTimes = (inventory.getSpecialGoodsBag().getSize() - 16) / 8 + 1;
                bagName = getInstance().getConfig().special_bag_tab_name;
                break;
            }
            case PET_EQUIPMENT_BAG: {
                upgradeTimes = (inventory.getPetEquipmentBag().getSize() - 8) / 8 + 1;
                bagName = "\u5ba0\u7269";
                break;
            }
            case PET_GOODS_BAG: {
                upgradeTimes = (inventory.getPetGoodsBag().getSize() - 8) / 8 + 1;
                bagName = "\u5ba0\u7269";
                break;
            }
            case PET_BAG: {
                upgradeTimes = (inventory.getPetContainer().getSize() - 8) / 8 + 1;
                bagName = "\u5ba0\u7269";
                break;
            }
            case STORAGE_BAG: {
                Warehouse warehouse = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                byte level = warehouse.getLevel();
                upgradeTimes = level + 1;
                bagName = "\u4ed3\u5e93";
                break;
            }
            default: {
                GoodsServiceImpl.log.info("\u83b7\u5f97\u65e0\u6cd5\u5339\u914d\u7684\u7c7b\u578b");
                break;
            }
        }
        if (upgradeTimes <= 3) {
            int fee = upgradeTimes * 20;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new UpgradeBagAnswer("\u4f60\u7684%fn\u5305\u88f9\u662f\u7b2c%fx\u6b21\u6269\u5bb9\uff0c\u9700\u8981%fy\u70b9\u6570\uff0c\u662f\u5426\u6269\u5bb9\uff1f".replaceAll("%fn", bagName).replaceAll("%fx", String.valueOf(upgradeTimes)).replaceAll("%fy", String.valueOf(fee)), _bagType));
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5305\u88f9\u5df2\u662f\u6700\u5927\u5bb9\u91cf", (byte) 0));
        }
    }

    public void upgradeBag(final HeroPlayer _player, final byte _bagType) {
        int upgradeTimes = 0;
        boolean upgradeSuccessful = false;
        Inventory inventory = _player.getInventory();
        EBagType bagType = EBagType.getBagType(_bagType);
        switch (bagType) {
            case EQUIPMENT_BAG: {
                upgradeTimes = (inventory.getEquipmentBag().getSize() - 16) / 8 + 1;
                break;
            }
            case MEDICAMENT_BAG: {
                upgradeTimes = (inventory.getMedicamentBag().getSize() - 16) / 8 + 1;
                break;
            }
            case MATERIAL_BAG: {
                upgradeTimes = (inventory.getMaterialBag().getSize() - 16) / 8 + 1;
                break;
            }
            case SPECIAL_GOODS_BAG: {
                upgradeTimes = (inventory.getSpecialGoodsBag().getSize() - 16) / 8 + 1;
                break;
            }
            case PET_EQUIPMENT_BAG: {
                upgradeTimes = (inventory.getPetEquipmentBag().getSize() - 8) / 8 + 1;
                break;
            }
            case PET_GOODS_BAG: {
                upgradeTimes = (inventory.getPetGoodsBag().getSize() - 8) / 8 + 1;
                break;
            }
            case PET_BAG: {
                upgradeTimes = (inventory.getPetContainer().getSize() - 8) / 8 + 1;
                break;
            }
            case STORAGE_BAG: {
                Warehouse warehouse = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                upgradeTimes = warehouse.getLevel() + 1;
                break;
            }
        }
        if (upgradeTimes > 0 && upgradeTimes <= 3) {
            BagExpan bagExpan = (BagExpan) GoodsContents.getGoods(((GoodsConfig) this.config).getSpecialConfig().bag_expan_goods_id);
            if (bagExpan == null) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6269\u5c55\u80cc\u5305\u5931\u8d25", (byte) 1));
                return;
            }
            if (ChargeServiceImpl.getInstance().reducePoint(_player, upgradeTimes * 20, ((GoodsConfig) this.config).getSpecialConfig().bag_expan_goods_id, bagExpan.getName(), 1, ServiceType.BAG_EXPAN)) {
                switch (bagType) {
                    case EQUIPMENT_BAG: {
                        if (!inventory.getEquipmentBag().upgrade()) {
                            return;
                        }
                        upgradeSuccessful = true;
                        break;
                    }
                    case MEDICAMENT_BAG: {
                        if (!inventory.getMedicamentBag().upgrade()) {
                            return;
                        }
                        upgradeSuccessful = true;
                        break;
                    }
                    case MATERIAL_BAG: {
                        if (!inventory.getMaterialBag().upgrade()) {
                            return;
                        }
                        upgradeSuccessful = true;
                        break;
                    }
                    case SPECIAL_GOODS_BAG: {
                        if (!inventory.getSpecialGoodsBag().upgrade()) {
                            return;
                        }
                        upgradeSuccessful = true;
                        break;
                    }
                    case PET_EQUIPMENT_BAG: {
                        if (!inventory.getPetEquipmentBag().upgrade()) {
                            return;
                        }
                        upgradeSuccessful = true;
                        break;
                    }
                    case PET_GOODS_BAG: {
                        if (!inventory.getPetGoodsBag().upgrade()) {
                            return;
                        }
                        upgradeSuccessful = true;
                        break;
                    }
                    case PET_BAG: {
                        if (!inventory.getPetContainer().upgrade()) {
                            return;
                        }
                        upgradeSuccessful = true;
                        break;
                    }
                    case STORAGE_BAG: {
                        Warehouse warehouse2 = WarehouseDict.getInstance().getWarehouseByNickname(_player.getName());
                        byte level = warehouse2.getLevel();
                        if (level >= 8) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6269\u5bb9\u5df2\u8fbe\u6700\u9ad8\u7ea7\u4e0d\u80fd\u518d\u6269\u5bb9\u4e86"));
                        }
                        upgradeSuccessful = true;
                        warehouse2.upLevel();
                        break;
                    }
                }
                if (upgradeSuccessful) {
                    GoodsDAO.updatePlayerBagSize(_player);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SendBagSize(inventory.getEquipmentBag().getSize(), inventory.getMedicamentBag().getSize(), inventory.getMaterialBag().getSize(), inventory.getSpecialGoodsBag().getSize(), inventory.getPetEquipmentBag().getSize(), inventory.getPetContainer().getSize(), inventory.getPetGoodsBag().getSize()));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(bagType.getDescription()) + " \u6210\u529f\u6269\u5bb9\uff0c\u8bf7\u56de\u76f8\u5e94\u5305\u88f9\u67e5\u770b", (byte) 0));
                }
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new UpgradeBagAnswer("\u6e38\u620f\u70b9\u6570\u4e0d\u591f\uff1a" + upgradeTimes * upgradeTimes * 20 + ",\u8bf7\u5230\u5546\u57ce\u5145\u503c", 0));
            }
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u80cc\u5305\u5df2\u662f\u6700\u5927\u5bb9\u91cf", (byte) 0));
        }
    }

    public void processEquipmentDurabilityInFighting(final ME2GameObject _attacker, final ME2GameObject _target) {
        ArrayList<EquipmentInstance> equipmentListThatNeedUpdate = null;
        if (_attacker.getObjectType() == EObjectType.PLAYER) {
            HeroPlayer player = (HeroPlayer) _attacker;
            EquipmentInstance weapon = player.getBodyWear().getWeapon();
            if (weapon != null) {
                int durabilityPointBeforeChange = 0;
                int durabilityPointNow = 0;
                durabilityPointBeforeChange = weapon.getCurrentDurabilityPoint();
                if (durabilityPointBeforeChange > 0) {
                    weapon.reduceCurrentDurabilityPoint(1);
                    durabilityPointNow = weapon.getCurrentDurabilityPoint();
                    if (durabilityPointBeforeChange > durabilityPointNow) {
                        equipmentListThatNeedUpdate = new ArrayList<EquipmentInstance>();
                        equipmentListThatNeedUpdate.add(weapon);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new RefreshEquipmentDurabilityPoint(player.getBodyWear()));
                        if (durabilityPointNow == 0) {
                            PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
                            PlayerServiceImpl.getInstance().refreshRoleProperty(player);
                        }
                    }
                }
            }
        }
        if (_target.getObjectType() == EObjectType.PLAYER) {
            HeroPlayer player = (HeroPlayer) _target;
            boolean durabilityChanged = false;
            boolean needRecalculteProperty = false;
            int durabilityPointBeforeChange2 = 0;
            int durabilityPointNow2 = 0;
            EquipmentInstance[] equipmentList;
            for (int length = (equipmentList = player.getBodyWear().getEquipmentList()).length, i = 0; i < length; ++i) {
                EquipmentInstance ei = equipmentList[i];
                if (ei != null && ei.getArchetype().getEquipmentType() == 2) {
                    durabilityPointBeforeChange2 = ei.getCurrentDurabilityPoint();
                    if (durabilityPointBeforeChange2 > 0) {
                        ei.reduceCurrentDurabilityPoint(1);
                        durabilityPointNow2 = ei.getCurrentDurabilityPoint();
                        if (durabilityPointBeforeChange2 > durabilityPointNow2) {
                            if (equipmentListThatNeedUpdate == null) {
                                equipmentListThatNeedUpdate = new ArrayList<EquipmentInstance>();
                            }
                            equipmentListThatNeedUpdate.add(ei);
                            durabilityChanged = true;
                            if (durabilityPointNow2 == 0) {
                                needRecalculteProperty = true;
                            }
                        }
                    }
                }
            }
            if (durabilityChanged) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new RefreshEquipmentDurabilityPoint(player.getBodyWear()));
                GoodsDAO.updateEquipmentDurability(equipmentListThatNeedUpdate);
            }
            if (needRecalculteProperty) {
                PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
                PlayerServiceImpl.getInstance().refreshRoleProperty(player);
            }
        }
    }

    public void restoreEquipmentDurability(final ArrayList<EquipmentInstance> _equipmentInsList) {
        for (final EquipmentInstance ei : _equipmentInsList) {
            ei.beRepaired();
        }
        GoodsDAO.updateEquipmentDurability(_equipmentInsList);
    }

    public int autoSellMAE(final HeroPlayer _player) {
        EGoodsTrait sellGoodsTrait = _player.getAutoSellTrait();
        if (sellGoodsTrait != null) {
            int money = 0;
            if (_player.getInventory().getEquipmentBag().getFullGridNumber() > 0) {
                EquipmentInstance[] equipmentList = _player.getInventory().getEquipmentBag().getEquipmentList();
                for (int i = 0; i < equipmentList.length; ++i) {
                    EquipmentInstance ei = equipmentList[i];
                    if (ei != null && ei.getArchetype().getTrait().value() <= sellGoodsTrait.value()) {
                        try {
                            if (ei == this.removeEquipmentOfBag(_player, _player.getInventory().getEquipmentBag(), i, CauseLog.SALE)) {
                                money += CEService.sellPriceOfEquipment(ei.getArchetype().getSellPrice(), ei.getCurrentDurabilityPoint(), ei.getArchetype().getMaxDurabilityPoint());
                            }
                        } catch (BagException be) {
                            be.printStackTrace();
                        }
                    }
                }
            }
            if (_player.getInventory().getMaterialBag().getFullGridNumber() > 0) {
                int[][] materialDataList = _player.getInventory().getMaterialBag().getAllItem();
                for (int i = 0; i < materialDataList.length; ++i) {
                    if (materialDataList[i][0] != 0) {
                        Goods material = GoodsContents.getGoods(materialDataList[i][0]);
                        if (material != null && material.getTrait().value() <= sellGoodsTrait.value()) {
                            try {
                                int number = materialDataList[i][1];
                                if (getInstance().diceSingleGoods(_player, _player.getInventory().getMaterialBag(), i, material.getID(), CauseLog.SALE)) {
                                    money += material.getRetrievePrice() * number;
                                }
                            } catch (BagException be) {
                                be.printStackTrace();
                            }
                        }
                    }
                }
            }
            PlayerServiceImpl.getInstance().addMoney(_player, money, 1.0f, 2, "\u81ea\u52a8\u51fa\u552e\u52a3\u8d28\u7269\u54c1");
            return money;
        }
        return 0;
    }

    public void processEquipmentDurabilityAfterDie(final HeroPlayer _dier) {
        boolean durabilityChanged = false;
        boolean needRecalculteProperty = false;
        int durabilityPointBeforeChange = 0;
        int durabilityPointNow = 0;
        ArrayList<EquipmentInstance> equipmentListThatNeedUpdate = null;
        EquipmentInstance[] equipmentList;
        for (int length = (equipmentList = _dier.getBodyWear().getEquipmentList()).length, i = 0; i < length; ++i) {
            EquipmentInstance ei = equipmentList[i];
            if (ei != null) {
                durabilityPointBeforeChange = ei.getCurrentDurabilityPoint();
                if (durabilityPointBeforeChange > 0) {
                    ei.reduceCurrentDurabilityPercent(5);
                    durabilityPointNow = ei.getCurrentDurabilityPoint();
                    if (durabilityPointBeforeChange > durabilityPointNow) {
                        durabilityChanged = true;
                        if (equipmentListThatNeedUpdate == null) {
                            equipmentListThatNeedUpdate = new ArrayList<EquipmentInstance>();
                        }
                        equipmentListThatNeedUpdate.add(ei);
                        if (durabilityPointNow == 0) {
                            needRecalculteProperty = true;
                        }
                    }
                }
            }
        }
        if (durabilityChanged) {
            ResponseMessageQueue.getInstance().put(_dier.getMsgQueueIndex(), new RefreshEquipmentDurabilityPoint(_dier.getBodyWear()));
            GoodsDAO.updateEquipmentDurability(equipmentListThatNeedUpdate);
        }
        if (needRecalculteProperty) {
            PlayerServiceImpl.getInstance().reCalculateRoleProperty(_dier);
            PlayerServiceImpl.getInstance().refreshRoleProperty(_dier);
        }
    }

    public void sendLegacyBoxList(final Map _map, final HeroPlayer _player) {
        ArrayList<MonsterLegacyBox> legacyBoxList = new ArrayList<MonsterLegacyBox>();
        for (int i = 0; i < _map.getLegacyBoxList().size(); ++i) {
            MonsterLegacyBox monsterLegacyBox = _map.getLegacyBoxList().get(i);
            if (monsterLegacyBox instanceof PersonalPickerBox) {
                if (monsterLegacyBox.getPickerUserID() == _player.getUserID()) {
                    legacyBoxList.add(monsterLegacyBox);
                }
            } else if (((RaidPickerBox) monsterLegacyBox).containsVisibler(_player.getUserID())) {
                legacyBoxList.add(monsterLegacyBox);
            }
        }
        if (legacyBoxList.size() > 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new SendLegacyBoxList(legacyBoxList, _player.getUserID()));
        }
    }

    public Goods bagGoodsModel(final Object _container, final EGoodsType _goodsType, final int _bagIndex) {
        switch (_goodsType) {
            case EQUIPMENT: {
                EquipmentContainer ec = (EquipmentContainer) _container;
                EquipmentInstance ei = ec.get(_bagIndex);
                if (ei != null) {
                    return ei.getArchetype();
                }
                break;
            }
            default: {
                SingleGoodsBag sb = (SingleGoodsBag) _container;
                int[] goodsInfo = sb.getItemData(_bagIndex);
                if (goodsInfo != null && goodsInfo[0] != 0) {
                    return GoodsContents.getGoods(goodsInfo[0]);
                }
                break;
            }
        }
        return null;
    }

    public boolean changeEquimentViewDifference(final EquipmentInstance _uei, final EquipmentInstance _ei) {
        boolean result = false;
        boolean pngDifference = true;
        boolean flashDifference = true;
        if (_uei != null) {
            if (_uei.getGeneralEnhance().getFlashLevel() == _ei.getGeneralEnhance().getFlashLevel()) {
                pngDifference = false;
            }
            if (_uei.getArchetype().getImageID() == _ei.getArchetype().getImageID()) {
                flashDifference = false;
            }
            if (pngDifference || flashDifference) {
                result = true;
            }
        } else {
            result = true;
        }
        return result;
    }

    public List<HeavenBook> getPlayerSepcialBagHeavenBooks(final HeroPlayer _player) {
        List<HeavenBook> heavenBookList = new ArrayList<HeavenBook>();
        SingleGoodsBag singleGoodsBag = _player.getInventory().getSpecialGoodsBag();
        for (int i = 0; i < singleGoodsBag.getAllItem().length; ++i) {
            if (singleGoodsBag.getAllItem()[i][0] > 0) {
                SpecialGoods goods = (SpecialGoods) this.getGoodsByID(singleGoodsBag.getAllItem()[i][0]);
                if (goods.getType() == ESpecialGoodsType.HEAVEN_BOOK) {
                    heavenBookList.add((HeavenBook) goods);
                }
            }
        }
        return heavenBookList;
    }
}
