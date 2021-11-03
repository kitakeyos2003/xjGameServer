// Decompiled with: CFR 0.151
// Class Version: 6
package hero.group.clientHandler;

import hero.group.Group;
import hero.group.message.GroupInviteNotify;
import hero.group.service.GroupServiceImpl;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.social.service.SocialServiceImpl;
import java.io.IOException;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.queue.ResponseMessageQueue;

public class GroupOperation
        extends AbsClientProcess {

    private static Logger log = Logger.getLogger(GroupOperation.class);
    private static final byte OPERATION_OF_ADD_MEMBER = 1;
    private static final byte OPERATION_OF_CONFIRM_INVITOR = 2;
    private static final byte OPERATION_OF_UP_TO_ASSISTANT = 3;
    private static final byte OPERATION_OF_DOWN_TO_NORMAL = 4;
    private static final byte OPERATION_OF_TRANSFER_LEADER = 5;
    private static final byte OPERATION_OF_CHANGE_SUB_GROUP = 6;
    private static final byte OPERATION_OF_REMOVE_MEMBER = 7;
    private static final byte OPERATION_OF_LEFT_GROUP = 8;

    @Override
    public void read() throws Exception {
        HeroPlayer heroPlayer = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (heroPlayer != null) {
            try {
                byte by = this.yis.readByte();
                switch (by) {
                    case 1: {
                        String string = this.yis.readUTF();
                        log.debug("加人入队伍： " + string);
                        if (heroPlayer.getName().equals(string)) {
                            ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("不能自己组自己"));
                            return;
                        }
                        boolean bl = false;
                        HeroPlayer heroPlayer2 = PlayerServiceImpl.getInstance().getPlayerByName(string);
                        if (heroPlayer2 != null) {
                            if (!SocialServiceImpl.getInstance().beBlack(heroPlayer.getUserID(), heroPlayer2.getUserID())) {
                                log.debug(String.valueOf(string) + " groupid = " + heroPlayer2.getGroupID());
                                if (heroPlayer2.getGroupID() == 0) {
                                    if (heroPlayer2.getClan() != heroPlayer.getClan()) {
                                        ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("不同的种族"));
                                    } else if (heroPlayer.isDead()) {
                                        ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("无法邀请变成鬼魂的玩家"));
                                    } else {
                                        bl = true;
                                    }
                                } else {
                                    ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning(String.valueOf(string) + "已有队伍"));
                                }
                            } else {
                                ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("您被人家拉黑了"));
                            }
                        } else {
                            ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("玩家不在线"));
                        }
                        if (heroPlayer.getGroupID() > 0) {
                            Group group = GroupServiceImpl.getInstance().getGroup(heroPlayer.getGroupID());
                            if (group != null && group.getMemberNumber() >= 10) {
                                ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("对不起，您的队伍人满为患了"));
                            }
                            if (group != null && !GroupServiceImpl.getInstance().canInvite(group, heroPlayer.getUserID())) {
                                ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("您没有邀请权限"));
                            }
                        }
                        if (bl) {
                            ResponseMessageQueue.getInstance().put(heroPlayer2.getMsgQueueIndex(), new GroupInviteNotify(heroPlayer.getName()));
                            break;
                        }
                        return;
                    }
                    case 7: {
                        int n = this.yis.readInt();
                        GroupServiceImpl.getInstance().removeMember(heroPlayer, n);
                        break;
                    }
                    case 3: {
                        int n = this.yis.readInt();
                        GroupServiceImpl.getInstance().addAssistant(heroPlayer, n);
                        break;
                    }
                    case 4: {
                        int n = this.yis.readInt();
                        GroupServiceImpl.getInstance().removeAssistant(heroPlayer, n);
                        break;
                    }
                    case 6: {
                        int n = this.yis.readInt();
                        byte by2 = this.yis.readByte();
                        log.debug("改变队员位置： " + by2);
                        GroupServiceImpl.getInstance().changeSubGroup(heroPlayer, n, by2);
                        break;
                    }
                    case 8: {
                        GroupServiceImpl.getInstance().leftGroup(heroPlayer);
                        break;
                    }
                    case 5: {
                        int n = this.yis.readInt();
                        GroupServiceImpl.getInstance().changeGroupLeader(heroPlayer, n);
                        break;
                    }
                    case 2: {
                        Group group;
                        byte by3 = this.yis.readByte();
                        String string = this.yis.readUTF();
                        HeroPlayer heroPlayer3 = PlayerServiceImpl.getInstance().getPlayerByName(string);
                        if (heroPlayer3 == null || !heroPlayer3.isEnable()) {
                            if (by3 != 0) {
                                ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("邀请者已下线"));
                            }
                            return;
                        }
                        if (heroPlayer3.getGroupID() > 0 && (group = GroupServiceImpl.getInstance().getGroup(heroPlayer3.getGroupID())) != null && group.getMemberNumber() >= 10) {
                            ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("对不起，您的队伍人满为患了"));
                            return;
                        }
                        if (heroPlayer.getGroupID() != 0) {
                            ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("您已有队伍"));
                        }
                        if (by3 != 0) {
                            if (heroPlayer.getGroupID() == 0) {
                                if (heroPlayer3.getClan() != heroPlayer.getClan()) {
                                    ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("不同的种族"));
                                    break;
                                }
                                if (heroPlayer3.getGroupID() != 0) {
                                    GroupServiceImpl.getInstance().add(heroPlayer, heroPlayer3.getGroupID());
                                    break;
                                }
                                GroupServiceImpl.getInstance().createGroup(heroPlayer3, heroPlayer);
                                break;
                            }
                            ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("您已有队伍"));
                            ResponseMessageQueue.getInstance().put(heroPlayer3.getMsgQueueIndex(), new Warning(String.valueOf(heroPlayer.getName()) + "已有队伍"));
                            break;
                        }
                        if (heroPlayer3 == null || heroPlayer == null) {
                            break;
                        }
                        ResponseMessageQueue.getInstance().put(heroPlayer3.getMsgQueueIndex(), new Warning(String.valueOf(heroPlayer.getName()) + "婉拒了您的邀请"));
                    }
                    default: {
                        break;
                    }
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }
}
