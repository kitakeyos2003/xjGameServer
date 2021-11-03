// 
// Decompiled by Procyon v0.5.36
// 
package hero.map;

public enum EMapWeather {
    NONE("NONE", 0, 0, "\u65e0"),
    RAIN("RAIN", 1, 1, "\u4e0b\u96e8"),
    SNOW("SNOW", 2, 2, "\u4e0b\u96ea"),
    CLOUDY("CLOUDY", 3, 3, "\u591a\u4e91"),
    BUBBLE("BUBBLE", 4, 4, "\u6c14\u6ce1"),
    PETAL("PETAL", 5, 5, "\u82b1\u74e3");

    private int typeValue;
    private String desc;

    private EMapWeather(final String name, final int ordinal, final int _value, final String _desc) {
        this.typeValue = _value;
        this.desc = _desc;
    }

    public int getTypeValue() {
        return this.typeValue;
    }

    public String getDescription() {
        return this.desc;
    }

    public static EMapWeather get(final int _value) {
        EMapWeather[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EMapWeather weather = values[i];
            if (weather.typeValue == _value) {
                return weather;
            }
        }
        return EMapWeather.NONE;
    }
}
