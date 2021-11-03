// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

public enum MarryGoods {
    RANG("RANG", 0, 340004, "\u7ed3\u5a5a\u6212\u6307"),
    DIVORCE("DIVORCE", 1, 340005, "\u79bb\u5a5a\u534f\u8bae"),
    FORCE_DIVORCE("FORCE_DIVORCE", 2, 340006, "\u5f3a\u5236\u79bb\u5a5a\u8bc1\u660e"),
    TRANSPORT("TRANSPORT", 3, 340064, "\u592b\u59bb\u4f20\u9001\u7b26");

    private int id;
    private String name;

    private MarryGoods(final String name, final int ordinal, final int _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public static MarryGoods getMarryGoodsByGoodsID(final int id) {
        MarryGoods[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            MarryGoods goods = values[i];
            if (goods.id == id) {
                return goods;
            }
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
