// Decompiled with: FernFlower
// Class Version: 6
package hero.player.define;

public enum EClan {
    LONG_SHAN((short) 1, "龙之传人") {
    },
    HE_MU_DU((short) 2, "恶魔之子") {
    },
    NONE((short) 0, "ALL") {
    };

    private short id;
    private String desc;

    public static EClan getClan(int _id) {
        EClan[] var4;
        int var3 = (var4 = values()).length;

        for (int var2 = 0; var2 < var3; ++var2) {
            EClan c = var4[var2];
            if (c.getID() == _id) {
                return c;
            }
        }

        return NONE;
    }

    public static EClan[] getValues() {
        EClan[] clans = new EClan[]{HE_MU_DU, LONG_SHAN};
        return clans;
    }

    public static EClan getClanByDesc(String _name) {
        if (_name == null) {
            return NONE;
        } else {
            EClan[] var4;
            int var3 = (var4 = values()).length;

            for (int var2 = 0; var2 < var3; ++var2) {
                EClan c = var4[var2];
                if (c.getDesc().equalsIgnoreCase(_name)) {
                    return c;
                }
            }

            return NONE;
        }
    }

    private EClan(short _id, String _desc) {
        this.id = _id;
        this.desc = _desc;
    }

    public short getID() {
        return this.id;
    }

    public String getDesc() {
        return this.desc;
    }

    // $FF: synthetic method
    EClan(short var3, String var4, EClan var5) {
        this(var3, var4);
    }
}
