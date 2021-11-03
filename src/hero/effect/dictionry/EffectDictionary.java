// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.dictionry;

import hero.effect.detail.TouchEffect;
import hero.skill.detail.ETouchType;
import hero.effect.detail.DynamicEffect;
import java.util.Iterator;
import org.dom4j.Document;
import hero.skill.detail.AdditionalActionUnit;
import hero.skill.detail.ESpecialStatus;
import hero.skill.detail.EHarmType;
import java.util.ArrayList;
import hero.share.EMagic;
import hero.skill.detail.EMathCaluOperator;
import hero.share.service.LogWriter;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.effect.detail.StaticEffect;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.effect.service.EffectConfig;
import hero.effect.Effect;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class EffectDictionary {

    private static Logger log;
    private FastMap<Integer, Effect> effectTable;
    private static EffectDictionary instance;

    static {
        EffectDictionary.log = Logger.getLogger((Class) EffectDictionary.class);
    }

    private EffectDictionary() {
        this.effectTable = (FastMap<Integer, Effect>) new FastMap();
    }

    public static EffectDictionary getInstance() {
        if (EffectDictionary.instance == null) {
            EffectDictionary.instance = new EffectDictionary();
        }
        return EffectDictionary.instance;
    }

    public Effect getEffectRef(final int _effectID) {
        return (Effect) this.effectTable.get(_effectID);
    }

    public Effect getEffectInstance(final int _effectID) {
        try {
            return ((Effect) this.effectTable.get(_effectID)).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void load(final EffectConfig _config) {
        this.effectTable.clear();
        this.loadStaticEffect(_config.static_effect_data_path);
        this.loadDynamicEffect(_config.dynamic_effect_data_path);
        this.loadTouchEffect(_config.touch_effect_data_path);
    }

    private void loadStaticEffect(final String _dataPath) {
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
                            StaticEffect effect = new StaticEffect(Integer.parseInt(subE.elementText("id")), subE.elementText("name").trim());
                            effect.level = Short.parseShort(subE.elementTextTrim("level"));
                            effect.feature = Effect.EffectFeature.get(subE.elementTextTrim("featureType"));
                            effect.featureLevel = Byte.parseByte(subE.elementTextTrim("featureLevel"));
                            String data = subE.elementText("isbuff");
                            if (data != null && data.equals("\u662f")) {
                                effect.trait = Effect.EffectTrait.BUFF;
                            } else {
                                effect.trait = Effect.EffectTrait.DEBUFFF;
                            }
                            data = subE.elementText("disAfterDie");
                            if (data != null && data.equals("\u662f")) {
                                effect.isClearAfterDie = true;
                            }
                            data = subE.elementText("replaceID");
                            if (data != null) {
                                effect.replaceID = Integer.parseInt(data);
                            }
                            data = subE.elementText("keepTime");
                            if (data.equalsIgnoreCase("N/A")) {
                                effect.keepTimeType = Effect.EKeepTimeType.N_A;
                                ETargetType targetType = ETargetType.get(subE.elementText("targetType"));
                                if (targetType != null) {
                                    Effect.AureoleRadiationRange actionTarget = new Effect.AureoleRadiationRange(targetType);
                                    if (targetType != ETargetType.MYSELF) {
                                        actionTarget.targetRangeType = ETargetRangeType.get(subE.elementText("rangeType"));
                                        actionTarget.rangeRadiu = Byte.parseByte(subE.elementText("rangeRadiu"));
                                    }
                                    effect.aureoleRadiationRange = actionTarget;
                                } else {
                                    LogWriter.println("\u5149\u73af\u6ca1\u6709\u4f5c\u7528\u5bf9\u8c61\uff1a" + effect.ID);
                                }
                                data = subE.elementText("viewType");
                                if (data != null) {
                                    effect.viewType = Byte.valueOf(data);
                                }
                                data = subE.elementText("imageID");
                                if (data != null) {
                                    effect.imageID = Short.valueOf(data);
                                }
                                data = subE.elementText("animationID");
                                if (data != null) {
                                    effect.animationID = Short.valueOf(data);
                                }
                            } else {
                                effect.keepTimeType = Effect.EKeepTimeType.LIMITED;
                                effect.setKeepTime(Short.parseShort(data));
                                data = subE.elementText("isAdditional");
                                if (data != null && data.equals("\u662f")) {
                                    effect.totalTimes = Short.parseShort(subE.elementText("additionalTotal"));
                                }
                            }
                            data = subE.elementText("operator");
                            if (data != null) {
                                EMathCaluOperator caluOperator = EMathCaluOperator.get(data);
                                if (caluOperator != null) {
                                    effect.caluOperator = EMathCaluOperator.get(data);
                                } else {
                                    LogWriter.println("\u9759\u6001\u6548\u679c\u6ca1\u6709\u8fd0\u7b97\u64cd\u4f5c\u7b26\u53f7\uff1a" + effect.ID);
                                }
                            }
                            data = subE.elementText("defence");
                            if (data != null) {
                                effect.defense = Float.parseFloat(data);
                            }
                            data = subE.elementText("maxHp");
                            if (data != null) {
                                effect.maxHp = Float.parseFloat(data);
                            }
                            data = subE.elementText("maxMp");
                            if (data != null) {
                                effect.maxMp = Float.parseFloat(data);
                            }
                            data = subE.elementText("strength");
                            if (data != null) {
                                effect.strength = Float.parseFloat(data);
                            }
                            data = subE.elementText("agility");
                            if (data != null) {
                                effect.agility = Float.parseFloat(data);
                            }
                            data = subE.elementText("stamina");
                            if (data != null) {
                                effect.stamina = Float.parseFloat(data);
                            }
                            data = subE.elementText("inte");
                            if (data != null) {
                                effect.inte = Float.parseFloat(data);
                            }
                            data = subE.elementText("spirit");
                            if (data != null) {
                                effect.spirit = Float.parseFloat(data);
                            }
                            data = subE.elementText("lucky");
                            if (data != null) {
                                effect.lucky = Float.parseFloat(data);
                            }
                            data = subE.elementText("phsicsDeathblowLevel");
                            if (data != null) {
                                effect.physicsDeathblowLevel = Float.parseFloat(data);
                            }
                            data = subE.elementText("magicDeathblowLevel");
                            if (data != null) {
                                effect.magicDeathblowLevel = Float.parseFloat(data);
                            }
                            data = subE.elementText("hitLevel");
                            if (data != null) {
                                effect.hitLevel = Float.parseFloat(data);
                            }
                            data = subE.elementText("phsicsDuckLevel");
                            if (data != null) {
                                effect.physicsDuckLevel = Float.parseFloat(data);
                            }
                            data = subE.elementText("magicFastnessType");
                            if (data != null) {
                                if (data.equalsIgnoreCase("ALL")) {
                                    effect.magicFastnessValue = Float.parseFloat(subE.elementText("magicFastnessValue"));
                                } else {
                                    EMagic magicType = EMagic.getMagic(data);
                                    if (magicType != null) {
                                        effect.magicFastnessType = magicType;
                                        effect.magicFastnessValue = (float) Integer.parseInt(subE.elementText("magicFastnessValue"));
                                    } else {
                                        EffectDictionary.log.info(("\u4e0d\u5b58\u5728\u7684\u9b54\u6cd5\u7c7b\u578b\uff1a" + data));
                                    }
                                }
                            }
                            data = subE.elementText("hate");
                            if (data != null) {
                                effect.hate = Float.parseFloat(data);
                            }
                            data = subE.elementText("physicsAttackHarmValue");
                            if (data != null) {
                                effect.physicsAttackHarmValue = Float.parseFloat(data);
                            }
                            data = subE.elementText("bePhysicsHarmValue");
                            if (data != null) {
                                effect.bePhysicsHarmValue = Float.parseFloat(data);
                            }
                            data = subE.elementText("magicAttackHarmType");
                            if (data != null) {
                                if (data.equalsIgnoreCase("ALL")) {
                                    effect.magicHarmValue = Float.parseFloat(subE.elementText("magicAttackHarmValue"));
                                } else {
                                    EMagic magicType = EMagic.getMagic(data);
                                    if (magicType != null) {
                                        effect.magicHarmValue = Float.parseFloat(subE.elementText("magicAttackHarmValue"));
                                        effect.magicHarmType = magicType;
                                    } else {
                                        EffectDictionary.log.info(("\u4e0d\u5b58\u5728\u7684\u9b54\u6cd5\u7c7b\u578b\uff1a" + data));
                                    }
                                }
                            }
                            data = subE.elementText("beMagicHarmType");
                            if (data != null) {
                                EMagic magicType = EMagic.getMagic(data.replaceAll("ALL", "\u5168\u90e8"));
                                if (magicType != null) {
                                    effect.magicHarmValueBeAttack = Float.parseFloat(subE.elementText("beMagicHarmValue"));
                                    effect.magicHarmTypeBeAttack = magicType;
                                }
                            }
                            data = subE.elementText("physicsAttackInterval");
                            if (data != null) {
                                effect.physicsAttackInterval = Float.parseFloat(data);
                            }
                            data = subE.elementText("skillReleaseTime");
                            if (data != null) {
                                effect.allSkillReleaseTime = Float.parseFloat(data);
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
                                        effect.specialSkillReleaseTimeIDList = skillIDList;
                                        effect.specialSkillReleaseTime = Float.parseFloat(subE.elementText("specialSkillReleaseTime"));
                                    }
                                }
                            }
                            data = subE.elementText("reduceHarmType");
                            if (data != null) {
                                effect.reduceHarmType = EHarmType.get(data);
                                if (effect.reduceHarmType != null) {
                                    data = subE.elementText("reduceHarmValue");
                                    if (data.equals("N/A")) {
                                        effect.isReduceAllHarm = true;
                                    } else {
                                        effect.reduceHarm = Integer.parseInt(data);
                                    }
                                } else {
                                    LogWriter.println("\u5438\u6536\u4f24\u5bb3\u7c7b\u578b\u9519\u8bef\uff1a" + effect.ID);
                                }
                            }
                            data = subE.elementText("specialEffectType");
                            if (data != null) {
                                effect.specialStatus = ESpecialStatus.get(data);
                                if (effect.specialStatus != null) {
                                    effect.setSpecialStatusLevel(Byte.parseByte(subE.elementText("specialEffectLevel")));
                                } else {
                                    LogWriter.println("\u7279\u6b8a\u72b6\u6001\u7c7b\u578b\u9519\u8bef\uff1a" + effect.ID);
                                }
                            }
                            data = subE.elementText("resistType");
                            if (data != null) {
                                effect.resistSpecialStatus = ESpecialStatus.get(data);
                                if (effect.resistSpecialStatus != null) {
                                    effect.resistSpecialStatusOdds = Integer.parseInt(subE.elementText("resistOdds")) / 100.0f;
                                } else {
                                    LogWriter.println("\u62b5\u6297\u5c5e\u6027\u7c7b\u578b\u9519\u8bef\uff1a" + effect.ID);
                                }
                            }
                            data = subE.elementText("additionalEffectNumber");
                            if (data != null) {
                                int additionalEffectNumber = Byte.parseByte(data);
                                effect.additionalOddsActionUnitList = new AdditionalActionUnit[additionalEffectNumber];
                                for (int i = 0; i < additionalEffectNumber; ++i) {
                                    effect.additionalOddsActionUnitList[i] = new AdditionalActionUnit();
                                    effect.additionalOddsActionUnitList[i].skillOrEffectUnitID = Integer.parseInt(subE.elementText("additionalEffect" + (i + 1) + "ID"));
                                    effect.additionalOddsActionUnitList[i].activeTimes = Byte.parseByte(subE.elementText("additionalEffect" + (i + 1) + "ActionTimes"));
                                    effect.additionalOddsActionUnitList[i].activeOdds = Integer.parseInt(subE.elementText("additionalEffect" + (i + 1) + "Odds")) / 100.0f;
                                }
                            }
                            effect.traceReduceHarmValue = effect.reduceHarm;
                            effect.iconID = Short.parseShort(subE.elementText("iconID"));
                            data = subE.elementText("description");
                            if (data != null) {
                                effect.desc = data;
                            }
                            this.effectTable.put(effect.ID, effect);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDynamicEffect(final String _dataPath) {
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
                            DynamicEffect effect = new DynamicEffect(Integer.parseInt(subE.elementText("id")), subE.elementText("name").trim());
                            EffectDictionary.log.debug(("effect dictionary id=" + effect.ID + ", name = " + effect.name));
                            effect.level = Short.parseShort(subE.elementTextTrim("level"));
                            effect.feature = Effect.EffectFeature.get(subE.elementTextTrim("featureType"));
                            effect.featureLevel = Byte.parseByte(subE.elementTextTrim("featureLevel"));
                            String data = subE.elementText("isbuff");
                            if (data != null && data.equals("\u662f")) {
                                effect.trait = Effect.EffectTrait.BUFF;
                            } else {
                                effect.trait = Effect.EffectTrait.DEBUFFF;
                            }
                            data = subE.elementText("disAfterDie");
                            if (data != null && data.equals("\u662f")) {
                                effect.isClearAfterDie = true;
                            }
                            effect.replaceID = Integer.parseInt(subE.elementText("replaceID"));
                            data = subE.elementText("keepTime");
                            if (data.equalsIgnoreCase("N/A")) {
                                effect.keepTimeType = Effect.EKeepTimeType.N_A;
                                ETargetType targetType = ETargetType.get(subE.elementText("targetType"));
                                if (targetType != null) {
                                    Effect.AureoleRadiationRange actionTarget = new Effect.AureoleRadiationRange(targetType);
                                    actionTarget.targetRangeType = ETargetRangeType.get(subE.elementText("rangeType"));
                                    if (targetType == ETargetType.FRIEND || (targetType == ETargetType.ENEMY && actionTarget.targetRangeType != ETargetRangeType.SINGLE)) {
                                        actionTarget.rangeRadiu = Byte.parseByte(subE.elementText("rangeRadiu"));
                                    }
                                    effect.aureoleRadiationRange = actionTarget;
                                } else {
                                    LogWriter.println("\u5149\u73af\u76ee\u6807\u7c7b\u578b\u9519\u8bef\uff1a" + effect.ID);
                                }
                            } else {
                                effect.keepTimeType = Effect.EKeepTimeType.LIMITED;
                                effect.setKeepTime(Short.parseShort(data));
                                data = subE.elementText("isAdditional");
                                if (data != null && data.equals("\u662f")) {
                                    effect.totalTimes = Short.parseShort(subE.elementText("additionalTotal"));
                                }
                            }
                            effect.actionTimeType = DynamicEffect.ActionTimeType.get(subE.elementText("actionTimeType"));
                            data = subE.elementText("hpReduce");
                            if (data != null) {
                                effect.hpHarmTotal = Integer.parseInt(data);
                                effect.harmType = EHarmType.get(subE.elementText("harmType"));
                                if (effect.harmType != EHarmType.PHYSICS) {
                                    effect.harmMagicType = EMagic.getMagic(effect.harmType.getDesc());
                                }
                            }
                            data = subE.elementText("mpReduce");
                            if (data != null) {
                                effect.mpHarmTotal = Integer.parseInt(data);
                            }
                            data = subE.elementText("hpResume");
                            if (data != null) {
                                effect.hpResumeTotal = Integer.parseInt(data);
                            }
                            data = subE.elementText("mpResume");
                            if (data != null) {
                                effect.mpResumeTotal = Integer.parseInt(data);
                            }
                            data = subE.elementText("additionalEffectNumber");
                            if (data != null) {
                                int additionalEffectNumber = Byte.parseByte(data);
                                effect.additionalOddsActionUnitList = new AdditionalActionUnit[additionalEffectNumber];
                                for (int i = 0; i < additionalEffectNumber; ++i) {
                                    effect.additionalOddsActionUnitList[i].skillOrEffectUnitID = Integer.parseInt(subE.elementText("additionalEffect" + (i + 1) + "ID"));
                                    effect.additionalOddsActionUnitList[i].activeTimes = Byte.parseByte(subE.elementText("additionalEffect" + (i + 1) + "ActionTimes"));
                                    effect.additionalOddsActionUnitList[i].activeOdds = Integer.parseInt(subE.elementText("additionalEffect" + (i + 1) + "Odds")) / 100.0f;
                                }
                            }
                            try {
                                short s = Short.parseShort(subE.elementText("iconID"));
                            } catch (Exception e2) {
                                EffectDictionary.log.info(("effect id = " + effect.ID));
                                EffectDictionary.log.info(("effect name = " + effect.name));
                            }
                            effect.iconID = Short.parseShort(subE.elementText("iconID"));
                            data = subE.elementText("description");
                            if (data != null) {
                                effect.desc = data;
                            }
                            this.effectTable.put(effect.ID, effect);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTouchEffect(final String _dataPath) {
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
                            ETouchType touchType = ETouchType.get(subE.elementText("touthType"));
                            if (touchType == null) {
                                LogWriter.println("\u89e6\u53d1\u7c7b\u578b\u9519\u8bef\uff1a" + subE.elementText("id"));
                            } else {
                                TouchEffect effect = new TouchEffect(Integer.parseInt(subE.elementText("id")), subE.elementText("name").trim(), touchType, Integer.parseInt(subE.elementText("touthOdds")) / 100.0f);
                                effect.level = Short.parseShort(subE.elementTextTrim("level"));
                                effect.feature = Effect.EffectFeature.get(subE.elementTextTrim("featureType"));
                                effect.featureLevel = Byte.parseByte(subE.elementTextTrim("featureLevel"));
                                String data = subE.elementText("isbuff");
                                if (data != null && data.equals("\u662f")) {
                                    effect.trait = Effect.EffectTrait.BUFF;
                                } else {
                                    effect.trait = Effect.EffectTrait.DEBUFFF;
                                }
                                data = subE.elementText("disAfterDie");
                                if (data != null && data.equals("\u662f")) {
                                    effect.isClearAfterDie = true;
                                }
                                effect.targetType = ETargetType.get(subE.elementText("targetType"));
                                effect.replaceID = Integer.parseInt(subE.elementText("replaceID"));
                                data = subE.elementText("keepTime");
                                if (data.equalsIgnoreCase("N/A")) {
                                    effect.keepTimeType = Effect.EKeepTimeType.N_A;
                                } else {
                                    effect.keepTimeType = Effect.EKeepTimeType.LIMITED;
                                    effect.setKeepTime(Short.parseShort(data));
                                }
                                data = subE.elementText("hpReduce");
                                if (data != null) {
                                    effect.hpHarmValue = Integer.parseInt(data);
                                    effect.harmType = EHarmType.get(subE.elementText("harmType"));
                                    if (effect.harmType != EHarmType.PHYSICS) {
                                        effect.harmMagicType = EMagic.getMagic(effect.harmType.getDesc());
                                    }
                                }
                                data = subE.elementText("mpReduce");
                                if (data != null) {
                                    effect.mpHarmValue = Integer.parseInt(data);
                                }
                                data = subE.elementText("hpResume");
                                if (data != null) {
                                    effect.hpResumeValue = Integer.parseInt(data);
                                }
                                data = subE.elementText("mpResume");
                                if (data != null) {
                                    effect.mpResumeValue = Integer.parseInt(data);
                                }
                                data = subE.elementText("additionalEffectNumber");
                                if (data != null) {
                                    int additionalEffectNumber = Byte.parseByte(data);
                                    effect.additionalOddsActionUnitList = new AdditionalActionUnit[additionalEffectNumber];
                                    for (int i = 0; i < additionalEffectNumber; ++i) {
                                        effect.additionalOddsActionUnitList[i] = new AdditionalActionUnit();
                                        effect.additionalOddsActionUnitList[i].skillOrEffectUnitID = Integer.parseInt(subE.elementText("additionalEffect" + (i + 1) + "ID"));
                                        effect.additionalOddsActionUnitList[i].activeTimes = Byte.parseByte(subE.elementText("additionalEffect" + (i + 1) + "ActionTimes"));
                                        effect.additionalOddsActionUnitList[i].activeOdds = Integer.parseInt(subE.elementText("additionalEffect" + (i + 1) + "Odds")) / 100.0f;
                                    }
                                }
                                data = subE.elementText("effectImageID");
                                if (data != null) {
                                    effect.effectImageID = Short.parseShort(data);
                                } else {
                                    effect.effectImageID = -1;
                                }
                                data = subE.elementText("effectAnimationID");
                                if (data != null) {
                                    effect.effectAnimationID = Short.parseShort(data);
                                } else {
                                    effect.effectAnimationID = -1;
                                }
                                data = subE.elementText("tierRelation");
                                if (data != null) {
                                    effect.tierRelation = Short.parseShort(data);
                                }
                                effect.iconID = Short.parseShort(subE.elementText("iconID"));
                                data = subE.elementText("description");
                                if (data != null) {
                                    effect.desc = data;
                                }
                                this.effectTable.put(effect.ID, effect);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.println(e);
        }
    }

    public static void main(final String[] args) {
        getInstance().loadStaticEffect(String.valueOf(System.getProperty("user.dir")) + File.separator + "res/data/effect/static");
        getInstance().loadDynamicEffect(String.valueOf(System.getProperty("user.dir")) + File.separator + "res/data/effect/dynamic");
        getInstance().loadTouchEffect(String.valueOf(System.getProperty("user.dir")) + File.separator + "res/data/effect/touch");
    }
}
