// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

public enum EObjectLevel {
    NORMAL((short) 0, "\u666e\u901a") {
        @Override
        public int getBaseExperiencePara() {
            return 1;
        }

        @Override
        public int getHpCalPara() {
            return 1;
        }

        @Override
        public int getMpCalPara() {
            return 1;
        }

        @Override
        public int getPhysicsAttckCalPara() {
            return 1;
        }
    },
    PROMINENT((short) 1, "\u7cbe\u82f1") {
        @Override
        public int getBaseExperiencePara() {
            return 2;
        }

        @Override
        public int getHpCalPara() {
            return 4;
        }

        @Override
        public int getMpCalPara() {
            return 2;
        }

        @Override
        public int getPhysicsAttckCalPara() {
            return 2;
        }
    },
    HERO((short) 2, "\u82f1\u96c4") {
        @Override
        public int getBaseExperiencePara() {
            return 4;
        }

        @Override
        public int getHpCalPara() {
            return 8;
        }

        @Override
        public int getMpCalPara() {
            return 4;
        }

        @Override
        public int getPhysicsAttckCalPara() {
            return 4;
        }
    },
    MONARCH((short) 3, "\u738b\u8005") {
        @Override
        public int getBaseExperiencePara() {
            return 8;
        }

        @Override
        public int getHpCalPara() {
            return 16;
        }

        @Override
        public int getMpCalPara() {
            return 16;
        }

        @Override
        public int getPhysicsAttckCalPara() {
            return 6;
        }
    },
    JINN((short) 4, "\u795e\u7075") {
        @Override
        public int getBaseExperiencePara() {
            return 16;
        }

        @Override
        public int getHpCalPara() {
            return 64;
        }

        @Override
        public int getMpCalPara() {
            return 64;
        }

        @Override
        public int getPhysicsAttckCalPara() {
            return 10;
        }
    };

    private String desc;
    private int value;

    public String getDesc() {
        return this.desc;
    }

    private EObjectLevel(final int _value, final String _desc) {
        this.value = _value;
        this.desc = _desc;
    }

    public static EObjectLevel getNPCLevel(final String _desc) {
        EObjectLevel[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            EObjectLevel type = values[i];
            if (type.getDesc().equals(_desc)) {
                return type;
            }
        }
        return null;
    }

    public int value() {
        return this.value;
    }

    public abstract int getBaseExperiencePara();

    public abstract int getHpCalPara();

    public abstract int getMpCalPara();

    public abstract int getPhysicsAttckCalPara();
}
