// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.legacy;

import java.util.ArrayList;

public class TaskGoodsLegacyInfo {

    private int taskID;
    private int taskGoodsID;
    private int number;
    private ArrayList<Integer> pickerList;

    public TaskGoodsLegacyInfo(final int _taskID, final int _taskGoodsID, final int _number) {
        this.taskID = _taskID;
        this.taskGoodsID = _taskGoodsID;
        this.number = _number;
    }

    public void addPicker(final int _playerUserID) {
        if (this.pickerList == null) {
            (this.pickerList = new ArrayList<Integer>()).add(_playerUserID);
        } else if (!this.pickerList.contains(_playerUserID)) {
            this.pickerList.add(_playerUserID);
        }
    }

    public void removePicker(final int _playerUserID) {
        this.pickerList.remove((Object) _playerUserID);
    }

    public boolean pickedByAll() {
        return this.pickerList.size() == 0;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public int getTaskGoodsID() {
        return this.taskGoodsID;
    }

    public int getGoodsNumber() {
        return this.number;
    }

    public boolean canPick(final int _playerUserID) {
        return this.pickerList.contains(_playerUserID);
    }
}
