// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import java.util.Iterator;
import org.dom4j.Document;
import hero.share.service.LogWriter;
import hero.item.detail.EGoodsTrait;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.item.Material;
import javolution.util.FastMap;

public class MaterialDict {

    private static MaterialDict instance;
    private FastMap<Integer, Material> dictionary;

    private MaterialDict() {
        this.dictionary = (FastMap<Integer, Material>) new FastMap();
    }

    public static MaterialDict getInstance() {
        if (MaterialDict.instance == null) {
            MaterialDict.instance = new MaterialDict();
        }
        return MaterialDict.instance;
    }

    public Object[] getMaterialList() {
        return this.dictionary.values().toArray();
    }

    public Material getMaterial(final int _materialID) {
        return (Material) this.dictionary.get(_materialID);
    }

    public void load(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
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
                            String data = null;
                            Material material = new Material(Short.parseShort(subE.elementTextTrim("stackNums")));
                            try {
                                if ((data = subE.elementTextTrim("id")) != null) {
                                    material.setID(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("name")) != null) {
                                    material.setName(data);
                                }
                                if ((data = subE.elementTextTrim("trait")) != null) {
                                    material.setTrait(EGoodsTrait.getTrait(data));
                                }
                                if ((data = subE.elementTextTrim("exchangeable")) != null && data.equals("\u53ef\u4ea4\u6613")) {
                                    material.setExchangeable();
                                }
                                if ((data = subE.elementTextTrim("price")) != null) {
                                    material.setPrice(Integer.parseInt(data));
                                }
                                if ((data = subE.elementTextTrim("icon")) != null) {
                                    material.setIconID(Short.parseShort(data));
                                }
                                if ((data = subE.elementTextTrim("description")) != null) {
                                    material.appendDescription(data);
                                }
                                this.dictionary.put(material.getID(), material);
                            } catch (Exception ex) {
                                LogWriter.println("\u52a0\u8f7d\u6750\u6599\u6570\u636e\u51fa\u9519\uff0c\u7f16\u53f7:" + material.getID());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }
}
