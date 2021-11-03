// 
// Decompiled by Procyon v0.5.36
// 
package hero.map;

public enum EMapType {
    GENERIC("GENERIC", 0, 1),
    DUNGEON("DUNGEON", 1, 2);

    private int typeValue;

    private EMapType(final String name, final int ordinal, final int _typeValue) {
        this.typeValue = _typeValue;
    }

    public int getTypeValue() {
        return this.typeValue;
    }

    public static EMapType getMapType(final int _value) {
        EMapType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EMapType type = values[i];
            if (type.typeValue == _value) {
                return type;
            }
        }
        return null;
    }
}
