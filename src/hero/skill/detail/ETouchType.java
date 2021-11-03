// Decompiled with: CFR 0.151
// Class Version: 6
package hero.skill.detail;

public enum ETouchType {
    BE_ATTACKED_BY_NEAR_PHYSICS(1, "被近距离物理攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return BE_ATTACKED_BY_NEAR_PHYSICS == eTouchType;
        }
    },
    BE_ATTACKED_BY_DISTANCE_PHYSICS(2, "被远距离物理攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return BE_ATTACKED_BY_DISTANCE_PHYSICS == eTouchType;
        }
    },
    BE_ATTACKED_BY_PHYSICS(3, "被物理攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return BE_ATTACKED_BY_DISTANCE_PHYSICS == eTouchType || BE_ATTACKED_BY_NEAR_PHYSICS == eTouchType;
        }
    },
    BE_ATTACKED_BY_MAGIC(4, "被魔法攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return BE_ATTACKED_BY_MAGIC == eTouchType;
        }
    },
    BE_ATTACKED(5, "被攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return BE_ATTACKED_BY_MAGIC == eTouchType || BE_ATTACKED_BY_DISTANCE_PHYSICS == eTouchType || BE_ATTACKED_BY_NEAR_PHYSICS == eTouchType;
        }
    },
    ATTACK_BY_NEAR_PHYSICS(6, "近距离物理攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return ATTACK_BY_NEAR_PHYSICS == eTouchType;
        }
    },
    ATTACK_BY_DISTANCE_PHYSICS(7, "远距离物理攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return ATTACK_BY_DISTANCE_PHYSICS == eTouchType;
        }
    },
    ATTACK_BY_PHYSICS(8, "物理攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return ATTACK_BY_NEAR_PHYSICS == eTouchType || ATTACK_BY_DISTANCE_PHYSICS == eTouchType;
        }
    },
    ATTACK_BY_MAGIC(8, "魔法攻击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return ATTACK_BY_MAGIC == eTouchType;
        }
    },
    RESUME_BY_MAGIC(8, "辅助魔法") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return RESUME_BY_MAGIC == eTouchType;
        }
    },
    USE_MAGIC(10, "使用魔法") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            if (!bl) {
                return false;
            }
            return ATTACK_BY_NEAR_PHYSICS == eTouchType || ATTACK_BY_DISTANCE_PHYSICS == eTouchType || ATTACK_BY_MAGIC == eTouchType || RESUME_BY_MAGIC == eTouchType;
        }
    },
    ACTIVE(11, "主动行为") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return ATTACK_BY_MAGIC == eTouchType || RESUME_BY_MAGIC == eTouchType || ATTACK_BY_NEAR_PHYSICS == eTouchType || ATTACK_BY_DISTANCE_PHYSICS == eTouchType;
        }
    },
    BE_DEATHBLOW(12, "被暴击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return BE_DEATHBLOW_BY_MAGIC == eTouchType || BE_DEATHBLOW_BY_PHYSICS == eTouchType;
        }
    },
    BE_DEATHBLOW_BY_PHYSICS(13, "被物理暴击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return BE_DEATHBLOW_BY_PHYSICS == eTouchType;
        }
    },
    BE_DEATHBLOW_BY_MAGIC(14, "被魔法暴击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return BE_DEATHBLOW_BY_MAGIC == eTouchType;
        }
    },
    TOUCH_DEATHBLOW(15, "发动暴击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return TOUCH_DEATHBLOW_BY_MAGIC == eTouchType || TOUCH_DEATHBLOW_BY_PHYSICS == eTouchType;
        }
    },
    TOUCH_DEATHBLOW_BY_PHYSICS(16, "发动物理暴击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return TOUCH_DEATHBLOW_BY_PHYSICS == eTouchType;
        }
    },
    TOUCH_DEATHBLOW_BY_MAGIC(17, "发动魔法暴击") {

        @Override
        public boolean canTouch(ETouchType eTouchType, boolean bl) {
            return TOUCH_DEATHBLOW_BY_MAGIC == eTouchType;
        }
    };

    private byte value;
    private String desc;

    private ETouchType(int by, String string2) {
        this.value = (byte) by;
        this.desc = string2;
    }

    public byte value() {
        return this.value;
    }

    public static ETouchType get(String string) {
        ETouchType[] eTouchTypeArray = ETouchType.values();
        int n = eTouchTypeArray.length;
        int n2 = 0;
        while (n2 < n) {
            ETouchType eTouchType = eTouchTypeArray[n2];
            if (eTouchType.desc.equals(string)) {
                return eTouchType;
            }
            ++n2;
        }
        return null;
    }

    public boolean canTouch(ETouchType eTouchType, boolean bl) {
        return true;
    }

    public boolean canTouch(ETouchType eTouchType, ETouchType eTouchType2) {
        boolean bl = false;
        if (eTouchType == eTouchType2) {
            bl = true;
        }
        return bl;
    }

    ETouchType(String string, int n, byte by, String string2, ETouchType eTouchType) {
        this(by, string2);
    }
}
