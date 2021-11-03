// 
// Decompiled by Procyon v0.5.36
// 
package hero.player;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.Armor;
import hero.item.enhance.GenericEnhance;
import hero.item.service.EquipmentFactory;
import hero.item.Equipment;
import yoyo.service.tools.database.DBServiceImpl;
import hero.pet.Pet;
import hero.pet.service.PetDAO;
import hero.pet.service.PetServiceImpl;
import hero.item.Weapon;
import hero.player.service.PlayerServiceImpl;
import hero.player.service.PlayerConfig;
import yoyo.tools.YOYOOutputStream;
import hero.player.define.ESex;
import hero.share.EVocation;
import hero.player.define.EClan;

public class HeroRoleView {

    private static HeroRoleView instance;
    private static final String SELECT_PLAYER_BODY_EQUIPMENT_SQL = "SELECT equipment_id,generic_enhance_desc FROM equipment_instance JOIN player_carry_equipment ON user_id=? AND container_type=? AND player_carry_equipment.instance_id=equipment_instance.instance_id LIMIT 8";

    public static HeroRoleView getInstance() {
        if (HeroRoleView.instance == null) {
            HeroRoleView.instance = new HeroRoleView();
        }
        return HeroRoleView.instance;
    }

    private HeroRoleView() {
    }

    public byte[] getNewRoleDesc(final int _userID, final String _nickname, final EClan _clan, final EVocation _vocation, final ESex _sex, final int _clientType) {
        YOYOOutputStream outPipe = null;
        byte[] rtnValue = null;
        try {
            outPipe = new YOYOOutputStream();
            outPipe.writeInt(_userID);
            this.setSingleRoleView(outPipe, _nickname, _vocation, (short) 1, _sex, _clan);
            outPipe.flush();
            rtnValue = outPipe.getBytes();
            return rtnValue;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (outPipe != null) {
                    outPipe.close();
                    outPipe = null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                if (outPipe != null) {
                    outPipe.close();
                    outPipe = null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public byte[] get(final int[] _userIDs) {
        YOYOOutputStream outPipe = new YOYOOutputStream();
        byte[] rtnValue = null;
        try {
            for (int i = 0; i < _userIDs.length; ++i) {
                outPipe.writeInt(_userIDs[i]);
                this.setRoleViewFromDB(outPipe, _userIDs[i]);
            }
            outPipe.flush();
            rtnValue = outPipe.getBytes();
            outPipe.close();
            outPipe = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtnValue;
    }

    private void setSingleRoleView(final YOYOOutputStream _outPipe, final String _nickname, final EVocation _vocation, final short _level, final ESex _sex, final EClan _clan) {
        try {
            _outPipe.writeUTF(_nickname);
            _outPipe.writeByte(_vocation.value());
            _outPipe.writeByte(_clan.getID());
            _outPipe.writeByte(_sex.value());
            _outPipe.writeShort(_level);
            PlayerConfig config = PlayerServiceImpl.getInstance().getConfig();
            _outPipe.writeShort(config.getLimbsConfig().getHeadImage(_sex));
            _outPipe.writeShort(config.getLimbsConfig().getHeadAnimation(_sex));
            _outPipe.writeShort(config.getLimbsConfig().getHairImage(_sex, _clan));
            _outPipe.writeShort(config.getLimbsConfig().getHairAnimation(_sex, _clan));
            _outPipe.writeShort(config.getInitArmorImageGroup(_vocation.getType())[0]);
            _outPipe.writeShort(config.getInitArmorImageGroup(_vocation.getType())[1]);
            _outPipe.writeByte(config.getInitArmorImageGroup(_vocation.getType())[2]);
            _outPipe.writeShort(config.getInitArmorImageGroup(_vocation.getType())[3]);
            _outPipe.writeShort(config.getInitArmorImageGroup(_vocation.getType())[4]);
            _outPipe.writeByte(config.getInitArmorImageGroup(_vocation.getType())[5]);
            _outPipe.writeShort(-1);
            _outPipe.writeShort(-1);
            _outPipe.writeShort(config.getLimbsConfig().getLegImage(_sex));
            _outPipe.writeShort(config.getLimbsConfig().getLegAnimation(_sex));
            _outPipe.writeShort(config.getInitWeaponImageGroup(_vocation.getType())[0]);
            _outPipe.writeShort(config.getInitWeaponImageGroup(_vocation.getType())[1]);
            _outPipe.writeShort(-1);
            _outPipe.writeShort(-1);
            _outPipe.writeShort(-1);
            _outPipe.writeShort(-1);
            _outPipe.writeShort(config.getInitWeaponImageGroup(_vocation.getType())[2]);
            _outPipe.writeShort(config.getInitWeaponImageGroup(_vocation.getType())[3]);
            _outPipe.writeShort(-1);
            _outPipe.writeShort(-1);
            _outPipe.writeShort(config.getLimbsConfig().getDieImage(_clan));
            _outPipe.writeShort(config.getLimbsConfig().getDieAnimation(_clan));
            _outPipe.writeShort(config.getLimbsConfig().getTailImage(_sex, _clan));
            _outPipe.writeShort(config.getLimbsConfig().getTailAnimation(_sex, _clan));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSingleRoleView(final YOYOOutputStream _outPipe, final String _nickname, final EVocation _vocation, final short _level, final ESex _sex, final EClan _clan, final short _clothesImageID, final short _clothesAnimation, final byte _isDistinguish, final Weapon _weapon, final short _hatImageID, final short _hatAnimation, final byte _hatDistinguish, final int _userID, final short _enhanceClothesPNG, final short _enhanceClothesANU, final short _enhanceWeaponPNG, final short _enhanceWeaponANU, final int _weaponType) {
        try {
            _outPipe.writeUTF(_nickname);
            _outPipe.writeByte(_vocation.value());
            _outPipe.writeByte(_clan.getID());
            _outPipe.writeByte(_sex.value());
            _outPipe.writeShort(_level);
            PlayerConfig config = PlayerServiceImpl.getInstance().getConfig();
            _outPipe.writeShort(config.getLimbsConfig().getHeadImage(_sex));
            _outPipe.writeShort(config.getLimbsConfig().getHeadAnimation(_sex));
            _outPipe.writeShort(config.getLimbsConfig().getHairImage(_sex, _clan));
            _outPipe.writeShort(config.getLimbsConfig().getHairAnimation(_sex, _clan));
            _outPipe.writeShort(_hatImageID);
            _outPipe.writeShort(_hatAnimation);
            _outPipe.writeByte(_hatDistinguish);
            _outPipe.writeShort(_clothesImageID);
            _outPipe.writeShort(_clothesAnimation);
            _outPipe.writeByte(_isDistinguish);
            _outPipe.writeShort(_enhanceClothesPNG);
            _outPipe.writeShort(_enhanceClothesANU);
            _outPipe.writeShort(config.getLimbsConfig().getLegImage(_sex));
            _outPipe.writeShort(config.getLimbsConfig().getLegAnimation(_sex));
            _outPipe.writeShort(_weaponType);
            if (_weapon != null) {
                _outPipe.writeShort(_weapon.getImageID());
                _outPipe.writeShort(_weapon.getAnimationID());
            } else {
                _outPipe.writeShort(-1);
                _outPipe.writeShort(-1);
            }
            Pet pet = PetServiceImpl.getInstance().getPet(_userID, PetDAO.selectMountPet(_userID));
            _outPipe.writeShort((short) ((pet != null) ? pet.getImageID() : -1));
            _outPipe.writeShort((short) ((pet != null) ? pet.getAnimationID() : -1));
            _outPipe.writeShort(-1);
            _outPipe.writeShort(-1);
            if (_weapon != null) {
                _outPipe.writeShort(_weapon.getLightID());
                _outPipe.writeShort(_weapon.getLightAnimation());
                _outPipe.writeShort(_enhanceWeaponPNG);
                _outPipe.writeShort(_enhanceWeaponANU);
            } else {
                _outPipe.writeShort(-1);
                _outPipe.writeShort(-1);
                _outPipe.writeShort(-1);
                _outPipe.writeShort(-1);
            }
            _outPipe.writeShort(config.getLimbsConfig().getDieImage(_clan));
            _outPipe.writeShort(config.getLimbsConfig().getDieAnimation(_clan));
            _outPipe.writeShort(config.getLimbsConfig().getTailImage(_sex, _clan));
            _outPipe.writeShort(config.getLimbsConfig().getTailAnimation(_sex, _clan));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] setRoleViewFromDB(final YOYOOutputStream _outPipe, final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT nickname,clan,vocation,sex,lvl FROM player WHERE user_id=?");
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            if (rs.next()) {
                String nickname = rs.getString("nickname");
                EClan clan = EClan.getClan(rs.getShort("clan"));
                EVocation vocation = EVocation.getVocationByID(rs.getShort("vocation"));
                ESex sex = ESex.getSex(rs.getShort("sex"));
                short level = rs.getShort("lvl");
                if (level > PlayerServiceImpl.getInstance().getConfig().max_level) {
                    level = PlayerServiceImpl.getInstance().getConfig().max_level;
                }
                rs.close();
                rs = null;
                pstm.close();
                pstm = null;
                pstm = conn.prepareStatement("SELECT equipment_id,generic_enhance_desc FROM equipment_instance JOIN player_carry_equipment ON user_id=? AND container_type=? AND player_carry_equipment.instance_id=equipment_instance.instance_id LIMIT 8");
                pstm.setInt(1, _userID);
                pstm.setShort(2, (short) 2);
                rs = pstm.executeQuery();
                Weapon weapon = null;
                short clothesImageID = 0;
                short clothesAnimation = 0;
                short enhanceClothesPNG = 0;
                short enhanceClothesANU = 0;
                short enhanceWeaponPNG = 0;
                short enhanceWeaponANU = 0;
                short hatImageID = -1;
                short hatAnimation = -1;
                byte weaponLevel = 0;
                byte isDistinguish = 0;
                byte isDistinguishHat = 0;
                while (rs.next()) {
                    int equipmentID = rs.getInt("equipment_id");
                    String goodsDesc = rs.getString("generic_enhance_desc");
                    Equipment e = (Equipment) EquipmentFactory.getInstance().getEquipmentArchetype(equipmentID);
                    if (e != null) {
                        GenericEnhance ge = new GenericEnhance(e.getEquipmentType());
                        if (e instanceof Armor) {
                            if (((Armor) e).getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
                                clothesImageID = e.getImageID();
                                clothesAnimation = e.getAnimationID();
                                isDistinguish = ((Armor) e).getDistinguish();
                                enhanceClothesPNG = ge.getFlashByDBString(goodsDesc, EBodyPartOfEquipment.BOSOM)[0];
                                enhanceClothesANU = ge.getFlashByDBString(goodsDesc, EBodyPartOfEquipment.BOSOM)[1];
                            } else {
                                if (((Armor) e).getWearBodyPart() != EBodyPartOfEquipment.HEAD) {
                                    continue;
                                }
                                hatImageID = e.getImageID();
                                hatAnimation = e.getAnimationID();
                                isDistinguishHat = ((Armor) e).getDistinguish();
                            }
                        } else {
                            weapon = (Weapon) e;
                            enhanceWeaponPNG = ge.getFlashByDBString(goodsDesc, EBodyPartOfEquipment.WEAPON)[0];
                            enhanceWeaponANU = ge.getFlashByDBString(goodsDesc, EBodyPartOfEquipment.WEAPON)[1];
                        }
                    }
                }
                if (clothesImageID == 0) {
                    clothesImageID = PlayerServiceImpl.getInstance().getConfig().getDefaultClothesImageID(sex);
                    clothesAnimation = PlayerServiceImpl.getInstance().getConfig().getDefaultClothesAnimation(sex);
                }
                int type = -1;
                if (weapon != null) {
                    type = weapon.getWeaponType().getID();
                }
                this.setSingleRoleView(_outPipe, nickname, vocation, level, sex, clan, clothesImageID, clothesAnimation, isDistinguish, weapon, hatImageID, hatAnimation, isDistinguishHat, _userID, enhanceClothesPNG, enhanceClothesANU, enhanceWeaponPNG, enhanceWeaponANU, type);
            }
        } catch (Exception e2) {
            LogWriter.error(this, e2);
            e2.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex) {
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex2) {
        }
        return null;
    }
}
