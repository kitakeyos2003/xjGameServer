// 
// Decompiled by Procyon v0.5.36
// 
package hero.social;

public enum ESocialRelationType {
    FRIEND("FRIEND", 0, 1),
    BLACK("BLACK", 1, 2),
    ENEMY("ENEMY", 2, 3);

    byte value;

    private ESocialRelationType(final String name, final int ordinal, final int _value) {
        this.value = (byte) _value;
    }

    public static ESocialRelationType getSocialRelationType(final int _value) {
        ESocialRelationType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ESocialRelationType socialRelationType = values[i];
            if (socialRelationType.value == _value) {
                return socialRelationType;
            }
        }
        return null;
    }

    public byte value() {
        return this.value;
    }
}
