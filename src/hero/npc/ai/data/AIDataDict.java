// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai.data;

import hero.share.EMagic;
import hero.skill.service.SkillServiceImpl;
import hero.skill.ActiveSkill;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import hero.npc.ai.SkillAI;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class AIDataDict {

    private static Logger log;
    private FastMap<Integer, Shout> shoutDataTable;
    private FastMap<Integer, Call> callDataTable;
    private FastMap<Integer, Changes> changesDataTable;
    private FastMap<Integer, Disappear> disappearDataTable;
    private FastMap<Integer, RunAway> runAwayDataTable;
    private FastMap<Integer, SpecialAIData> specialAIDataTable;
    private FastMap<Integer, SkillAIData> skillAIDataTable;
    private FastMap<Integer, FightAIData> fightAIDataTable;
    private static AIDataDict instance;

    static {
        AIDataDict.log = Logger.getLogger((Class) AIDataDict.class);
    }

    public static AIDataDict getInstance() {
        if (AIDataDict.instance == null) {
            AIDataDict.instance = new AIDataDict();
        }
        return AIDataDict.instance;
    }

    private AIDataDict() {
        this.shoutDataTable = (FastMap<Integer, Shout>) new FastMap();
        this.callDataTable = (FastMap<Integer, Call>) new FastMap();
        this.changesDataTable = (FastMap<Integer, Changes>) new FastMap();
        this.disappearDataTable = (FastMap<Integer, Disappear>) new FastMap();
        this.runAwayDataTable = (FastMap<Integer, RunAway>) new FastMap();
        this.specialAIDataTable = (FastMap<Integer, SpecialAIData>) new FastMap();
        this.skillAIDataTable = (FastMap<Integer, SkillAIData>) new FastMap();
        this.fightAIDataTable = (FastMap<Integer, FightAIData>) new FastMap();
    }

    public void load(final String _shoutDataPath, final String _callDataPath, final String _changesDataPath, final String _disappearDataPath, final String _runAwayDataPath, final String _specialAIDataPath, final String _skillAIDataPath, final String _fightAIDataPath) {
        this.shoutDataTable.clear();
        this.callDataTable.clear();
        this.changesDataTable.clear();
        this.disappearDataTable.clear();
        this.runAwayDataTable.clear();
        this.specialAIDataTable.clear();
        this.skillAIDataTable.clear();
        this.fightAIDataTable.clear();
        this.loadSkillAIData(_skillAIDataPath);
        this.loadShoutData(_shoutDataPath);
        this.loadCallData(_callDataPath);
        this.loadDisappearData(_disappearDataPath);
        this.loadRunAwayData(_runAwayDataPath);
        this.loadChangesData(_changesDataPath);
        this.loadSpecialAIData(_specialAIDataPath);
        this.loadFightAIData(_fightAIDataPath);
    }

    public FightAIData getFightAIData(final int _fightAIID) {
        if (_fightAIID > 0) {
            return (FightAIData) this.fightAIDataTable.get(_fightAIID);
        }
        return null;
    }

    public Shout getShoutData(final int _id) {
        return (Shout) this.shoutDataTable.get(_id);
    }

    public Call getCallData(final int _id) {
        return (Call) this.callDataTable.get(_id);
    }

    public Changes getChangesData(final int _id) {
        return (Changes) this.changesDataTable.get(_id);
    }

    public Disappear getDisappearData(final int _id) {
        return (Disappear) this.disappearDataTable.get(_id);
    }

    public RunAway getRunAwayData(final int _id) {
        return (RunAway) this.runAwayDataTable.get(_id);
    }

    public SkillAI[] buildSkillAIList(final SkillAIData[] _dataList, final float _traceHpPercent) {
        if (_dataList != null && _dataList.length > 0) {
            SkillAI[] skillAIList = new SkillAI[_dataList.length];
            for (int i = 0; i < _dataList.length; ++i) {
                skillAIList[i] = new SkillAI(_dataList[i], _traceHpPercent);
            }
            return skillAIList;
        }
        return null;
    }

    public SpecialWisdom getSpecialWisdom(final SpecialAIData _specialAIData) {
        switch (_specialAIData.specialWisdomType) {
            case 1: {
                return (SpecialWisdom) this.callDataTable.get(_specialAIData.specialWisdomID);
            }
            case 2: {
                return (SpecialWisdom) this.changesDataTable.get(_specialAIData.specialWisdomID);
            }
            case 4: {
                return (SpecialWisdom) this.runAwayDataTable.get(_specialAIData.specialWisdomID);
            }
            case 3: {
                return (SpecialWisdom) this.disappearDataTable.get(_specialAIData.specialWisdomID);
            }
            case 5: {
                return (SpecialWisdom) this.shoutDataTable.get(_specialAIData.specialWisdomID);
            }
            default: {
                return null;
            }
        }
    }

    private void loadShoutData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
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
                            Shout shout = new Shout();
                            shout.id = Integer.parseInt(subE.elementTextTrim("id"));
                            shout.shoutContent = subE.elementTextTrim("shoutContent");
                            this.shoutDataTable.put(shout.id, shout);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCallData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
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
                            Call call = new Call();
                            call.id = Integer.parseInt(subE.elementTextTrim("id"));
                            call.shoutContent = subE.elementTextTrim("shoutContent");
                            byte npcTypeNumber = Byte.parseByte(subE.elementTextTrim("npcTypeNumber"));
                            if (npcTypeNumber > 0) {
                                call.monsterModelIDs = new String[npcTypeNumber];
                                call.monsterDataArray = new short[npcTypeNumber][4];
                                for (int i = 0; i < npcTypeNumber; ++i) {
                                    call.monsterModelIDs[i] = subE.elementTextTrim("npc" + (i + 1) + "Id");
                                    call.monsterDataArray[i][0] = Short.parseShort(subE.elementTextTrim("npc" + (i + 1) + "Number"));
                                    String locationType = subE.elementTextTrim("npc" + (i + 1) + "LocationType");
                                    if (locationType.equals("\u5730\u56fe\u56fa\u5b9a\u5750\u6807")) {
                                        call.monsterDataArray[i][1] = 1;
                                    } else {
                                        call.monsterDataArray[i][1] = 2;
                                    }
                                    call.monsterDataArray[i][2] = Short.parseShort(subE.elementTextTrim("npc" + (i + 1) + "X"));
                                    call.monsterDataArray[i][3] = Short.parseShort(subE.elementTextTrim("npc" + (i + 1) + "Y"));
                                }
                            }
                            this.callDataTable.put(call.id, call);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDisappearData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
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
                            Disappear disappear = new Disappear();
                            disappear.id = Integer.parseInt(subE.elementTextTrim("id"));
                            disappear.keepTime = Integer.parseInt(subE.elementTextTrim("keepTime")) * 1000;
                            disappear.shoutContent = subE.elementTextTrim("shoutContent");
                            this.disappearDataTable.put(disappear.id, disappear);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRunAwayData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
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
                            RunAway runAway = new RunAway();
                            runAway.id = Integer.parseInt(subE.elementTextTrim("id"));
                            runAway.range = Short.parseShort(subE.elementTextTrim("range"));
                            runAway.shoutContent = subE.elementTextTrim("shoutContent");
                            this.runAwayDataTable.put(runAway.id, runAway);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSkillAIData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
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
                            SkillAIData skillAI = new SkillAIData();
                            skillAI.id = Integer.parseInt(subE.elementTextTrim("id"));
                            String useTimesType = subE.elementTextTrim("useTimesType");
                            if (useTimesType.equals("\u95f4\u9694\u578b")) {
                                skillAI.useTimesType = 1;
                            } else {
                                skillAI.useTimesType = 2;
                            }
                            if (1 == skillAI.useTimesType) {
                                String conditionOfInterval = subE.elementTextTrim("hpConsumePercent");
                                if (conditionOfInterval != null) {
                                    skillAI.intervalCondition = 2;
                                } else {
                                    conditionOfInterval = subE.elementTextTrim("intervalTime");
                                    if (conditionOfInterval != null) {
                                        skillAI.intervalCondition = 1;
                                    } else {
                                        conditionOfInterval = subE.elementTextTrim("hatredTargetDie");
                                        if (conditionOfInterval == null || !conditionOfInterval.equals("\u662f")) {
                                            continue;
                                        }
                                        skillAI.intervalCondition = 3;
                                    }
                                }
                                skillAI.odds = Integer.parseInt(subE.elementTextTrim("odds")) / 100.0f;
                            } else {
                                String conditionOfOnly = subE.elementTextTrim("timeOfInFighting");
                                if (conditionOfOnly != null) {
                                    skillAI.onlyReleaseCondition = 1;
                                    skillAI.timeOfFighting = Integer.parseInt(conditionOfOnly) * 1000;
                                } else {
                                    conditionOfOnly = subE.elementTextTrim("hpTracePercent");
                                    if (conditionOfOnly != null) {
                                        skillAI.onlyReleaseCondition = 2;
                                        skillAI.hpTracePercent = Integer.parseInt(conditionOfOnly) / 100.0f;
                                    } else {
                                        conditionOfOnly = subE.elementTextTrim("mpTracePercent");
                                        if (conditionOfOnly == null) {
                                            continue;
                                        }
                                        skillAI.onlyReleaseCondition = 3;
                                        skillAI.mpTracePercent = Integer.parseInt(conditionOfOnly) / 100.0f;
                                    }
                                }
                            }
                            String targetSettingCondition = subE.elementTextTrim("targetSettingCondition");
                            if (targetSettingCondition.equals("\u4ec7\u6068\u503c") || targetSettingCondition.equals("\u4ec7\u6068")) {
                                skillAI.targetSettingCondition = 1;
                            } else if (targetSettingCondition.equals("\u751f\u547d\u503c")) {
                                skillAI.targetSettingCondition = 2;
                            } else if (targetSettingCondition.equals("\u9b54\u6cd5\u503c")) {
                                skillAI.targetSettingCondition = 3;
                            } else if (targetSettingCondition.equals("\u8ddd\u79bb")) {
                                skillAI.targetSettingCondition = 4;
                            } else {
                                if (!targetSettingCondition.equals("\u81ea\u8eab")) {
                                    continue;
                                }
                                skillAI.targetSettingCondition = 5;
                            }
                            if (5 != skillAI.targetSettingCondition) {
                                skillAI.sequenceOfSettingCondition = Byte.parseByte(subE.elementTextTrim("conditionSequence"));
                            }
                            String releaseDelay = subE.elementTextTrim("delay");
                            if (releaseDelay != null) {
                                skillAI.releaseDelay = Integer.parseInt(releaseDelay) * 1000;
                            }
                            int skillId = Integer.parseInt(subE.elementTextTrim("skillId"));
                            skillAI.shoutContentWhenRelease = subE.elementTextTrim("shoutContent");
                            skillAI.skill = (ActiveSkill) SkillServiceImpl.getInstance().getMonsterSkillModel(skillId);
                            if (skillAI.skill == null) {
                                AIDataDict.log.warn("wrong:\u52a0\u8f7d\u7684\u602a\u7269\u6280\u80fd\u4e3aNULL,\u8bf7\u68c0\u67e5\u914d\u7f6e\u6587\u4ef6");
                            }
                            this.skillAIDataTable.put(skillAI.id, skillAI);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChangesData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
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
                            Changes changes = new Changes();
                            changes.id = Integer.parseInt(subE.elementTextTrim("id"));
                            changes.shoutContent = subE.elementTextTrim("shoutContent");
                            changes.strength = Integer.parseInt(subE.elementTextTrim("strength"));
                            changes.agility = Integer.parseInt(subE.elementTextTrim("agility"));
                            changes.inte = Integer.parseInt(subE.elementTextTrim("inte"));
                            changes.spirit = Integer.parseInt(subE.elementTextTrim("spirit"));
                            changes.lucky = Integer.parseInt(subE.elementTextTrim("lucky"));
                            changes.minAttack = Integer.parseInt(subE.elementTextTrim("minAttack"));
                            changes.maxAttack = Integer.parseInt(subE.elementTextTrim("maxAttack"));
                            String magicType = subE.elementTextTrim("magic");
                            if (magicType != null) {
                                changes.magicType = EMagic.getMagic(magicType);
                                changes.minDamageValue = Integer.parseInt(subE.elementTextTrim("minDamageValue"));
                                changes.maxDamageValue = Integer.parseInt(subE.elementTextTrim("maxDamageValue"));
                            }
                            changes.defense = Integer.parseInt(subE.elementTextTrim("defense"));
                            changes.sanctityFastness = Integer.parseInt(subE.elementTextTrim("sanctityFastness"));
                            changes.fireFastness = Integer.parseInt(subE.elementTextTrim("fireFastness"));
                            changes.waterFastness = Integer.parseInt(subE.elementTextTrim("waterFastness"));
                            changes.soilFastness = Integer.parseInt(subE.elementTextTrim("soilFastness"));
                            if (subE.elementTextTrim("useNewHp").equals("\u662f")) {
                                changes.newHp = Integer.parseInt(subE.elementTextTrim("newHp"));
                            }
                            byte skillAiNumber = Byte.parseByte(subE.elementTextTrim("skillAiNumber"));
                            if (skillAiNumber > 0) {
                                changes.skillAIDataList = new SkillAIData[skillAiNumber];
                                for (int i = 0; i < skillAiNumber; ++i) {
                                    changes.skillAIDataList[i] = (SkillAIData) this.skillAIDataTable.get(Integer.parseInt(subE.elementTextTrim("skillAi" + (i + 1))));
                                }
                            }
                            changes.imageID = Short.parseShort(subE.elementTextTrim("imageId"));
                            this.changesDataTable.put(changes.id, changes);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSpecialAIData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
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
                            SpecialAIData specialAI = new SpecialAIData();
                            specialAI.id = Integer.parseInt(subE.elementTextTrim("id"));
                            String emergeTimesType = subE.elementTextTrim("emergeTimesType");
                            if (emergeTimesType.equals("\u95f4\u9694\u578b")) {
                                specialAI.useTimesType = 1;
                            } else {
                                specialAI.useTimesType = 2;
                            }
                            if (1 == specialAI.useTimesType) {
                                String conditionOfInterval = subE.elementTextTrim("hpConsumePercent");
                                if (conditionOfInterval != null) {
                                    specialAI.intervalCondition = 2;
                                    specialAI.hpConsumePercent = Integer.parseInt(conditionOfInterval) / 100.0f;
                                } else {
                                    conditionOfInterval = subE.elementTextTrim("intervalTime");
                                    if (conditionOfInterval != null) {
                                        specialAI.intervalCondition = 1;
                                        specialAI.intervalTime = Integer.parseInt(conditionOfInterval) * 1000;
                                    } else {
                                        conditionOfInterval = subE.elementTextTrim("hatredTargetDie");
                                        if (!conditionOfInterval.equals("\u662f")) {
                                            continue;
                                        }
                                        specialAI.intervalCondition = 3;
                                    }
                                }
                            } else {
                                String conditionOfOnly = subE.elementTextTrim("hpTracePercent");
                                if (conditionOfOnly != null) {
                                    specialAI.onlyReleaseCondition = 1;
                                    specialAI.hpTracePercent = Integer.parseInt(conditionOfOnly) / 100.0f;
                                } else {
                                    conditionOfOnly = subE.elementTextTrim("mpTracePercent");
                                    if (conditionOfOnly == null) {
                                        continue;
                                    }
                                    specialAI.onlyReleaseCondition = 2;
                                    specialAI.mpTracePercent = Integer.parseInt(conditionOfOnly) / 100.0f;
                                }
                            }
                            String specialWisdomType = subE.elementTextTrim("type");
                            int specialWisdomID = Integer.parseInt(subE.elementTextTrim("subAiId"));
                            specialAI.specialWisdomID = specialWisdomID;
                            if (specialWisdomType.equals("\u53d8\u8eab")) {
                                specialAI.specialWisdomType = 2;
                            } else if (specialWisdomType.equals("\u53ec\u5524")) {
                                specialAI.specialWisdomType = 1;
                            } else if (specialWisdomType.equals("\u6d88\u5931")) {
                                specialAI.specialWisdomType = 3;
                            } else if (specialWisdomType.equals("\u9003\u8dd1")) {
                                specialAI.specialWisdomType = 4;
                            } else {
                                if (!specialWisdomType.equals("\u558a\u8bdd")) {
                                    continue;
                                }
                                specialAI.specialWisdomType = 5;
                            }
                            this.specialAIDataTable.put(specialAI.id, specialAI);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFightAIData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        try {
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, k = 0; k < length; ++k) {
                File dataFile = array[k];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            FightAIData ai = new FightAIData();
                            ai.id = Integer.parseInt(subE.elementTextTrim("id"));
                            byte aiNumber = Byte.parseByte(subE.elementTextTrim("specialAiNumber"));
                            if (aiNumber > 0) {
                                ai.specialAIList = new SpecialAIData[aiNumber];
                                for (int i = 1; i <= aiNumber; ++i) {
                                    ai.specialAIList[i - 1] = (SpecialAIData) this.specialAIDataTable.get(Integer.parseInt(subE.elementTextTrim("specialAi" + i)));
                                }
                            }
                            aiNumber = Byte.parseByte(subE.elementTextTrim("skillAiNumber"));
                            if (aiNumber > 0) {
                                ai.skillAIList = new SkillAIData[aiNumber];
                                int skillAIID = 0;
                                SkillAIData data = null;
                                for (int j = 0; j < aiNumber; ++j) {
                                    skillAIID = Integer.parseInt(subE.elementTextTrim("skillAi" + (j + 1)));
                                    data = (SkillAIData) this.skillAIDataTable.get(skillAIID);
                                    if (data != null) {
                                        ai.skillAIList[j] = data;
                                    } else {
                                        AIDataDict.log.error(("\u52a0\u8f7d\u602a\u7269\u6280\u80fdAI\u5931\u8d25skillAIID=" + skillAIID));
                                    }
                                }
                            }
                            this.fightAIDataTable.put(ai.id, ai);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
