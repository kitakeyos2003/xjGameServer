// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.enhance;

import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.detail.EBodyPartOfEquipment;

public class GenericEnhance {

    public byte[][] detail;
    public float sumModulus;
    public static final float BONUS_CHIP_MODULUS = 0.03f;
    public static final float BONUS_CHIP_MODULUS_HIGH = 0.05f;
    public static final float BONUS_SUCCESS_MODULUS = 0.05f;
    public static final float BONUS_SUCCESS_MODULUS_HIGH = 0.1f;
    public static final float BONUS_FLASH_MODULUS = 0.06f;
    public static final float BONUS_FLASH_MODULUS_HIGH = 0.15f;
    public static final byte HIGH_SIDE = 9;

    public GenericEnhance(final int _equipmentType) {
        this.detail = new byte[][]{new byte[2], new byte[2], new byte[2], new byte[2], new byte[2], new byte[2], new byte[2], new byte[2], new byte[2], new byte[2], new byte[2], new byte[2]};
        this.initModulus();
    }

    public short[] getFlashByDBString(final String _DBString, final EBodyPartOfEquipment _body) {
        short[] result = new short[2];
        String[] genericEnhanceDataDesc = _DBString.split("#");
        for (int i = 0; i < genericEnhanceDataDesc.length; ++i) {
            if (!genericEnhanceDataDesc[i].equals("") && genericEnhanceDataDesc[i] != null) {
                byte enhance = Byte.parseByte(genericEnhanceDataDesc[i]);
                if (enhance == -1) {
                    this.initAllGrid(i, false, (byte) 0);
                } else {
                    this.initAllGrid(i, true, enhance);
                }
            }
        }
        if (_body == EBodyPartOfEquipment.WEAPON) {
            result = this.getFlashView();
        } else if (_body == EBodyPartOfEquipment.BOSOM) {
            result = this.getArmorFlashView();
        }
        return result;
    }

    public void initModulus() {
        try {
            this.sumModulus = 0.0f;
            for (int i = 0; i < this.detail.length; ++i) {
                if (this.detail[i][0] > 0) {
                    if (this.detail[i][1] == 3) {
                        this.sumModulus += this.getBounsValue(i, this.detail[i][1]);
                        if (i >= 1) {
                            this.sumModulus += this.getBounsValue(i - 1, this.detail[i - 1][1]);
                        }
                        if (i >= 2 && this.detail[i - 1][1] == 3 && this.detail[i - 2][1] == 3) {
                            this.sumModulus += this.getBounsValue(i - 2, this.detail[i - 2][1]);
                        }
                    } else {
                        this.sumModulus += this.getBounsValue(i, this.detail[i][1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getHoleModules(final int position) {
        float value = 0.0f;
        if (this.detail[position][0] > 0) {
            if (this.detail[position][1] == 3) {
                value = this.getBounsValue(position, this.detail[position][1]);
                if (position >= 1) {
                    value += this.getBounsValue(position, this.detail[position - 1][1]);
                }
                if (position >= 2 && this.detail[position - 1][1] == 3 && this.detail[position - 2][1] == 3) {
                    value += this.getBounsValue(position, this.detail[position - 2][1]);
                }
            } else {
                value = this.getBounsValue(position, this.detail[position][1]);
            }
        }
        return value;
    }

    private float getBounsValue(final int position, final int level) {
        float value = 0.0f;
        if (position < 9) {
            if (level == 1) {
                value = 0.03f;
            } else if (level == 2) {
                value = 0.05f;
            } else if (level == 3) {
                value = 0.06f;
            }
        } else if (level == 1) {
            value = 0.05f;
        } else if (level == 2) {
            value = 0.1f;
        } else if (level == 3) {
            value = 0.15f;
        }
        return value;
    }

    public void setDetailEnhance(final int _detailIndex, final byte _enhanceLevel) {
        if (_detailIndex >= 0 && _detailIndex < this.detail.length) {
            this.detail[_detailIndex][0] = 1;
            this.detail[_detailIndex][1] = _enhanceLevel;
        }
    }

    public void initAllGrid(final int _detailIndex, final boolean isActive, final byte _enhanceLevel) {
        if (_detailIndex >= 0 && _detailIndex < this.detail.length) {
            if (!isActive) {
                this.detail[_detailIndex][0] = 0;
                this.detail[_detailIndex][1] = 0;
            } else {
                this.detail[_detailIndex][0] = 1;
                this.detail[_detailIndex][1] = _enhanceLevel;
            }
        }
    }

    public byte getJewelLevel(final int _detailIndex) {
        byte level = 0;
        level = this.detail[_detailIndex][1];
        return level;
    }

    public boolean resetEnhanceLevel(final int _detailIndex) {
        boolean result = false;
        if (this.detail[_detailIndex][1] == 0) {
            return result;
        }
        if (_detailIndex >= 0 && _detailIndex < this.detail.length && this.detail[_detailIndex][0] == 1) {
            this.detail[_detailIndex][1] = 0;
            result = true;
        }
        this.initModulus();
        return result;
    }

    public boolean haveHole(final int _index) {
        boolean have = false;
        if (_index >= 0 && this.detail[_index][0] > 0) {
            return have = true;
        }
        return have;
    }

    public boolean haveJewel(final int _index) {
        boolean have = false;
        if (_index >= 0 && this.detail[_index][0] > 0 && this.detail[_index][1] > 0) {
            return have = true;
        }
        return have;
    }

    public boolean addDetailEnhance(final int _detailIndex, final byte level) {
        if (_detailIndex >= 0 && _detailIndex < this.detail.length) {
            this.detail[_detailIndex][1] = level;
            this.initModulus();
            return true;
        }
        return false;
    }

    public boolean addPerforate() {
        boolean result = false;
        for (int i = 0; i < this.detail.length; ++i) {
            if (this.detail[i][0] == 0) {
                result = true;
                this.detail[i][0] = 1;
                return result;
            }
        }
        return result;
    }

    public byte getHole() {
        byte hole = 0;
        for (byte i = 0; i < this.detail.length; ++i) {
            if (this.detail[i][0] == 1) {
                ++hole;
            }
        }
        return hole;
    }

    public byte getLevel() {
        byte levelCount = 0;
        for (byte i = 0; i < this.detail.length; ++i) {
            if (this.detail[i][1] > 0) {
                ++levelCount;
            }
        }
        return levelCount;
    }

    public byte getFlash() {
        byte levelCount = 0;
        for (byte i = 0; i < this.detail.length; ++i) {
            if (this.detail[i][1] == 3) {
                ++levelCount;
            }
        }
        return levelCount;
    }

    public byte getIndex() {
        byte index = -1;
        for (byte i = 0; i < this.detail.length; ++i) {
            if (this.detail[i][0] != 0 && this.detail[i][1] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    public float getBasicModulus() {
        return this.sumModulus;
    }

    public float getDefenseModulus() {
        return this.sumModulus;
    }

    public float getAdjuvantModulus() {
        return this.sumModulus;
    }

    public float getAttackModulus() {
        return this.sumModulus;
    }

    public float getSumModulus() {
        return this.sumModulus;
    }

    public String getUpString() {
        String up = String.valueOf(this.sumModulus * 100.0f) + "%";
        return up;
    }

    public String getUpEndString() {
        String up = "";
        String temp = "";
        if (this.sumModulus > 0.0) {
            temp = String.valueOf(this.sumModulus * 100.0f);
            temp = String.valueOf(temp.substring(0, temp.indexOf("."))) + "%";
            up = String.valueOf(GoodsServiceImpl.getInstance().getConfig().describe_enhance_string) + temp;
        }
        return up;
    }

    public String getUpString(final int _index) {
        String up = "";
        up = String.valueOf(this.getHoleModules(_index) * 100.0f) + "%";
        return up;
    }

    public short[] getFlashView() {
        short[] view = new short[2];
        view = GoodsServiceImpl.getInstance().getFlashView(this.getFlashLevel());
        return view;
    }

    public short[] getArmorFlashView() {
        short[] view = new short[2];
        view = GoodsServiceImpl.getInstance().getArmorFlashView(this.getFlashLevel());
        return view;
    }

    public short[] getYetSetJewel(final int _index) {
        short[] view = {-1, -1};
        byte level = this.detail[_index][1];
        view = GoodsServiceImpl.getInstance().getYetSetJewel(level);
        return view;
    }

    public byte getFlashLevel() {
        byte result = 0;
        if (this.sumModulus >= 0.21f && this.sumModulus <= 0.4f) {
            result = 1;
        } else if (this.sumModulus >= 0.41f && this.sumModulus <= 0.74f) {
            result = 2;
        } else if (this.sumModulus >= 0.75f && this.sumModulus <= 1.44f) {
            result = 3;
        } else if (this.sumModulus >= 1.45f && this.sumModulus <= 1.91f) {
            result = 4;
        } else if (this.sumModulus >= 1.92f && this.sumModulus <= 2.5f) {
            result = 5;
        } else if (this.sumModulus >= 2.51f) {
            result = 6;
        }
        return result;
    }
}
