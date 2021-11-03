// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import java.util.Iterator;
import org.dom4j.Document;
import hero.share.service.LogWriter;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import javolution.util.FastMap;

public class BoxDataDict {

    private FastMap<String, BoxData> boxDataDict;
    private static BoxDataDict instance;

    private BoxDataDict() {
        this.boxDataDict = (FastMap<String, BoxData>) new FastMap();
    }

    public static BoxDataDict getInstance() {
        if (BoxDataDict.instance == null) {
            BoxDataDict.instance = new BoxDataDict();
        }
        return BoxDataDict.instance;
    }

    public BoxData getBoxData(final String _boxModelID) {
        return (BoxData) this.boxDataDict.get(_boxModelID);
    }

    public void load(final String _dataPath) {
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
                            BoxData boxData = new BoxData();
                            try {
                                boxData.modelID = subE.elementTextTrim("id").toLowerCase();
                                boxData.rebirthInterval = Integer.parseInt(subE.elementTextTrim("rebirthInterval")) * 60000;
                                boxData.fixedGoodsTypeNumsPerTimes = Byte.parseByte(subE.elementTextTrim("fixedGoodsTypeNumsPerTime"));
                                byte fixedGoodsTypeNumsTotal = Byte.parseByte(subE.elementTextTrim("fixedGoodsTypeNumsTotal"));
                                boxData.fixedGoodsInfos = new int[fixedGoodsTypeNumsTotal][2];
                                for (int i = 1; i <= fixedGoodsTypeNumsTotal; ++i) {
                                    boxData.fixedGoodsInfos[i - 1][0] = Integer.parseInt(subE.elementTextTrim("fixedGoods" + i + "ID"));
                                    boxData.fixedGoodsInfos[i - 1][1] = Integer.parseInt(subE.elementTextTrim("fixedGoods" + i + "Nums"));
                                }
                                byte randomGoodsTypeNumsTotal = Byte.parseByte(subE.elementTextTrim("randomGoodsTypeNumsTotal"));
                                if (randomGoodsTypeNumsTotal > 0) {
                                    boxData.randomGoodsTypeNumsPerTimes = Byte.parseByte(subE.elementTextTrim("randomGoodsTypeNumsPerTime"));
                                    boxData.randomGoodsInfos = new int[randomGoodsTypeNumsTotal][3];
                                    for (int i = 1; i <= randomGoodsTypeNumsTotal; ++i) {
                                        boxData.randomGoodsInfos[i - 1][0] = Integer.parseInt(subE.elementTextTrim("randomGoods" + i + "ID"));
                                        boxData.randomGoodsInfos[i - 1][1] = Integer.parseInt(subE.elementTextTrim("randomGoods" + i + "Odds"));
                                        boxData.randomGoodsInfos[i - 1][2] = Integer.parseInt(subE.elementTextTrim("randomGoods" + i + "Nums"));
                                    }
                                }
                                boxData.pickType = PickType.getPickType(subE.elementTextTrim("pickType"));
                                if (!this.boxDataDict.containsKey(boxData.modelID)) {
                                    this.boxDataDict.put(boxData.modelID, boxData);
                                } else {
                                    LogWriter.println("\u91cd\u590d\u7684\u5b9d\u7bb1\u6570\u636e\uff0c\u7f16\u53f7:" + boxData.modelID);
                                }
                            } catch (Exception e2) {
                                LogWriter.println("\u52a0\u8f7d\u5b9d\u7bb1\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + boxData.modelID);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }

    public static class BoxData {

        public String modelID;
        public int rebirthInterval;
        public byte fixedGoodsTypeNumsPerTimes;
        public int[][] fixedGoodsInfos;
        public byte randomGoodsTypeNumsPerTimes;
        public int[][] randomGoodsInfos;
        public PickType pickType;
    }
}
