// 
// Decompiled by Procyon v0.5.36
// 
package hero.duel.clienthandler;

import hero.duel.Duel;
import hero.player.HeroPlayer;
import hero.duel.message.ResponseDuel;
import hero.social.service.SocialServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.duel.service.DuelServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class DuleRequest extends AbsClientProcess {

    private static final byte TYPE_OF_INVITE = 1;
    private static final byte TYPE_OF_ACCEPT = 2;
    private static final byte TYPE_OF_REFUSE = 3;
    private static final byte TYPE_OF_BUSY = 4;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte requestType = this.yis.readByte();
        switch (requestType) {
            case 1: {
                if (player == null || !player.isEnable() || player.isDead()) {
                    return;
                }
                if (DuelServiceImpl.getInstance().isDueling(player.getUserID())) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u5904\u4e8e\u51b3\u6597\u72b6\u6001"));
                    return;
                }
                int targetObjectID = this.yis.readInt();
                HeroPlayer target = player.where().getPlayer(targetObjectID);
                if (target == null || !target.isEnable() || target.isDead()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u65e0\u6548\u7684\u51b3\u6597\u76ee\u6807"));
                    return;
                }
                if (target.isInFighting()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u6b63\u5fd9"));
                    return;
                }
                if (DuelServiceImpl.getInstance().isDueling(target.getUserID())) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u65e0\u6548\u7684\u51b3\u6597\u76ee\u6807"));
                    return;
                }
                if (SocialServiceImpl.getInstance().beBlack(player.getUserID(), target.getUserID())) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u88ab\u4eba\u5bb6\u62c9\u9ed1\u4e86"));
                    break;
                }
                if (DuelServiceImpl.getInstance().inviteDuel(player, target)) {
                    ResponseMessageQueue.getInstance().put(target.getMsgQueueIndex(), new ResponseDuel(player.getID(), player.getName()));
                    break;
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u4e0e\u76ee\u6807\u4e0d\u5728\u4e00\u8d77"));
                break;
            }
            case 2: {
                if (player == null) {
                    return;
                }
                if (!player.isEnable() || player.isDead()) {
                    DuelServiceImpl.getInstance().removeByOneSide(player.getUserID());
                    return;
                }
                int invitorObjectID = this.yis.readInt();
                HeroPlayer invitor = player.where().getPlayer(invitorObjectID);
                if (invitor == null || !invitor.isEnable() || invitor.isDead()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u65e0\u6548\u7684\u51b3\u6597\u76ee\u6807"));
                    DuelServiceImpl.getInstance().removeByOneSide(player.getUserID());
                    return;
                }
                Duel duel = DuelServiceImpl.getInstance().getDuelByOneSide(player.getUserID());
                if (duel == null) {
                    break;
                }
                if (player.where().getID() != duel.duleMapID) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u9003\u51fa\u4e86\u51b3\u6597\u573a\u5730\uff0c\u51b3\u6597\u7ed3\u675f"));
                    ResponseMessageQueue.getInstance().put(invitor.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u9003\u51fa\u4e86\u51b3\u6597\u573a\u5730\uff0c\u51b3\u6597\u7ed3\u675f"));
                    break;
                }
                if (invitor.where().getID() != duel.duleMapID) {
                    DuelServiceImpl.getInstance().removeByOneSide(player.getUserID());
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u9003\u51fa\u4e86\u51b3\u6597\u573a\u5730\uff0c\u51b3\u6597\u7ed3\u675f"));
                    ResponseMessageQueue.getInstance().put(invitor.getMsgQueueIndex(), new Warning("\u60a8\u9003\u51fa\u4e86\u51b3\u6597\u573a\u5730\uff0c\u51b3\u6597\u7ed3\u675f"));
                    break;
                }
                duel.isConfirming = false;
                player.startDuel(invitor.getUserID());
                invitor.startDuel(player.getUserID());
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseDuel(invitor.getID()));
                ResponseMessageQueue.getInstance().put(invitor.getMsgQueueIndex(), new ResponseDuel(player.getID()));
                break;
            }
            case 3: {
                DuelServiceImpl.getInstance().removeByOneSide(player.getUserID());
                int invitorObjectID = this.yis.readInt();
                HeroPlayer invitor = player.where().getPlayer(invitorObjectID);
                if (invitor != null && invitor.isEnable()) {
                    ResponseMessageQueue.getInstance().put(invitor.getMsgQueueIndex(), new Warning(String.valueOf(player.getName()) + "\u4e0d\u5c51\u4e8e\u4e0e\u4f60\u51b3\u6597"));
                    break;
                }
                break;
            }
            case 4: {
                DuelServiceImpl.getInstance().removeByOneSide(player.getUserID());
                int invitorObjectID = this.yis.readInt();
                HeroPlayer invitor = player.where().getPlayer(invitorObjectID);
                if (invitor != null && invitor.isEnable()) {
                    ResponseMessageQueue.getInstance().put(invitor.getMsgQueueIndex(), new Warning(String.valueOf(player.getName()) + "\u6b63\u5fd9"));
                    break;
                }
                break;
            }
        }
    }
}
