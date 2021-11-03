// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.service;

import org.dom4j.Document;
import hero.share.service.LogWriter;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.Iterator;
import hero.pet.PetPK;
import hero.pet.Pet;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class PetDictionary {

    private static Logger log;
    private FastMap<Integer, Pet> dictionary;
    private static PetDictionary instance;

    static {
        PetDictionary.log = Logger.getLogger((Class) PetDictionary.class);
    }

    private PetDictionary() {
        this.dictionary = (FastMap<Integer, Pet>) new FastMap();
    }

    public static PetDictionary getInstance() {
        if (PetDictionary.instance == null) {
            PetDictionary.instance = new PetDictionary();
        }
        return PetDictionary.instance;
    }

    public Pet getPet(final int _aID) {
        return (Pet) this.dictionary.get(_aID);
    }

    public Pet getPet(final PetPK pk) {
        PetDictionary.log.debug(("get pet by pk : " + pk.intValue()));
        for (final Pet pet : this.dictionary.values()) {
            if (pet.aid == pk.intValue()) {
                return pet;
            }
        }
        return null;
    }

    public FastMap<Integer, Pet> getPetDict() {
        return this.dictionary;
    }

    public void load(final String _dataPath, final String _feed_dataPath) throws Exception {
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
                            Pet pet = new Pet();
                            PetPK pk = new PetPK();
                            try {
                                int aid = Integer.parseInt(subE.elementTextTrim("id"));
                                pet.name = subE.elementTextTrim("name");
                                short kind = Short.parseShort(subE.elementTextTrim("kind"));
                                short stage = Short.parseShort(subE.elementTextTrim("stage"));
                                short type = Short.parseShort(subE.elementTextTrim("type"));
                                short iconID = Short.parseShort(subE.elementTextTrim("iconID"));
                                short imageID = Short.parseShort(subE.elementTextTrim("imageID"));
                                short animationID = Short.parseShort(subE.elementTextTrim("animationID"));
                                short fun = Short.parseShort(subE.elementTextTrim("fun"));
                                int speed = Integer.parseInt(subE.elementTextTrim("speed"));
                                int a_str = Integer.parseInt(subE.elementTextTrim("a_str"));
                                int a_agi = Integer.parseInt(subE.elementTextTrim("a_agi"));
                                int a_intel = Integer.parseInt(subE.elementTextTrim("a_intel"));
                                int a_spi = Integer.parseInt(subE.elementTextTrim("a_spi"));
                                int a_luck = Integer.parseInt(subE.elementTextTrim("a_luck"));
                                data = subE.elementTextTrim("mountFunction");
                                if (data != null) {
                                    pet.mountFunction = Integer.parseInt(data);
                                }
                                if (stage == 2 && type == 2) {
                                    data = subE.elementTextTrim("atk");
                                    if (data != null) {
                                        pet.atk = Integer.parseInt(data);
                                    }
                                    data = subE.elementTextTrim("maxAtkHarm");
                                    if (data != null) {
                                        pet.maxAtkHarm = Integer.parseInt(data);
                                    }
                                    data = subE.elementTextTrim("minAtkHarm");
                                    if (data != null) {
                                        pet.minAtkHarm = Integer.parseInt(data);
                                    }
                                    data = subE.elementTextTrim("magicHarm");
                                    if (data != null) {
                                        pet.magicHarm = Integer.parseInt(data);
                                    }
                                    data = subE.elementTextTrim("maxMagicHarm");
                                    if (data != null) {
                                        pet.maxMagicHarm = Integer.parseInt(data);
                                    }
                                    data = subE.elementTextTrim("minMagicHarm");
                                    if (data != null) {
                                        pet.minMagicHarm = Integer.parseInt(data);
                                    }
                                }
                                pk.setKind(kind);
                                pk.setStage(stage);
                                pk.setType(type);
                                pet.aid = aid;
                                pet.pk = pk;
                                pet.iconID = iconID;
                                pet.imageID = imageID;
                                pet.animationID = animationID;
                                pet.fun = fun;
                                pet.speed = speed;
                                pet.a_str = a_str;
                                pet.a_agi = a_agi;
                                pet.a_intel = a_intel;
                                pet.a_spi = a_spi;
                                pet.a_luck = a_luck;
                                this.dictionary.put(aid, pet);
                            } catch (Exception ex) {
                                LogWriter.println("\u52a0\u8f7d\u5ba0\u7269\u51fa\u9519\uff0c\u7f16\u53f7:" + pet.pk.getKind());
                            }
                        }
                    }
                    PetDictionary.log.debug(("dictionary size = " + this.dictionary.size()));
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        }
    }
}
