// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.dict;

import java.util.Iterator;
import org.dom4j.Document;
import hero.effect.dictionry.EffectDictionary;
import hero.skill.unit.SkillUnit;
import hero.skill.unit.ActiveSkillUnit;
import hero.skill.ActiveSkill;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.skill.service.SkillConfig;
import hero.skill.Skill;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class MonsterSkillDict {

    private static Logger log;
    private FastMap<Integer, Skill> skillUnitTable;
    private static MonsterSkillDict instance;

    static {
        MonsterSkillDict.log = Logger.getLogger((Class) MonsterSkillDict.class);
    }

    private MonsterSkillDict() {
        this.skillUnitTable = (FastMap<Integer, Skill>) new FastMap();
    }

    public static MonsterSkillDict getInstance() {
        if (MonsterSkillDict.instance == null) {
            MonsterSkillDict.instance = new MonsterSkillDict();
        }
        return MonsterSkillDict.instance;
    }

    public Skill getSkillUnitRef(final int _skillUnitID) {
        if (_skillUnitID > 0) {
            return (Skill) this.skillUnitTable.get(_skillUnitID);
        }
        return null;
    }

    public Skill getSkillUnitInstance(final int _skillUnitID) {
        if (_skillUnitID > 0) {
            try {
                return ((Skill) this.skillUnitTable.get(_skillUnitID)).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void load(final SkillConfig _config) {
        try {
            File dataPath = new File(_config.monster_skill_data_path);
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, i = 0; i < length; ++i) {
                File dataFile = array[i];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            Skill skill = new ActiveSkill(Integer.parseInt(subE.elementTextTrim("skillID")), subE.elementTextTrim("name"));
                            int skillID = 0;
                            try {
                                skillID = Integer.parseInt(subE.elementTextTrim("effectIdx"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                break;
                            }
                            SkillUnit skillUnit = SkillUnitDict.getInstance().getSkillUnitInstance(skillID);
                            if (skillUnit == null) {
                                MonsterSkillDict.log.info(("\u602a\u7269\u6280\u80fd\u4e3aNUL,skillID:" + skillID));
                                break;
                            }
                            if (skillUnit instanceof ActiveSkillUnit) {
                                skill.setSkillUnit(skillUnit);
                            } else {
                                MonsterSkillDict.log.info("\u602a\u7269\u65e0\u6cd5\u52a0\u8f7d\u975e\u4e3b\u52a8\u6280\u80fd");
                            }
                            if (skillUnit.additionalSEID > 0) {
                                if (skillUnit.additionalSEID > 100000) {
                                    skill.addEffectUnit = EffectDictionary.getInstance().getEffectRef(skillUnit.additionalSEID);
                                } else {
                                    skill.addSkillUnit = SkillUnitDict.getInstance().getSkillUnitRef(skillUnit.additionalSEID);
                                }
                            }
                            this.skillUnitTable.put(skill.id, skill);
                            MonsterSkillDict.log.info(("\u52a0\u8f7d\u602a\u7269\u6280\u80fd\u6210\u529f:" + skill.id + ";" + skill.name));
                        }
                    }
                }
            }
        } catch (Exception e2) {
            MonsterSkillDict.log.info("Error:\u52a0\u8f7d\u602a\u7269\u6280\u80fd\u5931\u8d25");
            e2.printStackTrace();
        }
    }
}
