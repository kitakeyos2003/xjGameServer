// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.clienthandler;

import hero.effect.Effect;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.item.bag.PetContainer;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.pet.message.ResponsePetList;
import hero.share.message.Warning;
import hero.item.service.GoodsServiceImpl;
import hero.pet.message.PetChangeNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.item.detail.EGoodsTrait;
import hero.share.ME2GameObject;
import hero.effect.service.EffectServiceImpl;
import hero.effect.dictionry.EffectDictionary;
import hero.pet.service.PetServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.pet.message.ResponsePetContainer;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class OperatePet extends AbsClientProcess {

    private static Logger log;
    public static final byte LIST = 1;
    public static final byte SHOW = 2;
    public static final byte HIDE = 3;
    public static final byte DICE = 4;
    public static final byte SET_SELL_TRAIT = 5;
    public static final byte AUTO_SELL = 6;
    public static final byte COMPLETE_SELL = 7;
    public static final byte DIED_LIST = 8;
    public static final byte PET_CONTAINER = 10;
    public static final byte PET_GOODS_LIST = 11;
    public static final byte PLAYER_BODY_PETS = 12;
    public static final byte ATTACK_AWAIT = 9;
    public static final byte MOUNT_AWAIT = 13;

    static {
        OperatePet.log = Logger.getLogger((Class) OperatePet.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte operation = this.yis.readByte();
        int petID = this.yis.readInt();
        OperatePet.log.debug((Object) ("operate type = " + operation));
        OperatePet.log.debug((Object) ("operate pet id = " + petID));
        switch (operation) {
            case 1: {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetContainer(player.getInventory().getPetContainer()));
                break;
            }
            case 2: {
                OperatePet.log.debug((Object) ("\u8981\u663e\u793a\u7684\u5ba0\u7269\uff1a id = " + petID));
                PetServiceImpl.getInstance().showPetx(player, petID);
                Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                if (pet.pk.getType() == 1) {
                    player.setMount(true);
                    Effect effect = EffectDictionary.getInstance().getEffectRef(pet.mountFunction);
                    EffectServiceImpl.getInstance().appendSkillEffect(player, player, effect);
                    break;
                }
                break;
            }
            case 3: {
                if (petID == 0) {
                    EffectServiceImpl.getInstance().downMountEffect(player);
                    break;
                }
                PetServiceImpl.getInstance().hidePet(player, petID);
                Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                if (pet.pk.getType() == 1) {
                    player.setMount(false);
                    Effect effect = EffectDictionary.getInstance().getEffectRef(pet.mountFunction);
                    EffectServiceImpl.getInstance().appendSkillEffect(player, player, effect);
                    break;
                }
                break;
            }
            case 4: {
                PetServiceImpl.getInstance().dicePet(player, petID);
                break;
            }
            case 5: {
                byte traitValue = this.yis.readByte();
                PlayerServiceImpl.getInstance().updateAutoSellTrait(player, EGoodsTrait.getTrait(traitValue));
                break;
            }
            case 6: {
                Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                MapSynchronousInfoBroadcast.getInstance().put(player.where(), new PetChangeNotify(player.getID(), (byte) 3, pet.imageID, pet.pk.getType()), true, player.getID());
                int money = GoodsServiceImpl.getInstance().autoSellMAE(player);
                if (money != 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u5b8c\u6210\u4e86\u8d29\u5356"));
                    break;
                }
                break;
            }
            case 7: {
                Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petID);
                MapSynchronousInfoBroadcast.getInstance().put(player.where(), new PetChangeNotify(player.getID(), (byte) 2, pet.imageID, pet.pk.getType()), true, player.getID());
                break;
            }
            case 8: {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetList(PetServiceImpl.getInstance().getDiedPetList(player.getUserID()), (byte) player.getAutoSellTrait().value()));
            }
            case 10: {
                OperatePet.log.debug((Object) "\u67e5\u770b\u6ca1\u6709\u88ab\u88c5\u5907\u7684\u5ba0\u7269\u7684\u5217\u8868....");
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetContainer(player.getInventory().getPetContainer()));
                break;
            }
            case 11: {
                OperatePet.log.debug((Object) "\u67e5\u770b\u5ba0\u7269\u7269\u54c1\u5217\u8868");
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseSpecialGoodsBag(player.getInventory().getPetGoodsBag(), null));
                break;
            }
            case 12: {
                OperatePet.log.debug((Object) "\u67e5\u770b\u5df2\u7ecf\u88ab\u88c5\u5907\u7684\u5ba0\u7269\u7684\u5217\u8868....");
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetContainer(player.getBodyWearPetList()));
                break;
            }
        }
    }
}
