// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import java.util.Iterator;
import org.dom4j.Document;
import java.util.Vector;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class MonsterDataDict {

    private static Logger log;
    private FastMap<String, MonsterData> monsterDataDict;
    private static MonsterDataDict instance;

    static {
        MonsterDataDict.log = Logger.getLogger((Class) MonsterDataDict.class);
    }

    private MonsterDataDict() {
        this.monsterDataDict = (FastMap<String, MonsterData>) new FastMap();
    }

    public static MonsterDataDict getInstance() {
        if (MonsterDataDict.instance == null) {
            MonsterDataDict.instance = new MonsterDataDict();
        }
        return MonsterDataDict.instance;
    }

    public MonsterData getMonsterData(final String _monsterModelID) {
        return (MonsterData) this.monsterDataDict.get(_monsterModelID);
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
                            MonsterData monsterData = new MonsterData();
                            try {
                                monsterData.modelID = subE.elementTextTrim("id").toLowerCase();
                                MonsterDataDict.log.debug(("monsterData modelID = " + monsterData.modelID));
                                monsterData.name = subE.elementTextTrim("name");
                                monsterData.level = subE.elementTextTrim("level");
                                monsterData.clan = subE.elementTextTrim("clan");
                                monsterData.vocation = subE.elementTextTrim("vocation");
                                monsterData.type = subE.elementTextTrim("type");
                                monsterData.isActive = subE.elementTextTrim("activeOrPassive");
                                monsterData.existsTime = subE.elementTextTrim("existsTime");
                                monsterData.isInDungeon = subE.elementTextTrim("isInDungeon");
                                monsterData.normalOrBoss = subE.elementTextTrim("normalOrBoss");
                                monsterData.atkRange = subE.elementTextTrim("atkRange");
                                monsterData.immobilityTime = subE.elementTextTrim("immobilityTime");
                                monsterData.assistAttackRange = subE.elementTextTrim("assistAttackRange");
                                monsterData.assistPara = subE.elementTextTrim("assistPara");
                                monsterData.strength = subE.elementTextTrim("strength");
                                monsterData.agility = subE.elementTextTrim("agility");
                                monsterData.stamina = subE.elementTextTrim("stamina");
                                monsterData.inte = subE.elementTextTrim("inte");
                                monsterData.spirit = subE.elementTextTrim("spirit");
                                monsterData.lucky = subE.elementTextTrim("lucky");
                                monsterData.defense = subE.elementTextTrim("defense");
                                monsterData.minPhysicsAttack = subE.elementTextTrim("minPhysicsAttack");
                                monsterData.maxPhysicsAttack = subE.elementTextTrim("maxPhysicsAttack");
                                monsterData.sanctity = subE.elementTextTrim("sanctity");
                                monsterData.umbra = subE.elementTextTrim("umbra");
                                monsterData.fire = subE.elementTextTrim("fire");
                                monsterData.water = subE.elementTextTrim("water");
                                monsterData.soil = subE.elementTextTrim("soil");
                                monsterData.magicType = subE.elementTextTrim("magic");
                                monsterData.minDamageValue = subE.elementTextTrim("minDamageValue");
                                monsterData.maxDamageValue = subE.elementTextTrim("maxDamageValue");
                                monsterData.money = subE.elementTextTrim("money");
                                for (int i = 1; i <= 3; ++i) {
                                    String soulID = subE.elementTextTrim("soulID" + i);
                                    if (soulID == null) {
                                        break;
                                    }
                                    if (monsterData.soulIDList == null) {
                                        monsterData.soulIDList = new Vector<Integer>();
                                    }
                                    monsterData.soulIDList.add(Integer.parseInt(soulID));
                                }
                                monsterData.aiID = subE.elementTextTrim("aiID");
                                String legacyNumberStr = subE.elementTextTrim("legacyTypeNums");
                                if (legacyNumberStr != null) {
                                    monsterData.legacyTypeNums = legacyNumberStr;
                                    int legacyNumber = Integer.parseInt(legacyNumberStr);
                                    if (legacyNumber > 0 && legacyNumber <= 12) {
                                        legacyNumberStr = subE.elementTextTrim("legacyTypeSmallestNums");
                                        if (legacyNumberStr != null) {
                                            legacyNumber = Integer.parseInt(legacyNumberStr);
                                            if (legacyNumber < 0 || legacyNumber > Integer.parseInt(monsterData.legacyTypeNums)) {
                                                MonsterDataDict.log.error(("\u602a\u7269\u6389\u843d\u7269\u54c1\u6700\u5c11\u6570\u91cf\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + monsterData.modelID));
                                                continue;
                                            }
                                            monsterData.legacyTypeSmallestNums = legacyNumberStr;
                                        } else {
                                            monsterData.legacyTypeSmallestNums = "0";
                                        }
                                        String legacyTypeMostNums = subE.elementTextTrim("legacyTypeMostNums");
                                        if (legacyTypeMostNums != null) {
                                            legacyNumber = Integer.parseInt(legacyTypeMostNums);
                                            if (legacyNumber <= 0 || legacyNumber > Integer.parseInt(monsterData.legacyTypeNums)) {
                                                MonsterDataDict.log.error(("\u602a\u7269\u6389\u843d\u7269\u54c1\u6700\u591a\u6570\u91cf\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + monsterData.modelID));
                                                continue;
                                            }
                                            monsterData.legacyTypeMostNums = legacyTypeMostNums;
                                        } else {
                                            monsterData.legacyTypeMostNums = "0";
                                        }
                                    } else {
                                        monsterData.legacyTypeNums = null;
                                    }
                                }
                                monsterData.item1 = subE.elementTextTrim("item1");
                                if (monsterData.item1 != null) {
                                    monsterData.item1Odds = subE.elementTextTrim("item1Odds");
                                    monsterData.item1nums = subE.elementTextTrim("item1nums");
                                    monsterData.item2 = subE.elementTextTrim("item2");
                                    if (monsterData.item2 != null) {
                                        monsterData.item2Odds = subE.elementTextTrim("item2Odds");
                                        monsterData.item2nums = subE.elementTextTrim("item2nums");
                                        monsterData.item3 = subE.elementTextTrim("item3");
                                        if (monsterData.item3 != null) {
                                            monsterData.item3Odds = subE.elementTextTrim("item3Odds");
                                            monsterData.item3nums = subE.elementTextTrim("item3nums");
                                            monsterData.item4 = subE.elementTextTrim("item4");
                                            if (monsterData.item4 != null) {
                                                monsterData.item4Odds = subE.elementTextTrim("item4Odds");
                                                monsterData.item4nums = subE.elementTextTrim("item4nums");
                                                monsterData.item5 = subE.elementTextTrim("item5");
                                                if (monsterData.item5 != null) {
                                                    monsterData.item5Odds = subE.elementTextTrim("item5Odds");
                                                    monsterData.item5nums = subE.elementTextTrim("item5nums");
                                                    monsterData.item6 = subE.elementTextTrim("item6");
                                                    if (monsterData.item6 != null) {
                                                        monsterData.item6Odds = subE.elementTextTrim("item6Odds");
                                                        monsterData.item6nums = subE.elementTextTrim("item6nums");
                                                        monsterData.item7 = subE.elementTextTrim("item7");
                                                        if (monsterData.item7 != null) {
                                                            monsterData.item7Odds = subE.elementTextTrim("item7Odds");
                                                            monsterData.item7nums = subE.elementTextTrim("item7nums");
                                                            monsterData.item8 = subE.elementTextTrim("item8");
                                                            if (monsterData.item8 != null) {
                                                                monsterData.item8Odds = subE.elementTextTrim("item8Odds");
                                                                monsterData.item8nums = subE.elementTextTrim("item8nums");
                                                                monsterData.item9 = subE.elementTextTrim("item9");
                                                                if (monsterData.item9 != null) {
                                                                    monsterData.item9Odds = subE.elementTextTrim("item9Odds");
                                                                    monsterData.item9nums = subE.elementTextTrim("item9nums");
                                                                    monsterData.item10 = subE.elementTextTrim("item10");
                                                                    if (monsterData.item10 != null) {
                                                                        monsterData.item10Odds = subE.elementTextTrim("item10Odds");
                                                                        monsterData.item10nums = subE.elementTextTrim("item10nums");
                                                                        monsterData.item11 = subE.elementTextTrim("item11");
                                                                        if (monsterData.item11 != null) {
                                                                            monsterData.item11Odds = subE.elementTextTrim("item11Odds");
                                                                            monsterData.item11nums = subE.elementTextTrim("item11nums");
                                                                            monsterData.item12 = subE.elementTextTrim("item12");
                                                                            if (monsterData.item12 != null) {
                                                                                monsterData.item12Odds = subE.elementTextTrim("item12Odds");
                                                                                monsterData.item12nums = subE.elementTextTrim("item12nums");
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                monsterData.retime = subE.elementTextTrim("retime");
                                monsterData.imageID = subE.elementTextTrim("faceID");
                                monsterData.animationID = subE.elementTextTrim("animationID");
                                if (!this.monsterDataDict.containsKey(monsterData.modelID)) {
                                    this.monsterDataDict.put(monsterData.modelID, monsterData);
                                } else {
                                    MonsterDataDict.log.error(("\u91cd\u590d\u7684\u602a\u7269\u6570\u636e\uff0c\u7f16\u53f7:" + monsterData.modelID));
                                }
                            } catch (Exception e) {
                                MonsterDataDict.log.error(("\u52a0\u8f7d\u602a\u7269\u6570\u636e\u51fa\u9519\uff0c\u7f16\u53f7: " + monsterData.modelID), (Throwable) e);
                            }
                        }
                    }
                    MonsterDataDict.log.debug(("monsterdata dict size = " + this.monsterDataDict.size()));
                }
            }
        } catch (Exception e2) {
            MonsterDataDict.log.error("load monster error: ", (Throwable) e2);
        }
    }

    public static class MonsterData {

        public String modelID;
        public String name;
        public String level;
        public String clan;
        public String vocation;
        public String type;
        public String isActive;
        public String existsTime;
        public String normalOrBoss;
        public String isInDungeon;
        public String atkRange;
        public String immobilityTime;
        public String assistAttackRange;
        public String assistPara;
        public String strength;
        public String agility;
        public String stamina;
        public String inte;
        public String spirit;
        public String lucky;
        public String defense;
        public String minPhysicsAttack;
        public String maxPhysicsAttack;
        public String sanctity;
        public String umbra;
        public String fire;
        public String water;
        public String soil;
        public String magicType;
        public String minDamageValue;
        public String maxDamageValue;
        public String money;
        public String aiID;
        public String legacyTypeNums;
        public String legacyTypeSmallestNums;
        public String legacyTypeMostNums;
        public String retime;
        public String imageID;
        public String animationID;
        public String item1;
        public String item1Odds;
        public String item1nums;
        public String item2;
        public String item2Odds;
        public String item2nums;
        public String item3;
        public String item3Odds;
        public String item3nums;
        public String item4;
        public String item4Odds;
        public String item4nums;
        public String item5;
        public String item5Odds;
        public String item5nums;
        public String item6;
        public String item6Odds;
        public String item6nums;
        public String item7;
        public String item7Odds;
        public String item7nums;
        public String item8;
        public String item8Odds;
        public String item8nums;
        public String item9;
        public String item9Odds;
        public String item9nums;
        public String item10;
        public String item10Odds;
        public String item10nums;
        public String item11;
        public String item11Odds;
        public String item11nums;
        public String item12;
        public String item12Odds;
        public String item12nums;
        public Vector<Integer> soulIDList;
    }
}
