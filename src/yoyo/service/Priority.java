// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service;

public enum Priority {
    REAL_TIME("REAL_TIME", 0, 0),
    DELAY("DELAY", 1, 1);

    private int value;

    private Priority(final String name, final int ordinal, final int v) {
        this.value = v;
    }

    public int getValue() {
        return this.value;
    }

    public static Priority getPriority(final int v) {
        Priority[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            Priority p = values[i];
            if (p.value == v) {
                return p;
            }
        }
        return Priority.REAL_TIME;
    }
}
