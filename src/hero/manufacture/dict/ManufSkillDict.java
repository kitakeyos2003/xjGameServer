// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.dict;

import java.util.List;
import java.util.Iterator;
import org.dom4j.Document;
import hero.manufacture.Odd;
import java.util.ArrayList;
import hero.manufacture.JewelerCategory;
import hero.manufacture.PurifyCategory;
import hero.manufacture.GatherCategory;
import hero.manufacture.DispenserCategory;
import hero.manufacture.CraftsmanCategory;
import hero.manufacture.BlacksmithCategory;
import hero.manufacture.ManufactureType;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class ManufSkillDict {

    private static Logger log;
    private static ManufSkillDict instance;
    private HashMap<Integer, ManufSkill> list;
    private static final String DESC_1 = "\u53ef\u4ee5\u63d0\u5347";
    private static final String DESC_2 = "\u719f\u7ec3\u5ea6";
    private static final String DESC_3 = "\u70b9\n\u6bcf\u6b21\u53ef\u5236\u9020\u6570\u91cf\uff1a";
    private static final String DESC_4 = "\n";

    static {
        ManufSkillDict.log = Logger.getLogger((Class) ManufSkillDict.class);
    }

    private ManufSkillDict() {
        this.list = new HashMap<Integer, ManufSkill>();
    }

    public void loadManufSkills(final String[] paths) {
        for (byte i = 0; i < paths.length; ++i) {
            this.loadData(paths[i], (byte) (i + 1));
        }
    }

    private void loadData(final String path, final byte _type) {
        File dataPath;
        try {
            dataPath = new File(path);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + path);
            return;
        }
        try {
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, l = 0; l < length; ++l) {
                File dataFile = array[l];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            ManufSkill skill = new ManufSkill();
                            skill.id = Integer.parseInt(subE.elementTextTrim("id"));
                            skill.name = subE.elementTextTrim("name");
                            skill.type = _type;
                            skill.icon = Short.parseShort(subE.elementTextTrim("icon"));
                            skill.npcStudy = subE.elementTextTrim("npcStudy").equals("\u662f");
                            ManufactureType _mtype = ManufactureType.get(_type);
                            String category = subE.elementTextTrim("category");
                            ManufSkillDict.log.debug((Object) ("manufSkill id = " + skill.id + " -- category =" + category));
                            if (_mtype == ManufactureType.BLACKSMITH) {
                                skill.category = BlacksmithCategory.getCategory(category).getId();
                            } else if (_mtype == ManufactureType.CRAFTSMAN) {
                                skill.category = CraftsmanCategory.getCategory(category).getId();
                            } else if (_mtype == ManufactureType.DISPENSER) {
                                skill.category = DispenserCategory.getCategory(category).getId();
                            } else if (_mtype == ManufactureType.GRATHER) {
                                skill.category = GatherCategory.getGatherCategory(category).getId();
                            } else if (_mtype == ManufactureType.PURIFY) {
                                skill.category = PurifyCategory.getPurifyCategory(category).getId();
                            } else {
                                skill.category = JewelerCategory.getCategory(category).getId();
                            }
                            skill.needSkillPoint = Short.parseShort(subE.elementTextTrim("needSkillPoint"));
                            String needLvlDesc = subE.elementTextTrim("lvl");
                            if (needLvlDesc != null) {
                                skill.needLevel = Byte.parseByte(needLvlDesc);
                            }
                            String money = subE.elementTextTrim("money");
                            if (money != null && money.trim().length() > 0) {
                                skill.money = Integer.parseInt(money);
                            }
                            for (int i = 0; i < skill.stagesNeedPoint.length; ++i) {
                                String data = subE.elementTextTrim("stage" + (i + 1) + "NeedPoint");
                                if (data != null && data.trim().length() > 0) {
                                    skill.stagesNeedPoint[i] = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("stage" + (i + 1) + "GetPointOdd");
                                if (data != null && data.trim().length() > 0) {
                                    skill.stagesGetPointOdd[i] = Byte.parseByte(data);
                                }
                            }
                            String abruptID = subE.elementTextTrim("abruptID");
                            if (abruptID != null && abruptID.trim().length() > 0) {
                                skill.abruptID = Integer.parseInt(abruptID);
                            }
                            for (int j = 0; j < skill.needGoodsID.length; ++j) {
                                String idStr = subE.elementTextTrim("goods" + (j + 1) + "ID");
                                if (idStr != null && idStr.trim().length() > 0) {
                                    skill.needGoodsID[j] = Integer.parseInt(idStr);
                                }
                                String numStr = subE.elementTextTrim("goods" + (j + 1) + "Num");
                                if (numStr != null && numStr.trim().length() > 0) {
                                    skill.needGoodsNum[j] = Short.parseShort(numStr);
                                }
                            }
                            List<Odd> getGoodsOddList = new ArrayList<Odd>();
                            for (int k = 0; k < skill.getGoodsID.length; ++k) {
                                String idStr2 = subE.elementTextTrim("getGoods" + (k + 1) + "ID");
                                if (idStr2 != null && idStr2.trim().length() > 0) {
                                    skill.getGoodsID[k] = Integer.parseInt(idStr2);
                                }
                                String numStr2 = subE.elementTextTrim("getGoods" + (k + 1) + "Num");
                                if (numStr2 != null && numStr2.trim().length() > 0) {
                                    skill.getGoodsNum[k] = Short.parseShort(numStr2);
                                }
                                String oddStr = subE.elementTextTrim("getGoods" + (k + 1) + "Odd");
                                if (oddStr != null && oddStr.trim().length() > 0) {
                                    skill.getGoodsOdd[k] = Byte.parseByte(oddStr);
                                    getGoodsOddList.add(new Odd(skill.getGoodsOdd[k], (byte) k));
                                }
                            }
                            skill.setGetGoodsOddList(getGoodsOddList);
                            this.list.put(skill.id, skill);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte getNeedLvl(final String str) {
        if (str.equals("\u521d\u5b66")) {
            return 1;
        }
        if (str.equals("\u719f\u7ec3")) {
            return 2;
        }
        if (str.equals("\u7cbe\u901a")) {
            return 3;
        }
        if (str.equals("\u5927\u5e08")) {
            return 4;
        }
        return 5;
    }

    public static ManufSkillDict getInstance() {
        if (ManufSkillDict.instance == null) {
            ManufSkillDict.instance = new ManufSkillDict();
        }
        return ManufSkillDict.instance;
    }

    public ManufSkill getManufSkillByID(final int _manufID) {
        return this.list.get(_manufID);
    }

    public Iterator<ManufSkill> getManufSkills() {
        return this.list.values().iterator();
    }
}
