// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.exchange;

import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import javolution.util.FastMap;

public class TraderExchangeContentDict {

    private FastMap<String, int[]> goodsListMap;
    private static TraderExchangeContentDict instance;

    public static TraderExchangeContentDict getInstance() {
        if (TraderExchangeContentDict.instance == null) {
            TraderExchangeContentDict.instance = new TraderExchangeContentDict();
        }
        return TraderExchangeContentDict.instance;
    }

    private TraderExchangeContentDict() {
        this.goodsListMap = (FastMap<String, int[]>) new FastMap();
    }

    public int[] getExchangeGoodsList(final String _npcModelID) {
        return (int[]) this.goodsListMap.get(_npcModelID.toLowerCase());
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
                            String npcID = subE.elementTextTrim("npcID").toLowerCase();
                            int goodsTypeNums = Integer.parseInt(subE.elementTextTrim("goodsTypeNums"));
                            int[] goodsList = new int[goodsTypeNums];
                            if (goodsTypeNums <= 0) {
                                continue;
                            }
                            for (int i = 0; i < goodsTypeNums; ++i) {
                                goodsList[i] = Integer.parseInt(subE.elementTextTrim("goods" + (i + 1) + "ID"));
                            }
                            this.goodsListMap.put(npcID, goodsList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
