// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.dict;

import hero.skill.detail.ESpecialStatus;
import hero.skill.unit.ChangePropertyUnit;
import java.util.regex.Pattern;
import hero.skill.detail.EMathCaluOperator;
import hero.share.service.LogWriter;
import hero.skill.unit.EnhanceSkillUnit;
import hero.skill.unit.TouchUnit;
import java.util.Iterator;
import org.dom4j.Document;
import hero.skill.detail.ETouchType;
import hero.effect.Effect;
import hero.share.EMagic;
import hero.skill.detail.EHarmType;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.EActiveSkillType;
import hero.skill.unit.ActiveSkillUnit;
import org.dom4j.Element;
import hero.skill.detail.AdditionalActionUnit;
import java.util.ArrayList;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.skill.service.SkillConfig;
import hero.skill.unit.SkillUnit;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class SkillUnitDict {

    private static Logger log;
    private FastMap<Integer, SkillUnit> skillUnitTable;
    private static SkillUnitDict instance;

    static {
        SkillUnitDict.log = Logger.getLogger((Class) SkillUnitDict.class);
    }

    private SkillUnitDict() {
        this.skillUnitTable = (FastMap<Integer, SkillUnit>) new FastMap();
    }

    public static SkillUnitDict getInstance() {
        if (SkillUnitDict.instance == null) {
            SkillUnitDict.instance = new SkillUnitDict();
        }
        return SkillUnitDict.instance;
    }

    public SkillUnit getSkillUnitRef(final int _skillUnitID) {
        if (_skillUnitID > 0) {
            return (SkillUnit) this.skillUnitTable.get(_skillUnitID);
        }
        return null;
    }

    public SkillUnit getSkillUnitInstance(final int _skillUnitID) {
        SkillUnit skillUnit = null;
        if (_skillUnitID > 0) {
            try {
                skillUnit = (SkillUnit) this.skillUnitTable.get(_skillUnitID);
                if (skillUnit != null) {
                    skillUnit = skillUnit.clone();
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return skillUnit;
    }

    public void load(final SkillConfig _config) {
        this.loadActiveSkillUnit(_config.active_skill_data_path);
        this.loadTouchPassiveSkillUnit(_config.touch_skill_data_path);
        this.loadEnhanceSkillUnit(_config.enhance_skill_data_path);
        this.loadPropertySkillUnit(_config.change_property_skill_data_path);
    }

    public static void main(final String[] args) {
        getInstance();
        SkillUnitDict.instance.loadPropertySkillUnit(String.valueOf(System.getProperty("user.dir")) + "/res/data/skill/unit/property");
    }

    public void loadActiveSkillUnit(final String _dataPath) {
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
                    ArrayList<AdditionalActionUnit> additionalOddsActionUnitList = new ArrayList<AdditionalActionUnit>();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        additionalOddsActionUnitList.clear();
                        if (subE != null) {
                            ActiveSkillUnit skillUnit = new ActiveSkillUnit(Integer.parseInt(subE.elementTextTrim("id")));
                            String data = subE.elementTextTrim("name");
                            if (data == null) {
                                SkillUnitDict.log.info(("\u6ca1\u6709\u540d\u5b57\uff1a" + skillUnit.id));
                                break;
                            }
                            skillUnit.name = data;
                            skillUnit.activeSkillType = EActiveSkillType.getType(subE.elementTextTrim("type"));
                            if (skillUnit.activeSkillType == null) {
                                SkillUnitDict.log.info(("\u6280\u80fd\u7c7b\u578b\u6ca1\u6709\uff1a" + skillUnit.id));
                                break;
                            }
                            data = subE.elementTextTrim("consumeType");
                            if (data != null) {
                                if (data.equals("\u529b\u69fd")) {
                                    skillUnit.consumeFp = Short.parseShort(subE.elementTextTrim("consumeValue"));
                                } else if (data.equals("\u6c14\u69fd")) {
                                    skillUnit.consumeGp = Short.parseShort(subE.elementTextTrim("consumeValue"));
                                } else if (data.equals("\u9b54\u6cd5\u69fd")) {
                                    skillUnit.consumeMp = Integer.parseInt(subE.elementTextTrim("consumeValue"));
                                }
                            }
                            data = subE.elementTextTrim("consumeHp");
                            if (data != null) {
                                skillUnit.consumeHp = Short.parseShort(data);
                            }
                            data = subE.elementTextTrim("targetType");
                            skillUnit.targetType = ETargetType.get(data);
                            if (skillUnit.targetType == null) {
                                SkillUnitDict.log.info(("\u6ca1\u6709\u76ee\u6807\u7c7b\u578b\uff1a" + skillUnit.id));
                                break;
                            }
                            data = subE.elementTextTrim("range");
                            skillUnit.targetRangeType = ETargetRangeType.get(data);
                            if (skillUnit.targetRangeType == ETargetRangeType.TEAM || skillUnit.targetRangeType == ETargetRangeType.SOME) {
                                skillUnit.rangeTargetNumber = Byte.parseByte(subE.elementTextTrim("rangeTargetNumber"));
                                skillUnit.rangeBaseLine = EAOERangeBaseLine.get(subE.elementTextTrim("rangeBase"));
                                skillUnit.rangeMode = EAOERangeType.get(subE.elementTextTrim("rangeMode"));
                                skillUnit.rangeX = Byte.parseByte(subE.elementTextTrim("rangeX"));
                                if (skillUnit.rangeMode == EAOERangeType.FRONT_RECT) {
                                    skillUnit.rangeY = Byte.parseByte(subE.elementTextTrim("rangeY"));
                                }
                                data = subE.elementTextTrim("animationModel");
                                if (data != null) {
                                    skillUnit.animationModel = Byte.parseByte(data);
                                }
                                data = subE.elementTextTrim("average");
                                if (data != null) {
                                    skillUnit.isAverageResult = data.equals("\u662f");
                                }
                            }
                            data = subE.elementTextTrim("releaseTime");
                            if (data != null) {
                                skillUnit.releaseTime = Float.parseFloat(data);
                            }
                            if (skillUnit.targetType != ETargetType.MYSELF && (skillUnit.targetRangeType == ETargetRangeType.SINGLE || skillUnit.rangeBaseLine == EAOERangeBaseLine.TARGET)) {
                                data = subE.elementTextTrim("distance");
                                if (data != null) {
                                    skillUnit.targetDistance = Byte.parseByte(data);
                                } else {
                                    skillUnit.targetDistance = 1;
                                    SkillUnitDict.log.warn(("\u6280\u80fd\u5355\u5143:\u76ee\u6807\u8ddd\u79bb\uff08\u683c\u5b50\u6570\uff09\u6b20\u7f3a,\u8bf7\u586b\u5199:" + skillUnit.id));
                                }
                            }
                            data = subE.elementTextTrim("physicsHarmType");
                            if (data != null) {
                                skillUnit.physicsHarmType = EHarmType.get(data);
                                data = subE.elementTextTrim("weaponHarmMult");
                                if (data != null) {
                                    skillUnit.weaponHarmMult = Float.parseFloat(data);
                                }
                                data = subE.elementTextTrim("physicsHarmValue");
                                if (data != null) {
                                    skillUnit.physicsHarmValue = Integer.parseInt(data);
                                }
                            }
                            data = subE.elementTextTrim("needHit");
                            if (data != null && data.equals("\u662f")) {
                                skillUnit.needMagicHit = true;
                                skillUnit.magicHarmType = EMagic.getMagic(subE.elementTextTrim("magicHarmType"));
                            }
                            data = subE.elementTextTrim("magicHarmValue");
                            if (data != null) {
                                skillUnit.magicHarmHpValue = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("magicMpReduce");
                            if (data != null) {
                                skillUnit.magicHarmMpValue = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("hpResume");
                            if (data != null) {
                                skillUnit.resumeHp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("mpResume");
                            if (data != null) {
                                skillUnit.resumeMp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("hateValue");
                            if (data != null) {
                                skillUnit.hateValue = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("effectFeature");
                            if (data != null) {
                                skillUnit.cleanEffectFeature = Effect.EffectFeature.get(data);
                                skillUnit.cleanEffectMaxLevel = Byte.parseByte(subE.elementTextTrim("featureMaxLevel"));
                                if (subE.elementTextTrim("cleanNumberPerTimes") == null) {
                                    SkillUnitDict.log.info(("skillUnit.id:" + skillUnit.id));
                                }
                                skillUnit.cleanEffectNumberPerTimes = Byte.parseByte(subE.elementTextTrim("cleanNumberPerTimes"));
                            }
                            data = subE.elementTextTrim("effectIDs");
                            if (data != null) {
                                String[] detailCleanEffectIDInfo = data.split("-");
                                skillUnit.cleanDetailEffectLowerID = Integer.parseInt(detailCleanEffectIDInfo[0]);
                                skillUnit.cleandetailEffectUperID = Integer.parseInt(detailCleanEffectIDInfo[1]);
                            }
                            data = subE.elementTextTrim("odds");
                            if (data != null) {
                                skillUnit.cleanEffectOdds = Float.parseFloat(data) / 100.0f;
                            }
                            data = subE.elementTextTrim("additionalSEID");
                            if (data != null) {
                                skillUnit.additionalSEID = Integer.parseInt(data);
                            }
                            for (int i = 1; i <= 3; ++i) {
                                data = subE.elementTextTrim("additionalSE" + i + "ID");
                                if (data == null) {
                                    break;
                                }
                                AdditionalActionUnit additionalActionUnit = new AdditionalActionUnit();
                                additionalActionUnit.skillOrEffectUnitID = Integer.parseInt(data);
                                additionalActionUnit.activeOdds = Integer.parseInt(subE.elementTextTrim("additionalSE" + i + "Odds")) / 100.0f;
                                additionalActionUnit.activeTimes = Byte.parseByte(subE.elementTextTrim("additionalSE" + i + "ActionTimes"));
                                additionalOddsActionUnitList.add(additionalActionUnit);
                            }
                            if (additionalOddsActionUnitList.size() > 0) {
                                skillUnit.additionalOddsActionUnitList = new AdditionalActionUnit[additionalOddsActionUnitList.size()];
                                for (int i = 0; i < additionalOddsActionUnitList.size(); ++i) {
                                    skillUnit.additionalOddsActionUnitList[i] = additionalOddsActionUnitList.get(i);
                                }
                            }
                            data = subE.elementTextTrim("animationAction");
                            if (data != null) {
                                skillUnit.animationAction = Byte.valueOf(data);
                            }
                            data = subE.elementTextTrim("releaseAnimationID");
                            if (data != null) {
                                skillUnit.releaseAnimationID = Short.parseShort(data);
                            } else {
                                skillUnit.releaseAnimationID = -1;
                            }
                            data = subE.elementTextTrim("releaseImageID");
                            if (data != null) {
                                skillUnit.releaseImageID = Short.parseShort(data);
                            } else {
                                skillUnit.releaseImageID = -1;
                            }
                            data = subE.elementTextTrim("activeImageID");
                            if (data != null) {
                                skillUnit.activeImageID = Short.parseShort(data);
                            }
                            data = subE.elementTextTrim("tierRelation");
                            if (data != null) {
                                skillUnit.tierRelation = Byte.valueOf(data);
                            } else {
                                skillUnit.tierRelation = 1;
                            }
                            data = subE.elementTextTrim("heightRelation");
                            if (data != null) {
                                skillUnit.heightRelation = Byte.valueOf(data);
                            }
                            data = subE.elementTextTrim("releaseheightRelation");
                            if (data != null) {
                                skillUnit.releaseHeightRelation = Byte.valueOf(data);
                            }
                            data = subE.elementTextTrim("isDirection");
                            if (data != null) {
                                skillUnit.isDirection = Byte.parseByte(data);
                            }
                            data = subE.elementTextTrim("activeAnimationID");
                            if (data != null) {
                                skillUnit.activeAnimationID = Short.parseShort(data);
                            }
                            if (skillUnit.activeSkillType == EActiveSkillType.MAGIC) {
                                if (skillUnit.targetType == ETargetType.ENEMY) {
                                    skillUnit.activeTouchType = ETouchType.ATTACK_BY_MAGIC;
                                    skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_MAGIC;
                                } else {
                                    skillUnit.activeTouchType = ETouchType.RESUME_BY_MAGIC;
                                }
                            } else if (skillUnit.targetType == ETargetType.ENEMY) {
                                if (skillUnit.physicsHarmValue > 0) {
                                    if (skillUnit.physicsHarmType == EHarmType.DISTANCE_PHYSICS) {
                                        skillUnit.activeTouchType = ETouchType.ATTACK_BY_DISTANCE_PHYSICS;
                                        skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_DISTANCE_PHYSICS;
                                    } else {
                                        skillUnit.activeTouchType = ETouchType.ATTACK_BY_NEAR_PHYSICS;
                                        skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_NEAR_PHYSICS;
                                    }
                                } else if (skillUnit.targetDistance <= 3) {
                                    skillUnit.activeTouchType = ETouchType.ATTACK_BY_NEAR_PHYSICS;
                                    skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_NEAR_PHYSICS;
                                } else {
                                    skillUnit.activeTouchType = ETouchType.ATTACK_BY_DISTANCE_PHYSICS;
                                    skillUnit.passiveTouchType = ETouchType.BE_ATTACKED_BY_DISTANCE_PHYSICS;
                                }
                            } else {
                                skillUnit.activeTouchType = ETouchType.ACTIVE;
                            }
                            this.skillUnitTable.put(skillUnit.id, skillUnit);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTouchPassiveSkillUnit(final String _dataPath) {
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
                    ArrayList<AdditionalActionUnit> additionalOddsActionUnitList = new ArrayList<AdditionalActionUnit>();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        additionalOddsActionUnitList.clear();
                        if (subE != null) {
                            TouchUnit skillUnit = new TouchUnit(Integer.parseInt(subE.elementTextTrim("id")));
                            String data = subE.elementTextTrim("name");
                            if (data == null) {
                                SkillUnitDict.log.info(("\u6ca1\u6709\u540d\u5b57\uff1a" + skillUnit.id));
                                break;
                            }
                            skillUnit.name = data;
                            skillUnit.touchType = ETouchType.get(subE.elementTextTrim("touthType"));
                            if (skillUnit.touchType == null) {
                                SkillUnitDict.log.info(("\u6ca1\u6709\u89e6\u53d1\u7c7b\u578b\uff1a" + skillUnit.id));
                                break;
                            }
                            data = subE.elementTextTrim("targetType");
                            skillUnit.targetType = ETargetType.get(data);
                            if (skillUnit.targetType == null) {
                                SkillUnitDict.log.info(("\u6ca1\u6709\u76ee\u6807\u7c7b\u578b\uff1a" + skillUnit.id));
                                break;
                            }
                            data = subE.elementTextTrim("range");
                            skillUnit.targetRangeType = ETargetRangeType.get(data);
                            if (skillUnit.targetRangeType == ETargetRangeType.TEAM || skillUnit.targetRangeType == ETargetRangeType.SOME) {
                                skillUnit.rangeTargetNumber = Byte.parseByte(subE.elementTextTrim("rangeTargetNumber"));
                                skillUnit.rangeLine = EAOERangeBaseLine.get(subE.elementTextTrim("rangeBase"));
                                skillUnit.rangeMode = EAOERangeType.get(subE.elementTextTrim("rangeMode"));
                                skillUnit.rangeX = Byte.parseByte(subE.elementTextTrim("rangeX"));
                                if (skillUnit.rangeMode == EAOERangeType.FRONT_RECT) {
                                    skillUnit.rangeY = Byte.parseByte(subE.elementTextTrim("rangeY"));
                                }
                                data = subE.elementTextTrim("average");
                                if (data != null) {
                                    skillUnit.isAverageResult = data.equals("\u662f");
                                } else {
                                    skillUnit.isAverageResult = false;
                                }
                            }
                            data = subE.elementTextTrim("physicsHarmType");
                            if (data != null) {
                                skillUnit.physicsHarmType = EHarmType.get(data);
                                data = subE.elementTextTrim("weaponHarmMult");
                                if (data != null) {
                                    skillUnit.weaponHarmMult = Float.parseFloat(data);
                                }
                                data = subE.elementTextTrim("physicsHarmValue");
                                if (data != null) {
                                    skillUnit.physicsHarmValue = Integer.parseInt(data);
                                }
                            }
                            data = subE.elementTextTrim("magicHarmType");
                            if (data != null) {
                                skillUnit.magicHarmType = EMagic.getMagic(data);
                                skillUnit.magicHarmHpValue = Integer.parseInt(subE.elementTextTrim("magicHarmValue"));
                            }
                            data = subE.elementTextTrim("magicMpReduce");
                            if (data != null) {
                                skillUnit.magicHarmMpValue = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("hpResume");
                            if (data != null) {
                                skillUnit.resumeHp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("mpResume");
                            if (data != null) {
                                skillUnit.resumeMp = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("hateValue");
                            if (data != null) {
                                skillUnit.hateValue = Integer.parseInt(data);
                            }
                            data = subE.elementTextTrim("additionalSEID");
                            if (data != null) {
                                skillUnit.additionalSEID = Integer.parseInt(data);
                            }
                            for (int i = 1; i <= 3; ++i) {
                                data = subE.elementTextTrim("additionalSE" + i + "ID");
                                if (data == null) {
                                    break;
                                }
                                AdditionalActionUnit additionalActionUnit = new AdditionalActionUnit();
                                additionalActionUnit.skillOrEffectUnitID = Integer.parseInt(data);
                                additionalActionUnit.activeOdds = Float.parseFloat(subE.elementTextTrim("additionalSE" + i + "Odds")) / 100.0f;
                                additionalActionUnit.activeTimes = Byte.parseByte(subE.elementTextTrim("additionalSE" + i + "ActionTimes"));
                                additionalOddsActionUnitList.add(additionalActionUnit);
                            }
                            if (additionalOddsActionUnitList.size() > 0) {
                                skillUnit.additionalOddsActionUnitList = new AdditionalActionUnit[additionalOddsActionUnitList.size()];
                                for (int i = 0; i < additionalOddsActionUnitList.size(); ++i) {
                                    skillUnit.additionalOddsActionUnitList[i] = additionalOddsActionUnitList.get(i);
                                }
                            }
                            data = subE.elementTextTrim("activeAnimationID");
                            if (data != null) {
                                skillUnit.activeAnimationID = Short.parseShort(data);
                            }
                            data = subE.elementTextTrim("activeImageID");
                            if (data != null) {
                                skillUnit.activeImageID = Short.parseShort(data);
                            }
                            this.skillUnitTable.put(skillUnit.id, skillUnit);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadEnhanceSkillUnit(final String _dataPath) {
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
                    ArrayList<EnhanceSkillUnit.EnhanceUnit> enhanceUnitList = new ArrayList<EnhanceSkillUnit.EnhanceUnit>();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        enhanceUnitList.clear();
                        if (subE != null) {
                            EnhanceSkillUnit skillUnit = new EnhanceSkillUnit(Integer.parseInt(subE.elementTextTrim("id")));
                            String data = subE.elementTextTrim("name");
                            if (data == null) {
                                break;
                            }
                            skillUnit.name = data;
                            for (int i = 1; i <= 5; ++i) {
                                EnhanceSkillUnit.EnhanceUnit enhanceUnit = new EnhanceSkillUnit.EnhanceUnit();
                                enhanceUnit.skillDataType = EnhanceSkillUnit.EnhanceDataType.get(subE.elementTextTrim("target" + i + "Type"));
                                if (enhanceUnit.skillDataType == null) {
                                    break;
                                }
                                enhanceUnit.skillName = subE.elementTextTrim("target" + i + "Name");
                                if (enhanceUnit.skillName == null) {
                                    LogWriter.println("\u5f3a\u5316\u5355\u5143\u6570\u636e\u9519\u8bef-\u76ee\u6807\u6280\u80fd\u540d\u79f0\uff1a" + skillUnit.name);
                                    break;
                                }
                                enhanceUnit.dataField = EnhanceSkillUnit.SkillDataField.get(subE.elementTextTrim("target" + i + "ColumnName"));
                                if (enhanceUnit.dataField == null) {
                                    LogWriter.println("\u5f3a\u5316\u5355\u5143\u6570\u636e\u9519\u8bef-\u76ee\u6807\u6280\u80fd\u5217\u540d\uff1a" + skillUnit.name);
                                    break;
                                }
                                enhanceUnit.caluOperator = EMathCaluOperator.get(subE.elementTextTrim("target" + i + "Operator"));
                                if (enhanceUnit.caluOperator == null) {
                                    LogWriter.println("\u5f3a\u5316\u5355\u5143\u6570\u636e\u9519\u8bef-\u8fd0\u7b97\u7b26\uff1a" + skillUnit.name);
                                    break;
                                }
                                data = subE.elementTextTrim("target" + i + "Value");
                                if (data == null) {
                                    LogWriter.println("\u5f3a\u5316\u5355\u5143\u6570\u636e\u9519\u8bef-\u6570\u503c\uff1a" + skillUnit.name);
                                    break;
                                }
                                Pattern pattern = Pattern.compile("[0-9]*");
                                if (pattern.matcher(data).matches()) {
                                    enhanceUnit.changeMulti = Float.parseFloat(data);
                                } else {
                                    enhanceUnit.changeString = data.replace('\u3001', ',').replace('\uff0c', ',').split(",");
                                }
                                enhanceUnitList.add(enhanceUnit);
                            }
                            if (enhanceUnitList.size() <= 0) {
                                continue;
                            }
                            skillUnit.enhanceUnitList = new EnhanceSkillUnit.EnhanceUnit[enhanceUnitList.size()];
                            for (int i = 0; i < enhanceUnitList.size(); ++i) {
                                skillUnit.enhanceUnitList[i] = enhanceUnitList.get(i);
                            }
                            enhanceUnitList.clear();
                            this.skillUnitTable.put(skillUnit.id, skillUnit);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPropertySkillUnit(final String _dataPath) {
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
                    ArrayList<AdditionalActionUnit> additionalOddsActionUnitList = new ArrayList<AdditionalActionUnit>();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        additionalOddsActionUnitList.clear();
                        if (subE != null) {
                            ChangePropertyUnit skillUnit = new ChangePropertyUnit(Integer.parseInt(subE.elementTextTrim("id")));
                            String data = subE.elementTextTrim("name");
                            if (data == null) {
                                break;
                            }
                            skillUnit.name = data;
                            skillUnit.caluOperator = EMathCaluOperator.get(subE.elementTextTrim("operator"));
                            data = subE.elementText("defence");
                            if (data != null) {
                                skillUnit.defense = Float.parseFloat(data);
                            }
                            data = subE.elementText("maxHp");
                            if (data != null) {
                                skillUnit.maxHp = Float.parseFloat(data);
                            }
                            data = subE.elementText("maxMp");
                            if (data != null) {
                                skillUnit.maxMp = Float.parseFloat(data);
                            }
                            data = subE.elementText("strength");
                            if (data != null) {
                                skillUnit.strength = Float.parseFloat(data);
                            }
                            data = subE.elementText("agility");
                            if (data != null) {
                                skillUnit.agility = Float.parseFloat(data);
                            }
                            data = subE.elementText("stamina");
                            if (data != null) {
                                skillUnit.stamina = Float.parseFloat(data);
                            }
                            data = subE.elementText("inte");
                            if (data != null) {
                                skillUnit.inte = Float.parseFloat(data);
                            }
                            data = subE.elementText("spirit");
                            if (data != null) {
                                skillUnit.spirit = Float.parseFloat(data);
                            }
                            data = subE.elementText("luck");
                            if (data != null) {
                                skillUnit.lucky = Float.parseFloat(data);
                            }
                            data = subE.elementText("phsicsDeathblowLevel");
                            if (data != null) {
                                skillUnit.physicsDeathblowLevel = Float.parseFloat(data);
                            }
                            data = subE.elementText("magicDeathblowLevel");
                            if (data != null) {
                                skillUnit.magicDeathblowLevel = Float.parseFloat(data);
                            }
                            data = subE.elementText("hitLevel");
                            if (data != null) {
                                skillUnit.hitLevel = Float.parseFloat(data);
                            }
                            data = subE.elementText("phsicsDuckLevel");
                            if (data != null) {
                                skillUnit.physicsDuckLevel = Float.parseFloat(data);
                            }
                            data = subE.elementText("magicFastnessType");
                            if (data != null) {
                                if (data.equalsIgnoreCase("ALL")) {
                                    skillUnit.magicFastnessValue = (float) Integer.parseInt(subE.elementText("magicFastnessValue"));
                                } else {
                                    EMagic magicType = EMagic.getMagic(data);
                                    if (magicType != null) {
                                        skillUnit.magicFastnessType = magicType;
                                        skillUnit.magicFastnessValue = Float.parseFloat(subE.elementText("magicFastnessValue"));
                                    } else {
                                        SkillUnitDict.log.info(("\u4e0d\u5b58\u5728\u7684\u9b54\u6cd5\u7c7b\u578b\uff1a" + data));
                                    }
                                }
                            }
                            data = subE.elementText("hate");
                            if (data != null) {
                                skillUnit.hate = Float.parseFloat(data);
                            }
                            data = subE.elementText("physicsAttackHarmValue");
                            if (data != null) {
                                skillUnit.physicsAttackHarmValue = Float.parseFloat(data);
                            }
                            data = subE.elementText("bePhysicsHarmValue");
                            if (data != null) {
                                skillUnit.bePhysicsHarmValue = Float.parseFloat(data);
                            }
                            data = subE.elementText("magicAttackHarmType");
                            if (data != null) {
                                if (data.equalsIgnoreCase("ALL")) {
                                    skillUnit.magicHarmValue = Float.parseFloat(subE.elementText("magicAttackHarmValue"));
                                } else {
                                    EMagic magicType = EMagic.getMagic(data);
                                    if (magicType != null) {
                                        skillUnit.magicHarmValue = Float.parseFloat(subE.elementText("magicAttackHarmValue"));
                                        skillUnit.magicHarmType = magicType;
                                    } else {
                                        SkillUnitDict.log.info(("\u4e0d\u5b58\u5728\u7684\u9b54\u6cd5\u7c7b\u578b\uff1a" + data));
                                    }
                                }
                            }
                            data = subE.elementText("beMagicHarmType");
                            if (data != null) {
                                if (data.equalsIgnoreCase("ALL")) {
                                    skillUnit.magicHarmValueBeAttack = Float.parseFloat(subE.elementText("beMagicHarmValue"));
                                } else {
                                    EMagic magicType = EMagic.getMagic(data);
                                    if (magicType != null) {
                                        skillUnit.magicHarmValueBeAttack = Float.parseFloat(subE.elementText("beMagicHarmValue"));
                                        skillUnit.magicHarmTypeBeAttack = magicType;
                                    }
                                }
                            }
                            data = subE.elementText("physicsAttackInterval");
                            if (data != null) {
                                skillUnit.physicsAttackInterval = Float.parseFloat(data);
                            }
                            data = subE.elementText("skillReleaseTime");
                            if (data != null) {
                                skillUnit.allSkillReleaseTime = Float.parseFloat(data);
                            } else {
                                data = subE.elementText("specialSkillIDList");
                                if (data != null) {
                                    ArrayList<Integer> skillIDList = new ArrayList<Integer>();
                                    String[] skillIDDomainList = data.split(",");
                                    String[] array2;
                                    for (int length2 = (array2 = skillIDDomainList).length, k = 0; k < length2; ++k) {
                                        String skillIDomain = array2[k];
                                        String[] skillIDDescList = skillIDomain.split("-");
                                        if (skillIDDescList.length == 1) {
                                            skillIDList.add(Integer.parseInt(skillIDDescList[0]));
                                        } else {
                                            for (int minSkillID = Integer.parseInt(skillIDDescList[0]), maxSkillID = Integer.parseInt(skillIDDescList[skillIDDescList.length - 1]); minSkillID <= maxSkillID; ++minSkillID) {
                                                skillIDList.add(minSkillID);
                                            }
                                        }
                                    }
                                    if (skillIDList.size() > 0) {
                                        skillUnit.specialSkillReleaseTimeIDList = skillIDList;
                                        skillUnit.specialSkillReleaseTime = Float.parseFloat(subE.elementText("specialSkillReleaseTime"));
                                    }
                                }
                            }
                            data = subE.elementText("resistType");
                            if (data != null) {
                                skillUnit.resistSpecialStatus = ESpecialStatus.get(data);
                                if (skillUnit.resistSpecialStatus == null) {
                                    break;
                                }
                                skillUnit.resistSpecialStatusOdds = Integer.parseInt(subE.elementText("resistOdds")) / 100.0f;
                            }
                            data = subE.elementTextTrim("additionalSETouchType");
                            if (data != null) {
                                skillUnit.touchType = ETouchType.get(data);
                                if (skillUnit.touchType != null) {
                                    data = subE.elementTextTrim("additionalSEID");
                                    if (data != null) {
                                        skillUnit.additionalSEID = Integer.parseInt(data);
                                    }
                                    for (int i = 1; i <= 3; ++i) {
                                        data = subE.elementTextTrim("additionalSE" + i + "ID");
                                        if (data == null) {
                                            break;
                                        }
                                        AdditionalActionUnit additionalActionUnit = new AdditionalActionUnit();
                                        additionalActionUnit.skillOrEffectUnitID = Integer.parseInt(data);
                                        additionalActionUnit.activeOdds = Float.parseFloat(subE.elementTextTrim("additionalSE" + i + "Odds")) / 100.0f;
                                        additionalActionUnit.activeTimes = Byte.parseByte(subE.elementTextTrim("additionalSE" + i + "ActionTimes"));
                                        additionalOddsActionUnitList.add(new AdditionalActionUnit());
                                    }
                                    if (additionalOddsActionUnitList.size() > 0) {
                                        skillUnit.additionalOddsActionUnitList = new AdditionalActionUnit[additionalOddsActionUnitList.size()];
                                        for (int i = 0; i < additionalOddsActionUnitList.size(); ++i) {
                                            skillUnit.additionalOddsActionUnitList[i] = additionalOddsActionUnitList.get(i);
                                        }
                                    }
                                }
                            }
                            this.skillUnitTable.put(skillUnit.id, skillUnit);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
