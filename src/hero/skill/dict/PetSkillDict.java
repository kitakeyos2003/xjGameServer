// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.dict;

import hero.skill.detail.EHarmType;
import hero.effect.Effect;
import org.dom4j.Document;
import hero.skill.detail.EMathCaluOperator;
import hero.skill.PetPassiveSkill;
import hero.share.EMagic;
import hero.skill.detail.EAOERangeType;
import hero.skill.detail.EAOERangeBaseLine;
import hero.skill.detail.ETargetRangeType;
import hero.skill.detail.ETargetType;
import hero.skill.detail.EActiveSkillType;
import hero.skill.PetActiveSkill;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.pet.service.PetConfig;
import java.util.Iterator;
import hero.pet.PetKind;
import java.util.ArrayList;
import java.util.List;
import hero.pet.Pet;
import hero.skill.detail.PetAdditionEffect;
import hero.skill.PetSkill;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class PetSkillDict {

    private static Logger log;
    private FastMap<Integer, PetSkill> petSkillDict;
    private FastMap<Integer, PetAdditionEffect> petAdditionEffectDict;
    private static PetSkillDict instance;

    static {
        PetSkillDict.log = Logger.getLogger((Class) PetSkillDict.class);
    }

    public static PetSkillDict getInstance() {
        if (PetSkillDict.instance == null) {
            PetSkillDict.instance = new PetSkillDict();
        }
        return PetSkillDict.instance;
    }

    private PetSkillDict() {
        this.petSkillDict = (FastMap<Integer, PetSkill>) new FastMap();
        this.petAdditionEffectDict = (FastMap<Integer, PetAdditionEffect>) new FastMap();
    }

    public PetSkill getPetSkill(final int skillId) {
        return (PetSkill) this.petSkillDict.get(skillId);
    }

    public PetAdditionEffect getPetAdditionEffect(final int id) {
        return (PetAdditionEffect) this.petAdditionEffectDict.get(id);
    }

    public List<PetSkill> getPetCanLearnSkillList(final Pet pet) {
        List<PetSkill> canLearnSkillList = new ArrayList<PetSkill>();
        for (final PetSkill skill : this.petSkillDict.values()) {
            if (skill.needLevel == pet.level && (skill.petKind.getKindID() == pet.pk.getKind() || skill.petKind == PetKind.ALL)) {
                canLearnSkillList.add(skill);
            }
        }
        return canLearnSkillList;
    }

    public void load(final PetConfig config) {
        PetSkillDict.log.debug("start loading pet skill data...");
        this.loadPetSkillData(config.pet_skill_data_path);
        PetSkillDict.log.debug("loading pet skill data end ...");
    }

    public void loadPetSkillData(final String _dataPath) {
        try {
            File file = new File(_dataPath);
            if (file.exists()) {
                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                while (rootIt.hasNext()) {
                    Element subE = rootIt.next();
                    String id = subE.elementTextTrim("id");
                    String name = subE.elementTextTrim("name");
                    String data = subE.elementTextTrim("type");
                    if (data.equals("1") || data.equals("\u4e3b\u52a8")) {
                        PetActiveSkill skill = new PetActiveSkill(Integer.parseInt(id), name);
                        data = subE.elementTextTrim("level");
                        skill.level = Integer.parseInt(data);
                        data = subE.elementTextTrim("skillType");
                        if (data != null) {
                            skill.skillType = EActiveSkillType.getType(data);
                        }
                        data = subE.elementTextTrim("getFrom");
                        if (data.equals("\u76f4\u63a5\u4e60\u5f97")) {
                            skill.getFrom = 1;
                        } else {
                            skill.getFrom = 2;
                        }
                        data = subE.elementTextTrim("lev");
                        skill.needLevel = Integer.parseInt(data);
                        data = subE.elementTextTrim("petKind");
                        skill.petKind = PetKind.getPetKind(data);
                        data = subE.elementTextTrim("publicVar");
                        if (data != null) {
                            skill.coolPublicVar = Integer.parseInt(data);
                        }
                        data = subE.elementTextTrim("time");
                        if (data != null) {
                            skill.coolDownTime = Integer.parseInt(data);
                        }
                        data = subE.elementTextTrim("useMp");
                        if (data != null) {
                            skill.useMp = Integer.parseInt(data);
                        }
                        data = subE.elementTextTrim("targetType");
                        if (data != null) {
                            skill.targetType = ETargetType.get(data);
                        }
                        data = subE.elementTextTrim("range");
                        if (data != null) {
                            skill.targetRangeType = ETargetRangeType.get(data);
                        } else {
                            skill.targetRangeType = ETargetRangeType.SINGLE;
                        }
                        data = subE.elementTextTrim("distance");
                        if (data != null) {
                            skill.targetDistance = Byte.parseByte(data);
                        }
                        data = subE.elementTextTrim("rangeBase");
                        if (data != null) {
                            skill.rangeBaseLine = EAOERangeBaseLine.get(data);
                        }
                        data = subE.elementTextTrim("rangeMode");
                        if (data != null) {
                            skill.rangeMode = EAOERangeType.get(data);
                        }
                        data = subE.elementTextTrim("rangeX");
                        if (data != null) {
                            skill.rangeX = Byte.parseByte(data);
                        }
                        data = subE.elementTextTrim("rangeY");
                        if (data != null) {
                            skill.rangeY = Byte.parseByte(data);
                        }
                        data = subE.elementTextTrim("atkMult");
                        if (data != null) {
                            skill.atkMult = Byte.parseByte(data);
                        }
                        data = subE.elementTextTrim("physicsHarmValue");
                        if (data != null) {
                            skill.physicsHarmValue = Integer.parseInt(data);
                        }
                        data = subE.elementTextTrim("magicHarmType");
                        if (data != null) {
                            skill.magicHarmType = EMagic.getMagic(data);
                        }
                        data = subE.elementTextTrim("magicAtkValue");
                        if (data != null) {
                            skill.magicHarmHpValue = Integer.parseInt(data);
                        }
                        data = subE.elementTextTrim("resumeHp");
                        if (data != null) {
                            skill.resumeHp = Integer.parseInt(data);
                        }
                        data = subE.elementTextTrim("resumeMp");
                        if (data != null) {
                            skill.resumeMp = Integer.parseInt(data);
                        }
                        data = subE.elementTextTrim("code");
                        if (data != null) {
                            skill.effectID = Integer.parseInt(data);
                        }
                        data = subE.elementTextTrim("odds");
                        if (data != null) {
                            skill.effectOdds = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("iconID");
                        if (data != null) {
                            skill.iconID = Short.parseShort(data);
                        }
                        data = subE.elementTextTrim("fireImageId");
                        if (data != null) {
                            skill.releaseAnimationID = Short.parseShort(data);
                        }
                        data = subE.elementTextTrim("actImageId");
                        if (data != null) {
                            skill.activeAnimationID = Short.parseShort(data);
                        }
                        data = subE.elementTextTrim("description");
                        if (data != null) {
                            skill.description = data;
                        }
                        this.petSkillDict.put(Integer.parseInt(id), skill);
                    } else if (data.equals("2") || data.equals("\u88ab\u52a8")) {
                        PetPassiveSkill skill2 = new PetPassiveSkill(Integer.parseInt(id), name);
                        data = subE.elementTextTrim("level");
                        skill2.level = Integer.parseInt(data);
                        data = subE.elementTextTrim("getFrom");
                        if (data.equals("\u76f4\u63a5\u4e60\u5f97")) {
                            skill2.getFrom = 1;
                        } else {
                            skill2.getFrom = 2;
                        }
                        data = subE.elementTextTrim("lev");
                        skill2.needLevel = Integer.parseInt(data);
                        data = subE.elementTextTrim("petKind");
                        skill2.petKind = PetKind.getPetKind(data);
                        data = subE.elementTextTrim("calMode");
                        if (data != null) {
                            skill2.caluOperator = EMathCaluOperator.get(data);
                        }
                        data = subE.elementTextTrim("targetType");
                        if (data != null) {
                            skill2.targetType = ETargetType.get(data);
                        }
                        data = subE.elementTextTrim("range");
                        if (data != null) {
                            skill2.targetRangeType = ETargetRangeType.get(data);
                        } else {
                            skill2.targetRangeType = ETargetRangeType.SINGLE;
                        }
                        data = subE.elementTextTrim("str");
                        if (data != null) {
                            skill2.strength = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("agile");
                        if (data != null) {
                            skill2.agility = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("intel");
                        if (data != null) {
                            skill2.inte = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("spi");
                        if (data != null) {
                            skill2.spirit = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("luck");
                        if (data != null) {
                            skill2.lucky = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("maxMp");
                        if (data != null) {
                            skill2.maxMp = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("hitLevel");
                        if (data != null) {
                            skill2.hitLevel = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("physicsDeathblowLevel");
                        if (data != null) {
                            skill2.physicsDeathblowLevel = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("magicDeathblowLevel");
                        if (data != null) {
                            skill2.magicDeathblowLevel = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("physicsAttackHarmValue");
                        if (data != null) {
                            skill2.physicsAttackHarmValue = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("magicHarmType");
                        if (data != null) {
                            skill2.magicHarmType = EMagic.getMagic(data);
                        }
                        data = subE.elementTextTrim("magicHarmValue");
                        if (data != null) {
                            skill2.magicHarmValue = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("physicsAttackInterval");
                        if (data != null) {
                            skill2.physicsAttackInterval = Float.parseFloat(data);
                        }
                        data = subE.elementTextTrim("iconID");
                        if (data != null) {
                            skill2.iconID = Short.parseShort(data);
                        }
                        data = subE.elementTextTrim("description");
                        if (data != null) {
                            skill2.description = data;
                        }
                        this.petSkillDict.put(Integer.parseInt(id), skill2);
                    }
                    PetSkillDict.log.debug("\u52a0\u8f7d\u5ba0\u7269\u6280\u80fd\u6570\u636e\u5b8c\u6210...");
                }
            }
        } catch (Exception e) {
            PetSkillDict.log.error("\u52a0\u8f7d\u5ba0\u7269\u6280\u80fd\u6570\u636e error : ", (Throwable) e);
        }
    }

    public void loadPetSkillEffectData(final String _dataPath) {
        try {
            File file = new File(_dataPath);
            if (file.exists()) {
                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                Element rootE = document.getRootElement();
                Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                while (rootIt.hasNext()) {
                    PetAdditionEffect pef = new PetAdditionEffect();
                    Element subE = rootIt.next();
                    String data = subE.elementTextTrim("id");
                    pef.id = Integer.parseInt(data);
                    data = subE.elementTextTrim("name");
                    pef.name = data;
                    data = subE.elementTextTrim("needLevel");
                    pef.needLevel = Integer.parseInt(data);
                    data = subE.elementTextTrim("type");
                    if (data != null) {
                        pef.additonType = Effect.EffectFeature.get(data);
                    }
                    data = subE.elementTextTrim("level");
                    if (data != null) {
                        pef.additionLevel = Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("disAfterDie");
                    if (data != null) {
                        pef.disAfterDie = data.equals("\u662f");
                    } else {
                        pef.disAfterDie = false;
                    }
                    data = subE.elementTextTrim("isBuff");
                    if (data != null) {
                        pef.isBuff = data.equals("\u662f");
                    } else {
                        pef.isBuff = false;
                    }
                    data = subE.elementTextTrim("replaceID");
                    if (data != null) {
                        pef.replaceID = Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("keepTime");
                    if (data != null) {
                        pef.keepTime = Float.parseFloat(data);
                    }
                    data = subE.elementTextTrim("hpHarmValue");
                    if (data != null) {
                        pef.harmHpValue = Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("harmType");
                    if (data != null) {
                        pef.harmType = EHarmType.get(data);
                    }
                    data = subE.elementTextTrim("resumeHp");
                    if (data != null) {
                        pef.resumeHp = Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("resumeMp");
                    if (data != null) {
                        pef.resumeMp = Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("calMode");
                    if (data != null) {
                        pef.caluOperator = EMathCaluOperator.get(data);
                    }
                    data = subE.elementTextTrim("str");
                    if (data != null) {
                        pef.strength = (float) Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("agile");
                    if (data != null) {
                        pef.agility = (float) Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("intel");
                    if (data != null) {
                        pef.inte = (float) Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("spi");
                    if (data != null) {
                        pef.spirit = (float) Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("luck");
                    if (data != null) {
                        pef.lucky = (float) Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("maxMp");
                    if (data != null) {
                        pef.maxMp = (float) Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("hitLevel");
                    if (data != null) {
                        pef.hitLevel = (float) Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("physicsDeathblowLevel");
                    if (data != null) {
                        pef.physicsDeathblowLevel = Float.parseFloat(data);
                    }
                    data = subE.elementTextTrim("magicDeathblowLevel");
                    if (data != null) {
                        pef.magicDeathblowLevel = Float.parseFloat(data);
                    }
                    data = subE.elementTextTrim("physicsAttackHarmValue");
                    if (data != null) {
                        pef.physicsAttackHarmValue = (float) Integer.parseInt(data);
                    }
                    data = subE.elementTextTrim("magicHarmType");
                    if (data != null) {
                        pef.magicHarmType = EMagic.getMagic(data);
                    }
                    data = subE.elementTextTrim("magicHarmValue");
                    if (data != null) {
                        pef.magicHarmValue = Float.parseFloat(data);
                    }
                    data = subE.elementTextTrim("physicsAttackInterval");
                    if (data != null) {
                        pef.physicsAttackInterval = Float.parseFloat(data);
                    }
                    data = subE.elementTextTrim("iconID");
                    if (data != null) {
                        pef.iconID = Short.parseShort(data);
                    }
                    data = subE.elementTextTrim("description");
                    if (data != null) {
                        pef.description = data;
                    }
                    this.petAdditionEffectDict.put(pef.id, pef);
                }
                PetSkillDict.log.debug("\u52a0\u8f7d\u5ba0\u7269\u6280\u80fd\u6301\u7eed\u578b\u6548\u679c\u6570\u636e\u5b8c\u6210...");
            }
        } catch (Exception e) {
            PetSkillDict.log.error("\u52a0\u8f7d\u5ba0\u7269\u6280\u80fd\u6301\u7eed\u578b\u6548\u679c\u6570\u636e error : ", (Throwable) e);
        }
    }
}
