// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import java.util.ArrayList;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.player.message.OtherWalkNotify;
import hero.login.LoginServiceImpl;
import hero.skill.detail.ESpecialStatus;
import hero.effect.detail.StaticEffect;
import hero.effect.Effect;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class Walk extends AbsClientProcess {

    private static Logger log;
    private static final byte COM_MAX_GRID = 6;
    private static final byte DEBUFF_ADD = 4;
    private static final byte BUFF_ADD = 8;
    private static final byte MOUNT_ADD = 8;

    static {
        Walk.log = Logger.getLogger((Class) Walk.class);
    }

    @Override
    public void read() throws Exception {
        try {
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
            if (player != null) {
                HeroPlayer heroPlayer = player;
                ++heroPlayer.walkCounter;
                if (player.walkCounter >= 50) {
                    long time = System.currentTimeMillis() - player.timeVerify;
                    Walk.log.info((Object) (String.valueOf(player.getName()) + "[\u79fb\u52a850\u6b21\u82b1\u8d39]:" + String.valueOf(time)));
                    if (time > 0L && time < 60001L) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Không thể đi", (byte) 1));
                        HeroPlayer heroPlayer2 = player;
                        ++heroPlayer2.illegalOperation;
                    }
                    player.timeVerify = System.currentTimeMillis();
                    player.walkCounter = 1;
                }
                byte walkGridNum = this.yis.readByte();
                byte[] walkPath = new byte[walkGridNum];
                boolean slowly = false;
                boolean fast = false;
                boolean mount = false;
                boolean wrong = false;
                for (final Effect ef : player.effectList) {
                    if (ef instanceof StaticEffect) {
                        if (((StaticEffect) ef).specialStatus == ESpecialStatus.HIDE) {
                            slowly = true;
                        } else if (((StaticEffect) ef).specialStatus == ESpecialStatus.MOVE_SLOWLY) {
                            slowly = true;
                        } else {
                            if (((StaticEffect) ef).specialStatus != ESpecialStatus.MOVE_FAST) {
                                continue;
                            }
                            fast = true;
                        }
                    }
                }
                if (!player.isInFighting()) {
                    Pet[] pets = player.getBodyWearPetList().getPetList();
                    for (int i = 0; i < pets.length; ++i) {
                        if (pets[i] != null && pets[i].pk.getType() == 1) {
                            mount = true;
                        }
                    }
                }
                if (slowly && fast) {
                    if (walkGridNum > 6) {
                        wrong = true;
                    }
                } else if (slowly) {
                    if (walkGridNum > 4) {
                        wrong = true;
                    }
                } else if (fast) {
                    if (walkGridNum > 8) {
                        wrong = true;
                    }
                } else if (mount) {
                    if (walkGridNum > 8) {
                        wrong = true;
                    }
                } else if (walkGridNum > 6) {
                    wrong = true;
                }
                if (wrong) {
                    HeroPlayer heroPlayer3 = player;
                    ++heroPlayer3.walkIllegalCounter;
                    return;
                }
                HeroPlayer heroPlayer4 = player;
                --heroPlayer4.walkIllegalCounter;
                if (player.illegalOperation >= 2) {
                    Walk.log.error((Object) ("\u73a9\u5bb6" + player.getName() + "\u4f7f\u7528\u79fb\u52a8\u52a0\u901f\u5916\u6302,\u8bf7\u6ce8\u610f"));
                    LoginServiceImpl.getInstance().resetPlayersStatus(player.getLoginInfo().accountID);
                }
                if (player.walkIllegalCounter >= 2) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("Để bảo mật tài khoản của bạn, vui lòng không sử dụng phần mềm của bên thứ ba khác để chơi trò chơi", (byte) 1));
                }
                if (player.walkIllegalCounter >= 3) {
                    Walk.log.error((Object) ("\u73a9\u5bb6" + player.getName() + "\u4f7f\u7528\u7be1\u6539\u5ba2\u6237\u7aef,\u8bf7\u6ce8\u610f"));
                    LoginServiceImpl.getInstance().resetPlayersStatus(player.getLoginInfo().accountID);
                }
                for (int j = 0; j < walkPath.length; ++j) {
                    player.go(walkPath[j] = this.yis.readByte());
                }
                byte endX = this.yis.readByte();
                byte endY = this.yis.readByte();
                player.setCellX(endX);
                player.setCellY(endY);
                player.needUpdateDB = true;
                if (player.where().getPlayerList().size() > 0) {
                    AbsResponseMessage msg = new OtherWalkNotify(player.getID(), player.getMoveSpeed(), walkPath, endX, endY);
                    for (int k = 0; k < player.where().getPlayerList().size(); ++k) {
                        HeroPlayer other = (HeroPlayer) player.where().getPlayerList().get(k);
                        if (other.isEnable() && other != player) {
                            ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), msg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
