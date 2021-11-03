// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.dictionary;

import java.io.IOException;
import hero.share.MagicFastnessList;
import hero.share.service.MagicDamage;
import hero.share.AccessorialOriginalAttribute;
import java.util.Iterator;
import org.dom4j.Document;
import hero.share.service.LogWriter;
import hero.share.EMagic;
import hero.item.detail.EBodyPartOfEquipment;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import yoyo.tools.YOYOOutputStream;
import java.io.File;
import hero.item.Weapon;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class WeaponDict {

    private static Logger log;
    private FastMap<Integer, Weapon> dictionary;
    private static WeaponDict instance;

    static {
        WeaponDict.log = Logger.getLogger((Class) WeaponDict.class);
    }

    private WeaponDict() {
        this.dictionary = (FastMap<Integer, Weapon>) new FastMap();
    }

    public static WeaponDict getInstance() {
        if (WeaponDict.instance == null) {
            WeaponDict.instance = new WeaponDict();
        }
        return WeaponDict.instance;
    }

    public Weapon getWeapon(final int _weaponID) {
        return (Weapon) this.dictionary.get(_weaponID);
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
                            Weapon weapon = new Weapon();
                            outputStreamTool.reset();
                            try {
                                weapon.setWearBodyPart(EBodyPartOfEquipment.WEAPON);
                                weapon.setID(Integer.parseInt(subE.elementTextTrim("id")));
                                weapon.setName(subE.elementTextTrim("name"));
                                weapon.setWeaponType(subE.elementTextTrim("type"));
                                weapon.setTrait(subE.elementTextTrim("trait"));
                                weapon.setNeedLevel(Integer.parseInt(subE.elementTextTrim("needLevel")));
                                String data = subE.elementTextTrim("existSeal");
                                if (data != null && data.equals("\u662f")) {
                                    weapon.setSeal();
                                }
                                data = subE.elementTextTrim("bindType");
                                if (data != null) {
                                    if (data.equals("\u88c5\u5907\u7ed1\u5b9a")) {
                                        weapon.setBindType((byte) 2);
                                    } else if (data.equals("\u62fe\u53d6\u7ed1\u5b9a")) {
                                        weapon.setBindType((byte) 3);
                                    }
                                }
                                weapon.setPrice(Integer.parseInt(subE.elementTextTrim("price")));
                                weapon.setAttackDistance(Short.parseShort(subE.elementTextTrim("atkRange")));
                                weapon.setMaxDurabilityPoint(Integer.parseInt(subE.elementTextTrim("durability")));
                                weapon.setRepairable(subE.elementTextTrim("repairable").equals("\u53ef\u4ee5"));
                                weapon.setImmobilityTime(Float.parseFloat(subE.elementTextTrim("immobilityTime")));
                                weapon.setMinPhysicsAttack(Integer.parseInt(subE.elementTextTrim("minPhysicsAttack")));
                                weapon.setMaxPhysicsAttack(Integer.parseInt(subE.elementTextTrim("maxPhysicsAttack")));
                                String addMagic = subE.elementTextTrim("magic");
                                if (addMagic != null) {
                                    weapon.setMagicDamage(EMagic.getMagic(addMagic), Integer.parseInt(subE.elementTextTrim("minDamageValue")), Integer.parseInt(subE.elementTextTrim("maxDamageValue")));
                                }
                                data = subE.elementTextTrim("skillID1");
                                if (data != null) {
                                    weapon.addAccessorialSkill(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("skillID2");
                                if (data != null) {
                                    weapon.addAccessorialSkill(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("skillID3");
                                if (data != null) {
                                    weapon.addAccessorialSkill(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("pvpEnhanceID");
                                if (data != null) {
                                    weapon.setPvpEnhanceID(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("pveEnhanceID");
                                if (data != null) {
                                    weapon.setPveEnhanceID(Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("defense");
                                if (data != null) {
                                    weapon.atribute.defense = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("hp");
                                if (data != null) {
                                    weapon.atribute.hp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("mp");
                                if (data != null) {
                                    weapon.atribute.mp = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("strength");
                                if (data != null) {
                                    weapon.atribute.strength = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("agility");
                                if (data != null) {
                                    weapon.atribute.agility = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("stamina");
                                if (data != null) {
                                    weapon.atribute.stamina = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("inte");
                                if (data != null) {
                                    weapon.atribute.inte = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("spirit");
                                if (data != null) {
                                    weapon.atribute.spirit = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("luck");
                                if (data != null) {
                                    weapon.atribute.lucky = Integer.parseInt(data);
                                }
                                data = subE.elementTextTrim("physicsDeathblowLevel");
                                if (data != null) {
                                    weapon.atribute.physicsDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("magicDeathblowLevel");
                                if (data != null) {
                                    weapon.atribute.magicDeathblowLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("hitOdds");
                                if (data != null) {
                                    weapon.atribute.hitLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("duckOdds");
                                if (data != null) {
                                    weapon.atribute.duckLevel = Short.parseShort(data);
                                }
                                data = subE.elementTextTrim("sanctity");
                                if (data != null) {
                                    weapon.setMagicFastness(EMagic.SANCTITY, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("umbra");
                                if (data != null) {
                                    weapon.setMagicFastness(EMagic.UMBRA, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("fire");
                                if (data != null) {
                                    weapon.setMagicFastness(EMagic.FIRE, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("water");
                                if (data != null) {
                                    weapon.setMagicFastness(EMagic.WATER, Integer.parseInt(data));
                                }
                                data = subE.elementTextTrim("soil");
                                if (data != null) {
                                    weapon.setMagicFastness(EMagic.SOIL, Integer.parseInt(data));
                                }
                                weapon.setIconID(Short.parseShort(subE.elementTextTrim("miniImageID")));
                                data = subE.elementTextTrim("animationID");
                                if (data != null) {
                                    weapon.setAnimationID(Short.parseShort(data));
                                } else {
                                    WeaponDict.log.info(("warn:\u6b66\u5668\u672a\u586b\u5199\u5bf9\u5e94\u52a8\u753b\u6587\u4ef6\u9879,ID:" + weapon.getID()));
                                }
                                weapon.setImageID(Short.parseShort(subE.elementTextTrim("equipmentImageID")));
                                data = subE.elementTextTrim("weaponLight");
                                if (data != null) {
                                    weapon.setLightID(Short.parseShort(data));
                                } else {
                                    weapon.setLightID((short) 605);
                                }
                                data = subE.elementTextTrim("lightAnimation");
                                if (data != null) {
                                    weapon.setLightAnimation(Short.parseShort(data));
                                } else {
                                    weapon.setLightAnimation((short) 605);
                                }
                                if (weapon.getName().equals("\u53e4\u94dc\u5251.\u706b")) {
                                    WeaponDict.log.info("");
                                }
                                weapon.initDescription();
                                data = subE.elementTextTrim("description");
                                if (data != null) {
                                    if (-1 != data.indexOf("\\n")) {
                                        data = data.replaceAll("\\\\n", "\n");
                                    }
                                    this.setFixPropertyBytes(weapon, outputStreamTool, data);
                                    weapon.appendDescription(data);
                                } else {
                                    this.setFixPropertyBytes(weapon, outputStreamTool, "");
                                }
                                this.dictionary.put(weapon.getID(), weapon);
                            } catch (Exception e) {
                                WeaponDict.log.error(("\u52a0\u8f7d\u6b66\u5668\u6570\u636e\u51fa\u9519\uff0c\u7f16\u53f7:" + weapon.getID()), (Throwable) e);
                                LogWriter.error(this, e);
                            }
                        }
                    }
                    outputStreamTool.close();
                    outputStreamTool = null;
                }
            }
        } catch (Exception e2) {
            WeaponDict.log.error("\u52a0\u8f7d\u6b66\u5668 errors : ", (Throwable) e2);
        }
    }

    public Object[] getWeaponList() {
        return this.dictionary.values().toArray();
    }

    private void setFixPropertyBytes(final Weapon _weapon, final YOYOOutputStream _output, final String _desc) throws IOException {
        _output.reset();
        _output.writeByte((byte) 1);
        _output.writeShort(_weapon.getWeaponType().getID());
        _output.writeByte((byte) 3);
        _output.writeShort(EBodyPartOfEquipment.WEAPON.value());
        _output.writeByte((byte) 4);
        _output.writeShort(_weapon.getNeedLevel());
        _output.writeByte((byte) 5);
        _output.writeShort(_weapon.getTrait().value());
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
        MagicDamage magicMamage = _weapon.getMagicDamage();
        if (magicMamage != null) {
            switch (magicMamage.magic) {
                case ALL: {
                    _output.writeByte((byte) 42);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte((byte) 41);
                    _output.writeShort(magicMamage.maxDamageValue);
                    break;
                }
                case SANCTITY: {
                    _output.writeByte((byte) 27);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte((byte) 32);
                    _output.writeShort(magicMamage.maxDamageValue);
                    break;
                }
                case UMBRA: {
                    _output.writeByte((byte) 28);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte((byte) 33);
                    _output.writeShort(magicMamage.maxDamageValue);
                    break;
                }
                case FIRE: {
                    _output.writeByte((byte) 29);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte((byte) 34);
                    _output.writeShort(magicMamage.maxDamageValue);
                    break;
                }
                case WATER: {
                    _output.writeByte((byte) 30);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte((byte) 35);
                    _output.writeShort(magicMamage.maxDamageValue);
                    break;
                }
                case SOIL: {
                    _output.writeByte((byte) 31);
                    _output.writeShort(magicMamage.minDamageValue);
                    _output.writeByte((byte) 36);
                    _output.writeShort(magicMamage.maxDamageValue);
                    break;
                }
            }
        }
        MagicFastnessList mfl = _weapon.getMagicFastnessList();
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
        _weapon.setFixPropertyBytes(_output.getBytes());
    }
}
