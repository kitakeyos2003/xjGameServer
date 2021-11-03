// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.dict;

import org.dom4j.Document;
import hero.skill.detail.ETargetType;
import hero.effect.dictionry.EffectDictionary;
import hero.item.Weapon;
import hero.skill.unit.PassiveSkillUnit;
import hero.skill.PassiveSkill;
import hero.skill.unit.SkillUnit;
import hero.skill.ActiveSkill;
import hero.skill.unit.ActiveSkillUnit;
import org.dom4j.Element;
import hero.skill.detail.AdditionalActionUnit;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.skill.service.SkillConfig;
import hero.share.ESystemFeature;
import java.util.Iterator;
import java.util.ArrayList;
import hero.share.EVocation;
import hero.skill.Skill;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class SkillDict {

    private static Logger log;
    private FastMap<Integer, Skill> skillTable;
    private static SkillDict instance;

    static {
        SkillDict.log = Logger.getLogger((Class) SkillDict.class);
    }

    private SkillDict() {
        this.skillTable = (FastMap<Integer, Skill>) new FastMap();
    }

    public static SkillDict getInstance() {
        if (SkillDict.instance == null) {
            SkillDict.instance = new SkillDict();
        }
        return SkillDict.instance;
    }

    public Skill getSkill(final int _skillID) {
        return (Skill) this.skillTable.get(_skillID);
    }

    public ArrayList<Skill> getChangeVocationSkills(final EVocation _educateVocation) {
        Iterator<Skill> iterator = this.skillTable.values().iterator();
        ArrayList<Skill> list = new ArrayList<Skill>();
        while (iterator.hasNext()) {
            Skill skill = iterator.next();
            if (skill.level == 0) {
                for (int i = 0; i < skill.learnerVocation.length; ++i) {
                    if (skill.learnerVocation[i] == _educateVocation) {
                        list.add(skill);
                        break;
                    }
                }
            }
        }
        return list;
    }

    public ArrayList<Skill> getSkillsByVocation(final EVocation _educateVocation) {
        Iterator<Skill> iterator = this.skillTable.values().iterator();
        ArrayList<Skill> list = new ArrayList<Skill>();
        while (iterator.hasNext()) {
            Skill skill = iterator.next();
            if (skill.level == 0) {
                for (int i = 0; i < skill.learnerVocation.length; ++i) {
                    EVocation vocation = skill.learnerVocation[i];
                    if ((vocation == _educateVocation || _educateVocation.baseIs(skill.learnerVocation[i])) && !skill.getFromSkillBook) {
                        list.add(skill);
                        break;
                    }
                }
            }
        }
        return list;
    }

    public ArrayList<Skill> getSkillList(final EVocation _educateVocation, final ESystemFeature _feature) {
        Iterator<Skill> iterator = this.skillTable.values().iterator();
        ArrayList<Skill> list = new ArrayList<Skill>();
        while (iterator.hasNext()) {
            Skill skill = iterator.next();
            for (int i = 0; i < skill.learnerVocation.length; ++i) {
                if ((skill.learnerVocation[i] == _educateVocation || _educateVocation.baseIs(skill.learnerVocation[i])) && !skill.getFromSkillBook) {
                    list.add(skill);
                    break;
                }
            }
        }
        return list;
    }

    public void load(final SkillConfig _config) {
        this.skillTable.clear();
        FastMap<String, ArrayList<Skill>> heroFeatureSkill = (FastMap<String, ArrayList<Skill>>) new FastMap();
        FastMap<String, ArrayList<Skill>> reverFeatureSkill = (FastMap<String, ArrayList<Skill>>) new FastMap();
        FastMap<String, ArrayList<Skill>> noneFeatureSkill = (FastMap<String, ArrayList<Skill>>) new FastMap();
        ArrayList<Skill> skillList = null;
        try {
            File dataPath = new File(_config.player_skill_data_path);
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, j = 0; j < length; ++j) {
                File dataFile = array[j];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    ArrayList<AdditionalActionUnit> additionalOddsActionUnitList = new ArrayList<AdditionalActionUnit>();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        additionalOddsActionUnitList.clear();
                        if (subE != null) {
                            String data = subE.elementTextTrim("skillUnitDataID");
                            SkillUnit skillUnit;
                            try {
                                skillUnit = SkillUnitDict.getInstance().getSkillUnitInstance(Integer.parseInt(data));
                            } catch (Exception e2) {
                                skillUnit = null;
                                SkillDict.log.info(("***\u83b7\u53d6skillUnitDataID\u5931\u8d25:" + data));
                                continue;
                            }
                            Skill skill;
                            if (skillUnit instanceof ActiveSkillUnit) {
                                skill = new ActiveSkill(Integer.parseInt(subE.elementTextTrim("id")), subE.elementTextTrim("name"));
                                skill.setSkillUnit(skillUnit);
                            } else {
                                skill = new PassiveSkill(Integer.parseInt(subE.elementTextTrim("id")), subE.elementTextTrim("name"));
                                skill.setSkillUnit(skillUnit);
                            }
                            skill.level = Short.parseShort(subE.elementTextTrim("level"));
                            data = subE.elementTextTrim("feature");
                            if (data != null) {
                                skill.getFromSkillBook = data.equals("\u662f");
                            }
                            data = subE.elementTextTrim("learnerLevel");
                            if (data != null) {
                                skill.learnerLevel = Short.parseShort(data);
                            }
                            skill.skillRank = Byte.valueOf(subE.elementTextTrim("skillRank"));
                            String[] vocations = subE.elementTextTrim("learnerVocation").replace('\uff0c', ',').replace('\u3001', ',').split(",");
                            skill.learnerVocation = new EVocation[vocations.length];
                            for (int i = 0; i < vocations.length; ++i) {
                                skill.learnerVocation[i] = EVocation.getVocationByDesc(vocations[i]);
                            }
                            data = subE.elementTextTrim("needMoney");
                            if (data != null) {
                                skill.learnFreight = Integer.parseInt(data);
                            }
                            if (skill instanceof ActiveSkill) {
                                data = subE.elementTextTrim("canUseInFight");
                                if (data != null && data.equals("\u975e\u6218\u6597")) {
                                    ((ActiveSkill) skill).onlyNotFightingStatus = true;
                                }
                            }
                            data = subE.elementTextTrim("needWeaponType");
                            if (data != null) {
                                skill.needWeaponType = Weapon.EWeaponType.getTypes(data.replace('\uff0c', ',').replace('\u3001', ',').split(","));
                            }
                            data = subE.elementTextTrim("needSkillPoint");
                            if (data != null) {
                                skill.skillPoints = Short.parseShort(data);
                            }
                            skill.iconID = Short.parseShort(subE.elementTextTrim("icon"));
                            if (skillUnit.additionalSEID > 0) {
                                if (skillUnit.additionalSEID > 100000) {
                                    skill.addEffectUnit = EffectDictionary.getInstance().getEffectRef(skillUnit.additionalSEID);
                                } else {
                                    skill.addSkillUnit = SkillUnitDict.getInstance().getSkillUnitRef(skillUnit.additionalSEID);
                                }
                            }
                            data = subE.elementTextTrim("maxLevel");
                            if (data != null) {
                                skill.maxLevel = Short.valueOf(data);
                            }
                            if (skill instanceof ActiveSkill) {
                                data = subE.elementTextTrim("coolDownID");
                                if (data != null) {
                                    ((ActiveSkill) skill).coolDownID = Integer.parseInt(data);
                                    data = subE.elementTextTrim("coolDownTime");
                                    if (data != null) {
                                        ((ActiveSkill) skill).coolDownTime = Integer.parseInt(subE.elementTextTrim("coolDownTime"));
                                    } else {
                                        SkillDict.log.warn(("\u8be5\u6280\u80fdCD\u65f6\u95f4\u4e0d\u80fd\u4e3aNULL,\u8bf7\u68c0\u67e5\u73a9\u5bb6\u6280\u80fd\u8868. skillid=" + String.valueOf(skill.id)));
                                    }
                                }
                                data = subE.elementTextTrim("hpConditionTargetType");
                                if (data != null) {
                                    ((ActiveSkill) skill).hpConditionTargetType = ETargetType.get(data);
                                    data = subE.elementTextTrim("compareLineType");
                                    if (data.equals("\u5927\u4e8e\u7b49\u4e8e")) {
                                        ((ActiveSkill) skill).hpConditionCompareLine = 1;
                                    } else {
                                        ((ActiveSkill) skill).hpConditionCompareLine = -1;
                                    }
                                    ((ActiveSkill) skill).hpConditionPercent = Float.parseFloat(subE.elementTextTrim("hpPercent")) / 100.0f;
                                }
                                ((ActiveSkill) skill).releaserExistsEffectName = subE.elementTextTrim("selfExistsEffect");
                                ((ActiveSkill) skill).releaserUnexistsEffectName = subE.elementTextTrim("selfUnexistsEffect");
                                ((ActiveSkill) skill).targetUnexistsEffectName = subE.elementTextTrim("targetExistsEffect");
                                ((ActiveSkill) skill).targetUnexistsEffectName = subE.elementTextTrim("targetUnexistsEffect");
                                StringBuffer desc = new StringBuffer();
                                if (skill.level == 0) {
                                    desc.append("\u6280\u80fd\u7b49\u7ea7:\u672a\u5b66\u4e60").append("#HH");
                                    desc.append("\u5347\u7ea7\u6240\u9700\u6280\u80fd\u70b9:").append(skill.skillPoints).append("#HH");
                                    desc.append("\u5347\u7ea7\u6240\u9700\u91d1\u94b1:").append(skill.learnFreight);
                                } else {
                                    String descTemp = null;
                                    if (skill.maxLevel == skill.level) {
                                        desc.append("\u6280\u80fd\u7b49\u7ea7:").append(skill.level).append("(\u5df2\u8fbe\u6700\u9ad8\u7b49\u7ea7)#HH");
                                    } else {
                                        desc.append("\u6280\u80fd\u7b49\u7ea7:").append(skill.level).append("#HH");
                                    }
                                    descTemp = subE.elementTextTrim("description_1");
                                    desc.append("\u6280\u80fd\u76ee\u6807:").append((descTemp == null) ? "" : descTemp).append("#HH");
                                    desc.append("\u6280\u80fd\u51b7\u5374:").append(((ActiveSkill) skill).coolDownTime).append("#HH");
                                    descTemp = subE.elementTextTrim("description_2");
                                    desc.append("\u6280\u80fd\u8ddd\u79bb:").append((descTemp == null) ? "" : descTemp).append("#HH");
                                    descTemp = subE.elementTextTrim("description_3");
                                    desc.append("\u65bd\u6cd5\u65f6\u95f4:").append((descTemp == null) ? "" : descTemp).append("#HH");
                                    descTemp = subE.elementTextTrim("description_4");
                                    desc.append("\u6d88\u8017\u6cd5\u529b:").append((descTemp == null) ? "" : descTemp).append("#HH");
                                    descTemp = subE.elementTextTrim("description_5");
                                    desc.append("\u6280\u80fd\u63cf\u8ff0:").append((descTemp == null) ? "" : descTemp).append("#HH");
                                    desc.append("\u6240\u9700\u6b66\u5668:");
                                    if (skill.needWeaponType != null) {
                                        for (final Weapon.EWeaponType wtype : skill.needWeaponType) {
                                            desc.append(wtype.getDesc()).append(",");
                                        }
                                        desc.deleteCharAt(desc.length() - 1);
                                    }
                                    desc.append("#HH");
                                    desc.append("\u5347\u7ea7\u6240\u9700\u6280\u80fd\u70b9:").append(skill.skillPoints).append("#HH");
                                    desc.append("\u5347\u7ea7\u6240\u9700\u91d1\u94b1:").append(skill.learnFreight);
                                }
                                skill.description = desc.toString();
                            } else {
                                StringBuffer desc = new StringBuffer();
                                if (skill.level == 0) {
                                    desc.append("\u6280\u80fd\u7b49\u7ea7:\u672a\u5b66\u4e60").append("#HH");
                                    desc.append("\u5347\u7ea7\u6240\u9700\u6280\u80fd\u70b9:").append(skill.skillPoints).append("#HH");
                                    desc.append("\u5347\u7ea7\u6240\u9700\u91d1\u94b1:").append(skill.learnFreight).append("#HH");
                                } else {
                                    String descTemp = null;
                                    if (skill.maxLevel == skill.level) {
                                        desc.append("\u6280\u80fd\u7b49\u7ea7:").append(skill.level).append("(\u5df2\u8fbe\u6700\u9ad8\u7b49\u7ea7)#HH");
                                    } else {
                                        desc.append("\u6280\u80fd\u7b49\u7ea7:").append(skill.level).append("#HH");
                                    }
                                    desc.append("\u6240\u9700\u6b66\u5668:");
                                    if (skill.needWeaponType != null) {
                                        for (final Weapon.EWeaponType wtype : skill.needWeaponType) {
                                            desc.append(wtype.getDesc()).append(",");
                                        }
                                        desc.deleteCharAt(desc.length() - 1);
                                    }
                                    desc.append("#HH");
                                    descTemp = subE.elementTextTrim("description_5");
                                    desc.append("\u6280\u80fd\u63cf\u8ff0:").append((descTemp == null) ? "" : descTemp).append("#HH");
                                    desc.append("\u5347\u7ea7\u6240\u9700\u6280\u80fd\u70b9:").append(skill.skillPoints).append("#HH");
                                    desc.append("\u5347\u7ea7\u6240\u9700\u91d1\u94b1:").append(skill.learnFreight).append("#HH");
                                }
                                skill.description = desc.toString();
                            }
                            this.skillTable.put(skill.id, skill);
                            skillList = (ArrayList<Skill>) noneFeatureSkill.get(skill.name);
                            if (skillList == null) {
                                skillList = new ArrayList<Skill>();
                                noneFeatureSkill.put(skill.name, skillList);
                            }
                            skillList.add(skill);
                        }
                    }
                }
            }
            Iterator<ArrayList<Skill>> iterator = heroFeatureSkill.values().iterator();
            while (iterator.hasNext()) {
                skillList = iterator.next();
                if (skillList.size() > 1) {
                    for (final Skill skill2 : skillList) {
                        for (final Skill skill3 : skillList) {
                            if (skill2.level + 1 == skill3.level) {
                                skill2.next = skill3;
                                skill3.prev = skill2;
                                break;
                            }
                        }
                    }
                }
            }
            iterator = reverFeatureSkill.values().iterator();
            while (iterator.hasNext()) {
                skillList = iterator.next();
                if (skillList.size() > 1) {
                    for (final Skill skill2 : skillList) {
                        for (final Skill skill3 : skillList) {
                            if (skill2.level + 1 == skill3.level) {
                                skill2.next = skill3;
                                skill3.prev = skill2;
                                break;
                            }
                        }
                    }
                }
            }
            iterator = noneFeatureSkill.values().iterator();
            while (iterator.hasNext()) {
                skillList = iterator.next();
                if (skillList.size() > 1) {
                    for (final Skill skill2 : skillList) {
                        for (final Skill skill3 : skillList) {
                            if (skill2.level + 1 == skill3.level) {
                                skill2.next = skill3;
                                skill3.prev = skill2;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
