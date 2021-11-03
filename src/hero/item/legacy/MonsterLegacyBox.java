// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.legacy;

import hero.player.HeroPlayer;
import hero.share.service.IDManager;
import java.util.ArrayList;
import hero.map.Map;

public abstract class MonsterLegacyBox {

    private int id;
    private int monsterID;
    private short locationX;
    private short locationY;
    protected Map where;
    protected int pickerUserID;
    protected ArrayList<int[]> legacyList;
    protected ArrayList<TaskGoodsLegacyInfo> taskGoodsInfoList;
    protected long buildTime;
    public static final byte PICKER_TYPE_PERSION = 1;
    public static final byte PICKER_TYPE_RAID = 2;
    public static final long KEEP_TIME_OF_PERSONAL_LEGACY = 180000L;
    public static final long KEEP_TIME_OF_RAID_LEGACY = 600000L;
    public static final int KEEP_TIME_OF_DISTRIBUTE_OPERATION = 90000;
    public static final int DELAY_CORRECT_TIME_OF_DISTRIBUTE_OPERATION = 93000;

    private MonsterLegacyBox() {
        this.id = IDManager.buildMonsterLegacyBoxID();
        this.buildTime = System.currentTimeMillis();
    }

    public MonsterLegacyBox(final int _pickerUserID, final int _monsterID, final Map _map, final ArrayList<int[]> _legacyList, final ArrayList<TaskGoodsLegacyInfo> _taskGoodsList, final short _locationX, final short _locationY) {
        this();
        this.pickerUserID = _pickerUserID;
        this.monsterID = _monsterID;
        this.legacyList = _legacyList;
        this.taskGoodsInfoList = _taskGoodsList;
        this.where = _map;
        this.locationX = _locationX;
        this.locationY = _locationY;
        _map.getLegacyBoxList().add(this);
    }

    public int getPickerUserID() {
        return this.pickerUserID;
    }

    public int getID() {
        return this.id;
    }

    public int getMonsterID() {
        return this.monsterID;
    }

    public Map where() {
        return this.where;
    }

    public short getLocationX() {
        return this.locationX;
    }

    public short getLocationY() {
        return this.locationY;
    }

    public boolean isEmpty() {
        return (this.legacyList == null || this.legacyList.size() == 0) && (this.taskGoodsInfoList == null || this.taskGoodsInfoList.size() == 0);
    }

    public boolean becomeDue() {
        if (1 == this.getPickerType()) {
            if (System.currentTimeMillis() - this.buildTime >= 180000L) {
                return true;
            }
        } else if (System.currentTimeMillis() - this.buildTime >= 600000L) {
            return true;
        }
        return false;
    }

    public abstract boolean bePicked(final HeroPlayer p0);

    public abstract byte getPickerType();
}
