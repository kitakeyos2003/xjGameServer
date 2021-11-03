// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge;

public enum RechargeTypeCard {
    CMCC("CMCC", 0, 0, "\u79fb\u52a8"),
    CU("CU", 1, 1, "\u8054\u901a"),
    CT("CT", 2, 2, "\u7535\u4fe1");

    private int id;
    private String name;

    private RechargeTypeCard(final String name2, final int ordinal, final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static RechargeTypeCard getCardTypeByName(final String name) {
        RechargeTypeCard[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            RechargeTypeCard card = values[i];
            if (card.name.equals(name)) {
                return card;
            }
        }
        return null;
    }

    public int getId() {
        return this.id;
    }
}
