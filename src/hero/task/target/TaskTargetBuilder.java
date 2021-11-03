// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

public class TaskTargetBuilder {

    private static TaskTargetBuilder instance;

    private TaskTargetBuilder() {
    }

    public static TaskTargetBuilder getInstance() {
        if (TaskTargetBuilder.instance == null) {
            TaskTargetBuilder.instance = new TaskTargetBuilder();
        }
        return TaskTargetBuilder.instance;
    }

    public BaseTaskTarget create() {
        BaseTaskTarget taskTarget = null;
        return taskTarget;
    }
}
