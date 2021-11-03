// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import java.io.IOException;
import hero.share.MagicFastnessList;
import hero.share.AccessorialOriginalAttribute;
import java.util.Iterator;
import org.dom4j.Document;
import hero.share.EMagic;
import hero.item.detail.EBodyPartOfEquipment;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import yoyo.tools.YOYOOutputStream;
import java.io.File;
import hero.item.Armor;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class ArmorDict {

    private static Logger log;
    FastMap<Integer, Armor> dictionary;
    private static ArmorDict instance;

    static {
        ArmorDict.log = Logger.getLogger((Class) ArmorDict.class);
    }

    private ArmorDict() {
        this.dictionary = (FastMap<Integer, Armor>) new FastMap();
    }

    public static ArmorDict getInstance() {
        if (ArmorDict.instance == null) {
            ArmorDict.instance = new ArmorDict();
        }
        return ArmorDict.instance;
    }

    public Object[] getArmorList() {
        return this.dictionary.values().toArray();
    }

    public Armor add(final Armor _armor) {
        return (Armor) this.dictionary.put(_armor.getID(), _armor);
    }

    public Armor getArmor(final int _armorID) {
        return (Armor) this.dictionary.get(_armorID);
    }

    public void load(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            YOYOOutputStream outputStreamTool = new YOYOOutputStream();
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
                            Armor armor = new Armor();
                            outputStreamTool.reset();
                            try {
                                armor.setID(Integer.parseInt(subE.elementTextTrim("id")));
                                armor.setName(subE.elementTextTrim("name"));
                                armor.setArmorType(subE.elementTextTrim("type"));
                                armor.setTrait(subE.elementTextTrim("trait"));
                                armor.setNeedLevel(Integer.parseInt(subE.elementTextTrim("needLevel")));
                                String data = subE.elementTextTrim("bindType");
                                if (data != null) {
                                    if (data.equals("\u88c5\u5907\u7ed1\u5b9a")) {
                                        armor.setBindType((byte) 2);
                                    } else if (data.equals("\u62fe\u53d6\u7ed1\u5b9a")) {
                                        armor.setBindType((byte) 3);
                                    } else if (data.equals("\u4e0d\u7ed1\u5b9a")) {
                                        armor.setBindType((byte) 1);
                                    }
                                }
                                data = subE.elementTextTrim("existSeal");
                                if (data != null && data.equals("\u662f")) {
                                    armor.setSeal();
                                }
                                data = subE.elementTextTrim("suiteID");
                                if (data != null) {
                                    armor.setSuiteID(Short.parseShort(data));
                                }
                                armor.setWearBodyPart(EBodyPartOfEquipment.getBodyPart(subE.elementTextTrim("bodyPart")));
                                armor.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                                armor.setMaxDurabilityPoint(Integer.parseInt(subE.elementTextTrim("durability")));
                                armor.setRepairable(subE.elementTextTrim("repairable").equals("\u53ef\u4ee5"));
                                data = subE.elementTextTrim("defense");
                                if (data != null) {
                                    armor.atribute.defense = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("hp");
                                if (data != null) {
                                    armor.atribute.hp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("mp");
                                if (data != null) {
                                    armor.atribute.mp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("strength");
                                if (data != null) {
                                    armor.atribute.strength = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("agility");
                                if (data != null) {
                                    armor.atribute.agility = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("stamina");
                                if (data != null) {
                                    armor.atribute.stamina = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("inte");
                                if (data != null) {
                                    armor.atribute.inte = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("spirit");
                                if (data != null) {
                                    armor.atribute.spirit = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("luck");
                                if (data != null) {
                                    armor.atribute.lucky = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("physicsDeathblowLevel");
                                if (data != null) {
                                    armor.atribute.physicsDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("magicDeathblowLevel");
                                if (data != null) {
                                    armor.atribute.magicDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("hitOdds");
                                if (data != null) {
                                    armor.atribute.hitLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("duckOdds");
                                if (data != null) {
                                    armor.atribute.duckLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("sanctity");
                                if (data != null) {
                                    armor.setMagicFastness(EMagic.SANCTITY, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("umbra");
                                if (data != null) {
                                    armor.setMagicFastness(EMagic.UMBRA, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("fire");
                                if (data != null) {
                                    armor.setMagicFastness(EMagic.FIRE, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("water");
                                if (data != null) {
                                    armor.setMagicFastness(EMagic.WATER, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("soil");
                                if (data != null) {
                                    armor.setMagicFastness(EMagic.SOIL, Integer.parseInt(data));
                                }
                                armor.setIconID(Short.parseShort(subE.elementTextTrim("miniImageID")));
                                data = subE.elementTextTrim("animationID");
                                if (data != null) {
                                    armor.setAnimationID(Short.parseShort(data));
                                }
                                data = subE.elementTextTrim("equipmentImageID");
                                if (data != null) {
                                    armor.setImageID(Short.parseShort(data));
                                }
                                armor.initDescription();
                                data = subE.elementTextTrim("description");
                                if (data != null) {
                                    this.setFixPropertyBytes(armor, outputStreamTool, data);
                                    armor.appendDescription(data);
                                } else {
                                    this.setFixPropertyBytes(armor, outputStreamTool, "");
                                }
                                data = subE.elementTextTrim("isDistinguish");
                                if (data != null) {
                                    armor.setDistinguish(Byte.parseByte(data));
                                } else {
                                    if (armor.getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
                                        ArmorDict.log.info(("warn:\u83b7\u53d6\u88c5\u5907:" + armor.getID() + "\u7684isDistinguish\u5b57\u6bb5\u5931\u8d25,\u91c7\u53d6\u9ed8\u8ba4\u503c0"));
                                    }
                                    armor.setDistinguish((byte) 0);
                                }
                                this.dictionary.put(armor.getID(), armor);
                            } catch (Exception e) {
                                ArmorDict.log.error(("\u52a0\u8f7d\u9632\u5177\u6570\u636e\u51fa\u9519\uff0c\u7f16\u53f7:" + armor.getID()), (Throwable) e);
                            }
                        }
                    }
                }
            }
            outputStreamTool.close();
            outputStreamTool = null;
        } catch (Exception e2) {
            ArmorDict.log.error("\u52a0\u8f7d\u9632\u5177 errors : ", (Throwable) e2);
        }
    }

    private void setFixPropertyBytes(final Armor _armor, final YOYOOutputStream _output, final String _desc) throws IOException {
        _output.reset();
        _output.writeByte((byte) 2);
        _output.writeShort(_armor.getArmorType().getTypeValue());
        _output.writeByte((byte) 3);
        _output.writeShort(_armor.getWearBodyPart().value());
        _output.writeByte((byte) 4);
        _output.writeShort(_armor.getNeedLevel());
        _output.writeByte((byte) 5);
        _output.writeShort(_armor.getTrait().value());
        _output.writeByte((byte) 6);
        _output.writeShort(_armor.getBindType());
        _output.writeByte((byte) 38);
        _output.writeShort(_armor.getMaxDurabilityPoint());
        AccessorialOriginalAttribute atribute = _armor.atribute;
        if (atribute.stamina > 0) {
            _output.writeByte((byte) 7);
            _output.writeShort(atribute.stamina);
        }
        if (atribute.inte > 0) {
            _output.writeByte((byte) 8);
            _output.writeShort(atribute.inte);
        }
        if (atribute.strength > 0) {
            _output.writeByte((byte) 9);
            _output.writeShort(atribute.strength);
        }
        if (atribute.spirit > 0) {
            _output.writeByte((byte) 10);
            _output.writeShort(atribute.spirit);
        }
        if (atribute.agility > 0) {
            _output.writeByte((byte) 11);
            _output.writeShort(atribute.agility);
        }
        if (atribute.lucky > 0) {
            _output.writeByte((byte) 12);
            _output.writeShort(atribute.lucky);
        }
        if (atribute.hp > 0) {
            _output.writeByte((byte) 13);
            _output.writeShort(atribute.hp);
        }
        if (atribute.mp > 0) {
            _output.writeByte((byte) 14);
            _output.writeShort(atribute.mp);
        }
        if (atribute.physicsDeathblowLevel > 0) {
            _output.writeByte((byte) 15);
            _output.writeShort(atribute.physicsDeathblowLevel);
        }
        if (atribute.magicDeathblowLevel > 0) {
            _output.writeByte((byte) 16);
            _output.writeShort(atribute.magicDeathblowLevel);
        }
        if (atribute.hitLevel > 0) {
            _output.writeByte((byte) 17);
            _output.writeShort(atribute.hitLevel);
        }
        if (atribute.duckLevel > 0) {
            _output.writeByte((byte) 18);
            _output.writeShort(atribute.duckLevel);
        }
        if (atribute.defense > 0) {
            _output.writeByte((byte) 19);
            _output.writeShort(atribute.defense);
        }
        MagicFastnessList mfl = _armor.getMagicFastnessList();
        if (mfl != null) {
            int value = mfl.getEMagicFastnessValue(EMagic.SANCTITY);
            if (value > 0) {
                _output.writeByte((byte) 20);
                _output.writeShort(value);
            }
            value = mfl.getEMagicFastnessValue(EMagic.UMBRA);
            if (value > 0) {
                _output.writeByte((byte) 21);
                _output.writeShort(value);
            }
            value = mfl.getEMagicFastnessValue(EMagic.FIRE);
            if (value > 0) {
                _output.writeByte((byte) 22);
                _output.writeShort(value);
            }
            value = mfl.getEMagicFastnessValue(EMagic.WATER);
            if (value > 0) {
                _output.writeByte((byte) 23);
                _output.writeShort(value);
            }
            value = mfl.getEMagicFastnessValue(EMagic.SOIL);
            if (value > 0) {
                _output.writeByte((byte) 24);
                _output.writeShort(value);
            }
        }
        if (_armor.getSuiteID() > 0) {
            SuiteEquipmentDataDict.SuiteEData suiteEData = SuiteEquipmentDataDict.getInstance().getSuiteData(_armor.getSuiteID());
            if (suiteEData != null) {
                _output.writeByte((byte) 81);
                _output.writeUTF(suiteEData.name);
                _output.writeByte((byte) 82);
                _output.writeUTF(suiteEData.description);
            }
        }
        _output.writeByte(-1);
        _output.writeUTF(_desc);
        _output.flush();
        _armor.setFixPropertyBytes(_output.getBytes());
    }
}
