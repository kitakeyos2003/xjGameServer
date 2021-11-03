// 
// Decompiled by Procyon v0.5.36
// 
package hero.lover.client;

import hero.player.HeroPlayer;
import hero.item.dictionary.GoodsContents;
import hero.item.special.SpouseTransport;
import hero.npc.function.system.MarryNPC;
import hero.npc.function.system.MarryGoods;
import hero.npc.message.AskPlayerAgreeWedding;
import hero.group.service.GroupServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.lover.service.LoverServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class OptionMarryRelation extends AbsClientProcess {

    private static final byte SHOW = 1;
    private static final byte DIVORCE = 2;
    private static final byte FORCE_DIVORCE = 3;
    private static final byte TRANSPORT = 4;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        switch (type) {
            case 1: {
                LoverServiceImpl.getInstance().showMarryRelation(player);
                break;
            }
            case 2: {
                String othername = player.spouse;
                HeroPlayer otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(othername);
                if (otherMarryPlayer == null) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u5728\u7ebf\uff01"));
                    break;
                }
                String myLover = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                if (!myLover.equals(othername)) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning(String.valueOf(othername) + "\u548c\u4f60\u4e0d\u662f\u592b\u59bb\uff01"));
                    break;
                }
                if (player.getGroupID() <= 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u592b\u59bb\u53cc\u65b9\u5fc5\u987b\u8981\u5355\u72ec\u7ec4\u6210\u4e00\u4e2a\u961f\u4f0d\uff01"));
                    break;
                }
                if (otherMarryPlayer.getGroupID() <= 0 || player.getGroupID() != otherMarryPlayer.getGroupID()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u592b\u59bb\u53cc\u65b9\u5fc5\u987b\u8981\u5355\u72ec\u7ec4\u6210\u4e00\u4e2a\u961f\u4f0d\uff01"));
                    break;
                }
                if (GroupServiceImpl.getInstance().getGroup(player.getGroupID()).getMemberNumber() == 2) {
                    ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(), new AskPlayerAgreeWedding(player, otherMarryPlayer, String.valueOf(player.getName()) + "\u8981\u548c\u4f60\u79bb\u5a5a\uff0c\n\u4f60\u540c\u610f\u5417\uff1f", (byte) 3));
                    break;
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u592b\u59bb\u961f\u4f0d\u91cc\u5fc5\u987b\u53ea\u6709\u53cc\u65b9\u4e24\u4e2a\u4eba\uff01"));
                break;
            }
            case 3: {
                String othername = player.spouse;
                HeroPlayer otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(othername);
                if (otherMarryPlayer == null) {
                    otherMarryPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(othername);
                    otherMarryPlayer = PlayerServiceImpl.getInstance().load(otherMarryPlayer.getUserID());
                }
                if (otherMarryPlayer == null) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u627e\u4e0d\u5230\u5bf9\u65b9\u73a9\u5bb6\uff0c\u5f3a\u5236\u79bb\u5a5a\u5931\u8d25"));
                    break;
                }
                String myLover = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                if (!myLover.equals(othername)) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning(String.valueOf(othername) + "\u548c\u4f60\u4e0d\u662f\u592b\u59bb\uff01"));
                    break;
                }
                int num = player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.FORCE_DIVORCE.getId());
                if (num == 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u6ca1\u6709\u5f3a\u5236\u79bb\u5a5a\u8bc1\u660e\uff0c\u662f\u5426\u53bb\u5546\u57ce\u8d2d\u4e70\uff01", (byte) 2, (byte) 1));
                    return;
                }
                MarryNPC.divorce(player, otherMarryPlayer, (byte) 1);
                break;
            }
            case 4: {
                int goodsID = MarryGoods.TRANSPORT.getId();
                int rangnum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID);
                if (rangnum == 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u9700\u8981\"\u592b\u59bb\u4f20\u9001\u7b26\"\u624d\u53ef\u4ee5\u4f20\u9001"));
                    break;
                }
                SpouseTransport stgoods = (SpouseTransport) GoodsContents.getGoods(goodsID);
                if (stgoods != null && stgoods.beUse(player, null, -1) && stgoods.disappearImmediatelyAfterUse()) {
                    stgoods.remove(player, (short) (-1));
                    break;
                }
                break;
            }
        }
    }
}
