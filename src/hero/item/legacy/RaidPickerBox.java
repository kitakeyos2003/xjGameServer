// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.legacy;

import hero.item.TaskTool;
import hero.task.service.TaskServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.log.service.CauseLog;
import hero.item.Goods;
import java.util.Iterator;
import hero.item.detail.EGoodsTrait;
import hero.item.service.GoodsServiceImpl;
import hero.map.Map;
import java.util.HashMap;
import hero.player.HeroPlayer;
import java.util.ArrayList;

public class RaidPickerBox extends MonsterLegacyBox {

    private int groupID;
    private ArrayList<HeroPlayer> boxVisiblePlayerList;
    private HashMap<Integer, Boolean> boxStatusTable;
    private ArrayList<DistributeGoods> distributeGoodsList;
    private boolean distributeUIShowed;
    private boolean containsHighTraitGoods;

    public RaidPickerBox(final int _groupID, final int _pickerUserID, final ArrayList<HeroPlayer> _boxVisiblePlayerList, final int _monsterID, final Map _map, final ArrayList<int[]> _legacyList, final ArrayList<TaskGoodsLegacyInfo> _taskGoodsList, final short _locationX, final short _locationY) {
        super(_pickerUserID, _monsterID, _map, null, _taskGoodsList, _locationX, _locationY);
        this.groupID = _groupID;
        this.boxVisiblePlayerList = _boxVisiblePlayerList;
        this.boxStatusTable = new HashMap<Integer, Boolean>();
        if (_legacyList != null && _legacyList.size() > 0) {
            this.distributeGoodsList = new ArrayList<DistributeGoods>(_legacyList.size());
            for (final int[] legacy : _legacyList) {
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(legacy[0]);
                if (goods != null) {
                    DistributeGoods distributeGoods = new DistributeGoods();
                    distributeGoods.box = this;
                    distributeGoods.goods = goods;
                    distributeGoods.number = (byte) legacy[1];
                    if (distributeGoods.goods.getTrait() == EGoodsTrait.SHI_QI || distributeGoods.goods.getTrait() == EGoodsTrait.BING_ZHI || distributeGoods.goods.getTrait() == EGoodsTrait.JIANG_ZHI) {
                        distributeGoods.pickerUserID = _pickerUserID;
                    } else {
                        this.containsHighTraitGoods = true;
                    }
                    this.distributeGoodsList.add(distributeGoods);
                }
            }
        }
        this.setStateOfPicking();
    }

    private void setStateOfPicking() {
        if (this.containsHighTraitGoods) {
            for (final HeroPlayer player : this.boxVisiblePlayerList) {
                this.boxStatusTable.put(player.getUserID(), true);
            }
            return;
        }
        for (final HeroPlayer player : this.boxVisiblePlayerList) {
            this.boxStatusTable.put(player.getUserID(), false);
        }
        if (this.distributeGoodsList != null) {
            this.boxStatusTable.put(this.pickerUserID, true);
        }
        if (this.taskGoodsInfoList != null && this.taskGoodsInfoList.size() > 0) {
            for (final HeroPlayer player : this.boxVisiblePlayerList) {
                if (this.distributeGoodsList == null || player.getUserID() != this.pickerUserID) {
                    for (final TaskGoodsLegacyInfo legacyTaskGoods : this.taskGoodsInfoList) {
                        if (legacyTaskGoods.canPick(player.getUserID())) {
                            this.boxStatusTable.put(player.getUserID(), true);
                            break;
                        }
                    }
                }
            }
        }
    }

    public int getGroupID() {
        return this.groupID;
    }

    public boolean containsHighTraitGoods() {
        return this.containsHighTraitGoods;
    }

    @Override
    public byte getPickerType() {
        return 2;
    }

    public boolean distributeUIHasShowed() {
        return this.distributeUIShowed;
    }

    public void showDistributeUI() {
        this.distributeUIShowed = true;
    }

    @Override
    public boolean isEmpty() {
        return (this.distributeGoodsList == null || this.distributeGoodsList.size() == 0) && (this.taskGoodsInfoList == null || this.taskGoodsInfoList.size() == 0);
    }

    public boolean getStateOfPicking(final int _userID) {
        return this.boxStatusTable.get(_userID);
    }

    public boolean containsVisibler(final int _userID) {
        return this.boxStatusTable.containsKey(_userID);
    }

    public boolean statusIsChanged(final int _userID) {
        if (this.boxStatusTable.get(_userID)) {
            if (this.distributeGoodsList != null && this.distributeGoodsList.size() > 0) {
                for (final DistributeGoods goods : this.distributeGoodsList) {
                    if (goods.pickerUserID == _userID || (goods.hasOperated && goods.pickerUserID == 0)) {
                        return false;
                    }
                }
            }
            if (this.taskGoodsInfoList != null) {
                for (final TaskGoodsLegacyInfo legacyTaskGoods : this.taskGoodsInfoList) {
                    if (legacyTaskGoods.canPick(_userID)) {
                        return false;
                    }
                }
            }
            this.boxStatusTable.put(_userID, false);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean bePicked(final HeroPlayer _player) {
        boolean changed = false;
        if (this.distributeGoodsList != null && this.distributeGoodsList.size() > 0) {
            int i = 0;
            while (i < this.distributeGoodsList.size()) {
                DistributeGoods distributeGoods = this.distributeGoodsList.get(i);
                if (distributeGoods.goods.getTrait() == EGoodsTrait.JIANG_ZHI || distributeGoods.goods.getTrait() == EGoodsTrait.YU_ZHI || distributeGoods.goods.getTrait() == EGoodsTrait.SHENG_QI) {
                    if (!this.distributeUIShowed) {
                        MonsterLegacyManager.getInstance().notifyGoodsDistributeUI(distributeGoods);
                    } else if (distributeGoods.hasOperated && (distributeGoods.pickerUserID == 0 || _player.getUserID() == distributeGoods.pickerUserID) && GoodsServiceImpl.getInstance().addGoods2Package(_player, distributeGoods.goods, distributeGoods.number, CauseLog.DROP) != null) {
                        this.distributeGoodsList.remove(i);
                        MonsterLegacyManager.getInstance().removeDistributeFromMonitor(distributeGoods.id);
                        changed = true;
                        LogServiceImpl.getInstance().getMonsterLegacyGoodsLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), distributeGoods.goods.getID(), distributeGoods.goods.getName(), distributeGoods.number, distributeGoods.goods.getTrait().getDesc(), distributeGoods.goods.getGoodsType().getDescription());
                        continue;
                    }
                } else if (_player.getUserID() == distributeGoods.pickerUserID && GoodsServiceImpl.getInstance().addGoods2Package(_player, distributeGoods.goods, distributeGoods.number, CauseLog.DROP) != null) {
                    this.distributeGoodsList.remove(i);
                    MonsterLegacyManager.getInstance().removeDistributeFromMonitor(distributeGoods.id);
                    changed = true;
                    LogServiceImpl.getInstance().getMonsterLegacyGoodsLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), distributeGoods.goods.getID(), distributeGoods.goods.getName(), distributeGoods.number, distributeGoods.goods.getTrait().getDesc(), distributeGoods.goods.getGoodsType().getDescription());
                    continue;
                }
                ++i;
            }
        }
        if (this.taskGoodsInfoList != null) {
            int j = 0;
            while (j < this.taskGoodsInfoList.size()) {
                TaskGoodsLegacyInfo taskGoodsLegacyInfo = this.taskGoodsInfoList.get(j);
                if (taskGoodsLegacyInfo.canPick(_player.getUserID())) {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(taskGoodsLegacyInfo.getTaskGoodsID());
                    if (goods != null) {
                        if (GoodsServiceImpl.getInstance().addGoods2Package(_player, goods, taskGoodsLegacyInfo.getGoodsNumber(), CauseLog.DROP) != null) {
                            taskGoodsLegacyInfo.removePicker(_player.getUserID());
                            TaskServiceImpl.getInstance().addTaskGoods(_player, taskGoodsLegacyInfo.getTaskID(), goods, taskGoodsLegacyInfo.getGoodsNumber());
                            changed = true;
                            if (!((TaskTool) goods).isShare() || taskGoodsLegacyInfo.pickedByAll()) {
                                this.taskGoodsInfoList.remove(j);
                            } else {
                                ++j;
                            }
                        } else {
                            ++j;
                        }
                    } else {
                        changed = false;
                    }
                } else {
                    ++j;
                }
            }
        }
        this.distributeUIShowed = true;
        return changed;
    }

    public void removeNormalGoods(final DistributeGoods _distributeGoods) {
        this.distributeGoodsList.remove(_distributeGoods);
    }

    public void removeGoods(final int _goodsID) {
    }

    public ArrayList<HeroPlayer> getVisitorList() {
        return this.boxVisiblePlayerList;
    }
}
