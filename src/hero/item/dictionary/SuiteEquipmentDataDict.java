// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import java.util.Iterator;
import org.dom4j.Document;
import hero.share.service.LogWriter;
import hero.share.EMagic;
import hero.share.MagicFastnessList;
import hero.share.AccessorialOriginalAttribute;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import javolution.util.FastMap;

public class SuiteEquipmentDataDict {

    private FastMap<Short, SuiteEData> table;
    private static SuiteEquipmentDataDict instance;

    private SuiteEquipmentDataDict() {
        this.table = (FastMap<Short, SuiteEData>) new FastMap();
    }

    public static SuiteEquipmentDataDict getInstance() {
        if (SuiteEquipmentDataDict.instance == null) {
            SuiteEquipmentDataDict.instance = new SuiteEquipmentDataDict();
        }
        return SuiteEquipmentDataDict.instance;
    }

    public void load(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
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
                            SuiteEData suiteEData = new SuiteEData();
                            try {
                                suiteEData.id = Short.parseShort(subE.elementTextTrim("id"));
                                suiteEData.name = subE.elementTextTrim("name");
                                for (int i = 1; i <= 4; ++i) {
                                    String data = subE.elementTextTrim("e" + i);
                                    if (data == null) {
                                        return;
                                    }
                                    suiteEData.idList[i - 1] = Integer.parseInt(data);
                                }
                                String data = subE.elementTextTrim("defense");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.defense = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("hp");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.hp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("mp");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.mp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("strength");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.strength = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("agility");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.agility = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("stamina");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.stamina = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("inte");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.inte = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("spirit");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.spirit = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("luck");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.lucky = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("physicsDeathblowLevel");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.physicsDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("magicDeathblowLevel");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.magicDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("hitLevel");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.hitLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("duckLevel");
                                if (data != null) {
                                    if (suiteEData.atribute == null) {
                                        suiteEData.atribute = new AccessorialOriginalAttribute();
                                    }
                                    suiteEData.atribute.duckLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("sanctity");
                                if (data != null) {
                                    if (suiteEData.fastnessList == null) {
                                        suiteEData.fastnessList = new MagicFastnessList();
                                    }
                                    suiteEData.fastnessList.reset(EMagic.SANCTITY, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("umbra");
                                if (data != null) {
                                    if (suiteEData.fastnessList == null) {
                                        suiteEData.fastnessList = new MagicFastnessList();
                                    }
                                    suiteEData.fastnessList.reset(EMagic.UMBRA, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("fire");
                                if (data != null) {
                                    if (suiteEData.fastnessList == null) {
                                        suiteEData.fastnessList = new MagicFastnessList();
                                    }
                                    suiteEData.fastnessList.reset(EMagic.FIRE, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("water");
                                if (data != null) {
                                    if (suiteEData.fastnessList == null) {
                                        suiteEData.fastnessList = new MagicFastnessList();
                                    }
                                    suiteEData.fastnessList.reset(EMagic.WATER, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("soil");
                                if (data != null) {
                                    if (suiteEData.fastnessList == null) {
                                        suiteEData.fastnessList = new MagicFastnessList();
                                    }
                                    suiteEData.fastnessList.reset(EMagic.SOIL, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("description");
                                if (data != null) {
                                    if (-1 != data.indexOf("\\n")) {
                                        data = data.replaceAll("\\\\n", "\n");
                                    }
                                    suiteEData.description = data;
                                } else {
                                    suiteEData.description = "";
                                }
                                this.table.put(suiteEData.id, suiteEData);
                            } catch (Exception e) {
                                LogWriter.println("\u52a0\u8f7d\u5957\u88c5\u88c5\u5907\u6570\u636e\u51fa\u9519");
                                LogWriter.error(null, e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e2) {
            LogWriter.error(this, e2);
        }
    }

    public SuiteEData getSuiteData(final short _suiteID) {
        return (SuiteEData) this.table.get(_suiteID);
    }

    public class SuiteEData {

        public short id;
        public String name;
        public int[] idList;
        public AccessorialOriginalAttribute atribute;
        public MagicFastnessList fastnessList;
        public String description;

        public SuiteEData() {
            this.idList = new int[4];
        }
    }
}
