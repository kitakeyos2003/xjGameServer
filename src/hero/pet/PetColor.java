// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet;

public enum PetColor {
    WHITE("WHITE", 0, 1, "\u767d\u8272"),
    GOLDEN("GOLDEN", 1, 2, "\u91d1\u8272"),
    BLUE("BLUE", 2, 3, "\u84dd\u8272");

    private int id;
    private String color;

    private PetColor(final String name, final int ordinal, final int id, final String color) {
        this.id = id;
        this.color = color;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public static PetColor get(final int id) {
        PetColor[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            PetColor pc = values[i];
            if (pc.id == id) {
                return pc;
            }
        }
        return PetColor.WHITE;
    }

    public static PetColor getRandomPetEggColor() {
        int r = (int) (100.0 * Math.random());
        if (r <= 10) {
            return PetColor.GOLDEN;
        }
        return PetColor.WHITE;
    }
}
