// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import java.util.Iterator;
import org.dom4j.Document;
import hero.item.dictionary.GoodsContents;
import hero.item.Goods;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.ArrayList;
import javolution.util.FastMap;

public class EvidenveGiftDict {

    private FastMap<Integer, EvidenveAward> awardDict;
    private ArrayList<EvidenveData> evidenveList;
    private static EvidenveGiftDict instance;

    public static EvidenveGiftDict getInstance() {
        if (EvidenveGiftDict.instance == null) {
            EvidenveGiftDict.instance = new EvidenveGiftDict();
        }
        return EvidenveGiftDict.instance;
    }

    private EvidenveGiftDict() {
    }

    public static void main(final String[] args) {
        getInstance().load(String.valueOf(System.getProperty("user.dir")) + "/" + "res/data/npc/normal_npc/fun_data/evidenve_gift/", String.valueOf(System.getProperty("user.dir")) + "/" + "res/data/npc/normal_npc/fun_data/evidenve_gift/award/");
        getInstance().getEvidenveData(1);
    }

    public EvidenveData getEvidenveData(final int _groupID) {
        return this.evidenveList.get(_groupID);
    }

    public String[] getEvidenveGift() {
        String[] functionName = new String[this.evidenveList.size()];
        for (int i = 0; i < this.evidenveList.size(); ++i) {
            functionName[i] = this.evidenveList.get(i).name;
        }
        return functionName;
    }

    private void loadAward(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            this.awardDict = (FastMap<Integer, EvidenveAward>) new FastMap();
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
                            EvidenveAward awardData = new EvidenveAward();
                            awardData.id = Integer.valueOf(subE.elementTextTrim("id"));
                            String data = subE.elementTextTrim("exp");
                            if (data != null) {
                                awardData.exp = Integer.valueOf(data);
                            }
                            data = subE.elementTextTrim("money");
                            if (data != null) {
                                awardData.money = Integer.valueOf(data);
                            }
                            awardData.goodsSum = Integer.valueOf(subE.elementTextTrim("goodsSum"));
                            awardData.goodsList = new Goods[awardData.goodsSum];
                            int index = 0;
                            int goodsID = 0;
                            for (int i = 0; i < awardData.goodsSum; ++i) {
                                index = i + 1;
                                goodsID = Integer.valueOf(subE.elementTextTrim("goods" + index + "ID"));
                                Goods goods = GoodsContents.getGoods(goodsID);
                                awardData.goodsList[i] = goods;
                            }
                            if (this.awardDict.containsKey(awardData.id)) {
                                continue;
                            }
                            this.awardDict.put(awardData.id, awardData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(final String _funPath, final String _awardPath) {
        this.loadAward(_awardPath);
        try {
            File dataPath = new File(_funPath);
            File[] dataFileList = dataPath.listFiles();
            this.evidenveList = new ArrayList<EvidenveData>();
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
                            EvidenveData evidenve = new EvidenveData();
                            evidenve.id = Integer.valueOf(subE.elementTextTrim("id"));
                            evidenve.name = subE.elementTextTrim("name");
                            if (subE.elementTextTrim("isOpen").equals("\u662f")) {
                                evidenve.isOpen = true;
                            } else {
                                evidenve.isOpen = false;
                            }
                            if (!evidenve.isOpen) {
                                continue;
                            }
                            evidenve.tableName = subE.elementTextTrim("tableName");
                            evidenve.awardID = Integer.valueOf(subE.elementTextTrim("awardID"));
                            evidenve.inputBoxSum = Integer.valueOf(subE.elementTextTrim("inputBoxSum"));
                            evidenve.inputBoxLenghts = new int[evidenve.inputBoxSum];
                            evidenve.inputBoxContents = new String[evidenve.inputBoxSum];
                            int index = 0;
                            for (int i = 0; i < evidenve.inputBoxSum; ++i) {
                                index = i + 1;
                                evidenve.inputBoxLenghts[i] = Integer.valueOf(subE.elementTextTrim("inputBox" + index + "Lenght"));
                                evidenve.inputBoxContents[i] = subE.elementTextTrim("inputBox" + index + "Name");
                            }
                            evidenve.wrongByInput = subE.elementTextTrim("wrongByInput");
                            evidenve.wrongByJoinIt = subE.elementTextTrim("wrongByJoinIt");
                            evidenve.wrongByUse = subE.elementTextTrim("wrongByUse");
                            evidenve.award = (EvidenveAward) this.awardDict.get(evidenve.awardID);
                            evidenve.columnSum = Integer.valueOf(subE.elementTextTrim("columnSum"));
                            evidenve.columnNames = new String[evidenve.columnSum];
                            for (int i = 0; i < evidenve.columnSum; ++i) {
                                index = i + 1;
                                evidenve.columnNames[i] = subE.elementTextTrim("column" + index + "Name");
                            }
                            this.evidenveList.add(evidenve);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class EvidenveData {

        public int id;
        public int awardID;
        public int inputBoxSum;
        public int columnSum;
        public int[] inputBoxLenghts;
        public String[] inputBoxContents;
        public String[] columnNames;
        public EvidenveAward award;
        public boolean isOpen;
        public String name;
        public String tableName;
        public String wrongByInput;
        public String wrongByJoinIt;
        public String wrongByUse;
    }

    public static class EvidenveAward {

        public int id;
        public int money;
        public int exp;
        public int goodsSum;
        public Goods[] goodsList;
    }
}
