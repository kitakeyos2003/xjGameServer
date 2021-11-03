// 
// Decompiled by Procyon v0.5.36
// 
package hero.task;

import java.util.Iterator;
import hero.task.target.BaseTaskTarget;
import java.util.ArrayList;

public class TaskInstance {

    private Task task;
    private ArrayList<BaseTaskTarget> tastTargetList;
    public static final short MAX_TAST_NUMBER = 20;

    public TaskInstance(final Task _task) {
        this.task = _task;
        ArrayList<BaseTaskTarget> targetList = _task.getTargetList();
        if (targetList != null) {
            this.tastTargetList = new ArrayList<BaseTaskTarget>();
            for (int i = 0; i < targetList.size(); ++i) {
                try {
                    this.tastTargetList.add(targetList.get(i).clone());
                } catch (CloneNotSupportedException cnse) {
                    cnse.printStackTrace();
                }
            }
        }
    }

    public Task getArchetype() {
        return this.task;
    }

    public void changeTargetState(final BaseTaskTarget _taskTarget) {
        this.tastTargetList.contains(_taskTarget);
    }

    public boolean isCompleted() {
        boolean completeState = true;
        for (final BaseTaskTarget target : this.tastTargetList) {
            completeState &= target.isCompleted();
        }
        return completeState;
    }

    public ArrayList<BaseTaskTarget> getTargetList() {
        return this.tastTargetList;
    }
}
