// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

import java.util.HashMap;

public class MagicHarmList {

    private HashMap<EMagic, Float> magicMap;

    public MagicHarmList() {
        (this.magicMap = new HashMap<EMagic, Float>(5)).put(EMagic.SANCTITY, 0.0f);
        this.magicMap.put(EMagic.UMBRA, 0.0f);
        this.magicMap.put(EMagic.WATER, 0.0f);
        this.magicMap.put(EMagic.FIRE, 0.0f);
        this.magicMap.put(EMagic.SOIL, 0.0f);
        this.magicMap.put(EMagic.ALL, 0.0f);
    }

    public MagicHarmList(final float _value) {
        (this.magicMap = new HashMap<EMagic, Float>(5)).put(EMagic.SANCTITY, _value);
        this.magicMap.put(EMagic.UMBRA, _value);
        this.magicMap.put(EMagic.WATER, _value);
        this.magicMap.put(EMagic.FIRE, _value);
        this.magicMap.put(EMagic.SOIL, _value);
        this.magicMap.put(EMagic.ALL, _value);
    }

    public float add(final EMagic _magic, final float _value) {
        Float value = this.magicMap.remove(_magic);
        value += _value;
        this.magicMap.put(_magic, value);
        return value;
    }

    public void reset(final EMagic _magic, final float _value) {
        this.magicMap.put(_magic, _value);
    }

    public void resetByInte(final EMagic _magic, final float _value) {
        float value = this.magicMap.get(EMagic.ALL) + _value;
        this.magicMap.put(_magic, value);
    }

    public void reset(final MagicHarmList _magicFastList) {
        if (_magicFastList != null) {
            this.reset(EMagic.SANCTITY, _magicFastList.getEMagicHarmValue(EMagic.SANCTITY));
            this.reset(EMagic.UMBRA, _magicFastList.getEMagicHarmValue(EMagic.UMBRA));
            this.reset(EMagic.WATER, _magicFastList.getEMagicHarmValue(EMagic.WATER));
            this.reset(EMagic.FIRE, _magicFastList.getEMagicHarmValue(EMagic.FIRE));
            this.reset(EMagic.SOIL, _magicFastList.getEMagicHarmValue(EMagic.SOIL));
            this.reset(EMagic.ALL, _magicFastList.getEMagicHarmValue(EMagic.ALL));
        }
    }

    public void clear() {
        this.magicMap.put(EMagic.SANCTITY, 0.0f);
        this.magicMap.put(EMagic.UMBRA, 0.0f);
        this.magicMap.put(EMagic.WATER, 0.0f);
        this.magicMap.put(EMagic.FIRE, 0.0f);
        this.magicMap.put(EMagic.SOIL, 0.0f);
        this.magicMap.put(EMagic.ALL, 0.0f);
    }

    public float[] add(final MagicHarmList _magicHarmList) {
        float[] currentValue = null;
        if (_magicHarmList != null) {
            currentValue = new float[]{this.add(EMagic.SANCTITY, _magicHarmList.getEMagicHarmValue(EMagic.SANCTITY)), this.add(EMagic.UMBRA, _magicHarmList.getEMagicHarmValue(EMagic.UMBRA)), this.add(EMagic.WATER, _magicHarmList.getEMagicHarmValue(EMagic.WATER)), this.add(EMagic.FIRE, _magicHarmList.getEMagicHarmValue(EMagic.FIRE)), this.add(EMagic.SOIL, _magicHarmList.getEMagicHarmValue(EMagic.SOIL))};
        }
        return currentValue;
    }

    public float getEMagicHarmValue(EMagic _magic) {
        float harm = 1.0f;
        if (_magic == null) {
            System.out.println("\u4f20\u5165\u9b54\u6cd5\u5c5e\u6027\u53c2\u6570\u4e3aNULL,\u8fd9\u6837\u7684\u60c5\u51b5\u5e94\u8be5\u53bb\u6392\u67e5\u6280\u80fd\u8868\u683c");
            _magic = EMagic.FIRE;
        }
        if (_magic == EMagic.ALL) {
            harm = this.magicMap.get(EMagic.ALL);
        } else {
            harm = this.magicMap.get(_magic) + this.magicMap.get(EMagic.ALL);
        }
        return harm;
    }
}
