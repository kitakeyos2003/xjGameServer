// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

public enum ESystemFeature {
    DEAD("DEAD", 0, "\u4ea1\u8005"),
    HERO("HERO", 1, "\u82f1\u96c4");

    private String desciption;

    private ESystemFeature(final String name, final int ordinal, final String _desciption) {
        this.desciption = _desciption;
    }

    public String getDesc() {
        return this.desciption;
    }

    public static ESystemFeature getFeatureByDesc(final String _desciption) {
        ESystemFeature[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ESystemFeature feature = values[i];
            if (feature.getDesc().equals(_desciption)) {
                return feature;
            }
        }
        return null;
    }
}
