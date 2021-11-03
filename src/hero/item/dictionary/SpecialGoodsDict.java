// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import hero.item.Goods;
import hero.item.special.RepeateTaskExpan;
import hero.item.special.HeavenBook;
import hero.item.special.PetSkillBook;
import hero.pet.FeedType;
import hero.item.special.PetFeed;
import hero.item.special.SkillBook;
import hero.item.special.PetArchetype;
import hero.item.special.Drawings;
import hero.item.special.ESpecialGoodsType;
import hero.item.special.SpecialGoodsBuilder;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import hero.item.SpecialGiftBagData;
import hero.item.SpecialGoods;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class SpecialGoodsDict {

    private static Logger log;
    private static SpecialGoodsDict instance;
    private FastMap<Integer, SpecialGoods> dictionary;
    private FastMap<Integer, SpecialGiftBagData> giftBagDictionary;

    static {
        SpecialGoodsDict.log = Logger.getLogger((Class) SpecialGoodsDict.class);
    }

    private SpecialGoodsDict() {
        this.dictionary = (FastMap<Integer, SpecialGoods>) new FastMap();
        this.giftBagDictionary = (FastMap<Integer, SpecialGiftBagData>) new FastMap();
    }

    public static SpecialGoodsDict getInstance() {
        if (SpecialGoodsDict.instance == null) {
            SpecialGoodsDict.instance = new SpecialGoodsDict();
        }
        return SpecialGoodsDict.instance;
    }

    public Object[] getSpecialGoodsList() {
        return this.dictionary.values().toArray();
    }

    public SpecialGoods getSpecailGoods(final int _goodsID) {
        return (SpecialGoods) this.dictionary.get(_goodsID);
    }

    public SpecialGiftBagData getBagData(final int _giftBagID) {
        return (SpecialGiftBagData) this.giftBagDictionary.get(_giftBagID);
    }

    public void loadGiftBagData(final String _dataPath) {
        File dataPath;
        try {
            dataPath = new File(_dataPath);
        } catch (Exception e2) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _dataPath);
            return;
        }
        File[] dataFileList = dataPath.listFiles();
        try {
            File[] array;
            for (int length = (array = dataFileList).length, j = 0; j < length; ++j) {
                File dataFile = array[j];
                if (dataFile.getName().endsWith(".xml")) {
                    SpecialGoodsDict.log.debug((" special Goods xml name = " + dataFile.getName()));
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    SpecialGiftBagData giftBag = null;
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            giftBag = new SpecialGiftBagData();
                            giftBag.id = Integer.valueOf(subE.elementTextTrim("id"));
                            String data = subE.elementTextTrim("goodsSum");
                            if (data == null || data.equals("")) {
                                continue;
                            }
                            giftBag.goodsSum = Integer.valueOf(data);
                            giftBag.goodsList = new int[giftBag.goodsSum];
                            giftBag.numberList = new int[giftBag.goodsSum];
                            for (int i = 0; i < giftBag.goodsSum; ++i) {
                                giftBag.goodsList[i] = Integer.valueOf(subE.elementTextTrim("goods" + (i + 1) + "ID"));
                                giftBag.numberList[i] = Integer.valueOf(subE.elementTextTrim("goods" + (i + 1) + "Num"));
                            }
                        }
                        if (!this.giftBagDictionary.containsKey(giftBag.id)) {
                            this.giftBagDictionary.put(giftBag.id, giftBag);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(final String _dataPath) {
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
                    SpecialGoodsDict.log.debug((" special Goods xml name = " + dataFile.getName()));
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            int id = Integer.parseInt(subE.elementTextTrim("id"));
                            SpecialGoods goods = SpecialGoodsBuilder.build(id, Short.parseShort(subE.elementTextTrim("stackNumber")), subE.elementTextTrim("type"));
                            if (goods == null) {
                                continue;
                            }
                            goods.setName(subE.elementTextTrim("name"));
                            goods.setTrait(subE.elementTextTrim("trait"));
                            goods.setNeedLevel(Short.parseShort(subE.elementTextTrim("levelLimit")));
                            if (subE.elementTextTrim("useable").equals("\u662f")) {
                                goods.setUseable();
                            }
                            if (subE.elementTextTrim("isOnlyInBag").equals("\u662f")) {
                                goods.setOnly();
                            }
                            if (subE.elementTextTrim("exchangeable").equals("\u662f")) {
                                goods.setExchangeable();
                            }
                            if (subE.elementTextTrim("sellable").equals("\u662f")) {
                                goods.setCanBeSell();
                                goods.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                            }
                            String data = subE.elementTextTrim("price");
                            if (data != null) {
                                goods.setPrice(Integer.parseInt(data));
                            }
                            goods.appendDescription(subE.elementTextTrim("description"));
                            goods.setIconID(Short.parseShort(subE.elementTextTrim("icon")));
                            if (ESpecialGoodsType.DRAWINGS == goods.getType()) {
                                ((Drawings) goods).setNeedManufactureType(subE.elementTextTrim("needSkill"));
                                ((Drawings) goods).setManufactureItemID(Integer.parseInt(subE.elementTextTrim("getSkillItemID")));
                                int goodsID = Integer.parseInt(subE.elementTextTrim("getItemID"));
                                Goods getGoods = GoodsContents.getGoods(goodsID);
                                if (getGoods != null) {
                                    goods.appendDescription(getGoods.getDescription());
                                }
                            } else if (ESpecialGoodsType.PET_ARCHETYPE == goods.getType()) {
                                int petAID = Integer.parseInt(subE.elementTextTrim("id"));
                                ((PetArchetype) goods).setPetAID(petAID);
                            } else if (ESpecialGoodsType.SKILL_BOOK == goods.getType()) {
                                ((SkillBook) goods).setSkillID(Integer.parseInt(subE.elementTextTrim("getSkillItemID")));
                            } else if (ESpecialGoodsType.PET_FEED == goods.getType()) {
                                data = subE.elementTextTrim("feedtype");
                                ((PetFeed) goods).setFeedType(FeedType.getFeedType(data));
                            } else if (ESpecialGoodsType.PET_REVIVE != goods.getType() && ESpecialGoodsType.PET_DICARD != goods.getType()) {
                                if (ESpecialGoodsType.PET_SKILL_BOOK == goods.getType()) {
                                    ((PetSkillBook) goods).setSkillID(Integer.parseInt(subE.elementTextTrim("getSkillItemID")));
                                } else if (ESpecialGoodsType.HEAVEN_BOOK == goods.getType()) {
                                    data = subE.elementTextTrim("getHeavenBookPoint");
                                    ((HeavenBook) goods).setSkillPoint(Short.parseShort(data));
                                } else if (ESpecialGoodsType.REPEATE_TASK_EXPAN == goods.getType()) {
                                    data = subE.elementTextTrim("use_times");
                                    ((RepeateTaskExpan) goods).setUsedTimes(Integer.parseInt(data));
                                }
                            }
                            this.dictionary.put(goods.getID(), goods);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
