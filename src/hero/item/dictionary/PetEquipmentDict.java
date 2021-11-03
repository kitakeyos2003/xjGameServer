// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import hero.share.MagicFastnessList;
import hero.share.EMagic;
import java.io.IOException;
import hero.share.AccessorialOriginalAttribute;
import java.util.Iterator;
import org.dom4j.Document;
import hero.item.detail.EPetBodyPartOfEquipment;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import yoyo.tools.YOYOOutputStream;
import java.io.File;
import hero.item.PetWeapon;
import hero.item.PetArmor;
import javolution.util.FastMap;

public class PetEquipmentDict {

    FastMap<Integer, PetArmor> petArmorDictionary;
    FastMap<Integer, PetWeapon> petWeaponDictionary;
    private static PetEquipmentDict instance;

    private PetEquipmentDict() {
        this.petArmorDictionary = (FastMap<Integer, PetArmor>) new FastMap();
        this.petWeaponDictionary = (FastMap<Integer, PetWeapon>) new FastMap();
    }

    public static PetEquipmentDict getInstance() {
        if (PetEquipmentDict.instance == null) {
            PetEquipmentDict.instance = new PetEquipmentDict();
        }
        return PetEquipmentDict.instance;
    }

    public PetArmor getPetArmor(final int id) {
        return (PetArmor) this.petArmorDictionary.get(id);
    }

    public PetWeapon getPetWeapon(final int id) {
        return (PetWeapon) this.petWeaponDictionary.get(id);
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
                            PetArmor petArmor = null;
                            PetWeapon petWeapon = null;
                            outputStreamTool.reset();
                            String id = subE.elementTextTrim("id");
                            String name = subE.elementTextTrim("name");
                            String type = subE.elementTextTrim("type");
                            if (!type.equals("\u722a\u90e8")) {
                                petArmor = new PetArmor();
                                petArmor.setID(Integer.parseInt(id));
                                petArmor.setName(name);
                                petArmor.setWearBodyPart(EPetBodyPartOfEquipment.getBodyPart(type));
                                petArmor.setTrait(subE.elementTextTrim("trait"));
                                petArmor.setNeedLevel(Integer.parseInt(subE.elementTextTrim("needLevel")));
                                String data = subE.elementTextTrim("exchangeable");
                                if (data.equals("\u662f")) {
                                    petArmor.setExchangeable();
                                }
                                data = subE.elementTextTrim("bind");
                                if (data != null) {
                                    petArmor.setBindType((byte) (data.equals("\u662f") ? 1 : 0));
                                } else {
                                    petArmor.setBindType((byte) 0);
                                }
                                petArmor.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                                petArmor.setMaxDurabilityPoint(Integer.parseInt(subE.elementTextTrim("durability")));
                                data = subE.elementTextTrim("repairable");
                                if (data.equals("\u662f")) {
                                    petArmor.setRepairable(true);
                                } else {
                                    petArmor.setRepairable(false);
                                }
                                data = subE.elementTextTrim("immobilityTime");
                                if (data != null) {
                                    petArmor.setImmobilityTime(Float.parseFloat(data));
                                }
                                data = subE.elementTextTrim("mp");
                                if (data != null) {
                                    petArmor.atribute.mp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("strength");
                                if (data != null) {
                                    petArmor.atribute.strength = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("agility");
                                if (data != null) {
                                    petArmor.atribute.agility = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("inte");
                                if (data != null) {
                                    petArmor.atribute.inte = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("spirit");
                                if (data != null) {
                                    petArmor.atribute.spirit = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("luck");
                                if (data != null) {
                                    petArmor.atribute.lucky = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("physicsDeathblowLevel");
                                if (data != null) {
                                    petArmor.atribute.physicsDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("magicDeathblowLevel");
                                if (data != null) {
                                    petArmor.atribute.magicDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("hitOdds");
                                if (data != null) {
                                    petArmor.atribute.hitLevel = Short.parseShort(data);
                                }
                                petArmor.setIconID(Short.parseShort(subE.elementTextTrim("miniImageID")));
                                data = subE.elementTextTrim("equipmentImageID");
                                if (data != null) {
                                    petArmor.setImageID(Short.parseShort(data));
                                }
                                petArmor.initDescription();
                                data = subE.elementTextTrim("description");
                                if (data != null) {
                                    this.setFixPropertyBytes(petArmor, outputStreamTool, data);
                                    petArmor.appendDescription(data);
                                } else {
                                    this.setFixPropertyBytes(petArmor, outputStreamTool, "");
                                }
                                this.petArmorDictionary.put(Integer.parseInt(id), petArmor);
                            } else {
                                petWeapon = new PetWeapon();
                                petWeapon.setID(Integer.parseInt(id));
                                petWeapon.setName(name);
                                petWeapon.setWearBodyPart(EPetBodyPartOfEquipment.CLAW);
                                petWeapon.setTrait(subE.elementTextTrim("trait"));
                                petWeapon.setNeedLevel(Integer.parseInt(subE.elementTextTrim("needLevel")));
                                String data = subE.elementTextTrim("exchangeable");
                                if (data.equals("\u662f")) {
                                    petWeapon.setExchangeable();
                                }
                                data = subE.elementTextTrim("bind");
                                if (data != null) {
                                    petWeapon.setBindType((byte) (data.equals("\u662f") ? 1 : 0));
                                } else {
                                    petWeapon.setBindType((byte) 0);
                                }
                                petWeapon.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                                petWeapon.setMaxDurabilityPoint(Integer.parseInt(subE.elementTextTrim("durability")));
                                petWeapon.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                                data = subE.elementTextTrim("atkRange");
                                if (data != null) {
                                    petWeapon.setAttackDistance(Short.parseShort(data));
                                }
                                petWeapon.setMaxDurabilityPoint(Integer.parseInt(subE.elementTextTrim("durability")));
                                data = subE.elementTextTrim("repairable");
                                if (data.equals("\u662f")) {
                                    petWeapon.setRepairable(true);
                                } else {
                                    petWeapon.setRepairable(false);
                                }
                                data = subE.elementTextTrim("immobilityTime");
                                if (data != null) {
                                    petWeapon.setImmobilityTime(Float.parseFloat(data));
                                }
                                data = subE.elementTextTrim("minPhysicsAttack");
                                if (data != null) {
                                    petWeapon.setMinPhysicsAttack(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("maxPhysicsAttack");
                                if (data != null) {
                                    petWeapon.setMaxPhysicsAttack(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("defense");
                                if (data != null) {
                                    petWeapon.atribute.defense = Integer.parseInt(data);
                                }
                                if (data != null) {
                                    petWeapon.atribute.defense = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("hp");
                                if (data != null) {
                                    petWeapon.atribute.hp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("mp");
                                if (data != null) {
                                    petWeapon.atribute.mp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("strength");
                                if (data != null) {
                                    petWeapon.atribute.strength = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("agility");
                                if (data != null) {
                                    petWeapon.atribute.agility = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("inte");
                                if (data != null) {
                                    petWeapon.atribute.inte = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("spirit");
                                if (data != null) {
                                    petWeapon.atribute.spirit = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("luck");
                                if (data != null) {
                                    petWeapon.atribute.lucky = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("physicsDeathblowLevel");
                                if (data != null) {
                                    petWeapon.atribute.physicsDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("magicDeathblowLevel");
                                if (data != null) {
                                    petWeapon.atribute.magicDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("hitOdds");
                                if (data != null) {
                                    petWeapon.atribute.hitLevel = Short.parseShort(data);
                                }
                                petWeapon.setIconID(Short.parseShort(subE.elementTextTrim("miniImageID")));
                                data = subE.elementTextTrim("equipmentImageID");
                                if (data != null) {
                                    petWeapon.setImageID(Short.parseShort(data));
                                }
                                petWeapon.initDescription();
                                data = subE.elementTextTrim("description");
                                if (data != null) {
                                    this.setFixPropertyBytes(petWeapon, outputStreamTool, data);
                                    petWeapon.appendDescription(data);
                                } else {
                                    this.setFixPropertyBytes(petWeapon, outputStreamTool, "");
                                }
                                this.petWeaponDictionary.put(Integer.parseInt(id), petWeapon);
                            }
                        }
                    }
                }
            }
            outputStreamTool.close();
            outputStreamTool = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFixPropertyBytes(final PetWeapon _weapon, final YOYOOutputStream _output, final String _desc) throws IOException {
        _output.reset();
        _output.writeByte((byte) 1);
        _output.writeUTF(_weapon.getName());
        _output.writeByte((byte) 4);
        _output.writeShort(_weapon.getNeedLevel());
        _output.writeByte((byte) 25);
        _output.writeShort(_weapon.getMinPhysicsAttack());
        _output.writeByte((byte) 26);
        _output.writeShort(_weapon.getMaxPhysicsAttack());
        _output.writeByte((byte) 40);
        _output.writeShort(_weapon.getImmobilityTime() * 1000.0f);
        _output.writeByte((byte) 6);
        _output.writeShort(_weapon.getBindType());
        _output.writeByte((byte) 38);
        _output.writeShort(_weapon.getMaxDurabilityPoint());
        _output.writeByte((byte) 39);
        _output.writeShort(_weapon.getAttackDistance());
        AccessorialOriginalAttribute atribute = _weapon.atribute;
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
        _output.writeByte(-1);
        _output.writeUTF(_desc);
        _output.flush();
        _weapon.setFixPropertyBytes(_output.getBytes());
    }

    private void setFixPropertyBytes(final PetArmor _armor, final YOYOOutputStream _output, final String _desc) throws IOException {
        _output.reset();
        _output.writeByte((byte) 2);
        _output.writeShort(_armor.getWearBodyPart().value());
        _output.writeByte((byte) 3);
        _output.writeShort(_armor.getWearBodyPart().value());
        _output.writeByte((byte) 4);
        _output.writeShort(_armor.getNeedLevel());
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
        _output.writeByte(-1);
        _output.writeUTF(_desc);
        _output.flush();
        _armor.setFixPropertyBytes(_output.getBytes());
    }
}
