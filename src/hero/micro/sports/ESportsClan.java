// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.sports;

public enum ESportsClan {
    CHI_YOU_MAN_YI("CHI_YOU_MAN_YI", 0, 1, "\u86a9\u5c24\u86ee\u5937"),
    YAN_LONG_YONG_SHI("YAN_LONG_YONG_SHI", 1, 2, "\u708e\u9f99\u52c7\u58eb"),
    TIAN_YU_ZHI_JUN("TIAN_YU_ZHI_JUN", 2, 3, "\u5929\u865e\u4e4b\u519b"),
    SHUN_WANG_WEI_DUI("SHUN_WANG_WEI_DUI", 3, 4, "\u821c\u738b\u536b\u961f");

    private int id;
    private String name;

    private ESportsClan(final String name, final int ordinal, final int _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
