// 
// Decompiled by Procyon v0.5.36
// 
package hero.task;

import hero.task.target.BaseTaskTarget;
import java.util.ArrayList;

public class Task {

    private String name;
    private int ID;
    private short level;
    private boolean isMainLine;
    private boolean repeated;
    private ArrayList<BaseTaskTarget> targetList;
    private final String[] descriptionList;
    private Condition condition;
    private Push taskPush;
    private Award award;
    private ArrayList<int[]> receiveGoodsList;
    private int receiveEffectID;
    private String distributeNpcModelID;
    private String submitNpcModelID;
    private ETaskDifficultyLevel difficultyLevel;

    public Task(final int _ID, final String _name, final short _level, final boolean _isRepeated) {
        this.descriptionList = new String[3];
        this.ID = _ID;
        this.name = _name;
        this.level = _level;
        this.repeated = _isRepeated;
        this.targetList = new ArrayList<BaseTaskTarget>();
    }

    public int getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public short getLevel() {
        return this.level;
    }

    public boolean getMainLine() {
        return this.isMainLine;
    }

    public void setMainLine() {
        this.isMainLine = true;
    }

    public boolean isRepeated() {
        return this.repeated;
    }

    public void addTarget(final BaseTaskTarget _taskTarget) {
        this.targetList.add(_taskTarget);
    }

    public ArrayList<BaseTaskTarget> getTargetList() {
        return this.targetList;
    }

    public void setCondition(final Condition _condition) {
        this.condition = _condition;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public void setTaskPush(final Push _taskPush) {
        this.taskPush = _taskPush;
    }

    public Push getTaskPush() {
        return this.taskPush;
    }

    public void setAward(final Award _award) {
        this.award = _award;
    }

    public Award getAward() {
        return this.award;
    }

    public void setDescList(final String[] _descList) {
        if (_descList.length != 3) {
            return;
        }
        this.descriptionList[0] = String.valueOf(this.difficultyLevel.getDescription()) + "\n" + _descList[0];
        this.descriptionList[1] = String.valueOf(this.difficultyLevel.getDescription()) + "\n" + _descList[1];
        this.descriptionList[2] = _descList[2];
    }

    public String getReceiveDesc() {
        if (this.descriptionList != null) {
            return this.descriptionList[0];
        }
        return "";
    }

    public String getViewDesc() {
        if (this.descriptionList != null) {
            return this.descriptionList[1];
        }
        return "";
    }

    public String getSubmitDesc() {
        if (this.descriptionList != null) {
            return this.descriptionList[2];
        }
        return "";
    }

    public void addReceiveGoods(final int _goodsID, final short _number) {
        if (this.receiveGoodsList == null) {
            this.receiveGoodsList = new ArrayList<int[]>();
        }
        this.receiveGoodsList.add(new int[]{_goodsID, _number});
    }

    public ArrayList<int[]> getReceiveGoodsList() {
        return this.receiveGoodsList;
    }

    public void setReceiveEffectID(final int _effectID) {
        this.receiveEffectID = _effectID;
    }

    public int getReceiveEffectID() {
        return this.receiveEffectID;
    }

    public void setSubmitNpcID(final String _npcModelID) {
        this.submitNpcModelID = _npcModelID;
    }

    public String getSubmitNpcID() {
        return this.submitNpcModelID;
    }

    public void setDistributeNpcModelID(final String _npcModelID) {
        this.distributeNpcModelID = _npcModelID;
    }

    public String getDistributeNpcModelID() {
        return this.distributeNpcModelID;
    }

    public void setDifficultyLevel(final ETaskDifficultyLevel _difficultyLevel) {
        this.difficultyLevel = _difficultyLevel;
    }

    public ETaskDifficultyLevel getDifficultyLevel() {
        return this.difficultyLevel;
    }

    public enum ETaskDifficultyLevel {
        EASY("EASY", 0, "\u96be\u5ea6\uff1a\u7b80\u5355"),
        DIFFICULT("DIFFICULT", 1, "\u96be\u5ea6\uff1a\u56f0\u96be"),
        NIGHTMARE("NIGHTMARE", 2, "\u96be\u5ea6\uff1a\u5669\u68a6");

        private static String[] difficultyMark;
        private String description;

        static {
            ETaskDifficultyLevel.difficultyMark = new String[]{"\u7b80\u5355", "\u56f0\u96be", "\u5669\u68a6"};
        }

        private ETaskDifficultyLevel(final String name, final int ordinal, final String _description) {
            this.description = _description;
        }

        public String getDescription() {
            return this.description;
        }

        public static ETaskDifficultyLevel get(final String _mark) {
            if (_mark.equals(ETaskDifficultyLevel.difficultyMark[0])) {
                return ETaskDifficultyLevel.EASY;
            }
            if (_mark.equals(ETaskDifficultyLevel.difficultyMark[1])) {
                return ETaskDifficultyLevel.DIFFICULT;
            }
            if (_mark.equals(ETaskDifficultyLevel.difficultyMark[2])) {
                return ETaskDifficultyLevel.NIGHTMARE;
            }
            return ETaskDifficultyLevel.EASY;
        }
    }
}
