// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.detail;

public enum EMathCaluOperator {
    MUL("MUL", 0, "\u4e58"),
    DIV("DIV", 1, "\u9664"),
    ADD("ADD", 2, "\u52a0"),
    DEC("DEC", 3, "\u51cf");

    String desc;

    private EMathCaluOperator(final String name, final int ordinal, final String _desc) {
        this.desc = _desc;
    }

    public static EMathCaluOperator get(final String _desc) {
        EMathCaluOperator[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EMathCaluOperator operator = values[i];
            if (operator.desc.equals(_desc)) {
                return operator;
            }
        }
        return null;
    }

    public static EMathCaluOperator getReverseCaluOperator(final EMathCaluOperator operator) {
        if (operator != null) {
            switch (operator) {
                case MUL: {
                    return EMathCaluOperator.DIV;
                }
                case DIV: {
                    return EMathCaluOperator.MUL;
                }
                case ADD: {
                    return EMathCaluOperator.DEC;
                }
                case DEC: {
                    return EMathCaluOperator.ADD;
                }
            }
        }
        return null;
    }
}
