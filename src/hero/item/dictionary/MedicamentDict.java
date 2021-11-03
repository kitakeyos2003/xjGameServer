// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import java.util.Iterator;
import org.dom4j.Document;
import hero.share.service.LogWriter;
import hero.item.detail.EGoodsTrait;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.item.detail.AdditionEffect;
import java.util.ArrayList;
import java.io.File;
import hero.item.Medicament;
import javolution.util.FastMap;

public class MedicamentDict {

    private static MedicamentDict instance;
    private FastMap<Integer, Medicament> dictionary;

    private MedicamentDict() {
        this.dictionary = (FastMap<Integer, Medicament>) new FastMap();
    }

    public static MedicamentDict getInstance() {
        if (MedicamentDict.instance == null) {
            MedicamentDict.instance = new MedicamentDict();
        }
        return MedicamentDict.instance;
    }

    public Object[] getMedicamentList() {
        return this.dictionary.values().toArray();
    }

    public Medicament getMedicament(final int _medicamentID) {
        return (Medicament) this.dictionary.get(_medicamentID);
    }

    public void load(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            ArrayList<AdditionEffect> additionEffectList = new ArrayList<AdditionEffect>();
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
                            String data = null;
                            additionEffectList.clear();
                            Medicament medicament = new Medicament(Short.parseShort(subE.elementTextTrim("stackNums")));
                            try {
                                if ((data = subE.elementTextTrim("id")) != null) {
                                    medicament.setID(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("name")) != null) {
                                    medicament.setName(data);
                                }
                                if ((data = subE.elementTextTrim("trait")) != null) {
                                    medicament.setTrait(EGoodsTrait.getTrait(data));
                                }
                                if ((data = subE.elementTextTrim("needLevel")) != null) {
                                    medicament.setNeedLevel(Byte.parseByte(data));
                                }
                                if ((data = subE.elementTextTrim("canUseInFight")) != null) {
                                    if (data.equals("\u65e0\u9650\u5236")) {
                                        medicament.setCanUseInFight(true);
                                    } else if (data.equals("\u975e\u6218\u6597")) {
                                        medicament.setCanUseInFight(false);
                                    }
                                }
                                if ((data = subE.elementTextTrim("isDisappearAfterDead")) != null) {
                                    if (data.equals("\u6d88\u5931")) {
                                        medicament.setIsDisappearAfterDead(true);
                                    } else if (data.equals("\u4e0d\u6d88\u5931")) {
                                        medicament.setIsDisappearAfterDead(false);
                                    }
                                }
                                if ((data = subE.elementTextTrim("exchangeable")).equals("\u53ef\u4ea4\u6613")) {
                                    medicament.setExchangeable();
                                }
                                if ((data = subE.elementTextTrim("price")) != null) {
                                    medicament.setPrice(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("delayType")) != null) {
                                    medicament.setPublicCdVariable(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("delayTime")) != null) {
                                    medicament.setMaxCdTime(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("hp_resume")) != null) {
                                    medicament.setResumeHp(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("mp_resume")) != null) {
                                    medicament.setResumeMp(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("qi_resume")) != null) {
                                    medicament.setResumeGasQuantity(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("li_resume")) != null) {
                                    medicament.setResumeForceQuantity(Integer.parseInt(data));
                                }
                                AdditionEffect additionSkill = null;
                                data = subE.elementTextTrim("add_id1");
                                if (data != null) {
                                    additionSkill = new AdditionEffect();
                                    additionSkill.effectUnitID = Integer.parseInt(data);
                                    additionSkill.activeTimes = Byte.parseByte(subE.elementTextTrim("add_count1"));
                                    additionSkill.activeOdds = Byte.parseByte(subE.elementTextTrim("add_percent1")) / 100.0f;
                                    additionEffectList.add(additionSkill);
                                }
                                data = subE.elementTextTrim("add_id2");
                                if (data != null) {
                                    additionSkill = new AdditionEffect();
                                    additionSkill.effectUnitID = Integer.parseInt(data);
                                    additionSkill.activeTimes = Byte.parseByte(subE.elementTextTrim("add_count2"));
                                    additionSkill.activeOdds = Byte.parseByte(subE.elementTextTrim("add_percent2")) / 100.0f;
                                    additionEffectList.add(additionSkill);
                                }
                                data = subE.elementTextTrim("add_id3");
                                if (data != null) {
                                    additionSkill = new AdditionEffect();
                                    additionSkill.effectUnitID = Integer.parseInt(data);
                                    additionSkill.activeTimes = Byte.parseByte(subE.elementTextTrim("add_count3"));
                                    additionSkill.activeOdds = Byte.parseByte(subE.elementTextTrim("add_percent3")) / 100.0f;
                                    additionEffectList.add(additionSkill);
                                }
                                if (additionEffectList.size() > 0) {
                                    medicament.additionEffectList = new AdditionEffect[additionEffectList.size()];
                                    for (int i = 0; i < additionEffectList.size(); ++i) {
                                        medicament.additionEffectList[i] = additionEffectList.get(i);
                                    }
                                }
                                if ((data = subE.elementTextTrim("icon")) != null) {
                                    medicament.setIconID(Short.parseShort(data));
                                }
                                if ((data = subE.elementTextTrim("fireEffect")) != null) {
                                    medicament.setReleaseAnimation(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("fireEffectImage");
                                if (data != null) {
                                    medicament.setReleaseImage(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("description")) != null) {
                                    if (-1 != data.indexOf("\\n")) {
                                        data = data.replaceAll("\\\\n", "\n");
                                    }
                                    medicament.appendDescription(data);
                                }
                                this.dictionary.put(medicament.getID(), medicament);
                            } catch (Exception ex) {
                                LogWriter.println("\u52a0\u8f7d\u836f\u6c34\u6570\u636e\u51fa\u9519\uff0c\u7f16\u53f7:" + medicament.getID());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }
}
