// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.service;

import java.util.Iterator;
import org.dom4j.Document;
import hero.item.detail.EGoodsTrait;
import hero.item.Weapon;
import hero.item.dictionary.WeaponDict;
import hero.effect.dictionry.EffectDictionary;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import java.util.HashMap;

public class WeaponPvpAndPveEffectDict {

    private static WeaponPvpAndPveEffectDict instance;
    private HashMap<Integer, int[]> effectIDListTable;

    private WeaponPvpAndPveEffectDict() {
        this.effectIDListTable = new HashMap<Integer, int[]>();
    }

    public void load(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
            this.effectIDListTable.clear();
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, j = 0; j < length; ++j) {
                File dataFile = array[j];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            int id = Integer.parseInt(subE.elementTextTrim("id"));
                            int[] effectIDList = new int[12];
                            for (int i = 1; i <= effectIDList.length; ++i) {
                                effectIDList[i - 1] = Integer.parseInt(subE.elementTextTrim("level" + i + "ID"));
                                if (EffectDictionary.getInstance().getEffectRef(effectIDList[i - 1]) == null) {
                                    LogWriter.println("\u6740\u622e\u3001\u5c60\u9b54\u6548\u679c\u4e0d\u5b58\u5728\uff1a" + effectIDList[i - 1]);
                                    return;
                                }
                            }
                            this.effectIDListTable.put(id, effectIDList);
                        }
                    }
                }
            }
            Object[] weaponList = WeaponDict.getInstance().getWeaponList();
            if (weaponList.length > 0) {
                Object[] array2;
                for (int length2 = (array2 = weaponList).length, k = 0; k < length2; ++k) {
                    Object weapon = array2[k];
                    if (((Weapon) weapon).getTrait() == EGoodsTrait.YU_ZHI || ((Weapon) weapon).getTrait() == EGoodsTrait.SHENG_QI) {
                        if (!this.effectIDListTable.containsKey(((Weapon) weapon).getPveEnhanceID())) {
                            LogWriter.println("\u6b66\u5668\u5173\u8054\u7684\u5c60\u9b54\u6548\u679c\u4e0d\u5b58\u5728\uff0c\u6b66\u5668\uff1a" + ((Weapon) weapon).getName() + "--" + ((Weapon) weapon).getTrait().getDesc() + "--" + ((Weapon) weapon).getPveEnhanceID());
                        } else if (!this.effectIDListTable.containsKey(((Weapon) weapon).getPvpEnhanceID())) {
                            LogWriter.println("\u6b66\u5668\u5173\u8054\u7684\u6740\u622e\u6548\u679c\u4e0d\u5b58\u5728\uff0c\u6b66\u5668\uff1a" + ((Weapon) weapon).getName() + "--" + ((Weapon) weapon).getTrait().getDesc() + "--" + ((Weapon) weapon).getPvpEnhanceID());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WeaponPvpAndPveEffectDict getInstance() {
        if (WeaponPvpAndPveEffectDict.instance == null) {
            WeaponPvpAndPveEffectDict.instance = new WeaponPvpAndPveEffectDict();
        }
        return WeaponPvpAndPveEffectDict.instance;
    }

    public int getEffectID(final int _weaponEnhanceID, final int _enhanceLevel) {
        if (_enhanceLevel > 0 && _enhanceLevel <= 12) {
            int[] _effectIDList = this.effectIDListTable.get(_weaponEnhanceID);
            if (_effectIDList != null) {
                return _effectIDList[_enhanceLevel - 1];
            }
        }
        return -1;
    }
}
