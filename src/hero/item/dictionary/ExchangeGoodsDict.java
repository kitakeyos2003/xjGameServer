// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import hero.share.service.LogWriter;
import java.io.File;
import java.util.ArrayList;
import javolution.util.FastMap;

public class ExchangeGoodsDict {

    private static ExchangeGoodsDict instance;
    private FastMap<Integer, ArrayList<int[]>> dictionory;
    private static final String INVALIDATE_PATH = "\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a";

    public static ExchangeGoodsDict getInstance() {
        if (ExchangeGoodsDict.instance == null) {
            ExchangeGoodsDict.instance = new ExchangeGoodsDict();
        }
        return ExchangeGoodsDict.instance;
    }

    public ArrayList<int[]> getMaterialList(final int _goodsID) {
        return (ArrayList<int[]>) this.dictionory.get(_goodsID);
    }

    private ExchangeGoodsDict() {
        this.dictionory = (FastMap<Integer, ArrayList<int[]>>) new FastMap();
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
                            int goodsID = Integer.parseInt(subE.elementTextTrim("itemID"));
                            ArrayList<int[]> materialList = new ArrayList<int[]>();
                            int materialType = Integer.parseInt(subE.elementTextTrim("materialType"));
                            if (materialType <= 0) {
                                continue;
                            }
                            for (int i = 1; i <= materialType; ++i) {
                                materialList.add(new int[]{Integer.parseInt(subE.elementTextTrim("material" + i + "ID")), Integer.parseInt(subE.elementTextTrim("material" + i + "Number"))});
                            }
                            if (materialList.size() <= 0) {
                                continue;
                            }
                            this.dictionory.put(goodsID, materialList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
