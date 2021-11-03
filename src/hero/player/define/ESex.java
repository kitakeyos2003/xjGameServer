// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.define;

public enum ESex {
    Male("Male", 0, (byte) 1, "\u7537"),
    Female("Female", 1, (byte) 2, "\u5973");

    private byte value;
    private String desc;

    public static ESex getSex(final int _value) {
        ESex[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ESex sex = values[i];
            if (sex.value == _value) {
                return sex;
            }
        }
        return null;
    }

    private ESex(final String name, final int ordinal, final byte _value, final String _desc) {
        this.value = _value;
        this.desc = _desc;
    }

    public byte value() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }
}
