// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.npc.ME2NotPlayer;
import hero.share.message.Warning;
import hero.npc.message.NpcRefreshNotify;
import hero.npc.message.MonsterRefreshNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.service.NpcConfig;
import hero.npc.service.NotPlayerServiceImpl;
import hero.map.message.DisappearNotify;
import hero.npc.Npc;
import hero.npc.Monster;
import hero.ui.message.ResponseSinglePackageChange;
import hero.item.service.GoodsDAO;
import yoyo.core.packet.AbsResponseMessage;
import hero.ui.message.NotifyAddGoods2SinglePackage;
import hero.item.bag.EBagType;
import yoyo.core.queue.ResponseMessageQueue;
import hero.task.service.TaskServiceImpl;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.share.ME2GameObject;
import hero.player.HeroPlayer;
import hero.item.detail.EGoodsTrait;

public class TaskTool extends SingleGoods {

    private boolean isShare;
    private boolean disappearAfterUse;
    private TaskTool getGoodsAfterUse;
    private String targetModelID;
    private float targetTraceHpPercent;
    private boolean targetDisappearAfterUse;
    private String refreshNpcModelIDAfterUse;
    private short refreshNpcNumberAfterUse;
    private boolean isLimitLocation;
    private LocationOfUse locationOfUse;
    private long timeOfLastUse;
    private static final int INTERVAL_TIME = 120000;

    public TaskTool(final short _stackNumber, final boolean _isShare, final boolean _useable) {
        super(_stackNumber);
        this.isShare = _isShare;
        this.useable = _useable;
        this.setTrait(EGoodsTrait.BING_ZHI);
    }

    public boolean isShare() {
        return this.isShare;
    }

    @Override
    public boolean useable() {
        return this.useable;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target) {
        ME2GameObject target = (ME2GameObject) _target;
        if (this.canBeUse(_player, target)) {
            try {
                if (this.getGoodsAfterUse != null) {
                    short[] gridChange = GoodsServiceImpl.getInstance().addGoods2Package(_player, this.getGoodsAfterUse, 1, CauseLog.TASKTOOL);
                    if (gridChange == null) {
                        return false;
                    }
                    TaskServiceImpl.getInstance().addTaskGoods(_player, this.getGoodsAfterUse.getID(), 1);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyAddGoods2SinglePackage(EBagType.TASK_TOOL_BAG.getTypeValue(), gridChange, this.getGoodsAfterUse, _player.getShortcutKeyList()));
                }
                if (this.disappearAfterUse) {
                    short[] gridChange = _player.getInventory().getTaskToolBag().removeOne(this.getID());
                    if (gridChange == null) {
                        return false;
                    }
                    if (gridChange[1] == 0) {
                        GoodsDAO.removeSingleGoodsFromBag(_player.getUserID(), gridChange[0], this.getID());
                    } else {
                        GoodsDAO.updateGridSingleGoodsNumberOfBag(_player.getUserID(), this.getID(), gridChange[1], gridChange[0]);
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseSinglePackageChange(EBagType.TASK_TOOL_BAG.getTypeValue(), gridChange));
                }
                if (this.targetDisappearAfterUse) {
                    target.invalid();
                    if (_target instanceof Monster) {
                        ((Monster) _target).clearFightInfo();
                        ((Monster) _target).setDieTime(System.currentTimeMillis());
                        target.where().getMonsterList().remove(_target);
                    } else {
                        ((Npc) _target).stopFollowTask();
                        target.where().getNpcList().remove(_target);
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new DisappearNotify(target.getObjectType().value(), target.getID(), target.getHp(), target.getBaseProperty().getHpMax(), target.getMp(), target.getBaseProperty().getMpMax()));
                } else if (this.targetModelID != null && _target instanceof Monster) {
                    ((Monster) _target).beHarmed(_player, 0);
                }
                if (this.refreshNpcModelIDAfterUse != null && this.refreshNpcNumberAfterUse > 0) {
                    short x;
                    short y;
                    if (this.targetModelID != null) {
                        x = target.getCellX();
                        y = target.getCellY();
                    } else if (this.isLimitLocation) {
                        x = this.locationOfUse.mapX;
                        y = this.locationOfUse.mapY;
                    } else {
                        x = _player.getCellX();
                        y = _player.getCellY();
                    }
                    if (this.refreshNpcModelIDAfterUse.startsWith("m")) {
                        for (int i = 1; i <= this.refreshNpcNumberAfterUse; ++i) {
                            Monster monster = NotPlayerServiceImpl.getInstance().buildMonsterInstance(this.refreshNpcModelIDAfterUse);
                            monster.setOrgMap(_player.where());
                            monster.setOrgX(x);
                            monster.setOrgY(y);
                            monster.setCellX(x);
                            monster.setCellY(y);
                            monster.setDirection((byte) 3);
                            monster.setExistsTime(NotPlayerServiceImpl.getInstance().getConfig().task_call_monster_exist_time);
                            monster.live(_player.where());
                            MapSynchronousInfoBroadcast.getInstance().put(monster.where(), new MonsterRefreshNotify(_player.getLoginInfo().clientType, monster), false, 0);
                            monster.where().getMonsterList().add(monster);
                            monster.active();
                        }
                        Monster monster = null;
                    } else {
                        for (int i = 1; i <= this.refreshNpcNumberAfterUse; ++i) {
                            Npc npc = NotPlayerServiceImpl.getInstance().buildNpcInstance(this.refreshNpcModelIDAfterUse);
                            npc.setOrgMap(_player.where());
                            npc.setOrgX(x);
                            npc.setOrgY(y);
                            npc.setCellX(x);
                            npc.setCellY(y);
                            npc.setDirection((byte) 3);
                            npc.live(_player.where());
                            MapSynchronousInfoBroadcast.getInstance().put(npc.where(), new NpcRefreshNotify(_player.getLoginInfo().clientType, npc), false, 0);
                            npc.where().getNpcList().add(npc);
                            npc.active();
                        }
                    }
                    this.timeOfLastUse = System.currentTimeMillis();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean canBeUse(final HeroPlayer _player, final ME2GameObject _target) {
        if (this.useable) {
            if (this.isLimitLocation) {
                if (_player.where().getID() != this.locationOfUse.mapID) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u5730\u56fe\u4e0d\u53ef\u4f7f\u7528", (byte) 0));
                    return false;
                }
                if (this.locationOfUse.pointRange < Math.abs(this.locationOfUse.mapX - _player.getCellX()) || this.locationOfUse.pointRange < Math.abs(this.locationOfUse.mapY - _player.getCellY())) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u65e0\u6548\u7684\u4f4d\u7f6e", (byte) 0));
                    return false;
                }
            }
            if (this.targetModelID != null) {
                if (!(_target instanceof ME2NotPlayer) || !((ME2NotPlayer) _target).getModelID().equals(this.targetModelID)) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u65e0\u6548\u7684\u76ee\u6807", (byte) 0));
                    return false;
                }
                if (_target.getHPPercent() > this.targetTraceHpPercent) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u76ee\u6807\u72b6\u6001\u65e0\u6548", (byte) 0));
                    return false;
                }
            } else if (this.refreshNpcModelIDAfterUse != null && System.currentTimeMillis() - this.timeOfLastUse < 120000L) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf((120000L - (System.currentTimeMillis() - this.timeOfLastUse)) / 1000L) + "\u79d2\u540e\u624d\u53ef\u4ee5\u4f7f\u7528", (byte) 0));
                return false;
            }
            return true;
        }
        return false;
    }

    public void setLocationOfUse(final int _mapID, final short _mapX, final short _mapY, final short _pointRange) {
        this.locationOfUse = new LocationOfUse();
        this.locationOfUse.mapID = _mapID;
        this.locationOfUse.mapX = _mapX;
        this.locationOfUse.mapY = _mapY;
        this.locationOfUse.pointRange = _pointRange;
    }

    public void limitLocation() {
        this.isLimitLocation = true;
    }

    public void disappearAfterUse() {
        this.disappearAfterUse = true;
    }

    public boolean isDisappearAfterUse() {
        return this.disappearAfterUse;
    }

    public void setGetGoodsAfterUse(final int _goodsID) {
        this.getGoodsAfterUse = (TaskTool) GoodsContents.getGoods(_goodsID);
    }

    public TaskTool getGoodsIDAfterUse() {
        return this.getGoodsAfterUse;
    }

    public void setTargetNpcModelID(final String _npcModelID) {
        this.targetModelID = _npcModelID;
    }

    public void setTargetTraceHpPercent(final float _hpPercent) {
        this.targetTraceHpPercent = _hpPercent;
    }

    public String getTargetNpcModelID() {
        return this.targetModelID;
    }

    public void targetDisappearAfterUse() {
        this.targetDisappearAfterUse = true;
    }

    public boolean targetIsDisappearAfterUse() {
        return this.targetDisappearAfterUse;
    }

    public void setRefreshNpcModelIDAfterUse(final String _npcModelID) {
        this.refreshNpcModelIDAfterUse = _npcModelID;
    }

    public String getRefreshNpcModelIDAfterUse() {
        return this.refreshNpcModelIDAfterUse;
    }

    public void setRefreshNpcNumsAfterUse(final short _number) {
        this.refreshNpcNumberAfterUse = _number;
        if (this.refreshNpcNumberAfterUse <= 0) {
            this.refreshNpcNumberAfterUse = 1;
        }
    }

    public short getRefreshNpcNumsAfterUse() {
        return this.refreshNpcNumberAfterUse;
    }

    @Override
    public byte getSingleGoodsType() {
        return 3;
    }

    @Override
    public EGoodsType getGoodsType() {
        return EGoodsType.TASK_TOOL;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    class LocationOfUse {

        public int mapID;
        public short mapX;
        public short mapY;
        public short pointRange;
    }
}
