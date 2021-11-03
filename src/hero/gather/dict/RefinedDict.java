// 
// Decompiled by Procyon v0.5.36
// 
package hero.gather.dict;

import java.util.Iterator;
import org.dom4j.Document;
import hero.gather.RefinedCategory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import java.util.HashMap;

public class RefinedDict {

    private static RefinedDict instance;
    private HashMap<Integer, Refined> list;
    private static final String DESC_1 = "\u53ef\u4ee5\u63d0\u5347";
    private static final String DESC_2 = "\u6280\u80fd";
    private static final String DESC_3 = "\u70b9\n\u6bcf\u6b21\u53ef\u5236\u9020\u6570\u91cf\uff1a";

    private RefinedDict() {
        this.list = new HashMap<Integer, Refined>();
    }

    public void loadRefineds(final String path) {
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
                            Refined skill = new Refined();
                            skill.id = Integer.parseInt(subE.elementTextTrim("id"));
                            skill.name = subE.elementTextTrim("name");
                            skill.icon = Short.parseShort(subE.elementTextTrim("icon"));
                            skill.npcStudy = subE.elementTextTrim("npcStudy").equals("\u662f");
                            skill.category = RefinedCategory.getCategory(subE.elementTextTrim("category")).getId();
                            String needLvlDesc = subE.elementTextTrim("needLvl");
                            skill.needLvl = this.getNeedLvl(needLvlDesc);
                            skill.point = Integer.parseInt(subE.elementTextTrim("point"));
                            skill.money = Integer.parseInt(subE.elementTextTrim("money"));
                            String abruptID = subE.elementTextTrim("abruptID");
                            if (abruptID != null && !abruptID.equals("")) {
                                skill.abruptID = Integer.parseInt(abruptID);
                            }
                            for (int i = 0; i < skill.needSoulID.length; ++i) {
                                String idStr = subE.elementTextTrim("goods" + (i + 1) + "ID");
                                if (idStr != null && !idStr.equals("")) {
                                    skill.needSoulID[i] = Integer.parseInt(idStr);
                                }
                                String numStr = subE.elementTextTrim("goods" + (i + 1) + "Num");
                                if (numStr != null && !numStr.equals("")) {
                                    skill.needSoulNum[i] = Short.parseShort(numStr);
                                }
                            }
                            for (int i = 0; i < skill.getGoodsID.length; ++i) {
                                String idStr = subE.elementTextTrim("getGoods" + (i + 1) + "ID");
                                if (idStr != null && !idStr.equals("")) {
                                    skill.getGoodsID[i] = Integer.parseInt(idStr);
                                }
                                String numStr = subE.elementTextTrim("getGoods" + (i + 1) + "Num");
                                if (numStr != null && !numStr.equals("")) {
                                    skill.getGoodsNum[i] = Short.parseShort(numStr);
                                }
                            }
                            String needGourd = subE.elementTextTrim("needGourd");
                            if (needGourd != null && !needGourd.equals("")) {
                                skill.needGourd = Integer.parseInt(needGourd);
                            }
                            skill.desc = "\u53ef\u4ee5\u63d0\u5347" + needLvlDesc + "\u6280\u80fd" + skill.point + "\u70b9\n\u6bcf\u6b21\u53ef\u5236\u9020\u6570\u91cf\uff1a" + skill.getGoodsNum[1];
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

    public static RefinedDict getInstance() {
        if (RefinedDict.instance == null) {
            RefinedDict.instance = new RefinedDict();
        }
        return RefinedDict.instance;
    }

    public Refined getRefinedByID(final int _refinedID) {
        return this.list.get(_refinedID);
    }

    public Iterator<Refined> getRefineds() {
        return this.list.values().iterator();
    }
}
