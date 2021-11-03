// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

public enum FlowLog {
    GET("GET", 0, 0, "\u83b7\u5f97"),
    LOSE("LOSE", 1, 1, "\u5931\u53bb");

    private int id;
    private String name;

    private FlowLog(final String name, final int ordinal, final int _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
