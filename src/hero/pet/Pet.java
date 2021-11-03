// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet;

import hero.share.ME2GameObject;
import javolution.util.FastMap;
import hero.pet.service.PetDictionary;
import java.util.Iterator;
import hero.expressions.service.CEService;
import hero.share.EObjectType;
import java.util.ArrayList;
import java.util.Random;
import hero.item.detail.EGoodsTrait;
import hero.item.bag.PetBodyWear;
import hero.skill.PetPassiveSkill;
import hero.skill.PetActiveSkill;
import java.util.List;
import org.apache.log4j.Logger;
import hero.npc.ME2NotPlayer;

public class Pet extends ME2NotPlayer {

    private static Logger log;
    private static final int PET_TOTAL_KIND = 8;
    public static final long PET_HATCH_TIME = 1L;
    public int id;
    public int aid;
    public int masterID;
    public PetPK pk;
    public static final short PET_STAGE_EGG = 0;
    public static final short PET_STAGE_CHILD = 1;
    public static final short PET_STAGE_ADULT = 2;
    public static final short PET_TYPE_HERBIVORE = 1;
    public static final short PET_TYPE_CARNIVORE = 2;
    public short iconID;
    public short imageID;
    public short animationID;
    public String name;
    public short color;
    public int currEvolvePoint;
    public static final int MAXEVOLVEPOINT = 15;
    public int currHerbPoint;
    public static final int MAXHERBPOINT = 4;
    public int currCarnPoint;
    public static final int MAXCARNPOINT = 3;
    public short bornFrom;
    public int level;
    public int currLevelTime;
    public int currFightPoint;
    public long totalOnlineTime;
    public int healthTime;
    public long startHatchTime;
    public long loginTime;
    public short bind;
    public short viewStatus;
    public boolean isView;
    public int feeding;
    public static final int FEEDING_GREEN_FULL = 300;
    public static final int FEEDING_GREEN_HALF = 250;
    public static final int FEEDING_YELLOW_FULL = 200;
    public static final int FEEDING_YELLOW_HALF = 150;
    public static final int FEEDING_RED_FULL = 100;
    public static final int FEEDING_RED_HALF = 50;
    public static final int FEEDING_NULL = 0;
    public int mp;
    public int str;
    public int a_str;
    public int agi;
    public int a_agi;
    public int intel;
    public int a_intel;
    public int spi;
    public int a_spi;
    public int luck;
    public int a_luck;
    public int rage;
    public static final byte RAGECODE = 1;
    public int wit;
    public static final byte WITCODE = 2;
    public byte dicard_code;
    public int agile;
    public static final byte AGILECODE = 3;
    public static final int MAXPERPOINT = 10;
    public int grow_exp;
    public int fight_exp;
    public short fun;
    public int mountFunction;
    public int atk;
    public int maxAtkHarm;
    public int minAtkHarm;
    public int magicHarm;
    public int maxMagicHarm;
    public int minMagicHarm;
    public short attackRange;
    public long lastAttackTime;
    public int speed;
    public static final short PET_EGG_FUN = 0;
    public static final short PET_CHILD_FUN = 1;
    public static final short PET_HERBIVORE_FUN = 2;
    public static final short PET_CARNIVORE_FUN = 3;
    public List<PetActiveSkill> petActiveSkillList;
    public List<PetPassiveSkill> petPassiveSkillList;
    public List<Integer> petEquList;
    private PetBodyWear bodyWear;
    private byte direction;
    public EGoodsTrait trait;
    public short physicsDeathblowLevel;
    public short magicDeathblowLevel;
    public short hitLevel;
    public short duckLevel;
    private static final Random RANDOM;

    static {
        Pet.log = Logger.getLogger((Class) Pet.class);
        RANDOM = new Random();
    }

    public boolean isEgg() {
        return this.pk.getStage() == 0;
    }

    public Pet() {
        this.totalOnlineTime = 0L;
        this.healthTime = 0;
        this.petActiveSkillList = new ArrayList<PetActiveSkill>();
        this.petPassiveSkillList = new ArrayList<PetPassiveSkill>();
        this.petEquList = new ArrayList<Integer>();
        this.bodyWear = new PetBodyWear();
        this.trait = EGoodsTrait.YU_ZHI;
        this.objectType = EObjectType.PET;
        this.initPet();
    }

    @Override
    public int getID() {
        return this.id;
    }

    private void initPet() {
        int minAtk = CEService.minPhysicsAttack(this.str, this.agi, 0.3f, 0.3f, 0.3f, this.minAtkHarm, 0, (float) this.getActualAttackImmobilityTime(), 1);
        int maxAtk = CEService.maxPhysicsAttack(this.str, this.agi, 0.3f, 0.3f, 0.3f, this.maxAtkHarm, 0, (float) this.getActualAttackImmobilityTime(), 1);
        this.setLevel(this.level);
        this.getBaseProperty().setMinPhysicsAttack(minAtk);
        this.getBaseProperty().setMaxPhysicsAttack(maxAtk);
        this.getBaseProperty().setAgility(this.agi);
        this.getBaseProperty().setStrength(this.str);
        this.getBaseProperty().setInte(this.intel);
        this.getBaseProperty().setSpirit(this.spi);
    }

    public int getToNextLevelNeedTime() {
        return 3;
    }

    public boolean isDied() {
        if (this.pk.getStage() != 0 && this.feeding <= 0) {
            this.viewStatus = 0;
            this.isView = false;
            return true;
        }
        return false;
    }

    public void refreshLastAttackTime() {
        this.lastAttackTime = System.currentTimeMillis();
    }

    public void updFightPoint() {
        if (this.healthTime == 60) {
            ++this.fight_exp;
            ++this.currFightPoint;
            this.healthTime = 0;
        }
    }

    public void updEvolvePoint() {
        if (this.currEvolvePoint < 15) {
            this.currEvolvePoint = this.currFightPoint / 15;
        }
    }

    public void updFEPoint() {
        this.updFightPoint();
        this.updEvolvePoint();
    }

    public int getATK() {
        if (this.isDied()) {
            return 0;
        }
        if (this.pk.getType() == 2) {
            this.atk += this.atk * this.rage * 10 / 100;
            if (this.feeding > 200) {
                return this.atk;
            }
            if (this.feeding > 150) {
                return this.atk * 80 / 100;
            }
            if (this.feeding > 100) {
                return this.atk * 60 / 100;
            }
            if (this.feeding > 50) {
                return this.atk * 40 / 100;
            }
            if (this.feeding > 0) {
                return this.atk * 20 / 100;
            }
        }
        return 0;
    }

    public int getMagicHarm() {
        return this.magicHarm;
    }

    @Override
    public short getAttackRange() {
        return this.attackRange;
    }

    @Override
    public void setAttackRange(final short attackRange) {
        this.attackRange = attackRange;
    }

    public int getSpeed() {
        if (this.isDied()) {
            return 0;
        }
        if (this.feeding > 200) {
            return this.speed;
        }
        if (this.feeding > 150) {
            return this.speed * 80 / 100;
        }
        if (this.feeding > 100) {
            return this.speed * 60 / 100;
        }
        if (this.feeding > 50) {
            return this.speed * 40 / 100;
        }
        if (this.feeding > 0) {
            return this.speed * 20 / 100;
        }
        return 0;
    }

    public byte getFace() {
        if (this.isDied()) {
            return 0;
        }
        if (this.feeding == 0) {
            return 0;
        }
        if (this.feeding > 200) {
            return 5;
        }
        if (this.feeding > 150) {
            return 4;
        }
        if (this.feeding > 100) {
            return 3;
        }
        if (this.feeding > 50) {
            return 2;
        }
        if (this.feeding > 0) {
            return 1;
        }
        return 5;
    }

    public PetActiveSkill getPetActiveSkillByID(final int skillID) {
        for (final PetActiveSkill skill : this.petActiveSkillList) {
            if (skill.id == skillID) {
                return skill;
            }
        }
        return null;
    }

    public PetPassiveSkill getPetPassiveSkillByID(final int skillID) {
        for (final PetPassiveSkill skill : this.petPassiveSkillList) {
            if (skill.id == skillID) {
                return skill;
            }
        }
        return null;
    }

    public PetBodyWear getPetBodyWear() {
        return this.bodyWear;
    }

    public static Pet getRandomPetEgg() {
        List<Pet> eggList = new ArrayList<Pet>();
        FastMap<Integer, Pet> petMap = PetDictionary.getInstance().getPetDict();
        Pet.log.debug((Object) ("petMap size = " + petMap.size()));
        for (final Pet pet : petMap.values()) {
            if (pet.pk.getStage() == 0) {
                Pet.log.debug((Object) ("pet pk stage = " + pet.pk.getStage()));
                eggList.add(pet);
            }
        }
        Pet.log.debug((Object) ("egglist size = " + eggList.size()));
        Random random = new Random();
        if (eggList.size() > 0) {
            int r = random.nextInt(eggList.size());
            return eggList.get(r);
        }
        return null;
    }

    @Override
    public void setDirection(final byte direction) {
        this.direction = direction;
    }

    @Override
    public byte getDirection() {
        return this.direction;
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean canBeAttackBy(final ME2GameObject object) {
        return false;
    }

    @Override
    public byte getDefaultSpeed() {
        return 3;
    }

    @Override
    public void happenFight() {
    }
}
