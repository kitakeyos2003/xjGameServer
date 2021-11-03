// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.service;

import java.util.Iterator;
import org.dom4j.Document;
import hero.item.Goods;
import hero.share.service.LogWriter;
import hero.item.dictionary.GoodsContents;
import hero.item.detail.EGoodsTrait;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.Hashtable;
import javolution.util.FastMap;
import hero.charge.MallGoods;
import java.util.ArrayList;

public class MallGoodsDict {

    ArrayList<MallGoods> equipmentList;
    ArrayList<MallGoods> medicamentList;
    ArrayList<MallGoods> materialList;
    ArrayList<MallGoods> skillBookList;
    ArrayList<MallGoods> hotGoodsList;
    ArrayList<MallGoods> petList;
    ArrayList<MallGoods> petGoodsList;
    ArrayList<MallGoods> petEquipmentList;
    ArrayList<MallGoods> bagList;
    FastMap<Integer, MallGoods> mallGoodsTable;
    Hashtable<Byte, ArrayList<MallGoods>> goodsTable;
    private static MallGoodsDict instance;

    private MallGoodsDict() {
        this.equipmentList = new ArrayList<MallGoods>();
        this.medicamentList = new ArrayList<MallGoods>();
        this.materialList = new ArrayList<MallGoods>();
        this.skillBookList = new ArrayList<MallGoods>();
        this.hotGoodsList = new ArrayList<MallGoods>();
        this.petList = new ArrayList<MallGoods>();
        this.bagList = new ArrayList<MallGoods>();
        this.mallGoodsTable = (FastMap<Integer, MallGoods>) new FastMap();
        this.petList = new ArrayList<MallGoods>();
        this.petGoodsList = new ArrayList<MallGoods>();
        this.petEquipmentList = new ArrayList<MallGoods>();
        this.goodsTable = new Hashtable<Byte, ArrayList<MallGoods>>();
        for (int i = 0; i < ChargeServiceImpl.getInstance().getConfig().type_string.length; ++i) {
            if (!ChargeServiceImpl.getInstance().getConfig().type_string[i].equals("nullPage")) {
                this.goodsTable.put((byte) i, new ArrayList<MallGoods>());
            }
        }
    }

    public MallGoods getMallGoods(final int _goodsID) {
        return (MallGoods) this.mallGoodsTable.get((Object) _goodsID);
    }

    public Hashtable<Byte, ArrayList<MallGoods>> getMallTable() {
        return this.goodsTable;
    }

    public ArrayList<MallGoods> getGoodsList(final byte _goodsType) {
        switch (_goodsType) {
            case 7: {
                return this.hotGoodsList;
            }
            case 5: {
                return this.petList;
            }
            case 1: {
                return this.equipmentList;
            }
            case 3: {
                return this.materialList;
            }
            case 2: {
                return this.medicamentList;
            }
            case 6: {
                return this.bagList;
            }
            case 4: {
                return this.skillBookList;
            }
            case 8: {
                return this.petEquipmentList;
            }
            case 9: {
                return this.petGoodsList;
            }
            default: {
                return null;
            }
        }
    }

    public static MallGoodsDict getInstance() {
        if (MallGoodsDict.instance == null) {
            MallGoodsDict.instance = new MallGoodsDict();
        }
        return MallGoodsDict.instance;
    }

    public void load(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            if (dataFileList.length > 0) {
                boolean isHot = false;
                Goods goods = null;
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
                                MallGoods mallGoods = new MallGoods();
                                try {
                                    mallGoods.desc = "";
                                    String data = subE.elementTextTrim("id");
                                    if (data != null) {
                                        mallGoods.id = Integer.parseInt(data);
                                        data = subE.elementTextTrim("name");
                                        if (data == null) {
                                            continue;
                                        }
                                        mallGoods.name = data;
                                        data = subE.elementTextTrim("hot");
                                        if (data == null) {
                                            continue;
                                        }
                                        if (data.equals("\u662f")) {
                                            isHot = true;
                                        }
                                        data = subE.elementTextTrim("type");
                                        if (data == null) {
                                            continue;
                                        }
                                        mallGoods.type = Byte.valueOf(data);
                                        data = subE.elementTextTrim("trait");
                                        if (data == null) {
                                            continue;
                                        }
                                        mallGoods.trait = EGoodsTrait.getTrait(data);
                                        data = subE.elementTextTrim("price");
                                        if (data == null) {
                                            continue;
                                        }
                                        mallGoods.price = Integer.parseInt(data);
                                        data = subE.elementTextTrim("icon");
                                        if (data == null) {
                                            continue;
                                        }
                                        mallGoods.icon = Short.parseShort(data);
                                        data = subE.elementTextTrim("goodsTypeNumber");
                                        if (data == null) {
                                            continue;
                                        }
                                        mallGoods.goodsList = new int[Integer.parseInt(data)][2];
                                        for (int i = 1; i <= mallGoods.goodsList.length; ++i) {
                                            data = subE.elementTextTrim("goods" + i + "ID");
                                            if (data != null) {
                                                mallGoods.goodsList[i - 1][0] = Integer.parseInt(data);
                                                goods = GoodsContents.getGoods(mallGoods.goodsList[i - 1][0]);
                                                if (goods != null) {
                                                    data = subE.elementTextTrim("goods" + i + "Num");
                                                    if (data != null) {
                                                        mallGoods.goodsList[i - 1][1] = Short.parseShort(data);
                                                        if (mallGoods.goodsList[i - 1][1] > 0) {
                                                            data = subE.elementTextTrim("description");
                                                            if (1 == mallGoods.goodsList.length) {
                                                                if (data != null) {
                                                                    mallGoods.desc = String.valueOf(data) + "\n" + goods.getDescription();
                                                                } else {
                                                                    mallGoods.desc = goods.getDescription();
                                                                }
                                                            } else if (data != null) {
                                                                mallGoods.desc = data;
                                                            } else {
                                                                LogWriter.println("\u5546\u57ce\u7269\u54c1\u65e0\u63cf\u8ff0\uff0c\u7f16\u53f7\uff1a" + mallGoods.id);
                                                            }
                                                        } else {
                                                            LogWriter.println("\u9519\u8bef\u7684\u5546\u57ce\u7269\u54c1\u6570\u91cf\uff0c\u7f16\u53f7\uff1a" + mallGoods.goodsList[i - 1][0]);
                                                        }
                                                    }
                                                } else {
                                                    LogWriter.println("\u9519\u8bef\u7684\u5546\u57ce\u7269\u54c1\u7f16\u53f7\uff1a" + mallGoods.goodsList[i - 1][0]);
                                                }
                                            }
                                        }
                                        if (isHot) {
                                            this.hotGoodsList.add(mallGoods);
                                            isHot = false;
                                        }
                                        if (goods != null && 1 == mallGoods.goodsList.length && 1 == mallGoods.goodsList[0][1] && 1 < goods.getMaxStackNums()) {
                                            mallGoods.setBuyNumberPerTime((byte) goods.getMaxStackNums());
                                        }
                                        data = subE.elementTextTrim("description");
                                        if (data != null) {
                                            mallGoods.desc = data;
                                        }
                                        this.mallGoodsTable.put(mallGoods.id, mallGoods);
                                        this.goodsTable.get(mallGoods.type).add(mallGoods);
                                    } else {
                                        LogWriter.println("\u5546\u57ce\u7269\u54c1\u6570\u636e\u9519\u8bef\uff0c\u65e0\u7f16\u53f7");
                                    }
                                } catch (Exception e2) {
                                    LogWriter.println("\u52a0\u8f7d\u5546\u57ce\u7269\u54c1\u6570\u636e\u9519\u8bef\uff0c\u7f16\u53f7:" + mallGoods.id);
                                }
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
