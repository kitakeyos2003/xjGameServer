// 
// Decompiled by Procyon v0.5.36
// 
package hero.social.clientHandler;

import hero.player.HeroPlayer;
import hero.share.service.LogWriter;
import hero.social.message.SelectOtherPlayer;
import hero.social.message.ResponseSocialRelationdList;
import hero.social.ESocialRelationType;
import hero.social.service.SocialServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class OperateSocialRelation extends AbsClientProcess {

    private static Logger log;
    private static final byte OPERATION_OF_VIEW_LIST = 1;
    private static final byte OPERATION_OF_ADD = 2;
    private static final byte OPERATION_OF_REMOVE = 3;
    private static final byte SELECT_OF_OTHER_PLAYER = 4;
    private static final byte SELECT_OF_RANDOM = 5;
    private static final byte SELECT_OF_NAME = 6;

    static {
        OperateSocialRelation.log = Logger.getLogger((Class) OperateSocialRelation.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte operation = this.yis.readByte();
            byte socialType = this.yis.readByte();
            switch (operation) {
                case 2: {
                    String name = this.yis.readUTF();
                    if (player.getName().equals(name)) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4e0d\u80fd\u662f\u81ea\u5df1"));
                        return;
                    }
                    HeroPlayer target = PlayerServiceImpl.getInstance().getPlayerByName(name);
                    if (target == null || !target.isEnable()) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u4e0d\u5728\u7ebf"));
                        break;
                    }
                    SocialServiceImpl.getInstance().add(player, target, socialType);
                    break;
                }
                case 3: {
                    String name = this.yis.readUTF();
                    SocialServiceImpl.getInstance().remove(player, name, socialType, true);
                    break;
                }
                case 1: {
                    OperateSocialRelation.log.debug((Object) ("\u8fdb\u5165\u793e\u4ea4\u5217\u8868 == 1,type=" + socialType));
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseSocialRelationdList(socialType, SocialServiceImpl.getInstance().getSocialRelationList(player.getUserID(), ESocialRelationType.getSocialRelationType(socialType))));
                    break;
                }
                case 4: {
                    byte sex = this.yis.readByte();
                    byte vocation = this.yis.readByte();
                    short level = this.yis.readShort();
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SelectOtherPlayer((byte) 2, sex, vocation, level, player));
                }
                case 5: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SelectOtherPlayer((byte) 1, player));
                }
                case 6: {
                    String name = this.yis.readUTF();
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SelectOtherPlayer((byte) 3, name, player));
                    break;
                }
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        }
    }
}
