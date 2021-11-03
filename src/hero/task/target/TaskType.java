// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.target;

public enum TaskType {
    SINGLE("SINGLE", 0, 1, "\u81ea\u5df1"),
    MASTER("MASTER", 1, 2, "\u5e08\u5f92"),
    MARRY("MARRY", 2, 3, "\u5a5a\u59fb");

    private int id;
    private String desc;

    private TaskType(final String name, final int ordinal, final int id, final String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static TaskType getTaskTypeByID(final int id) {
        TaskType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            TaskType type = values[i];
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }

    public static TaskType getTaskTypeByType(final String desc) {
        TaskType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            TaskType type = values[i];
            if (type.desc.equals(desc)) {
                return type;
            }
        }
        return null;
    }
}
