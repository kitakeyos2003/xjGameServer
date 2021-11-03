// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

import hero.effect.service.EffectServiceImpl;
import hero.player.HeroPlayer;
import java.util.Iterator;
import hero.skill.detail.ESpecialStatus;
import hero.effect.detail.StaticEffect;
import hero.share.service.IDManager;
import hero.share.cd.CDUnit;
import java.util.HashMap;
import hero.effect.Effect;
import hero.map.Map;
import hero.player.define.EClan;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public abstract class ME2GameObject implements Cloneable {

    private static Logger log;
    private int ID;
    private String name;
    private int hp;
    private int mp;
    private int forceQuantity;
    private int gasQuantity;
    protected int money;
    private short level;
    private short cellX;
    private short cellY;
    private short cellZ;
    private int baseAttackImmobilityTime;
    private int actualAttackImmobilityTime;
    private short attackRange;
    protected byte moveSpeed;
    protected byte moveSpeedState;
    private ArrayList<Boolean> isSlowSpeed;
    private ArrayList<Boolean> isAddSpeed;
    private boolean isMount;
    private byte direction;
    private EClan clan;
    private EVocation vocation;
    protected boolean enabled;
    protected boolean inFighting;
    protected boolean visible;
    protected boolean canReleaseMagicSkill;
    protected boolean isDead;
    protected boolean insensible;
    protected boolean sleeping;
    protected boolean moveable;
    protected boolean immuneAttack;
    protected EObjectType objectType;
    private EObjectLevel objectLevel;
    private ObjectProperty baseObjectProperty;
    private ObjectProperty actualObjectProperty;
    private Map where;
    private ResistOddsList resistOddsList;
    public ArrayList<Effect> effectList;
    public HashMap<Integer, CDUnit> userCDMap;

    static {
        ME2GameObject.log = Logger.getLogger((Class) ME2GameObject.class);
    }

    public ME2GameObject() {
        this.moveSpeed = 3;
        this.moveSpeedState = 1;
        this.isSlowSpeed = new ArrayList<Boolean>();
        this.isAddSpeed = new ArrayList<Boolean>();
        this.isMount = false;
        this.enabled = false;
        this.inFighting = false;
        this.visible = true;
        this.canReleaseMagicSkill = true;
        this.isDead = false;
        this.insensible = false;
        this.sleeping = false;
        this.moveable = true;
        this.immuneAttack = false;
        this.objectLevel = EObjectLevel.NORMAL;
        this.userCDMap = new HashMap<Integer, CDUnit>();
        this.setID(IDManager.buildObjectID());
        this.baseObjectProperty = new ObjectProperty(this);
        this.actualObjectProperty = new ObjectProperty(this);
        this.resistOddsList = new ResistOddsList();
    }

    public String getName() {
        return this.name;
    }

    public void setID(final int _ID) {
        this.ID = _ID;
    }

    public int getID() {
        return this.ID;
    }

    public EClan getClan() {
        return this.clan;
    }

    public void setClan(final EClan _clan) {
        this.clan = _clan;
    }

    public void setName(final String _name) {
        this.name = _name;
    }

    public short getCellX() {
        return this.cellX;
    }

    public void setCellX(final int x) {
        this.cellX = (short) x;
    }

    public short getCellY() {
        return this.cellY;
    }

    public void setCellY(final short y) {
        this.cellY = y;
    }

    public short getCellZ() {
        return this.cellZ;
    }

    public void setCellZ(final short z) {
        this.cellZ = z;
    }

    public short getLevel() {
        return this.level;
    }

    public Map where() {
        return this.where;
    }

    public void live(final Map _map) {
        this.where = _map;
    }

    public void setLevel(final int _level) {
        this.level = 0;
        this.addLevel(_level);
    }

    public void addLevel(final int _level) {
        int newLevel = this.level;
        newLevel += _level;
        if (newLevel > 1000000000) {
            newLevel = 1000000000;
        }
        this.level = (short) newLevel;
    }

    public void informBorn() {
    }

    public void informDead() {
    }

    public byte getDirection() {
        return this.direction;
    }

    public void setDirection(final byte _direction) {
        this.direction = _direction;
    }

    public void go(final byte _direction) {
        switch (_direction) {
            case 1: {
                --this.cellY;
                --this.cellZ;
                this.setDirection(_direction);
                break;
            }
            case 2: {
                ++this.cellY;
                ++this.cellZ;
                this.setDirection(_direction);
                break;
            }
            case 3: {
                --this.cellX;
                this.setDirection(_direction);
                break;
            }
            case 4: {
                ++this.cellX;
                this.setDirection(_direction);
                break;
            }
        }
    }

    public int getMoney() {
        return this.money;
    }

    public void setMoney(final int _money) {
        if (_money > 0) {
            this.money = _money;
        }
    }

    public EVocation getVocation() {
        return this.vocation;
    }

    public void setVocation(final EVocation _v) {
        this.vocation = _v;
        if (_v.getType() == EVocationType.PHYSICS) {
            this.balanceForceAndGas();
        }
    }

    public void setAttackRange(final short _atkRange) {
        this.attackRange = _atkRange;
    }

    public short getAttackRange() {
        return this.attackRange;
    }

    public int getBaseAttackImmobilityTime() {
        return this.baseAttackImmobilityTime;
    }

    public void setBaseAttackImmobilityTime(final int _immobilityTime) {
        this.baseAttackImmobilityTime = _immobilityTime;
    }

    public int getActualAttackImmobilityTime() {
        return this.actualAttackImmobilityTime;
    }

    public void setActualAttackImmobilityTime(final int _immobilityTime) {
        this.actualAttackImmobilityTime = _immobilityTime;
    }

    public void addActualAttackImmobilityTime(final int _immobilityTime) {
        this.actualAttackImmobilityTime += _immobilityTime;
    }

    public void initMoveSpeed() {
        for (final Effect effect : this.effectList) {
            if (effect != null && effect instanceof StaticEffect) {
                StaticEffect sEffect = (StaticEffect) effect;
                if (sEffect.specialStatus == ESpecialStatus.MOVE_FAST && sEffect.feature == Effect.EffectFeature.MOUNT) {
                    this.isAddSpeed.add(true);
                } else {
                    if (sEffect.specialStatus != ESpecialStatus.MOVE_SLOWLY) {
                        continue;
                    }
                    this.isSlowSpeed.add(true);
                }
            }
        }
        this.changeMoveSpeedState();
    }

    public void setMoveSpeed(final byte _speed) {
        this.moveSpeed = _speed;
    }

    public void changeMoveSpeed(final int _value) {
        this.moveSpeed += (byte) _value;
        if (this.moveSpeed <= 0) {
            this.moveSpeed = 1;
        }
    }

    public void setMount(final boolean _state) {
        this.isMount = _state;
        this.changeMoveSpeedState();
    }

    public boolean getMount() {
        this.changeMoveSpeedState();
        return this.isMount;
    }

    public void addAddSpeedState(final boolean _state) {
        this.isAddSpeed.add(_state);
        this.changeMoveSpeedState();
    }

    public void removeAddSpeedState() {
        if (this.isAddSpeed.size() > 0) {
            this.isAddSpeed.remove(0);
        }
        this.changeMoveSpeedState();
    }

    public void addSlowSpeedState(final boolean _state) {
        this.isSlowSpeed.add(_state);
        this.changeMoveSpeedState();
    }

    public void removeSlowSpeedState() {
        if (this.isSlowSpeed.size() > 0) {
            this.isSlowSpeed.remove(0);
        }
        this.changeMoveSpeedState();
    }

    private void changeMoveSpeedState() {
        if (this.isMount && this.isSlowSpeed.size() < 0) {
            this.moveSpeedState = 2;
            ME2GameObject.log.info((Object) "!!!!!!!!!!!!!!!!!!!!!!!\u9a91\u9a6c\u4e2d,\u6ca1\u6709\u88ab\u51cf\u901f");
        } else if (this.isAddSpeed.size() > 0 && this.isSlowSpeed.size() > 0) {
            this.moveSpeedState = 1;
            ME2GameObject.log.info((Object) "!!!!!!!!!!!!!!!!!!!!!!!\u88ab\u51cf\u901f,\u4e5f\u88ab\u52a0\u901f");
        } else if (this.isAddSpeed.size() > 0 && this.isSlowSpeed.size() == 0) {
            this.moveSpeedState = 2;
            ME2GameObject.log.info((Object) "!!!!!!!!!!!!!!!!!!!!!!!\u88ab\u52a0\u901f");
        } else if (this.isSlowSpeed.size() > 0 && this.isAddSpeed.size() == 0) {
            this.moveSpeedState = 0;
            ME2GameObject.log.info((Object) "!!!!!!!!!!!!!!!!!!!!!!!\u88ab\u51cf\u901f");
        } else {
            this.moveSpeedState = 1;
            ME2GameObject.log.info((Object) "!!!!!!!!!!!!!!!!!!!!!!!\u901f\u5ea6\u6b63\u5e38");
        }
    }

    public byte getMoveSpeedState() {
        return this.moveSpeedState;
    }

    public byte getMoveSpeed() {
        return this.moveSpeed;
    }

    public ResistOddsList getResistOddsList() {
        return this.resistOddsList;
    }

    public int getHp() {
        if (this.hp < 0) {
            return 0;
        }
        return this.hp;
    }

    public void setHp(final int _hp) {
        this.hp = _hp;
    }

    public void addHp(int _hp) {
        if (this instanceof HeroPlayer && _hp < 0) {
            HeroPlayer player = (HeroPlayer) this;
            for (final Effect effect : player.effectList) {
                if (effect != null && effect instanceof StaticEffect) {
                    StaticEffect sef = (StaticEffect) effect;
                    if (sef.isReduceAllHarm) {
                        _hp = 0;
                    } else {
                        if (sef.traceReduceHarmValue <= 0) {
                            continue;
                        }
                        int value = _hp + sef.traceReduceHarmValue;
                        if (value > 0) {
                            sef.traceReduceHarmValue = value;
                            _hp = 0;
                        } else {
                            ME2GameObject.log.info((Object) ("!!!!!!!!!!!!!!!!\u5438\u6536\u4f24\u5bb3\u540e\u76fe\u7834\u88c2:" + sef.traceReduceHarmValue));
                            sef.traceReduceHarmValue = 0;
                            _hp = value;
                            effect.setKeepTime((short) 0);
                        }
                    }
                }
            }
        }
        this.hp += _hp;
        if (this.hp > this.actualObjectProperty.getHpMax()) {
            this.hp = this.actualObjectProperty.getHpMax();
        } else if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public int getHPPercentElements() {
        return this.getHp() * 100 / this.actualObjectProperty.getHpMax();
    }

    public float getHPPercent() {
        return this.getHp() * 1.0f / this.actualObjectProperty.getHpMax();
    }

    public int getMp() {
        if (this.mp < 0) {
            return 0;
        }
        return this.mp;
    }

    public void setMp(final int _mp) {
        this.mp = _mp;
    }

    public void addMp(final int _mp) {
        this.mp += _mp;
        if (this.mp > this.actualObjectProperty.getMpMax()) {
            this.mp = this.actualObjectProperty.getMpMax();
        } else if (this.mp < 0) {
            this.mp = 0;
        }
    }

    public int getMPPercentElements() {
        return this.getMp() * 100 / this.actualObjectProperty.getMpMax();
    }

    public float getMPPercent() {
        return this.getMp() * 1.0f / this.actualObjectProperty.getMpMax();
    }

    public void balanceForceAndGas() {
        this.forceQuantity = 50;
        this.gasQuantity = 50;
    }

    public int getForceQuantity() {
        return this.forceQuantity;
    }

    public void setForceQuantity(final int _forceQuantity) {
        this.forceQuantity = _forceQuantity;
        this.gasQuantity = 100 - this.forceQuantity;
    }

    public int getGasQuantity() {
        return this.gasQuantity;
    }

    public void setGasQuantity(final int _gasQuantity) {
        this.gasQuantity = _gasQuantity;
        this.forceQuantity = 100 - this.gasQuantity;
    }

    public boolean consumeForceQuantity(final int _forceQuantityVal) {
        if (_forceQuantityVal <= this.forceQuantity) {
            this.forceQuantity -= _forceQuantityVal;
            if (this.forceQuantity > 100) {
                this.forceQuantity = 100;
            }
            if (this.forceQuantity < 0) {
                this.forceQuantity = 0;
            }
            this.gasQuantity = 100 - this.forceQuantity;
            return true;
        }
        return false;
    }

    public boolean consumeGasQuantity(final int _gasQuantityVal) {
        if (_gasQuantityVal <= this.gasQuantity) {
            this.gasQuantity -= _gasQuantityVal;
            if (this.gasQuantity > 100) {
                this.gasQuantity = 100;
            }
            if (this.gasQuantity < 0) {
                this.gasQuantity = 0;
            }
            this.forceQuantity = 100 - this.gasQuantity;
            return true;
        }
        return false;
    }

    public EObjectType getObjectType() {
        return this.objectType;
    }

    public EObjectLevel getObjectLevel() {
        return this.objectLevel;
    }

    public void setObjectLevel(final EObjectLevel _objectLevel) {
        this.objectLevel = _objectLevel;
    }

    public abstract boolean canBeAttackBy(final ME2GameObject p0);

    public ObjectProperty getActualProperty() {
        return this.actualObjectProperty;
    }

    public ObjectProperty getBaseProperty() {
        return this.baseObjectProperty;
    }

    public void die(final ME2GameObject _killer) {
        this.inFighting = false;
        this.isDead = true;
        this.sleeping = false;
        this.insensible = false;
        this.moveable = true;
        ArrayList<Effect> newList = new ArrayList<Effect>();
        ME2GameObject.log.info((Object) ("\u5bf9\u8c61\u6b7b\u4ea1,\u8eab\u4e0a\u6709\u6548\u679c:" + this.effectList.size()));
        for (int i = 0; i < this.effectList.size(); ++i) {
            Effect ef = this.effectList.get(i);
            if (!ef.isClearAfterDie) {
                newList.add(ef);
            } else if (ef instanceof StaticEffect) {
                EffectServiceImpl.getInstance().removeMove((StaticEffect) ef, this);
            }
        }
        this.getActualProperty().clearNoneBaseProperty();
        this.effectList = newList;
        ME2GameObject.log.info((Object) ("\u5bf9\u8c61\u6b7b\u4ea1,\u8eab\u4e0a\u4fdd\u7559\u6548\u679c:" + this.effectList.size()));
    }

    public void revive(final ME2GameObject _savior) {
        this.isDead = false;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public void active() {
        this.enabled = true;
    }

    public void invalid() {
        this.enabled = false;
    }

    public boolean isEnable() {
        return this.enabled;
    }

    public void enterFight() {
        this.inFighting = true;
    }

    public void disengageFight() {
        this.inFighting = false;
    }

    public boolean isInFighting() {
        return this.inFighting;
    }

    public void disappear() {
        this.visible = false;
    }

    public void emerge() {
        this.visible = true;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean canReleaseMagicSkill() {
        return this.canReleaseMagicSkill && !this.sleeping && !this.insensible;
    }

    public void forbidReleaseMagicSkill() {
        this.canReleaseMagicSkill = false;
    }

    public void relieveMagicSkillLimit() {
        this.canReleaseMagicSkill = true;
    }

    public void beInComa() {
        this.insensible = true;
    }

    public boolean isInsensible() {
        return this.insensible;
    }

    public void relieveComa() {
        this.insensible = false;
    }

    public void sleep() {
        this.sleeping = true;
    }

    public boolean isSleeping() {
        return this.sleeping;
    }

    public void wakeUp() {
        this.sleeping = false;
    }

    public boolean moveable() {
        return this.moveable && !this.sleeping && !this.insensible;
    }

    public void fixBody() {
        this.moveable = false;
    }

    public void relieveFixBodyLimit() {
        this.moveable = true;
    }

    public void enterImmuneAttackStatus() {
        this.immuneAttack = true;
    }

    public void relieveImmuneAttackStatus() {
        this.immuneAttack = false;
    }

    public boolean isImmuneAttack() {
        return this.immuneAttack;
    }

    public abstract void happenFight();

    public abstract byte getDefaultSpeed();
}
