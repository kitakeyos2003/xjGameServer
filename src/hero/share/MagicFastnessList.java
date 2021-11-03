// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

import javolution.util.FastMap;

public class MagicFastnessList {

    private FastMap<EMagic, Integer> magicMap;

    public MagicFastnessList() {
        (this.magicMap = (FastMap<EMagic, Integer>) new FastMap(5)).put(EMagic.SANCTITY, 0);
        this.magicMap.put(EMagic.UMBRA, 0);
        this.magicMap.put(EMagic.WATER, 0);
        this.magicMap.put(EMagic.FIRE, 0);
        this.magicMap.put(EMagic.SOIL, 0);
        this.magicMap.put(EMagic.ALL, 0);
    }

    public MagicFastnessList(final int _value) {
        (this.magicMap = (FastMap<EMagic, Integer>) new FastMap(5)).put(EMagic.SANCTITY, _value);
        this.magicMap.put(EMagic.UMBRA, _value);
        this.magicMap.put(EMagic.WATER, _value);
        this.magicMap.put(EMagic.FIRE, _value);
        this.magicMap.put(EMagic.SOIL, _value);
        this.magicMap.put(EMagic.ALL, _value);
    }

    public int add(final EMagic _magic, final int _value) {
        int value = (int) this.magicMap.remove(_magic);
        value += _value;
        if (value < 0) {
            value = 0;
        }
        this.magicMap.put(_magic, value);
        return value;
    }

    public int[] add(final MagicFastnessList _magicFastList) {
        int[] currentValue = null;
        if (_magicFastList != null) {
            currentValue = new int[5];
            currentValue[0] = _magicFastList.getEMagicFastnessValue(EMagic.SANCTITY);
            if (currentValue[0] != 0) {
                currentValue[0] = this.add(EMagic.SANCTITY, currentValue[0]);
            }
            currentValue[1] = _magicFastList.getEMagicFastnessValue(EMagic.UMBRA);
            if (currentValue[1] != 0) {
                currentValue[1] = this.add(EMagic.UMBRA, currentValue[1]);
            }
            currentValue[2] = _magicFastList.getEMagicFastnessValue(EMagic.WATER);
            if (currentValue[2] != 0) {
                currentValue[2] = this.add(EMagic.WATER, currentValue[2]);
            }
            currentValue[3] = _magicFastList.getEMagicFastnessValue(EMagic.FIRE);
            if (currentValue[3] != 0) {
                currentValue[3] = this.add(EMagic.FIRE, currentValue[3]);
            }
            currentValue[4] = _magicFastList.getEMagicFastnessValue(EMagic.SOIL);
            if (currentValue[4] != 0) {
                currentValue[4] = this.add(EMagic.SOIL, currentValue[4]);
            }
        }
        return currentValue;
    }

    public int[] add(final MagicFastnessList _magicFastList, final float _modulus) {
        int[] currentValue = null;
        if (_modulus > 0.0f && _magicFastList != null) {
            currentValue = new int[6];
            currentValue[0] = (int) (_magicFastList.getEMagicFastnessValue(EMagic.SANCTITY) * _modulus);
            if (currentValue[0] != 0) {
                currentValue[0] = this.add(EMagic.SANCTITY, currentValue[0]);
            }
            currentValue[1] = (int) (_magicFastList.getEMagicFastnessValue(EMagic.UMBRA) * _modulus);
            if (currentValue[1] != 0) {
                currentValue[1] = this.add(EMagic.UMBRA, currentValue[1]);
            }
            currentValue[2] = (int) (_magicFastList.getEMagicFastnessValue(EMagic.WATER) * _modulus);
            if (currentValue[2] != 0) {
                currentValue[2] = this.add(EMagic.WATER, currentValue[2]);
            }
            currentValue[3] = (int) (_magicFastList.getEMagicFastnessValue(EMagic.FIRE) * _modulus);
            if (currentValue[3] != 0) {
                currentValue[3] = this.add(EMagic.FIRE, currentValue[3]);
            }
            currentValue[4] = (int) (_magicFastList.getEMagicFastnessValue(EMagic.SOIL) * _modulus);
            if (currentValue[4] != 0) {
                currentValue[4] = this.add(EMagic.SOIL, currentValue[4]);
            }
            currentValue[5] = (int) (_magicFastList.getEMagicFastnessValue(EMagic.ALL) * _modulus);
            if (currentValue[5] != 0) {
                currentValue[5] = this.add(EMagic.ALL, currentValue[5]);
            }
        }
        return currentValue;
    }

    public void reset(final EMagic _magic, final int _value) {
        this.magicMap.put(_magic, _value);
    }

    public void reset(final MagicFastnessList _magicFastList) {
        if (_magicFastList != null) {
            this.magicMap.put(EMagic.SANCTITY, _magicFastList.getEMagicFastnessValue(EMagic.SANCTITY));
            this.magicMap.put(EMagic.UMBRA, _magicFastList.getEMagicFastnessValue(EMagic.UMBRA));
            this.magicMap.put(EMagic.WATER, _magicFastList.getEMagicFastnessValue(EMagic.WATER));
            this.magicMap.put(EMagic.FIRE, _magicFastList.getEMagicFastnessValue(EMagic.FIRE));
            this.magicMap.put(EMagic.SOIL, _magicFastList.getEMagicFastnessValue(EMagic.SOIL));
            this.magicMap.put(EMagic.ALL, _magicFastList.getEMagicFastnessValue(EMagic.ALL));
        }
    }

    public void clear() {
        this.magicMap.put(EMagic.SANCTITY, 0);
        this.magicMap.put(EMagic.UMBRA, 0);
        this.magicMap.put(EMagic.WATER, 0);
        this.magicMap.put(EMagic.FIRE, 0);
        this.magicMap.put(EMagic.SOIL, 0);
        this.magicMap.put(EMagic.ALL, 0);
    }

    public int[] reduce(final MagicFastnessList _magicFastList) {
        int[] currentValue = null;
        if (_magicFastList != null) {
            currentValue = new int[]{this.add(EMagic.SANCTITY, -_magicFastList.getEMagicFastnessValue(EMagic.SANCTITY)), this.add(EMagic.UMBRA, -_magicFastList.getEMagicFastnessValue(EMagic.UMBRA)), this.add(EMagic.WATER, -_magicFastList.getEMagicFastnessValue(EMagic.WATER)), this.add(EMagic.FIRE, -_magicFastList.getEMagicFastnessValue(EMagic.FIRE)), this.add(EMagic.SOIL, -_magicFastList.getEMagicFastnessValue(EMagic.SOIL)), this.add(EMagic.ALL, -_magicFastList.getEMagicFastnessValue(EMagic.ALL))};
        }
        return currentValue;
    }

    public int getEMagicFastnessValue(final EMagic _magic) {
        int value = 0;
        if (_magic != null) {
            value = (int) this.magicMap.get(_magic);
        } else {
            System.out.println("\u4f20\u5165\u9b54\u6cd5\u5c5e\u6027\u53c2\u6570\u4e3aNULL,\u8fd9\u6837\u7684\u60c5\u51b5\u5e94\u8be5\u53bb\u6392\u67e5\u6280\u80fd\u8868\u683c");
            value = EMagic.FIRE.getID();
        }
        return value;
    }
}
