// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.detail;

public enum EGoodsTrait {
    SHI_QI("SHI_QI", 0, 1, "\u666e\u901a", 0),
    BING_ZHI("BING_ZHI", 1, 2, "\u4f18\u79c0", 623372),
    JIANG_ZHI("JIANG_ZHI", 2, 3, "\u7cbe\u826f", 28864),
    YU_ZHI("YU_ZHI", 3, 4, "\u53f2\u8bd7", 13107400),
    SHENG_QI("SHENG_QI", 4, 5, "\u4f20\u8bf4", 13831703);

    private int value;
    private String description;
    private int viewRGB;

    private EGoodsTrait(final String name, final int ordinal, final int _value, final String _desc, final int _viewRGB) {
        this.value = _value;
        this.description = _desc;
        this.viewRGB = _viewRGB;
    }

    public int value() {
        return this.value;
    }

    public String getDesc() {
        return this.description;
    }

    public int getViewRGB() {
        return this.viewRGB;
    }

    public static EGoodsTrait getTrait(final int _value) {
        EGoodsTrait[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EGoodsTrait trait = values[i];
            if (trait.value() == _value) {
                return trait;
            }
        }
        return null;
    }

    public static EGoodsTrait getTrait(final String _traitDesc) {
        EGoodsTrait[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EGoodsTrait trait = values[i];
            if (trait.getDesc().equals(_traitDesc)) {
                return trait;
            }
        }
        return null;
    }
}
