// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.legacy;

import hero.item.Goods;
import hero.item.detail.EGoodsType;
import hero.item.dictionary.GoodsContents;
import org.dom4j.Document;
import hero.item.detail.EGoodsTrait;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.item.service.GoodsConfig;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;
import hero.item.Material;
import hero.item.Equipment;
import hero.item.Medicament;
import javolution.util.FastList;
import org.apache.log4j.Logger;

public class WorldLegacyDict {

    private static Logger log;
    private FastList<WorldLegacy> medicamentList;
    private FastList<Medicament> medicamentGoodsList;
    private FastList<WorldLegacy> equipmentList;
    private FastList<Equipment> equipmentGoodsList;
    private FastList<WorldLegacy> materialList;
    private FastList<Material> materialGoodsList;
    private static WorldLegacyDict instance;
    private static final Random RANDOM_BUILDER;
    private static final int ODDS_ENLARGE_MODULUE = 1000000;

    static {
        WorldLegacyDict.log = Logger.getLogger((Class) WorldLegacyDict.class);
        RANDOM_BUILDER = new Random();
    }

    private WorldLegacyDict() {
        this.medicamentList = (FastList<WorldLegacy>) new FastList();
        this.equipmentList = (FastList<WorldLegacy>) new FastList();
        this.materialList = (FastList<WorldLegacy>) new FastList();
    }

    public ArrayList<Integer> getLegacyGoodID(final short _monsterLevel) {
        ArrayList<Integer> goodsIDList = new ArrayList<Integer>();
        for (final WorldLegacy legacy : this.medicamentList) {
            if (legacy.matchLimit(_monsterLevel) && legacy.legacyOdds()) {
                int number = legacy.number;
                Random random = new Random();
                int size = legacy.goodsIDList.size();
                if (size <= 0) {
                    continue;
                }
                for (int i = 0; i < number; ++i) {
                    int index = random.nextInt(size);
                    goodsIDList.add(legacy.goodsIDList.get(index));
                }
            }
        }
        for (final WorldLegacy legacy : this.materialList) {
            if (legacy.matchLimit(_monsterLevel) && legacy.legacyOdds()) {
                int number = legacy.number;
                Random random = new Random();
                int size = legacy.goodsIDList.size();
                if (size <= 0) {
                    continue;
                }
                for (int i = 0; i < number; ++i) {
                    int index = random.nextInt(size);
                    goodsIDList.add(legacy.goodsIDList.get(index));
                }
            }
        }
        for (final WorldLegacy legacy : this.equipmentList) {
            if (legacy.matchLimit(_monsterLevel) && legacy.legacyOdds()) {
                int number = legacy.number;
                Random random = new Random();
                int size = legacy.goodsIDList.size();
                if (size <= 0) {
                    continue;
                }
                for (int i = 0; i < number; ++i) {
                    int index = random.nextInt(size);
                    goodsIDList.add(legacy.goodsIDList.get(index));
                }
            }
        }
        return goodsIDList;
    }

    public void load(final GoodsConfig config) {
        try {
            File dataPath = new File(config.world_legacy_material_data_path);
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
                            try {
                                WorldLegacy legacy = new WorldLegacy(Short.parseShort(subE.elementTextTrim("startLevel")), Short.parseShort(subE.elementTextTrim("endLevel")), Integer.parseInt(subE.elementTextTrim("number")), Integer.parseInt(subE.elementTextTrim("itemStartID")), Integer.parseInt(subE.elementTextTrim("itemEndID")));
                                String data = subE.elementTextTrim("startItemLevel");
                                if (data != null) {
                                    legacy.startItemLevel = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("endItemLevel");
                                if (data != null) {
                                    legacy.endItemLevel = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("trait");
                                if (data != null) {
                                    legacy.trait = EGoodsTrait.getTrait(data);
                                }
                                data = subE.elementTextTrim("odds");
                                legacy.odds = Float.parseFloat(data);
                                this.materialList.add(legacy);
                            } catch (Exception e) {
                                WorldLegacyDict.log.error("\u52a0\u8f7d\u4e16\u754c\u6389\u843d\u6750\u6599\u7269\u54c1\u6570\u636e\u51fa\u9519:", (Throwable) e);
                            }
                        }
                    }
                }
            }
            dataPath = new File(config.world_legacy_equip_data_path);
            dataFileList = dataPath.listFiles();
            File[] array2;
            for (int length2 = (array2 = dataFileList).length, j = 0; j < length2; ++j) {
                File dataFile = array2[j];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            try {
                                WorldLegacy legacy = new WorldLegacy(Short.parseShort(subE.elementTextTrim("startLevel")), Short.parseShort(subE.elementTextTrim("endLevel")), Integer.parseInt(subE.elementTextTrim("number")), Integer.parseInt(subE.elementTextTrim("itemStartID")), Integer.parseInt(subE.elementTextTrim("itemEndID")));
                                String data = subE.elementTextTrim("startItemLevel");
                                if (data != null) {
                                    legacy.startItemLevel = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("endItemLevel");
                                if (data != null) {
                                    legacy.endItemLevel = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("trait");
                                if (data != null) {
                                    legacy.trait = EGoodsTrait.getTrait(data);
                                }
                                data = subE.elementTextTrim("odds");
                                legacy.odds = Float.parseFloat(data);
                                this.equipmentList.add(legacy);
                            } catch (Exception e) {
                                WorldLegacyDict.log.error("\u52a0\u8f7d\u4e16\u754c\u6389\u843d\u88c5\u5907\u7269\u54c1\u6570\u636e\u51fa\u9519", (Throwable) e);
                            }
                        }
                    }
                }
            }
            dataPath = new File(config.world_legacy_medicament_data_path);
            dataFileList = dataPath.listFiles();
            File[] array3;
            for (int length3 = (array3 = dataFileList).length, k = 0; k < length3; ++k) {
                File dataFile = array3[k];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            try {
                                WorldLegacy legacy = new WorldLegacy(Short.parseShort(subE.elementTextTrim("startLevel")), Short.parseShort(subE.elementTextTrim("endLevel")), Integer.parseInt(subE.elementTextTrim("number")), Integer.parseInt(subE.elementTextTrim("itemStartID")), Integer.parseInt(subE.elementTextTrim("itemEndID")));
                                String data = subE.elementTextTrim("startItemLevel");
                                if (data != null) {
                                    legacy.startItemLevel = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("endItemLevel");
                                if (data != null) {
                                    legacy.endItemLevel = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("trait");
                                if (data != null) {
                                    legacy.trait = EGoodsTrait.getTrait(data);
                                }
                                data = subE.elementTextTrim("odds");
                                legacy.odds = Float.parseFloat(data);
                                this.medicamentList.add(legacy);
                            } catch (Exception e) {
                                WorldLegacyDict.log.error("\u52a0\u8f7d\u4e16\u754c\u6389\u843d\u836f\u6c34\u7269\u54c1\u6570\u636e\u51fa\u9519", (Throwable) e);
                            }
                        }
                    }
                }
            }
            for (final WorldLegacy w : this.medicamentList) {
                w.loadAllLegacyGoodsID();
            }
            for (final WorldLegacy w : this.materialList) {
                w.loadAllLegacyGoodsID();
            }
            for (final WorldLegacy w : this.equipmentList) {
                w.loadAllLegacyGoodsID();
            }
        } catch (Exception e2) {
            WorldLegacyDict.log.error("\u52a0\u8f7d\u4e16\u754c\u6389\u843d\u7269\u54c1\u51fa\u9519\uff1a", (Throwable) e2);
        }
    }

    public static WorldLegacyDict getInstance() {
        if (WorldLegacyDict.instance == null) {
            WorldLegacyDict.instance = new WorldLegacyDict();
        }
        return WorldLegacyDict.instance;
    }

    private class WorldLegacy {

        public short startMonsterLevel;
        public short endMonsterLevel;
        public int number;
        public int startItemID;
        public int endItemID;
        public int startItemLevel;
        public int endItemLevel;
        public EGoodsTrait trait;
        public float odds;
        public ArrayList<Integer> goodsIDList;

        public WorldLegacy(final short _startMonsterLevel, final short _endMonsterLevel, final int _number, final int _startItemID, final int _endItemID) {
            this.startMonsterLevel = _startMonsterLevel;
            this.endMonsterLevel = _endMonsterLevel;
            this.number = _number;
            this.startItemID = _startItemID;
            this.endItemID = _endItemID;
            this.goodsIDList = new ArrayList<Integer>();
        }

        public boolean matchLimit(final short _monsterLevel) {
            return _monsterLevel >= this.startMonsterLevel && _monsterLevel <= this.endMonsterLevel;
        }

        public boolean legacyOdds() {
            return this.odds > 0.0f && WorldLegacyDict.RANDOM_BUILDER.nextInt(1000000) <= this.odds * 1000000.0f;
        }

        public void loadAllLegacyGoodsID() {
            FastList<Goods> goodslist = (FastList<Goods>) new FastList();
            for (int i = this.startItemID; i <= this.endItemID; ++i) {
                Goods goods = GoodsContents.getGoods(i);
                if (goods != null && this.trait == goods.getTrait() && goods.getNeedLevel() >= this.startItemLevel && goods.getNeedLevel() <= this.endItemLevel) {
                    WorldLegacyDict.log.debug(("monster level[" + this.startMonsterLevel + " -- " + this.endMonsterLevel + "],world legacy goodsid = " + goods.getID() + ",trait=" + this.trait));
                    if (goods.getGoodsType() == EGoodsType.EQUIPMENT) {
                        Equipment eq = (Equipment) goods;
                        if (eq.getBindType() == 2 || eq.getBindType() == 1) {
                            goodslist.add(goods);
                        }
                    } else {
                        goodslist.add(goods);
                    }
                }
            }
            if (goodslist.size() > 0) {
                for (final Goods goods2 : goodslist) {
                    this.goodsIDList.add(goods2.getID());
                }
            }
        }
    }
}
