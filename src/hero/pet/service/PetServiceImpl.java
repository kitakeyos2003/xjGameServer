// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.service;

import javolution.util.FastList;
import hero.pet.message.ResponsePetStage;
import hero.skill.detail.ESkillType;
import hero.pet.message.PetLearnSkillNotify;
import hero.skill.dict.PetSkillDict;
import hero.skill.PetSkill;
import hero.item.EquipmentInstance;
import hero.item.PetWeapon;
import hero.share.EObjectLevel;
import hero.pet.message.ResponseFeedStatusChange;
import hero.pet.message.ResponsePetEvolveChange;
import hero.pet.FeedType;
import hero.item.dictionary.SpecialGoodsDict;
import hero.item.special.PetFeed;
import hero.expressions.service.CEService;
import hero.pet.message.ResponsePetRevive;
import hero.pet.message.ResponseDiscardPoint;
import hero.pet.message.ResponseAbilityListChange;
import java.io.IOException;
import hero.skill.PetPassiveSkill;
import hero.skill.PetActiveSkill;
import yoyo.tools.YOYOOutputStream;
import hero.skill.service.SkillServiceImpl;
import hero.pet.message.ResponsePetSkillIDList;
import hero.pet.message.PetChangeNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.pet.message.ResponseWearPetGridNumber;
import hero.pet.message.ResponsePetContainer;
import java.util.ArrayList;
import java.util.HashMap;
import hero.log.service.CauseLog;
import hero.log.service.FlowLog;
import hero.log.service.LoctionLog;
import hero.log.service.LogServiceImpl;
import java.util.TimerTask;
import java.util.Timer;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.item.bag.exception.BagException;
import hero.pet.PetColor;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import java.util.Iterator;
import yoyo.service.base.session.Session;
import hero.pet.PetList;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class PetServiceImpl extends AbsServiceAdaptor<PetConfig> {

    private static Logger log;
    private FastMap<Integer, PetList> petListContainer;
    public static final int VALIDATE_CD_TIME = 300;
    private static PetServiceImpl instance;
    public static final byte VIEW_STATUS_OF_UPDATE_OLD = 1;
    public static final byte VIEW_STATUS_OF_UPDATE_NOW = 2;
    public static final byte VIEW_STATUS_OF_UPDATE_ALL = 3;
    public static final int HATCH_PET_TASK_INTERVAL = 60000;

    static {
        PetServiceImpl.log = Logger.getLogger((Class) PetServiceImpl.class);
    }

    public static PetServiceImpl getInstance() {
        if (PetServiceImpl.instance == null) {
            PetServiceImpl.instance = new PetServiceImpl();
        }
        return PetServiceImpl.instance;
    }

    @Override
    public void createSession(final Session _session) {
    }

    @Override
    public void sessionFree(final Session _session) {
    }

    @Override
    public void clean(final int _userID) {
        PetList list = (PetList) this.petListContainer.remove(_userID);
        if (list != null) {
            if (list.getLastTimesViewPetID().size() == 0) {
                if (list.getViewPet() != null) {
                    Iterator<Integer> it = list.getViewPet().keySet().iterator();
                    while (it.hasNext()) {
                        PetDAO.updateViewStatus(_userID, it.next(), (byte) 2);
                    }
                }
            } else {
                PetDAO.updateViewStatus(_userID, 0, (byte) 1);
                if (list.getViewPet() != null) {
                    Iterator<Integer> it = list.getViewPet().keySet().iterator();
                    while (it.hasNext()) {
                        PetDAO.updateViewStatus(_userID, it.next(), (byte) 3);
                    }
                }
            }
        }
    }

    public void start() {
    }

    public Pet addPetEgg(final HeroPlayer _player) {
        Pet pet = Pet.getRandomPetEgg();
        PetServiceImpl.log.debug(("add pet egg = " + pet));
        short _color = (short) PetColor.getRandomPetEggColor().getId();
        PetServiceImpl.log.debug(("pet egg color = " + _color));
        pet.color = _color;
        pet.bind = 1;
        pet.bornFrom = 1;
        pet.viewStatus = 0;
        pet.isView = false;
        try {
            if (_player.getInventory().getPetContainer().add(pet) >= 0) {
                PetDAO.addPetForNewPlaye(_player.getUserID(), pet);
                PetServiceImpl.log.debug(("add pet egg after id = " + pet.id));
                pet.totalOnlineTime = 0L;
                pet.startHatchTime = System.currentTimeMillis();
                PetList list = new PetList();
                list.add(pet);
                this.petListContainer.put(_player.getUserID(), list);
                this.initInfo(list, _player);
            }
        } catch (BagException e) {
            PetServiceImpl.log.error("add pet egg error : ", (Throwable) e);
        }
        return pet;
    }

    public Pet addPet(final int _userID, final int _petAID) {
        PetList list = (PetList) this.petListContainer.get(_userID);
        if (list == null) {
            list = new PetList();
            this.petListContainer.put(_userID, list);
        }
        Pet pet = PetDictionary.getInstance().getPet(_petAID);
        try {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
            if (player.getInventory().getPetContainer().add(pet) >= 0) {
                if (list.add(pet)) {
                    PetDAO.add(_userID, pet);
                    this.initInfo(list, player);
                } else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6bcf\u4e2a\u73a9\u5bb6\u6700\u591a\u53ea\u80fd\u62e5\u6709 100 \u53ea\u5ba0\u7269\uff01"));
                }
            }
        } catch (Exception e) {
            PetServiceImpl.log.error("add pet error : ", (Throwable) e);
        }
        return pet;
    }

    private void initInfo(final PetList list, final HeroPlayer player) {
        try {
            PetServiceImpl.log.debug("\u521d\u59cb\u5316\u73a9\u5bb6\u5ba0\u7269\u76f8\u5173\u4fe1\u606f : ");
            player.getInventory().getPetContainer().init(list.getPetList());
            if (list.getViewPet().size() > 0) {
                player.getBodyWearPetList().init(list.getViewPet());
            }
        } catch (BagException e) {
            e.printStackTrace();
        }
        for (final Pet pet : list.getPetList()) {
            getInstance().initPetSkillList(pet, PetDAO.loadPetSkill(pet));
            pet.masterID = player.getUserID();
        }
    }

    public void hatchPet(final HeroPlayer _player, final Pet _pet) {
        if (_player != null && _pet != null && _pet.pk.getStage() == 0) {
            Timer timer = new Timer();
            timer.schedule(new HatchPetTask(_player, _pet), 0L, 60000L);
        }
    }

    public int modifyPetName(final HeroPlayer _player, final int petid, final String name) {
        PetList petList = (PetList) this.petListContainer.get(_player.getUserID());
        Pet pet = petList.getPet(petid);
        int succ = PetDAO.updatePetName(_player.getUserID(), petid, name);
        if (succ == 1) {
            pet.name = name;
        }
        return succ;
    }

    public int transactPet(final int sellerID, final int buyerID, final int petID) {
        PetList sellerPetList = (PetList) this.petListContainer.get(sellerID);
        Pet pet = sellerPetList.getPet(petID);
        PetList buyerPetList = (PetList) this.petListContainer.get(buyerID);
        int res = PetDAO.updatePetOwner(buyerID, sellerID, petID);
        if (res == 1 && sellerPetList.dicePet(pet) && buyerPetList.add(pet)) {
            LogServiceImpl.getInstance().petChangeLog(PlayerServiceImpl.getInstance().getPlayerByUserID(buyerID), pet, 1, LoctionLog.BAG, FlowLog.GET, CauseLog.EXCHANGE);
            return 1;
        }
        return 0;
    }

    public void showPetx(final HeroPlayer player, final int petID) {
        if (player.isDead()) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u6b7b\u4ea1\uff0c\u4e0d\u80fd\u8fdb\u884c\u6b64\u64cd\u4f5c\uff01"));
            return;
        }
        HashMap<Integer, Pet> viewPetMap = this.getViewPetList(player.getUserID());
        if (viewPetMap != null && viewPetMap.size() > 0) {
            Pet pet = this.getPet(player.getUserID(), petID);
            PetServiceImpl.log.debug(("will show pet id = " + pet.id + ",pet stage = " + pet.pk.getStage() + " , had view pet size = " + viewPetMap.size()));
            if (viewPetMap.size() == 2) {
                if (pet.pk.getStage() == 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u643a\u5e26\u4e86 2 \u53ea\u5ba0\u7269\n\u8bf7\u6536\u8d77\u4e00\u53ea\u540e\u518d\u8fdb\u884c\u5b75\u5316"));
                    return;
                }
                Pet[] hPets = new Pet[2];
                int i = 0;
                for (final Pet p : viewPetMap.values()) {
                    hPets[i] = p;
                    ++i;
                }
                if (hPets[0].pk.getStage() == 0 && hPets[1].pk.getStage() == 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6b63\u5728\u5b75\u5316 2 \u53ea\u5ba0\u7269\u86cb\n\u8bf7\u6536\u8d77\u4e00\u53ea\u540e\u518d\u8fdb\u884c\u88c5\u5907"));
                    return;
                }
            }
            PetServiceImpl.log.debug(("start show pet id = " + pet.id));
            if (viewPetMap.containsKey(petID)) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8fd9\u4e2a\u5ba0\u7269\u5df2\u7ecf\u663e\u793a\uff01"));
            } else if (this.showPety(pet, viewPetMap, player)) {
                this.showPet(player, petID);
            }
        } else {
            this.showPet(player, petID);
        }
    }

    private boolean showPety(final Pet pet, final HashMap<Integer, Pet> viewPetMap, final HeroPlayer player) {
        List<Pet> petlistx = new ArrayList<Pet>();
        for (final Pet p : viewPetMap.values()) {
            petlistx.add(p);
        }
        if (pet.pk.getStage() != 0) {
            if (pet.pk.getStage() == 1) {
                for (final Pet vpet : petlistx) {
                    PetServiceImpl.log.debug(("vpet fun = " + vpet.fun));
                    if (vpet.fun != 2 && vpet.pk.getStage() != 0) {
                        PetServiceImpl.log.debug("\u5df2\u88c5\u5907\u4e0a\u7684\u8ddf\u968f\u5ba0\u7269\u8981\u6536\u8d77\uff0c\u86cb\u7ee7\u7eed\u5b75\u5316");
                        this.hidePet(player, vpet.id);
                    }
                }
            } else {
                for (final Pet vpet : petlistx) {
                    PetServiceImpl.log.debug(("vpet 2 fun = " + vpet.fun));
                    if (pet.fun == 3 && vpet.fun != 2 && vpet.pk.getStage() != 0) {
                        this.hidePet(player, vpet.id);
                    } else {
                        if (pet.fun != 2 || vpet.fun != 2) {
                            continue;
                        }
                        this.hidePet(player, vpet.id);
                    }
                }
            }
        } else if (viewPetMap.size() == 2) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u643a\u5e26\u4e86 2 \u53ea\u5ba0\u7269\n\u8bf7\u6536\u8d77\u4e00\u53ea\u540e\u518d\u8fdb\u884c\u5b75\u5316"));
            return false;
        }
        return true;
    }

    private boolean showPet(final HeroPlayer _player, final int _petID) {
        PetList list = (PetList) this.petListContainer.get(_player.getUserID());
        if (list != null) {
            Pet pet = list.getPet(_petID);
            if (pet != null) {
                if (list.exists(pet)) {
                    PetServiceImpl.log.debug(("\u8981\u663e\u793a\u7684\u5ba0\u7269\u6240\u5728\u9636\u6bb5\uff1a stage = " + pet.pk.getStage()));
                    if (pet.pk.getStage() != 0) {
                        if (pet.isDied()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8fd9\u4e2a\u5ba0\u7269\u5df2\u7ecf\u6b7b\u4ea1\uff0c\u9700\u8981\u590d\u6d3b\u624d\u80fd\u663e\u793a\uff01"));
                            return false;
                        }
                        HashMap<Integer, Pet> viewPetMap = this.getViewPetList(_player.getUserID());
                        Pet currViewPet = null;
                        if (viewPetMap != null) {
                            PetServiceImpl.log.debug(("had view pet size = " + viewPetMap.size()));
                            Iterator<Pet> iterator = viewPetMap.values().iterator();
                            while (iterator.hasNext()) {
                                Pet pet_ = currViewPet = iterator.next();
                            }
                        }
                        if (pet.pk.getStage() == 1) {
                            if (currViewPet != null && currViewPet.pk.getStage() == 1) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u53ea\u80fd\u663e\u793a\u4e00\u53ea\u5e7c\u5e74\u5ba0\u7269\uff01"));
                                return false;
                            }
                            list.setViewPet(pet);
                            try {
                                _player.getInventory().getPetContainer().remove(pet.id);
                                _player.getBodyWearPetList().add(pet);
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetContainer(_player.getInventory().getPetContainer()));
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                            } catch (BagException e) {
                                e.printStackTrace();
                                return false;
                            }
                            MapSynchronousInfoBroadcast.getInstance().put(_player.where(), new PetChangeNotify(_player.getID(), (byte) 2, pet.imageID, pet.pk.getType()), true, _player.getID());
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u5ba0\u7269\"" + pet.name + "\"\u5df2\u51fa\u6218\uff01"));
                        } else if (pet.pk.getStage() == 2) {
                            if (currViewPet == null || currViewPet.pk.getType() != pet.pk.getType()) {
                                list.setViewPet(pet);
                                try {
                                    _player.getInventory().getPetContainer().remove(pet.id);
                                    _player.getBodyWearPetList().add(pet);
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetContainer(_player.getInventory().getPetContainer()));
                                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                                } catch (BagException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetSkillIDList(pet));
                                getInstance().reCalculatePetProperty(pet);
                                PetServiceImpl.log.debug(("add skill property before pet maxMp = " + pet.getActualProperty().getMpMax()));
                                SkillServiceImpl.getInstance().petReleasePassiveSkill(pet, 1);
                                PetServiceImpl.log.debug(("add skill property after pet maxMp = " + pet.getActualProperty().getMpMax()));
                                MapSynchronousInfoBroadcast.getInstance().put(_player.where(), new PetChangeNotify(_player.getID(), (byte) 2, pet.imageID, pet.pk.getType()), true, _player.getID());
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u5ba0\u7269\"" + pet.name + "\"\u5df2\u51fa\u6218\uff01"));
                            } else if (currViewPet.pk.getType() == pet.pk.getType()) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0d\u80fd\u540c\u65f6\u663e\u793a\u4e24\u53ea\u5750\u9a91\u5ba0\u7269\u6216\u4e24\u53ea\u6218\u6597\u5ba0\u7269"));
                                return false;
                            }
                        }
                    } else {
                        list.setViewPet(pet);
                        this.hatchPet(_player, pet);
                        try {
                            _player.getInventory().getPetContainer().remove(pet.id);
                            _player.getBodyWearPetList().add(pet);
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetContainer(_player.getInventory().getPetContainer()));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                        } catch (BagException e2) {
                            e2.printStackTrace();
                            return false;
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u86cb\"" + pet.name + "\"\u5f00\u59cb\u5b75\u5316\uff01"));
                    }
                    int currEmptyGridNum = _player.getBodyWear().getEmptyGridNumber();
                    PetServiceImpl.log.debug(("\u663e\u793a\u5ba0\u7269\u540e\uff0c\u73a9\u5bb6\u8eab\u4e0a  currEmptyGridNum=" + currEmptyGridNum));
                    currEmptyGridNum = _player.getBodyWearPetList().getEmptyGridNumber();
                    PetServiceImpl.log.debug(("\u663e\u793a\u5ba0\u7269\u540e\uff0c\u73a9\u5bb6\u8eab\u4e0a\u5ba0\u7269\u683c\u5b50 currEmptyGridNum = " + currEmptyGridNum));
                    return true;
                }
                PetServiceImpl.log.debug(("@@@ pet isview=" + pet.isView + "   pet viewstatus = " + pet.viewStatus));
            }
        }
        return false;
    }

    public void writePetSkillID(final Pet pet, final YOYOOutputStream output) {
        try {
            output.writeByte(pet.petActiveSkillList.size());
            for (final PetActiveSkill skill : pet.petActiveSkillList) {
                output.writeInt(skill.id);
                output.writeInt(skill.coolDownTime);
            }
            output.writeByte(pet.petPassiveSkillList.size());
            for (final PetPassiveSkill skill2 : pet.petPassiveSkillList) {
                output.writeInt(skill2.id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hidePet(final HeroPlayer _player, final int petID) {
        boolean noViewPet = this.getViewPetList(_player.getUserID()) == null || this.getViewPetList(_player.getUserID()).size() == 0;
        if (noViewPet) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u8981\u6536\u8d77\u7684\u5ba0\u7269\uff01"));
            return false;
        }
        PetServiceImpl.log.debug(("@@@@  hide pet id = " + petID));
        PetList list = (PetList) this.petListContainer.get(_player.getUserID());
        if (list != null) {
            Pet pet = list.getPet(petID);
            list.removeViewPet(list.getPet(petID));
            if (pet.pk.getStage() == 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u86cb\"" + pet.name + "\"\u5df2\u7ecf\u505c\u6b62\u5b75\u5316\uff01"));
            } else {
                MapSynchronousInfoBroadcast.getInstance().put(_player.where(), new PetChangeNotify(_player.getID(), (byte) 3, pet.imageID, pet.pk.getType()), true, _player.getID());
            }
            try {
                _player.getInventory().getPetContainer().add(pet);
                _player.getBodyWearPetList().remove(pet.id);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetContainer(_player.getInventory().getPetContainer()));
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                PetServiceImpl.log.debug(("del skill property before pet maxMp = " + pet.getActualProperty().getMpMax()));
                SkillServiceImpl.getInstance().petReleasePassiveSkill(pet, 2);
                PetServiceImpl.log.debug(("del skill property after pet maxMp = " + pet.getActualProperty().getMpMax()));
                PetServiceImpl.log.debug("hide pet end...");
            } catch (BagException e) {
                PetServiceImpl.log.error("hide pet error : ", (Throwable) e);
                e.printStackTrace();
                return false;
            }
            int currEmptyGridNum = _player.getBodyWear().getEmptyGridNumber();
            PetServiceImpl.log.debug(("\u6536\u8d77\u5ba0\u7269\u540e\uff0c\u73a9\u5bb6\u8eab\u4e0a\u683c\u5b50 currEmptyGridNum = " + currEmptyGridNum));
            currEmptyGridNum = _player.getBodyWearPetList().getEmptyGridNumber();
            PetServiceImpl.log.debug(("\u6536\u8d77\u5ba0\u7269\u540e\uff0c\u73a9\u5bb6\u8eab\u4e0a\u5ba0\u7269\u683c\u5b50 currEmptyGridNum = " + currEmptyGridNum));
            return true;
        }
        return false;
    }

    public ArrayList<Pet> getPetList(final int _userID) {
        PetList list = (PetList) this.petListContainer.get(_userID);
        if (list == null || list.getPetList().size() == 0) {
            return null;
        }
        PetServiceImpl.log.debug((" pet serviceimpl get petlist size = " + list.getPetList().size()));
        return list.getPetList();
    }

    public List<Pet> getDiedPetList(final int _userID) {
        PetList list = (PetList) this.petListContainer.get(_userID);
        PetServiceImpl.log.debug((" pet serviceimpl get died pet list size = " + list.getPetList().size()));
        if (list == null || list.getPetList().size() == 0) {
            return null;
        }
        List<Pet> petlist = list.getPetList();
        List<Pet> diedPetList = new ArrayList<Pet>();
        for (final Pet pet : petlist) {
            if (pet.isDied()) {
                diedPetList.add(pet);
            }
        }
        return diedPetList;
    }

    public HashMap<Integer, Pet> getViewPetList(final int _userID) {
        PetList list = (PetList) this.petListContainer.get(_userID);
        if (list == null || list.getViewPet().size() == 0) {
            PetServiceImpl.log.debug("get view pet list = null ########33");
            return null;
        }
        return list.getViewPet();
    }

    public Pet getPet(final int _userID, final int _petID) {
        PetList list = (PetList) this.petListContainer.get(_userID);
        if (list == null) {
            return null;
        }
        return list.getPet(_petID);
    }

    public short getViewPetImage(final int _userID, final int _petID) {
        PetList list = (PetList) this.petListContainer.get(_userID);
        if (list == null || list.getViewPet() == null) {
            return 0;
        }
        return list.getViewPet().get(_petID).imageID;
    }

    public Pet getViewPet(final int _userID, final int _petID) {
        PetList list = (PetList) this.petListContainer.get(_userID);
        if (list == null || list.getViewPet() == null) {
            return null;
        }
        return list.getViewPet().get(_petID);
    }

    public void updatePet(final int _userID, final Pet _pet) {
        PetServiceImpl.log.debug(("DB save update pet id = " + _pet.id));
        PetDAO.updatePet(_userID, _pet);
    }

    public void addAbilityPoint(final HeroPlayer player, final int petID, final byte code, final int points) {
        Pet pet = this.getPet(player.getUserID(), petID);
        if (points > 10) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6700\u591a\u53ea\u80fd\u589e\u52a0\u5230 4 \u70b9\uff01"));
        } else {
            if (code == 3) {
                int npoint = pet.agile + points;
                if (npoint > 10) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u654f\u6377\u69fd\u6700\u591a\u8fd8\u80fd\u5206\u914d " + (10 - pet.agile) + " \u70b9\uff01"));
                } else {
                    pet.agile = npoint;
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseAbilityListChange(player, pet, code, pet.agile));
                }
            }
            if (code == 1) {
                int npoint = pet.rage + points;
                if (npoint > 10) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6124\u6012\u69fd\u6700\u591a\u8fd8\u80fd\u5206\u914d " + (10 - pet.rage) + " \u70b9\uff01"));
                } else {
                    pet.rage = npoint;
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseAbilityListChange(player, pet, code, pet.rage));
                }
            }
            if (code == 2) {
                int npoint = pet.wit + points;
                if (npoint > 10) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u667a\u6167\u69fd\u6700\u591a\u8fd8\u80fd\u5206\u914d " + (10 - pet.rage) + " \u70b9\uff01"));
                } else {
                    pet.wit = npoint;
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseAbilityListChange(player, pet, code, pet.wit));
                }
            }
            this.updatePet(player.getUserID(), pet);
        }
    }

    public void dicardPoint(final HeroPlayer _player, final Pet pet) {
        if (pet.dicard_code == 3) {
            pet.currEvolvePoint += pet.agile;
            pet.agile = 0;
        }
        if (pet.dicard_code == 1) {
            pet.currEvolvePoint += pet.rage;
            pet.rage = 0;
        }
        if (pet.dicard_code == 2) {
            pet.currEvolvePoint += pet.wit;
            pet.wit = 0;
        }
        this.updatePet(_player.getUserID(), pet);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseDiscardPoint(_player, pet, pet.dicard_code));
    }

    public void dicePet(final HeroPlayer _player, final int _petID) {
        PetList list = (PetList) this.petListContainer.get(_player.getUserID());
        if (list != null) {
            Pet pet = list.getPet(_petID);
            if (pet != null) {
                if (list.exists(pet) && list.dicePet(pet)) {
                    try {
                        _player.getInventory().getPetContainer().remove(pet);
                        _player.getBodyWearPetList().remove(pet);
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetContainer(_player.getInventory().getPetContainer()));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseWearPetGridNumber(_player.getBodyWearPetList()));
                        SkillServiceImpl.getInstance().petReleasePassiveSkill(pet, 2);
                    } catch (BagException e) {
                        e.printStackTrace();
                    }
                    MapSynchronousInfoBroadcast.getInstance().put(_player.where(), new PetChangeNotify(_player.getID(), (byte) 3, (short) 0, pet.pk.getType()), true, _player.getID());
                }
                PetDAO.dice(_player.getUserID(), _petID);
            }
        }
    }

    public boolean petRevive(final HeroPlayer player, final int petID) {
        try {
            Pet pet = this.getPet(player.getUserID(), petID);
            if (pet != null) {
                pet.feeding = 250;
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetRevive(player, pet));
                return true;
            }
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u83b7\u53d6\u5ba0\u7269\u5931\u8d25\uff01"));
            return false;
        } catch (Exception e) {
            PetServiceImpl.log.error("pet revice error : ", (Throwable) e);
            return false;
        }
    }

    public int petUpgrade(final int userID, final Pet pet) {
        ++pet.currLevelTime;
        PetServiceImpl.log.debug(("pet level = " + pet.level + " ,curr level time = " + pet.currLevelTime));
        if (pet.currLevelTime == pet.getToNextLevelNeedTime()) {
            ++pet.level;
            pet.currLevelTime = 0;
            if (pet.pk.getStage() == 2 && pet.pk.getType() == 2) {
                Pet pet_x = PetDictionary.getInstance().getPet(pet.pk);
                pet.str = CEService.playerBaseAttribute(pet.level, (float) pet_x.a_str);
                pet.agi = CEService.playerBaseAttribute(pet.level, (float) pet_x.a_agi);
                pet.intel = CEService.playerBaseAttribute(pet.level, (float) pet_x.a_intel);
                pet.spi = CEService.playerBaseAttribute(pet.level, (float) pet_x.a_spi);
                pet.luck = CEService.playerBaseAttribute(pet.level, (float) pet_x.a_luck);
            }
            PetDAO.upgradePet(userID, pet);
            PetServiceImpl.log.debug(("upet upgrade after level = " + pet.level));
        }
        return pet.level;
    }

    public boolean feedPet(final int _userID, final Pet pet, final int feedID) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        if (!pet.isDied()) {
            PetFeed feed = (PetFeed) SpecialGoodsDict.getInstance().getSpecailGoods(feedID);
            if (feed != null) {
                if (pet.pk.getStage() == 1 && feed.getFeedType().getTypeID() < FeedType.DADIJH.getTypeID()) {
                    if (pet.feeding > 200) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u5728\u5f53\u524d\u72b6\u6001\u4e0b\u5582\u517b\u65e0\u6548"));
                        return false;
                    }
                    if (pet.feeding > 150 && pet.feeding <= 200) {
                        if (feed.getFeedType() == FeedType.NORMAL) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u5728\u5f53\u524d\u72b6\u6001\u4e0b\u5582\u517b\u666e\u901a\u9972\u6599\u65e0\u6548\uff0c\u8981\u5582\u517b\u6210\u957f\u9972\u6599"));
                            return false;
                        }
                        pet.feeding = 300;
                        if (feed.getFeedType() == FeedType.HERBIVORE) {
                            ++pet.currHerbPoint;
                        } else if (feed.getFeedType() == FeedType.CARNIVORE) {
                            ++pet.currCarnPoint;
                        }
                        if (pet.currCarnPoint >= 3) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8089\u98df\u8fdb\u5316\u70b9\u5df2\u8db3\u591f\uff0c\u53ef\u4ee5\u8fdb\u5316\u5230\u8089\u98df\u5ba0\u7269\uff01"));
                            return false;
                        }
                        if (pet.currHerbPoint >= 4) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8349\u98df\u8fdb\u5316\u70b9\u5df2\u8db3\u591f\uff0c\u53ef\u4ee5\u8fdb\u5316\u5230\u8349\u98df\u5ba0\u7269\uff01"));
                            return false;
                        }
                    } else if (pet.feeding <= 150 && pet.feeding > 100) {
                        pet.feeding = 200;
                    } else if (pet.feeding <= 100 && pet.feeding > 50) {
                        if (feed.getFeedType() == FeedType.NORMAL) {
                            pet.feeding = 100;
                        } else {
                            pet.feeding = 200;
                        }
                    } else if (pet.feeding <= 50) {
                        if (feed.getFeedType() == FeedType.NORMAL) {
                            pet.feeding = 100;
                        } else {
                            pet.feeding = 150;
                        }
                    }
                } else if (pet.pk.getStage() == 2 && feed.getFeedType().getTypeID() >= FeedType.DADIJH.getTypeID()) {
                    if (pet.feeding > 200) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u5728\u5f53\u524d\u72b6\u6001\u4e0b\u5582\u517b\u65e0\u6548"));
                        return false;
                    }
                    if (pet.feeding > 150 && pet.feeding <= 200) {
                        if (pet.pk.getType() == 1) {
                            pet.feeding = 300;
                        } else if (pet.pk.getType() == 2) {
                            if (feed.getFeedType() == FeedType.LYCZ) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u9f99\u6d8e\u8349\u6c41\u53ea\u80fd\u5582\u517b\u8349\u98df\u5ba0\u7269\uff01"));
                                return false;
                            }
                            if (feed.getFeedType() == FeedType.DADIJH) {
                                pet.feeding = 300;
                                ++pet.fight_exp;
                                ++pet.currFightPoint;
                            }
                        }
                    } else if (pet.feeding <= 150 && pet.feeding > 100) {
                        if (pet.pk.getType() == 1) {
                            pet.feeding = 200;
                        } else if (pet.pk.getType() == 2) {
                            if (feed.getFeedType() == FeedType.LYCZ) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u9f99\u6d8e\u8349\u6c41\u53ea\u80fd\u5582\u517b\u8349\u98df\u5ba0\u7269\uff01"));
                                return false;
                            }
                            if (feed.getFeedType() == FeedType.DADIJH) {
                                pet.feeding = 200;
                                ++pet.fight_exp;
                                ++pet.currFightPoint;
                            }
                        }
                    } else if (pet.feeding <= 100 && pet.feeding > 50) {
                        if (pet.pk.getType() == 1) {
                            pet.feeding = 150;
                        } else if (pet.pk.getType() == 2) {
                            if (feed.getFeedType() == FeedType.LYCZ) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u9f99\u6d8e\u8349\u6c41\u53ea\u80fd\u5582\u517b\u8349\u98df\u5ba0\u7269\uff01"));
                                return false;
                            }
                            if (feed.getFeedType() == FeedType.DADIJH) {
                                pet.feeding = 150;
                                ++pet.fight_exp;
                                ++pet.currFightPoint;
                            }
                        }
                    } else if (pet.feeding <= 50) {
                        if (pet.pk.getType() == 1) {
                            pet.feeding = 100;
                        } else if (pet.pk.getType() == 2) {
                            if (feed.getFeedType() == FeedType.LYCZ) {
                                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u9f99\u6d8e\u8349\u6c41\u53ea\u80fd\u5582\u517b\u8349\u98df\u5ba0\u7269\uff01"));
                                return false;
                            }
                            if (feed.getFeedType() == FeedType.DADIJH) {
                                pet.feeding = 100;
                                ++pet.fight_exp;
                                ++pet.currFightPoint;
                            }
                        }
                    }
                    pet.updFEPoint();
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetEvolveChange(player, pet));
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseFeedStatusChange(player.getUserID(), pet));
                this.updatePet(_userID, pet);
            }
            return true;
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8fd9\u4e2a\u5ba0\u7269\u5df2\u7ecf\u6b7b\u4ea1\uff0c\u4e0d\u80fd\u5582\u517b\uff0c\u8bf7\u7528\u590d\u6d3b\u9053\u5177\u590d\u6d3b\uff01"));
        return false;
    }

    public void updatePetEquipment(final Pet pet) {
        PetDAO.updPetEquipment(pet);
    }

    private PetServiceImpl() {
        this.config = new PetConfig();
        this.petListContainer = (FastMap<Integer, PetList>) new FastMap();
    }

    public void reCalculatePetProperty(final Pet pet) {
        try {
            int maxHP = 0;
            int maxMP = 0;
            int inte = 0;
            int strength = 0;
            int spirit = 0;
            int lucky = 0;
            int defense = 0;
            int agility = 0;
            short physicsDeathblowLevel = 0;
            short magicDeathblowLevel = 0;
            short hitLevel = 0;
            short duckLevel = 0;
            int level = pet.level;
            physicsDeathblowLevel = CEService.physicsDeathblowLevel(pet.a_agi, pet.a_luck);
            magicDeathblowLevel = CEService.magicDeathblowLevel(pet.a_intel, pet.a_luck);
            hitLevel = CEService.hitLevel(pet.a_luck);
            duckLevel = CEService.duckLevel(pet.a_agi, pet.a_luck);
            inte = CEService.playerBaseAttribute(level, (float) pet.a_intel);
            strength = CEService.playerBaseAttribute(level, (float) pet.a_str);
            spirit = CEService.playerBaseAttribute(level, (float) pet.a_spi);
            lucky = CEService.playerBaseAttribute(level, (float) pet.a_luck);
            agility = CEService.playerBaseAttribute(level, (float) pet.a_agi);
            EquipmentInstance[] equipmentList = pet.getPetBodyWear().getEquipmentList();
            PetServiceImpl.log.debug(("pet equipmentlist size = " + equipmentList.length));
            EquipmentInstance[] array;
            for (int length = (array = equipmentList).length, i = 0; i < length; ++i) {
                EquipmentInstance ei = array[i];
                PetServiceImpl.log.debug(("ei  == " + ei));
                if (ei != null) {
                    maxMP += ei.getArchetype().atribute.mp;
                    inte += ei.getArchetype().atribute.inte;
                    strength += ei.getArchetype().atribute.strength;
                    spirit += ei.getArchetype().atribute.spirit;
                    lucky += ei.getArchetype().atribute.lucky;
                    defense += ei.getArchetype().atribute.defense;
                    agility += ei.getArchetype().atribute.agility;
                    physicsDeathblowLevel += ei.getArchetype().atribute.physicsDeathblowLevel;
                    hitLevel += ei.getArchetype().atribute.hitLevel;
                    magicDeathblowLevel += ei.getArchetype().atribute.magicDeathblowLevel;
                    duckLevel += ei.getArchetype().atribute.duckLevel;
                }
            }
            maxMP += CEService.mpByInte(inte, level, EObjectLevel.NORMAL.getMpCalPara());
            pet.mp = maxMP;
            pet.intel += inte;
            pet.str += strength;
            pet.spi += spirit;
            pet.luck += lucky;
            pet.agi += agility;
            pet.physicsDeathblowLevel = physicsDeathblowLevel;
            pet.magicDeathblowLevel = magicDeathblowLevel;
            pet.hitLevel = hitLevel;
            pet.duckLevel = duckLevel;
            pet.getBaseProperty().setMpMax(pet.mp);
            pet.getBaseProperty().setInte(pet.intel);
            pet.getBaseProperty().setStrength(pet.str);
            pet.getBaseProperty().setSpirit(pet.spi);
            pet.getBaseProperty().setLucky(pet.luck);
            pet.getBaseProperty().setPhysicsDeathblowLevel(pet.physicsDeathblowLevel);
            pet.getBaseProperty().setMagicDeathblowLevel(pet.magicDeathblowLevel);
            pet.getBaseProperty().setHitLevel(pet.hitLevel);
            pet.getBaseProperty().setPhysicsDuckLevel(pet.duckLevel);
            PetWeapon weapon = null;
            EquipmentInstance ei2 = pet.getPetBodyWear().getPetEqWeapon();
            if (ei2 != null) {
                weapon = (PetWeapon) ei2.getArchetype();
                int weaponMinMagicHarm = 0;
                int weaponMaxMagicHarm = 0;
                pet.setAttackRange(weapon.getAttackDistance());
                pet.setBaseAttackImmobilityTime((int) weapon.getImmobilityTime());
                if (ei2.getMagicDamage() != null) {
                    weaponMaxMagicHarm = ei2.getMagicDamage().maxDamageValue;
                    weaponMinMagicHarm = ei2.getMagicDamage().minDamageValue;
                    pet.maxMagicHarm = pet.magicHarm + weaponMaxMagicHarm;
                    pet.minMagicHarm = pet.magicHarm + weaponMinMagicHarm;
                }
                pet.maxAtkHarm = pet.getATK() + weapon.getMaxPhysicsAttack();
                pet.minAtkHarm = pet.getATK() + weapon.getMinPhysicsAttack();
            }
            pet.getBaseProperty().setMaxPhysicsAttack(pet.maxAtkHarm);
            pet.getBaseProperty().setMinPhysicsAttack(pet.minAtkHarm);
            pet.getActualProperty().clearNoneBaseProperty();
            pet.getActualProperty().setMaxPhysicsAttack(pet.getBaseProperty().getMaxPhysicsAttack());
            pet.getActualProperty().setMinPhysicsAttack(pet.getBaseProperty().getMinPhysicsAttack());
            pet.setActualAttackImmobilityTime(pet.getBaseAttackImmobilityTime());
            pet.getActualProperty().setInte(pet.getBaseProperty().getInte());
            pet.getActualProperty().setStrength(pet.getBaseProperty().getStrength());
            pet.getActualProperty().setSpirit(pet.getBaseProperty().getSpirit());
            pet.getActualProperty().setLucky(pet.getBaseProperty().getLucky());
            pet.getActualProperty().setAgility(pet.getBaseProperty().getAgility());
            pet.getActualProperty().setDefense(pet.getBaseProperty().getDefense());
            pet.getActualProperty().setPhysicsDeathblowLevel(pet.getBaseProperty().getPhysicsDeathblowLevel());
            pet.getActualProperty().setMagicDeathblowLevel(pet.getBaseProperty().getMagicDeathblowLevel());
            pet.getActualProperty().setHitLevel(pet.getBaseProperty().getHitLevel());
            pet.getActualProperty().setPhysicsDuckLevel(pet.getBaseProperty().getPhysicsDuckLevel());
            pet.getActualProperty().setMpMax(pet.getBaseProperty().getMpMax());
        } catch (Exception e) {
            PetServiceImpl.log.error("\u5237\u65b0\u5ba0\u7269\u5c5e\u6027 error\uff1a", (Throwable) e);
        }
    }

    public PetSkill getPetSkillIns(final int id) {
        PetSkill petSkill = PetSkillDict.getInstance().getPetSkill(id);
        if (petSkill != null) {
            try {
                return petSkill.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                PetServiceImpl.log.error("\u83b7\u53d6\u5ba0\u7269\u6280\u80fd\u5b9e\u4f8b error : ", (Throwable) e);
            }
        }
        return null;
    }

    private void initPetSkillList(final Pet pet, final ArrayList<int[]> _skillInfoList) {
        pet.petActiveSkillList.clear();
        pet.petPassiveSkillList.clear();
        for (final int[] skillInfo : _skillInfoList) {
            PetSkill petSkill = getInstance().getPetSkillIns(skillInfo[0]);
            if (petSkill != null) {
                if (petSkill instanceof PetActiveSkill) {
                    ((PetActiveSkill) petSkill).reduceCoolDownTime = skillInfo[1];
                    pet.petActiveSkillList.add((PetActiveSkill) petSkill);
                } else {
                    pet.petPassiveSkillList.add((PetPassiveSkill) petSkill);
                }
            }
        }
    }

    public boolean petLearnSkill(final HeroPlayer player, final Pet pet) {
        List<PetSkill> canLearnSkillList = PetSkillDict.getInstance().getPetCanLearnSkillList(pet);
        PetServiceImpl.log.debug(("pet can learn skill size = " + canLearnSkillList.size()));
        List<PetSkill> newLearnSkillList = new ArrayList<PetSkill>();
        List<PetSkill> oldLearnSkillList = new ArrayList<PetSkill>();
        for (final PetSkill skill : canLearnSkillList) {
            if (skill instanceof PetActiveSkill) {
                if (skill.getFrom != 1) {
                    continue;
                }
                for (final PetActiveSkill actSkill : pet.petActiveSkillList) {
                    if (skill.isSameName(actSkill)) {
                        if (skill.level - actSkill.level == 1) {
                            skill.isNewSkill = false;
                            skill._lowLevelSkillID = actSkill.id;
                            oldLearnSkillList.add(actSkill);
                            pet.petActiveSkillList.remove(actSkill);
                            pet.petActiveSkillList.add((PetActiveSkill) skill);
                            newLearnSkillList.add(skill);
                            break;
                        }
                        return false;
                    }
                }
                if (skill.level != 1) {
                    continue;
                }
                pet.petActiveSkillList.add((PetActiveSkill) skill);
                newLearnSkillList.add(skill);
            } else {
                if (!(skill instanceof PetPassiveSkill) || skill.getFrom != 1) {
                    continue;
                }
                for (final PetPassiveSkill paSkill : pet.petPassiveSkillList) {
                    if (skill.isSameName(paSkill)) {
                        if (skill.level - paSkill.level == 1) {
                            skill.isNewSkill = false;
                            skill._lowLevelSkillID = paSkill.id;
                            oldLearnSkillList.add(paSkill);
                            pet.petPassiveSkillList.remove(paSkill);
                            pet.petPassiveSkillList.add((PetPassiveSkill) skill);
                            newLearnSkillList.add(skill);
                            break;
                        }
                        return false;
                    }
                }
                if (skill.level != 1) {
                    continue;
                }
                pet.petPassiveSkillList.add((PetPassiveSkill) skill);
                newLearnSkillList.add(skill);
            }
        }
        if (PetDAO.addSkill(pet.id, newLearnSkillList)) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new PetLearnSkillNotify(pet, newLearnSkillList));
            return true;
        }
        for (final PetSkill skillx : newLearnSkillList) {
            if (skillx.getType() == ESkillType.ACTIVE) {
                pet.petActiveSkillList.remove(skillx);
            } else {
                pet.petPassiveSkillList.remove(skillx);
            }
        }
        for (final PetSkill skillx : oldLearnSkillList) {
            if (skillx.getType() == ESkillType.ACTIVE) {
                pet.petActiveSkillList.add((PetActiveSkill) skillx);
            } else {
                pet.petPassiveSkillList.add((PetPassiveSkill) skillx);
            }
        }
        return false;
    }

    public boolean petLearnSkillFromSkillBook(final HeroPlayer player, final Pet pet, final int skillID) {
        PetServiceImpl.log.debug("@@ petLearnSkillFromSkillBook .....");
        PetSkill skill = PetSkillDict.getInstance().getPetSkill(skillID);
        if (skill == null) {
            PetServiceImpl.log.debug("\u6ca1\u6709\u627e\u5230\u8fd9\u4e2a\u6280\u80fd...");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u8fd9\u4e2a\u6280\u80fd\uff01"));
            return false;
        }
        PetServiceImpl.log.debug(("skill book skillID= " + skill.id));
        List<PetSkill> canLearnSkillList = PetSkillDict.getInstance().getPetCanLearnSkillList(pet);
        if (!canLearnSkillList.contains(skill) || skill.getFrom != 2) {
            PetServiceImpl.log.debug("\u5ba0\u7269\u4e0d\u80fd\u5b66\u4e60\u6b64\u6280\u80fd\u4e66\u4e0a\u7684\u6280\u80fd...");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u4e0d\u80fd\u5b66\u4e60\u6b64\u6280\u80fd\uff01"));
            return false;
        }
        PetServiceImpl.log.debug("\u5ba0\u7269\u4ece\u6280\u80fd\u4e66\u5b66\u4e60\u6280\u80fd \u5b66\u4e60\u6280\u80fd ....");
        List<PetSkill> newLearnSkillList = new ArrayList<PetSkill>();
        List<PetSkill> oldLearnSkillList = new ArrayList<PetSkill>();
        if (skill.getType() == ESkillType.ACTIVE) {
            for (final PetActiveSkill petSkill : pet.petActiveSkillList) {
                if (petSkill.isSameName(skill)) {
                    if (skill.level - petSkill.level == 1) {
                        skill.isNewSkill = false;
                        skill._lowLevelSkillID = petSkill.id;
                        oldLearnSkillList.add(petSkill);
                        pet.petActiveSkillList.remove(petSkill);
                        pet.petActiveSkillList.add((PetActiveSkill) skill);
                        newLearnSkillList.add(skill);
                        break;
                    }
                    return false;
                }
            }
            if (skill.level == 1) {
                pet.petActiveSkillList.add((PetActiveSkill) skill);
                newLearnSkillList.add(skill);
            }
        } else {
            for (final PetPassiveSkill petSkill2 : pet.petPassiveSkillList) {
                if (petSkill2.isSameName(skill)) {
                    if (skill.level - petSkill2.level == 1) {
                        skill.isNewSkill = false;
                        skill._lowLevelSkillID = petSkill2.id;
                        oldLearnSkillList.add(petSkill2);
                        pet.petPassiveSkillList.remove(petSkill2);
                        pet.petPassiveSkillList.add((PetPassiveSkill) skill);
                        newLearnSkillList.add(skill);
                        break;
                    }
                    return false;
                }
            }
            if (skill.level == 1) {
                pet.petPassiveSkillList.add((PetPassiveSkill) skill);
                newLearnSkillList.add(skill);
            }
        }
        if (PetDAO.addSkill(pet.id, newLearnSkillList)) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new PetLearnSkillNotify(pet, newLearnSkillList));
            return true;
        }
        for (final PetSkill skillx : newLearnSkillList) {
            if (skillx.getType() == ESkillType.ACTIVE) {
                pet.petActiveSkillList.remove(skillx);
            } else {
                pet.petPassiveSkillList.remove(skillx);
            }
        }
        for (final PetSkill skillx : oldLearnSkillList) {
            if (skillx.getType() == ESkillType.ACTIVE) {
                pet.petActiveSkillList.add((PetActiveSkill) skillx);
            } else {
                pet.petPassiveSkillList.add((PetPassiveSkill) skillx);
            }
        }
        return false;
    }

    private class HatchPetTask extends TimerTask {

        HeroPlayer player;
        Pet petegg;

        public HatchPetTask(final HeroPlayer player, final Pet petegg) {
            this.player = player;
            this.petegg = petegg;
        }

        @Override
        public void run() {
            FastList<HeroPlayer> playerList = PlayerServiceImpl.getInstance().getPlayerList();
            if (playerList.contains(this.player)) {
                PetServiceImpl.log.debug("@@@@@@@ pet egg hatching .... @@@@@@@@");
                PetList petList = (PetList) PetServiceImpl.this.petListContainer.get(this.player.getUserID());
                if (petList != null) {
                    Pet pet = petList.getPet(this.petegg.id);
                    if (pet != null) {
                        PetServiceImpl.log.debug(("pet egg id = " + pet.id));
                        PetServiceImpl.log.debug(("\u73a9\u5bb6 " + this.player.getName() + " \u6b63\u5728\u5b75\u5316\u5ba0\u7269\u86cb " + pet.name));
                        if (pet.viewStatus == 1) {
                            long needTime = 1L - pet.totalOnlineTime;
                            PetServiceImpl.log.debug(("\u8fd8\u9700\u65f6\u95f4\uff1a " + needTime));
                            if (needTime == 0L) {
                                PetServiceImpl.log.debug("\u65f6\u95f4\u5230\uff0c\u8fdb\u5316\u5230\u5e7c\u5e74 \u3002\u3002\u3002\u3002");
                                pet.pk.setStage((short) 1);
                                pet.feeding = 250;
                                pet.fun = 1;
                                pet.iconID = PetDictionary.getInstance().getPet(pet.pk).iconID;
                                pet.imageID = PetDictionary.getInstance().getPet(pet.pk).imageID;
                                pet.animationID = PetDictionary.getInstance().getPet(pet.pk).animationID;
                                PetServiceImpl.log.debug(("pet pk = " + pet.pk.intValue() + ", iconID = " + pet.iconID + " -- imageID = " + pet.imageID));
                                PetDAO.updatePet(this.player.getUserID(), pet);
                                ResponseMessageQueue.getInstance().put(this.player.getMsgQueueIndex(), new ResponsePetStage(this.player.getUserID(), pet));
                                this.cancel();
                                ResponseMessageQueue.getInstance().put(this.player.getMsgQueueIndex(), new Warning("\u606d\u559c\uff01\u60a8\u7684\u5ba0\u7269\"" + pet.name + "\"\u5df2\u7ecf\u6210\u957f\u5230\u5e7c\u5e74\uff01"));
                                PetServiceImpl.this.hidePet(this.player, pet.id);
                                PetServiceImpl.log.debug("\u5b75\u5316\u6210\u529f \uff0c\u7ec8\u6b62\u6b64\u4efb\u52a1\u3002");
                            } else {
                                PetServiceImpl.log.debug(("\u65f6\u95f4\u672a\u5230.... \u76ee\u524d\u603b\u5728\u7ebf\u65f6\u95f4 \u4e3a \uff1a " + pet.totalOnlineTime));
                                ++pet.totalOnlineTime;
                            }
                        } else {
                            PetServiceImpl.log.debug("\u5ba0\u7269\u86cb\u88ab\u6536\u8d77\uff0c\u505c\u6b62\u5b75\u5316..");
                            PetDAO.updatePet(this.player.getUserID(), pet);
                            this.cancel();
                        }
                    } else {
                        PetServiceImpl.log.debug("\u83b7\u53d6\u5ba0\u7269\u86cb\u5931\u8d25\uff0c\u65e0\u6cd5\u5b75\u5316\uff01");
                        ResponseMessageQueue.getInstance().put(this.player.getMsgQueueIndex(), new Warning("\u83b7\u53d6\u5ba0\u7269\u86cb\u5931\u8d25\uff0c\u65e0\u6cd5\u5b75\u5316\uff01"));
                        this.cancel();
                    }
                }
            } else {
                PetServiceImpl.log.debug("\u73a9\u5bb6\u4e0b\u7ebf....");
                PetServiceImpl.log.debug("\u53d6\u6d88\u6b64\u5b75\u5316\u4efb\u52a1....");
                this.cancel();
            }
        }
    }
}
