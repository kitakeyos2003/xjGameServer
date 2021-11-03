// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.clienthandler;

import hero.pet.Pet;
import hero.player.HeroPlayer;
import java.io.IOException;
import hero.item.bag.exception.BagException;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.special.BigTonicBall;
import hero.pet.service.PetServiceImpl;
import hero.item.special.ESpecialGoodsType;
import hero.item.dictionary.GoodsContents;
import hero.item.SpecialGoods;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.ResponseSpecialGoodsBag;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class OperateSpecialGoodsBag extends AbsClientProcess {

    private static Logger log;
    private static final byte LIST = 1;
    private static final byte USE = 2;
    private static final byte SET_SHORTCUT_KEY = 3;
    private static final byte DICE = 4;
    private static final byte SORT = 5;

    static {
        OperateSpecialGoodsBag.log = Logger.getLogger((Class) OperateSpecialGoodsBag.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte operation = this.yis.readByte();
            switch (operation) {
                case 1: {
                    OperateSpecialGoodsBag.log.debug((Object) ("operation special goods bag size=" + player.getInventory().getSpecialGoodsBag().getFullGridNumber()));
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseSpecialGoodsBag(player.getInventory().getSpecialGoodsBag(), player.getShortcutKeyList()));
                    break;
                }
                case 2: {
                    byte gridIndex = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    OperateSpecialGoodsBag.log.debug((Object) ("use special goods index = " + gridIndex + ", goodsid=" + goodsID));
                    boolean hadGoods = player.getInventory().getSpecialGoodsBag().getAllItem()[gridIndex][0] == goodsID || player.getInventory().getPetGoodsBag().getAllItem()[gridIndex][0] == goodsID;
                    if (!hadGoods) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u80cc\u5305\u4e2d\u65e0\u6b64\u7269\u54c1", (byte) 0));
                        break;
                    }
                    SpecialGoods specialGoods = (SpecialGoods) GoodsContents.getGoods(goodsID);
                    if (specialGoods.getNeedLevel() > player.getLevel()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u7b49\u7ea7\u4e0d\u591f", (byte) 0));
                        break;
                    }
                    OperateSpecialGoodsBag.log.debug((Object) ("special goods = " + specialGoods.getType()));
                    if (specialGoods.getType() == ESpecialGoodsType.PET_FEED) {
                        int petId = this.yis.readInt();
                        Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
                        if (pet == null) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u8fd9\u4e2a\u5ba0\u7269\uff01", (byte) 0));
                            break;
                        }
                        if (specialGoods.beUse(player, pet, goodsID) && specialGoods.disappearImmediatelyAfterUse()) {
                            specialGoods.remove(player, gridIndex);
                            break;
                        }
                        break;
                    } else if (specialGoods.getType() == ESpecialGoodsType.PET_REVIVE) {
                        int petId = this.yis.readInt();
                        if (specialGoods.beUse(player, petId, gridIndex) && specialGoods.disappearImmediatelyAfterUse()) {
                            specialGoods.remove(player, gridIndex);
                            break;
                        }
                        break;
                    } else if (specialGoods.getType() == ESpecialGoodsType.PET_DICARD) {
                        int petId = this.yis.readInt();
                        byte code = this.yis.readByte();
                        Pet pet2 = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
                        if (pet2 == null) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u8fd9\u4e2a\u5ba0\u7269\uff01", (byte) 0));
                            break;
                        }
                        pet2.dicard_code = code;
                        if (specialGoods.beUse(player, pet2, gridIndex) && specialGoods.disappearImmediatelyAfterUse()) {
                            specialGoods.remove(player, gridIndex);
                            break;
                        }
                        break;
                    } else if (specialGoods.getType() == ESpecialGoodsType.PET_SKILL_BOOK) {
                        int petId = this.yis.readInt();
                        OperateSpecialGoodsBag.log.debug((Object) ("pet use skillbook , petid=" + petId));
                        Pet pet = PetServiceImpl.getInstance().getPet(player.getUserID(), petId);
                        if (pet == null) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u8fd9\u4e2a\u5ba0\u7269\uff01", (byte) 0));
                            break;
                        }
                        OperateSpecialGoodsBag.log.debug((Object) ("start skill from skillbook , pet.level=" + pet.level));
                        if (specialGoods.beUse(player, pet, gridIndex) && specialGoods.disappearImmediatelyAfterUse()) {
                            specialGoods.remove(player, gridIndex);
                            break;
                        }
                        break;
                    } else if (specialGoods.getType() == ESpecialGoodsType.BIG_TONIC) {
                        if (player.getInventory().getSpecialGoodsBag().tonicList.size() <= 0) {
                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u8fd9\u4e2a\u7269\u54c1\uff01", (byte) 0));
                            break;
                        }
                        if (((BigTonicBall) specialGoods).isActivate == 2) {
                            player.getInventory().getSpecialGoodsBag().eatTonicBall(gridIndex, specialGoods.getID(), player);
                            break;
                        }
                        player.getInventory().getSpecialGoodsBag().installTonicBall(gridIndex, player);
                        break;
                    } else if (specialGoods.getType() == ESpecialGoodsType.PET_PER) {
                        if (player.getInventory().getSpecialGoodsBag().petPerCardList.size() > 0) {
                            player.getInventory().getSpecialGoodsBag().usePetPerCard(gridIndex, specialGoods.getID(), player);
                            break;
                        }
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u8fd9\u4e2a\u7269\u54c1\uff01", (byte) 0));
                    } else {
                        if (specialGoods.beUse(player, null, gridIndex) && specialGoods.disappearImmediatelyAfterUse()) {
                            specialGoods.remove(player, gridIndex);
                            break;
                        }
                    }
                    break;
                }
                case 3: {
                    byte shortcutKey = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    PlayerServiceImpl.getInstance().setShortcutKey(player, shortcutKey, (byte) 3, goodsID);
                    break;
                }
                case 4: {
                    byte gridIndex = this.yis.readByte();
                    int goodsID = this.yis.readInt();
                    try {
                        GoodsServiceImpl.getInstance().diceSingleGoods(player, player.getInventory().getSpecialGoodsBag(), gridIndex, goodsID, CauseLog.DEL);
                    } catch (BagException pe) {
                        OperateSpecialGoodsBag.log.error((Object) "\u7279\u6b8a\u7269\u54c1\u4e22\u5f03 error:", (Throwable) pe);
                    }
                    break;
                }
                case 5: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8be5\u529f\u80fd\u6682\u4e0d\u5f00\u653e"));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
