// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach.clienthandler;

import hero.player.HeroPlayer;
import hero.share.service.LogWriter;
import hero.share.message.Warning;
import hero.micro.teach.TeachService;
import yoyo.core.packet.AbsResponseMessage;
import hero.micro.teach.message.ResponseMAList;
import hero.micro.service.MicroServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class OperateMAList extends AbsClientProcess {

    private static Logger log;
    private static final byte OPERATION_OF_VIEW_LIST = 1;
    private static final byte OPERATION_OF_LEFT_MASTER = 2;
    private static final byte OPERATION_OF_REDUCE_APPRENTICE = 3;
    private static final byte OPERATION_OF_TEACH_KNOWLEDGE = 4;
    private static final byte OPERATION_OF_DISSMISS = 5;

    static {
        OperateMAList.log = Logger.getLogger((Class) OperateMAList.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        try {
            byte operation = this.yis.readByte();
            switch (operation) {
                case 1: {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMAList(MicroServiceImpl.getInstance().getMasterApprentice(player.getUserID()), player.getUserID()));
                    break;
                }
                case 2: {
                    if (TeachService.leftMaster(player)) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMAList(MicroServiceImpl.getInstance().getMasterApprentice(player.getUserID()), player.getUserID()));
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u79bb\u5f00\u5e08\u5085\u53d1\u751f\u5931\u8d25"));
                    break;
                }
                case 3: {
                    int apprenticeUserID = this.yis.readInt();
                    OperateMAList.log.debug((Object) ("apprenticeUserID = " + apprenticeUserID));
                    boolean flag = TeachService.reduceApprentice(player, apprenticeUserID);
                    if (flag) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMAList(MicroServiceImpl.getInstance().getMasterApprentice(player.getUserID()), player.getUserID()));
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u8e22\u6389\u5f92\u5f1f\u53d1\u751f\u5931\u8d25"));
                    break;
                }
                case 4: {
                    int apprenticeUserID = this.yis.readInt();
                    HeroPlayer apprenticeUser = PlayerServiceImpl.getInstance().getPlayerByUserID(apprenticeUserID);
                    TeachService.teachKnowledge(player, apprenticeUser);
                    break;
                }
                case 5: {
                    OperateMAList.log.debug((Object) "dissmiss apprentice ...");
                    TeachService.dismissAll(player.getUserID());
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMAList(MicroServiceImpl.getInstance().getMasterApprentice(player.getUserID()), player.getUserID()));
                    break;
                }
            }
        } catch (Exception e) {
            LogWriter.error(null, e);
        }
    }
}
