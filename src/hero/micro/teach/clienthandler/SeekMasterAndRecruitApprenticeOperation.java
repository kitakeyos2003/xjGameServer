// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach.clienthandler;

import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.micro.teach.message.MAOperateConfirm;
import yoyo.core.queue.ResponseMessageQueue;
import hero.micro.teach.TeachService;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class SeekMasterAndRecruitApprenticeOperation extends AbsClientProcess {

    private static final byte TYPE_OF_R_A = 1;
    private static final byte TYPE_OF_F_M = 2;
    private static final byte TYPE_OF_ACCEPT_R_A = 3;
    private static final byte TYPE_OF_ACCEPT_F_M = 4;
    private static final byte TYPE_OF_REFUSE_R_A = 5;
    private static final byte TYPE_OF_REFUSE_F_M = 6;

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte requestOperateType = this.yis.readByte();
        switch (requestOperateType) {
            case 1: {
                int apprenticeObjectID = this.yis.readInt();
                HeroPlayer apprentice = player.where().getPlayer(apprenticeObjectID);
                if (TeachService.authenRecruitAppr(player, apprentice)) {
                    ResponseMessageQueue.getInstance().put(apprentice.getMsgQueueIndex(), new MAOperateConfirm((byte) 1, player.getUserID(), player.getName()));
                    TeachService.waitingReply(apprentice);
                    break;
                }
                break;
            }
            case 2: {
                int masterObjectID = this.yis.readInt();
                HeroPlayer master = player.where().getPlayer(masterObjectID);
                if (TeachService.authenFollowMaster(player, master)) {
                    ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(), new MAOperateConfirm((byte) 2, player.getUserID(), player.getName()));
                    TeachService.waitingReply(master);
                    break;
                }
                break;
            }
            case 3: {
                int masterUserID = this.yis.readInt();
                HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(masterUserID);
                TeachService.recruitApprentice(master, player);
                TeachService.cancelWaitingTimer(player);
                break;
            }
            case 5: {
                int masterUserID = this.yis.readInt();
                HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(masterUserID);
                if (master != null) {
                    ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(), new Warning(String.valueOf(player.getName()) + "\u4e0d\u5c51\u505a\u60a8\u5f92\u5f1f"));
                }
                TeachService.cancelWaitingTimer(player);
                break;
            }
            case 4: {
                int apprenticeUserID = this.yis.readInt();
                HeroPlayer apprentice = PlayerServiceImpl.getInstance().getPlayerByUserID(apprenticeUserID);
                TeachService.followMaster(apprentice, player);
                TeachService.cancelWaitingTimer(player);
                break;
            }
            case 6: {
                int apprenticeUserID = this.yis.readInt();
                HeroPlayer apprentice = PlayerServiceImpl.getInstance().getPlayerByUserID(apprenticeUserID);
                if (apprentice != null) {
                    ResponseMessageQueue.getInstance().put(apprentice.getMsgQueueIndex(), new Warning(String.valueOf(player.getName()) + "\u62d2\u7edd\u6536\u60a8\u4e3a\u5f92"));
                }
                TeachService.cancelWaitingTimer(player);
                break;
            }
        }
    }
}
