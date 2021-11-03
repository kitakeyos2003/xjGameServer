// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.service;

import java.sql.Timestamp;
import hero.player.define.EClan;
import hero.player.define.ESex;
import hero.share.EVocation;
import hero.item.Weapon;
import hero.item.service.EquipmentFactory;
import hero.item.Armor;
import hero.share.EVocationType;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class PlayerConfig extends AbsConfig {

    private PlayerLimbsConfig limbsConfig;
    public String countdown_gift_data_path;
    public boolean open_countdown_gift;
    private int upgradeSkillPoint;
    public short max_length_of_name;
    public int db_update_interval;
    public int init_money;
    private int init_li_shi_skill;
    private int init_chi_hou_skill;
    private int init_fa_shi_skill;
    private int init_wu_yi_skill;
    public short max_level;
    public int free_attack_interval;
    private int init_weapon_id_of_wu_zhe;
    private int init_weapon_id_of_wu_zhe_second;
    private int init_weapon_id_of_magic;
    private int init_weapon_id_of_magic_second;
    private int init_weapon_id_of_mu_shi;
    private int init_weapon_id_of_mu_shi_second;
    private int init_weapon_id_of_you_xia;
    private int init_weapon_id_of_you_xia_second;
    private short init_hat_image_id_of_wu_zhe;
    private short init_hat_image_id_of_magic;
    private short init_hat_image_id_of_you_xia;
    private short init_hat_image_id_of_mu_shi;
    private short init_chothes_image_id_of_wu_zhe;
    private short init_chothes_image_id_of_magic;
    private short init_chothes_image_id_of_mu_shi;
    private short init_chothes_image_id_of_you_xia;
    private int[] init_armor_id_list_of_wu_zhe;
    private int[] init_armor_id_list_of_you_xia;
    private int[] init_armor_id_list_of_magic;
    private int[] init_armor_id_list_of_mu_shi;
    private int[] init_jewelry_id_list_of_wu_zhe;
    private int[] init_jewelry_id_list_of_you_xia;
    private int[] init_jewelry_id_list_of_magic;
    private int[] init_jewelry_id_list_of_mu_shi;
    private short default_clothes_image_id_of_male;
    private short default_clothes_animation_id_of_male;
    private short default_clothes_image_id_of_female;
    private short default_clothes_animation_id_of_female;
    private short born_map_id_of_long_shan;
    private byte[] born_point_of_long_shan;
    private short born_map_id_of_he_mu_du;
    private byte[] born_point_of_he_mu_du;
    private int init_hp_of_wu_zhe;
    private int init_fore_of_wu_zhe;
    private int init_hp_of_you_xia;
    private int init_mp_of_you_xia;
    private int init_hp_of_magic;
    private int init_mp_of_magic;
    private int init_hp_of_mu_shi;
    private int init_mp_of_mu_shi;
    private int[][] init_medicament_of_wu_zhe;
    private int[][] init_medicament_of_magic;
    private int[] init_skill_of_wu_zhe;
    private int[] init_skill_of_you_xia;
    private int[] init_skill_of_magic;
    private int[] init_skill_of_mu_shi;
    public int init_surplus_skill_point;
    public int forget_skill_back_point;
    public int[] init_new_male_role_armor_view_wu_zhe;
    public int[] init_new_male_role_armor_view_you_xia;
    public int[] init_new_male_role_armor_view_fa_shi;
    public int[] init_new_male_role_armor_view_mu_shi;
    public short[][] default_armor_list;
    public short[][] default_weapon_list;
    public byte use_novice;
    public float expModulus;
    public String expModulusStartTime;
    public String expModulusEndTime;
    public int default_red_medicament;
    public int default_blue_medicament;

    public PlayerConfig() {
        this.expModulus = 1.0f;
    }

    @Override
    public void init(final Element _xmlNode) throws Exception {
        Element paraElement = _xmlNode.element("para");
        Element other = _xmlNode.element("other");
        this.default_red_medicament = Integer.valueOf(other.elementTextTrim("default_red_medicament"));
        this.default_blue_medicament = Integer.valueOf(other.elementTextTrim("default_blue_medicament"));
        this.countdown_gift_data_path = paraElement.elementTextTrim("countdown_gift_data_path");
        this.open_countdown_gift = Boolean.valueOf(paraElement.elementTextTrim("open_countdown_gift"));
        this.limbsConfig = new PlayerLimbsConfig(paraElement);
        this.upgradeSkillPoint = Integer.parseInt(paraElement.elementTextTrim("upgradeSkillPoint"));
        this.default_armor_list = new short[9][8];
        for (int i = 0; i < 9; ++i) {
            String[] temp = other.elementTextTrim("default_armor_" + (i + 1)).split(",");
            for (int j = 0; j < temp.length; ++j) {
                this.default_armor_list[i][j] = Short.valueOf(temp[j]);
            }
        }
        this.default_weapon_list = new short[9][12];
        for (int i = 0; i < 9; ++i) {
            String[] temp = other.elementTextTrim("default_weapon_" + (i + 1)).split(",");
            for (int j = 0; j < temp.length; ++j) {
                this.default_weapon_list[i][j] = Short.valueOf(temp[j]);
            }
        }
        this.use_novice = Byte.parseByte(paraElement.elementTextTrim("use_novice"));
        this.expModulus = Float.parseFloat(paraElement.elementTextTrim("exp_modulus"));
        this.expModulusStartTime = paraElement.elementTextTrim("exp_modulus_start_time");
        this.expModulusEndTime = paraElement.elementTextTrim("exp_modulus_end_time");
        this.max_length_of_name = Short.parseShort(paraElement.elementTextTrim("max_length_of_name"));
        this.max_level = Short.parseShort(paraElement.elementTextTrim("max_level"));
        this.db_update_interval = Integer.parseInt(paraElement.elementTextTrim("db_update_interval"));
        this.default_clothes_image_id_of_male = Short.parseShort(paraElement.elementTextTrim("default_clothes_image_id_of_male"));
        this.default_clothes_image_id_of_female = Short.parseShort(paraElement.elementTextTrim("default_clothes_image_id_of_female"));
        this.born_map_id_of_long_shan = Short.parseShort(paraElement.elementTextTrim("born_map_id_of_long_shan"));
        this.born_point_of_long_shan = new byte[2];
        String point = paraElement.elementTextTrim("born_point_of_long_shan");
        String[] pointXY = point.split(",");
        for (int k = 0; k < 2; ++k) {
            this.born_point_of_long_shan[k] = Byte.parseByte(pointXY[k]);
        }
        this.born_point_of_he_mu_du = new byte[2];
        point = paraElement.elementTextTrim("born_point_of_he_mu_du");
        pointXY = point.split(",");
        for (int k = 0; k < 2; ++k) {
            this.born_point_of_he_mu_du[k] = Byte.parseByte(pointXY[k]);
        }
        this.born_map_id_of_he_mu_du = Short.parseShort(paraElement.elementTextTrim("born_map_id_of_he_mu_du"));
        this.init_money = Integer.parseInt(paraElement.elementTextTrim("init_money"));
        this.init_li_shi_skill = Integer.parseInt(paraElement.elementTextTrim("init_li_shi_skill"));
        this.init_chi_hou_skill = Integer.parseInt(paraElement.elementTextTrim("init_chi_hou_skill"));
        this.init_fa_shi_skill = Integer.parseInt(paraElement.elementTextTrim("init_fa_shi_skill"));
        this.init_wu_yi_skill = Integer.parseInt(paraElement.elementTextTrim("init_wu_yi_skill"));
        this.free_attack_interval = Integer.parseInt(paraElement.elementTextTrim("free_attack_interval"));
        this.init_weapon_id_of_wu_zhe = Integer.parseInt(paraElement.elementTextTrim("init_weapon_id_of_wu_zhe"));
        this.init_chothes_image_id_of_wu_zhe = Short.parseShort(paraElement.elementTextTrim("init_chothes_image_id_of_wu_zhe"));
        this.init_weapon_id_of_you_xia = Integer.parseInt(paraElement.elementTextTrim("init_weapon_id_of_you_xia"));
        this.init_chothes_image_id_of_you_xia = Short.parseShort(paraElement.elementTextTrim("init_chothes_image_id_of_you_xia"));
        this.init_weapon_id_of_magic = Integer.parseInt(paraElement.elementTextTrim("init_weapon_id_of_magic"));
        this.init_chothes_image_id_of_magic = Short.parseShort(paraElement.elementTextTrim("init_chothes_image_id_of_magic"));
        this.init_weapon_id_of_mu_shi = Integer.parseInt(paraElement.elementTextTrim("init_weapon_id_of_mu_shi"));
        this.init_chothes_image_id_of_mu_shi = Short.parseShort(paraElement.elementTextTrim("init_chothes_image_id_of_mu_shi"));
        this.init_weapon_id_of_you_xia_second = Integer.parseInt(paraElement.elementTextTrim("init_weapon_id_of_you_xia_second"));
        this.init_weapon_id_of_mu_shi_second = Integer.parseInt(paraElement.elementTextTrim("init_weapon_id_of_mu_shi_second"));
        this.init_weapon_id_of_magic_second = Integer.parseInt(paraElement.elementTextTrim("init_weapon_id_of_magic_second"));
        this.init_weapon_id_of_wu_zhe_second = Integer.parseInt(paraElement.elementTextTrim("init_weapon_id_of_wu_zhe_second"));
        this.init_armor_id_list_of_wu_zhe = new int[4];
        String ids = paraElement.elementTextTrim("init_armor_id_list_of_wu_zhe");
        String[] idList = ids.split(",");
        for (int l = 0; l < 4; ++l) {
            this.init_armor_id_list_of_wu_zhe[l] = Integer.parseInt(idList[l]);
        }
        this.init_armor_id_list_of_magic = new int[4];
        ids = paraElement.elementTextTrim("init_armor_id_list_of_magic");
        idList = ids.split(",");
        for (int l = 0; l < 4; ++l) {
            this.init_armor_id_list_of_magic[l] = Integer.parseInt(idList[l]);
        }
        this.init_armor_id_list_of_you_xia = new int[4];
        ids = paraElement.elementTextTrim("init_armor_id_list_of_you_xia");
        idList = ids.split(",");
        for (int l = 0; l < 4; ++l) {
            this.init_armor_id_list_of_you_xia[l] = Integer.parseInt(idList[l]);
        }
        this.init_armor_id_list_of_mu_shi = new int[4];
        ids = paraElement.elementTextTrim("init_armor_id_list_of_mu_shi");
        idList = ids.split(",");
        for (int l = 0; l < 4; ++l) {
            this.init_armor_id_list_of_mu_shi[l] = Integer.parseInt(idList[l]);
        }
        this.init_jewelry_id_list_of_wu_zhe = new int[3];
        ids = paraElement.elementTextTrim("init_jewelry_id_list_of_wu_zhe");
        idList = ids.split(",");
        for (int l = 0; l < 3; ++l) {
            this.init_jewelry_id_list_of_wu_zhe[l] = Integer.parseInt(idList[l]);
        }
        this.init_jewelry_id_list_of_you_xia = new int[3];
        ids = paraElement.elementTextTrim("init_jewelry_id_list_of_you_xia");
        idList = ids.split(",");
        for (int l = 0; l < 3; ++l) {
            this.init_jewelry_id_list_of_you_xia[l] = Integer.parseInt(idList[l]);
        }
        this.init_jewelry_id_list_of_magic = new int[3];
        ids = paraElement.elementTextTrim("init_jewelry_id_list_of_magic");
        idList = ids.split(",");
        for (int l = 0; l < 3; ++l) {
            this.init_jewelry_id_list_of_magic[l] = Integer.parseInt(idList[l]);
        }
        this.init_jewelry_id_list_of_mu_shi = new int[3];
        ids = paraElement.elementTextTrim("init_jewelry_id_list_of_mu_shi");
        idList = ids.split(",");
        for (int l = 0; l < 3; ++l) {
            this.init_jewelry_id_list_of_mu_shi[l] = Integer.parseInt(idList[l]);
        }
        this.init_new_male_role_armor_view_wu_zhe = new int[3];
        ids = paraElement.elementTextTrim("init_new_male_role_armor_view_wu_zhe");
        idList = ids.split(",");
        for (int l = 0; l < 3; ++l) {
            this.init_new_male_role_armor_view_wu_zhe[l] = Integer.parseInt(idList[l]);
        }
        this.init_new_male_role_armor_view_you_xia = new int[3];
        ids = paraElement.elementTextTrim("init_new_male_role_armor_view_you_xia");
        idList = ids.split(",");
        for (int l = 0; l < 3; ++l) {
            this.init_new_male_role_armor_view_you_xia[l] = Integer.parseInt(idList[l]);
        }
        this.init_new_male_role_armor_view_fa_shi = new int[3];
        ids = paraElement.elementTextTrim("init_new_male_role_armor_view_fa_shi");
        idList = ids.split(",");
        for (int l = 0; l < 3; ++l) {
            this.init_new_male_role_armor_view_fa_shi[l] = Integer.parseInt(idList[l]);
        }
        this.init_new_male_role_armor_view_mu_shi = new int[3];
        ids = paraElement.elementTextTrim("init_new_male_role_armor_view_mu_shi");
        idList = ids.split(",");
        for (int l = 0; l < 3; ++l) {
            this.init_new_male_role_armor_view_mu_shi[l] = Integer.parseInt(idList[l]);
        }
        this.init_hp_of_wu_zhe = Integer.parseInt(paraElement.elementTextTrim("init_hp_of_wu_zhe"));
        this.init_hp_of_magic = Integer.parseInt(paraElement.elementTextTrim("init_hp_of_magic"));
        this.init_hp_of_you_xia = Integer.parseInt(paraElement.elementTextTrim("init_hp_of_you_xia"));
        this.init_hp_of_mu_shi = Integer.parseInt(paraElement.elementTextTrim("init_hp_of_mu_shi"));
        this.init_fore_of_wu_zhe = Integer.parseInt(paraElement.elementTextTrim("init_fore_of_wu_zhe"));
        this.init_mp_of_you_xia = Integer.parseInt(paraElement.elementTextTrim("init_mp_of_you_xia"));
        this.init_mp_of_magic = Integer.parseInt(paraElement.elementTextTrim("init_hp_of_magic"));
        this.init_mp_of_mu_shi = Integer.parseInt(paraElement.elementTextTrim("init_mp_of_mu_shi"));
        String infoDesc = paraElement.elementTextTrim("init_medicament_of_wu_zhe");
        String[] infoList = infoDesc.split(";");
        if (infoList != null && infoList.length > 0 && !infoList[0].equals("")) {
            this.init_medicament_of_wu_zhe = new int[infoList.length][2];
            for (int m = 0; m < infoList.length; ++m) {
                String[] info = infoList[m].split(",");
                this.init_medicament_of_wu_zhe[m][1] = Integer.parseInt(info[1]);
                this.init_medicament_of_wu_zhe[m][0] = Integer.parseInt(info[0]);
            }
        }
        infoDesc = paraElement.elementTextTrim("init_medicament_of_magic");
        infoList = infoDesc.split(";");
        if (infoList != null && infoList.length > 0 && !infoList[0].equals("")) {
            this.init_medicament_of_magic = new int[infoList.length][2];
            for (int m = 0; m < infoList.length; ++m) {
                String[] info = infoList[m].split(",");
                this.init_medicament_of_magic[m][0] = Integer.parseInt(info[0]);
                this.init_medicament_of_magic[m][1] = Integer.parseInt(info[1]);
            }
        }
        ids = paraElement.elementTextTrim("init_skill_of_wu_zhe");
        idList = ids.split(",");
        this.init_skill_of_wu_zhe = new int[idList.length];
        for (int m = 0; m < idList.length; ++m) {
            this.init_skill_of_wu_zhe[m] = Integer.parseInt(idList[m]);
        }
        ids = paraElement.elementTextTrim("init_skill_of_magic");
        idList = ids.split(",");
        this.init_skill_of_magic = new int[idList.length];
        for (int m = 0; m < idList.length; ++m) {
            this.init_skill_of_magic[m] = Integer.parseInt(idList[m]);
        }
        ids = paraElement.elementTextTrim("init_skill_of_you_xia");
        idList = ids.split(",");
        this.init_skill_of_you_xia = new int[idList.length];
        for (int m = 0; m < idList.length; ++m) {
            this.init_skill_of_you_xia[m] = Integer.parseInt(idList[m]);
        }
        ids = paraElement.elementTextTrim("init_skill_of_mu_shi");
        idList = ids.split(",");
        this.init_skill_of_mu_shi = new int[idList.length];
        for (int m = 0; m < idList.length; ++m) {
            this.init_skill_of_mu_shi[m] = Integer.parseInt(idList[m]);
        }
        this.init_surplus_skill_point = Integer.valueOf(paraElement.elementTextTrim("init_surplus_skill_point"));
        this.forget_skill_back_point = Integer.valueOf(paraElement.elementTextTrim("forget_skill_back_point"));
        this.init_hat_image_id_of_mu_shi = Short.valueOf(paraElement.elementTextTrim("init_hat_image_id_of_mu_shi"));
        this.init_hat_image_id_of_magic = Short.valueOf(paraElement.elementTextTrim("init_hat_image_id_of_magic"));
        this.init_hat_image_id_of_wu_zhe = Short.valueOf(paraElement.elementTextTrim("init_hat_image_id_of_wu_zhe"));
        this.init_hat_image_id_of_you_xia = Short.valueOf(paraElement.elementTextTrim("init_hat_image_id_of_you_xia"));
        this.default_clothes_animation_id_of_male = Short.valueOf(paraElement.elementTextTrim("default_clothes_animation_id_of_male"));
        this.default_clothes_animation_id_of_female = Short.valueOf(paraElement.elementTextTrim("default_clothes_animation_id_of_female"));
    }

    public short[] getWeaponViewByLevel(final short _level) {
        short[] view = new short[10];
        for (int i = 0; i < this.default_weapon_list.length; ++i) {
            short start = this.default_weapon_list[i][0];
            short end = this.default_weapon_list[i][1];
            if (_level >= start && start <= end) {
                for (int j = 2; j < this.default_weapon_list.length; ++j) {
                    view = this.default_weapon_list[j];
                }
            }
        }
        return view;
    }

    public short[] getArmorViewByLevel(final short _level) {
        short[] view = new short[6];
        for (int i = 0; i < this.default_armor_list.length; ++i) {
            short start = this.default_armor_list[i][0];
            short end = this.default_armor_list[i][1];
            if (_level >= start && start <= end) {
                for (int j = 2; j < this.default_armor_list.length; ++j) {
                    view = this.default_armor_list[j];
                }
            }
        }
        return view;
    }

    public PlayerLimbsConfig getLimbsConfig() {
        return this.limbsConfig;
    }

    public int getUpgradeSkillPoint() {
        return this.upgradeSkillPoint;
    }

    public boolean useNovice() {
        return this.use_novice == 1;
    }

    public int[] getInitArmorIDs(final EVocationType _vocation) {
        if (EVocationType.PHYSICS == _vocation) {
            return this.init_armor_id_list_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vocation) {
            return this.init_armor_id_list_of_you_xia;
        }
        if (EVocationType.MAGIC == _vocation) {
            return this.init_armor_id_list_of_magic;
        }
        return this.init_armor_id_list_of_mu_shi;
    }

    public int[] getInitJewelryIDs(final EVocationType _vocation) {
        if (EVocationType.PHYSICS == _vocation) {
            return this.init_jewelry_id_list_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vocation) {
            return this.init_jewelry_id_list_of_you_xia;
        }
        if (EVocationType.MAGIC == _vocation) {
            return this.init_jewelry_id_list_of_magic;
        }
        return this.init_jewelry_id_list_of_mu_shi;
    }

    public int[] getInitArmorImageGroup(final EVocationType _vocation) {
        Armor armor = null;
        int[] result = {-1, -1, 0, -1, -1, 0};
        if (EVocationType.PHYSICS == _vocation) {
            if (this.init_new_male_role_armor_view_wu_zhe[0] != 0) {
                armor = (Armor) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_wu_zhe[0]);
                result[0] = armor.getImageID();
                result[1] = armor.getAnimationID();
                result[2] = armor.getDistinguish();
            }
            if (this.init_new_male_role_armor_view_wu_zhe[1] != 0) {
                armor = (Armor) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_wu_zhe[1]);
                result[3] = armor.getImageID();
                result[4] = armor.getAnimationID();
                result[5] = armor.getDistinguish();
            }
        } else if (EVocationType.RANGER == _vocation) {
            if (this.init_new_male_role_armor_view_you_xia[0] != 0) {
                armor = (Armor) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_you_xia[0]);
                result[0] = armor.getImageID();
                result[1] = armor.getAnimationID();
                result[2] = armor.getDistinguish();
            }
            if (this.init_new_male_role_armor_view_you_xia[1] != 0) {
                armor = (Armor) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_you_xia[1]);
                result[3] = armor.getImageID();
                result[4] = armor.getAnimationID();
                result[5] = armor.getDistinguish();
            }
        } else if (EVocationType.MAGIC == _vocation) {
            if (this.init_new_male_role_armor_view_fa_shi[0] != 0) {
                armor = (Armor) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_fa_shi[0]);
                result[0] = armor.getImageID();
                result[1] = armor.getAnimationID();
                result[2] = armor.getDistinguish();
            }
            if (this.init_new_male_role_armor_view_fa_shi[1] != 0) {
                armor = (Armor) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_fa_shi[1]);
                result[3] = armor.getImageID();
                result[4] = armor.getAnimationID();
                result[5] = armor.getDistinguish();
            }
        } else {
            if (this.init_new_male_role_armor_view_mu_shi[0] != 0) {
                armor = (Armor) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_mu_shi[0]);
                result[0] = armor.getImageID();
                result[1] = armor.getAnimationID();
                result[2] = armor.getDistinguish();
            }
            if (this.init_new_male_role_armor_view_mu_shi[1] != 0) {
                armor = (Armor) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_mu_shi[1]);
                result[3] = armor.getImageID();
                result[4] = armor.getAnimationID();
                result[5] = armor.getDistinguish();
            }
        }
        return result;
    }

    public int[] getInitWeaponImageGroup(final EVocationType _vocation) {
        Weapon weapon = null;
        int[] result = {-1, -1, -1, -1};
        if (EVocationType.PHYSICS == _vocation) {
            weapon = (Weapon) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_wu_zhe[2]);
            result[0] = weapon.getImageID();
            result[1] = weapon.getAnimationID();
            result[2] = weapon.getLightID();
            result[3] = weapon.getLightAnimation();
        } else if (EVocationType.RANGER == _vocation) {
            weapon = (Weapon) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_you_xia[2]);
            result[0] = weapon.getImageID();
            result[1] = weapon.getAnimationID();
            result[2] = weapon.getLightID();
            result[3] = weapon.getLightAnimation();
        } else if (EVocationType.MAGIC == _vocation) {
            weapon = (Weapon) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_fa_shi[2]);
            result[0] = weapon.getImageID();
            result[1] = weapon.getAnimationID();
            result[2] = weapon.getLightID();
            result[3] = weapon.getLightAnimation();
        } else {
            weapon = (Weapon) EquipmentFactory.getInstance().getEquipmentArchetype(this.init_new_male_role_armor_view_mu_shi[2]);
            result[0] = weapon.getImageID();
            result[1] = weapon.getAnimationID();
            result[2] = weapon.getLightID();
            result[3] = weapon.getLightAnimation();
        }
        return result;
    }

    public int getInitWeaponID(final EVocation _vocation) {
        if (EVocationType.PHYSICS == _vocation.getType()) {
            return this.init_weapon_id_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vocation.getType()) {
            return this.init_weapon_id_of_you_xia;
        }
        if (EVocationType.MAGIC == _vocation.getType()) {
            return this.init_weapon_id_of_magic;
        }
        return this.init_weapon_id_of_mu_shi;
    }

    public short getInitClothesImageID(final EVocationType _vocation, final ESex _sex) {
        return this.getInitMaleClothesImageID(_vocation);
    }

    public short getInitMaleClothesImageID(final EVocationType _vType) {
        if (EVocationType.PHYSICS == _vType) {
            return this.init_chothes_image_id_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vType) {
            return this.init_chothes_image_id_of_you_xia;
        }
        if (EVocationType.MAGIC == _vType) {
            return this.init_chothes_image_id_of_magic;
        }
        return this.init_chothes_image_id_of_mu_shi;
    }

    public short getInitMaleHatImageID(final EVocationType _vocation) {
        if (EVocationType.PHYSICS == _vocation) {
            return this.init_hat_image_id_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vocation) {
            return this.init_hat_image_id_of_you_xia;
        }
        if (EVocationType.MAGIC == _vocation) {
            return this.init_hat_image_id_of_magic;
        }
        return this.init_hat_image_id_of_mu_shi;
    }

    public short getInitFemaleClothesImageID(final EVocation _vocation) {
        if (EVocationType.PHYSICS == _vocation.getType()) {
            return this.init_chothes_image_id_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vocation.getType()) {
            return this.init_chothes_image_id_of_you_xia;
        }
        if (EVocationType.MAGIC == _vocation.getType()) {
            return this.init_chothes_image_id_of_magic;
        }
        return this.init_chothes_image_id_of_mu_shi;
    }

    public short getDefaultClothesImageID(final ESex _sex) {
        switch (_sex) {
            case Male: {
                return this.default_clothes_image_id_of_male;
            }
            case Female: {
                return this.default_clothes_image_id_of_female;
            }
            default: {
                return 0;
            }
        }
    }

    public short getDefaultClothesAnimation(final ESex _sex) {
        switch (_sex) {
            case Male: {
                return this.default_clothes_animation_id_of_male;
            }
            case Female: {
                return this.default_clothes_animation_id_of_female;
            }
            default: {
                return 0;
            }
        }
    }

    public short getDefaultMaleClothesImageID() {
        return this.default_clothes_image_id_of_male;
    }

    public short getDefaultMaleClothesAnimation() {
        return this.default_clothes_animation_id_of_male;
    }

    public short getDefaultFemaleClothesImageID() {
        return this.default_clothes_image_id_of_female;
    }

    public short getDefaultFemaleClothesAnimation() {
        return this.default_clothes_animation_id_of_female;
    }

    public short getBornMapID(final EClan _clan) {
        switch (_clan) {
            case LONG_SHAN: {
                return this.born_map_id_of_long_shan;
            }
            case HE_MU_DU: {
                return this.born_map_id_of_he_mu_du;
            }
            default: {
                return 0;
            }
        }
    }

    public int getInitSkill(final EVocation _vocation) {
        int skillID = 0;
        if (_vocation == EVocation.LI_SHI) {
            skillID = this.init_li_shi_skill;
        } else if (_vocation == EVocation.CHI_HOU) {
            skillID = this.init_chi_hou_skill;
        } else if (_vocation == EVocation.FA_SHI) {
            skillID = this.init_fa_shi_skill;
        } else if (_vocation == EVocation.WU_YI) {
            skillID = this.init_wu_yi_skill;
        }
        return skillID;
    }

    public byte[] getBornPoint(final EClan _clan) {
        switch (_clan) {
            case LONG_SHAN: {
                return this.born_point_of_long_shan;
            }
            case HE_MU_DU: {
                return this.born_point_of_he_mu_du;
            }
            default: {
                return new byte[]{20, 20};
            }
        }
    }

    public int getInitHp(final EVocation _vocation) {
        if (EVocationType.PHYSICS == _vocation.getType()) {
            return this.init_hp_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vocation.getType()) {
            return this.init_hp_of_you_xia;
        }
        if (EVocationType.MAGIC == _vocation.getType()) {
            return this.init_hp_of_magic;
        }
        return this.init_hp_of_mu_shi;
    }

    public int getInitMp(final EVocation _vocation) {
        if (EVocationType.PHYSICS == _vocation.getType()) {
            return this.init_fore_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vocation.getType()) {
            return this.init_mp_of_you_xia;
        }
        if (EVocationType.MAGIC == _vocation.getType()) {
            return this.init_mp_of_magic;
        }
        return this.init_mp_of_mu_shi;
    }

    public int[][] getInitMedicamentData(final EVocation _vocation) {
        if (EVocationType.PHYSICS == _vocation.getType()) {
            return this.init_medicament_of_wu_zhe;
        }
        return this.init_medicament_of_magic;
    }

    public int[] getInitSkillList(final EVocation _vocation) {
        if (EVocationType.PHYSICS == _vocation.getType()) {
            return this.init_skill_of_wu_zhe;
        }
        if (EVocationType.RANGER == _vocation.getType()) {
            return this.init_skill_of_you_xia;
        }
        if (EVocationType.MAGIC == _vocation.getType()) {
            return this.init_skill_of_magic;
        }
        return this.init_skill_of_mu_shi;
    }

    public int getInitSecondWeapon(final EVocation _vocation) {
        if (EVocationType.PHYSICS == _vocation.getType()) {
            return this.init_weapon_id_of_wu_zhe_second;
        }
        if (EVocationType.RANGER == _vocation.getType()) {
            return this.init_weapon_id_of_you_xia_second;
        }
        if (EVocationType.MAGIC == _vocation.getType()) {
            return this.init_weapon_id_of_magic_second;
        }
        return this.init_weapon_id_of_mu_shi_second;
    }

    public float getCurrExpModulus() {
        float currModulus = 1.0f;
        if (this.expModulus != 1.0f) {
            Timestamp startTime = Timestamp.valueOf(this.expModulusStartTime);
            Timestamp endTime = Timestamp.valueOf(this.expModulusEndTime);
            long currTime = System.currentTimeMillis();
            if (startTime.getTime() <= currTime && endTime.getTime() >= currTime) {
                currModulus = this.expModulus;
            }
        }
        return currModulus;
    }
}
