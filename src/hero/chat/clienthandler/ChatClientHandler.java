// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.clienthandler;

import hero.player.HeroPlayer;
import java.io.IOException;
import hero.item.Goods;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.dictionary.GoodsContents;
import hero.item.special.WorldHorn;
import hero.player.service.PlayerDAO;
import hero.log.service.LogServiceImpl;
import hero.chat.service.ChatServiceImpl;
import hero.social.service.SocialServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class ChatClientHandler extends AbsClientProcess {

    private static Logger log;
    private static final boolean IS_NOT_USE = false;
    private static final int WORLD_CHANNEL_WAIT = 180000;
    private static final int CLAN_CHANNEL_WAIT = 30000;

    static {
        ChatClientHandler.log = Logger.getLogger((Class) ChatClientHandler.class);
    }

    protected int getPriority() {
        return 0;
    }

    @Override
    public void read() throws Exception {
        try {
            HeroPlayer speaker = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            byte type = this.yis.readByte();
            ChatClientHandler.log.debug((Object) ("chat type = " + type));
            boolean isBlack = PlayerServiceImpl.getInstance().playerChatIsBlank(speaker.getLoginInfo().accountID, speaker.getUserID());
            if (isBlack) {
                ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u88ab\u7981\u8a00\uff01", (byte) 0));
                return;
            }
            if (type == 0) {
                String name = this.yis.readUTF();
                String content = this.yis.readUTF();
                HeroPlayer target = PlayerServiceImpl.getInstance().getPlayerByName(name);
                if (target != null && target.isEnable()) {
                    if (!SocialServiceImpl.getInstance().beBlack(speaker.getUserID(), target.getUserID())) {
                        content = ChatServiceImpl.parseGoodsInContent(speaker, content);
                        ChatServiceImpl.getInstance().sendSinglePlayer(speaker, target.getName(), speaker, content, false);
                        ChatServiceImpl.getInstance().sendSinglePlayer(speaker, target.getName(), target, content, false);
                        ChatServiceImpl.getInstance().toGMaddChatContent(speaker.getName(), target.getName(), content);
                        LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID, speaker.getUserID(), speaker.getName(), speaker.getLoginInfo().loginMsisdn, target.getUserID(), target.getName(), "\u79c1\u804a", speaker.where().getName(), content);
                    } else {
                        ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), new Warning("\u8fd9\u5bb6\u4f19\u597d\u50cf\u4e0d\u5728\u554a", (byte) 0));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), new Warning("\u8fd9\u5bb6\u4f19\u597d\u50cf\u4e0d\u5728\u554a", (byte) 0));
                }
            } else {
                String content = this.yis.readUTF();
                ChatClientHandler.log.debug((Object) ("chat content == " + content));
                content = ChatServiceImpl.parseGoodsInContent(speaker, content);
                ChatClientHandler.log.debug((Object) ("parseGoodsInContent = " + content));
                if (type == 1) {
                    if (System.currentTimeMillis() - speaker.chatWorldTime >= 180000L) {
                        ChatServiceImpl.getInstance().sendWorldPlayer(speaker, content);
                        speaker.chatWorldTime = System.currentTimeMillis();
                        PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatWorldTime);
                        LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID, speaker.getUserID(), speaker.getName(), speaker.getLoginInfo().loginMsisdn, 0, "", "\u4e16\u754c", speaker.where().getName(), content);
                    } else {
                        int forgetGoods = speaker.getInventory().getSpecialGoodsBag().getGoodsNumber(340024);
                        WorldHorn horn = (WorldHorn) GoodsContents.getGoods(340024);
                        if (forgetGoods <= 0) {
                            ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), new Warning("\u8bf4\u5f97\u8fd9\u4e48\u5feb\u4e00\u5b9a\u5f88\u7d2f\uff0c\u4f11\u606f3\u5206\u949f\u518d\u53d1\u8a00\u5427", (byte) 0));
                            return;
                        }
                        PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatWorldTime);
                        ChatServiceImpl.getInstance().sendWorldPlayer(speaker, content);
                        PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatWorldTime);
                        LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID, speaker.getUserID(), speaker.getName(), speaker.getLoginInfo().loginMsisdn, 0, "", "\u4e16\u754c", speaker.where().getName(), content);
                        GoodsServiceImpl.getInstance().deleteSingleGoods(speaker, speaker.getInventory().getSpecialGoodsBag(), horn, 1, CauseLog.CLANCHAT);
                        ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), new Warning("\u7a81\u7834\u558a\u8bdd\u9650\u5236,\u4f7f\u7528\u4e861\u4e2a%fn".replaceAll("%fn", horn.getName()), (byte) 0));
                    }
                } else if (type == 2) {
                    ChatServiceImpl.getInstance().sendMapPlayer(speaker, content);
                    LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID, speaker.getUserID(), speaker.getName(), speaker.getLoginInfo().loginMsisdn, 0, "", "\u5730\u56fe", speaker.where().getName(), content);
                } else if (type == 3) {
                    ChatServiceImpl.getInstance().sendGroupPlayer(speaker, content);
                    LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID, speaker.getUserID(), speaker.getName(), speaker.getLoginInfo().loginMsisdn, 0, "", "\u961f\u4f0d", speaker.where().getName(), content);
                } else if (type == 4) {
                    ChatServiceImpl.getInstance().sendGuildContent(speaker, content);
                    LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID, speaker.getUserID(), speaker.getName(), speaker.getLoginInfo().loginMsisdn, 0, "", "\u516c\u4f1a", speaker.where().getName(), content);
                } else if (type == 10) {
                    if (System.currentTimeMillis() - speaker.chatClanTime < 30000L) {
                        int forgetGoods = speaker.getInventory().getSpecialGoodsBag().getGoodsNumber(340024);
                        WorldHorn horn = (WorldHorn) GoodsContents.getGoods(340024);
                        ResponseMessageQueue.getInstance().put(speaker.getMsgQueueIndex(), new Warning("\u8bf4\u5f97\u8fd9\u4e48\u5feb\u4e00\u5b9a\u5f88\u7d2f\uff0c\u4f11\u606f30\u79d2\u518d\u53d1\u8a00\u5427", (byte) 0));
                        return;
                    }
                    ChatServiceImpl.getInstance().sendClan(speaker, speaker.getClan().getID(), content);
                    speaker.chatClanTime = System.currentTimeMillis();
                    PlayerDAO.updateClanChatWait(speaker.getUserID(), speaker.chatClanTime);
                    LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID, speaker.getUserID(), speaker.getName(), speaker.getLoginInfo().loginMsisdn, 0, "", "\u6c0f\u65cf", speaker.where().getName(), content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
