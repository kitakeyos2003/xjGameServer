// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.clienthandler;

import hero.charge.FeeIni;
import hero.task.Push;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.task.message.ResponseTaskPushType;
import yoyo.core.queue.ResponseMessageQueue;
import hero.charge.service.ChargeServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.share.service.ShareServiceImpl;
import hero.share.service.ShareConfig;
import hero.task.service.TaskServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class TaskPushFeeMode extends AbsClientProcess {

    private static Logger log;

    static {
        TaskPushFeeMode.log = Logger.getLogger((Class) TaskPushFeeMode.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int pushID = this.yis.readInt();
        String swcode = this.yis.readUTF();
        String pseudoCode = this.yis.readUTF();
        byte mode = 0;
        Push push = TaskServiceImpl.getInstance().getTaskPush(pushID);
        int price = push.point / ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
        LogServiceImpl.getInstance().taskPushOption(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), pushID, 0, "\u786e\u8ba4", price);
        if (player.getChargeInfo().pointAmount <= push.point) {
            int rmb = push.point / ShareServiceImpl.getInstance().getConfig().getFeePointConvert();
            FeeIni fee = ChargeServiceImpl.getInstance().getFeeIniForTask(player.getLoginInfo().accountID, player.getUserID(), swcode, player.getLoginInfo().loginMsisdn, pseudoCode, player.getLoginInfo().publisher, rmb);
            TaskPushFeeMode.log.info((Object) ("\u63a5\u6536\u5230\u8ba1\u8d39\u6a21\u5757\u8fd4\u56de\u4fe1\u606ffeeCode=" + fee.feeCode + ";feeType=feeType=" + fee.feeType + ";feeUrlID=" + fee.feeUrlID + ";price=" + fee.price + ";status=" + fee.status + ";sumPrice=" + fee.sumPrice + ";transID=" + fee.transID));
            if (fee.status == 0) {
                TaskPushFeeMode.log.debug((Object) ("\u4efb\u52a1\u8ba1\u8d39\u8bf7\u6c42\u8fd4\u56de\u8ba1\u8d39\u4fe1\u606f\uff1a" + fee.feeType));
                if (fee.feeType.equals("sms")) {
                    mode = 1;
                    int smsCount = fee.sumPrice / fee.price;
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskPushType(mode, 0, fee.feeUrlID, fee.feeCode, fee.transID, smsCount, fee.price));
                } else if (fee.feeType.equals("ng")) {
                    mode = 2;
                    if (fee.feeUrlID.equals("hero")) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskPushType(mode, 0, fee.feeCode, (byte) 1, fee.transID));
                    } else if (fee.feeUrlID.equals("jiutian")) {
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskPushType(mode, 0, fee.feeCode, (byte) 0, fee.transID));
                    } else {
                        TaskPushFeeMode.log.info((Object) ("\u63a5\u6536\u5230\u65e0\u6cd5\u8bc6\u522b\u7684\u6a21\u5f0f:" + fee.feeUrlID));
                    }
                } else {
                    TaskPushFeeMode.log.info((Object) ("\u63a5\u6536\u5230\u65e0\u6cd5\u8bc6\u522b\u7684\u6a21\u5f0f:" + fee.feeType));
                }
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskPushType(mode, 0));
                TaskPushFeeMode.log.warn((Object) ("\u8fdb\u884c\u4efb\u52a1\u8ba1\u8d39\u70b9\u7684\u65f6\u5019\u8bf7\u6c42\u8ba1\u8d39\u670d\u52a1\u5668\u5931\u8d25,\u72b6\u6001:" + String.valueOf(fee.status)));
                LogServiceImpl.getInstance().taskPushOption(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), pushID, 4, "\u4efb\u52a1\u8ba1\u8d39\u70b9\u83b7\u53d6\u8ba1\u8d39\u914d\u7f6e\u5931\u8d25:" + String.valueOf(fee.status) + ";\u79fb\u52a8\u4f2a\u7801=" + pseudoCode + ";\u624b\u673a\u53f7\u7801=" + player.getLoginInfo().loginMsisdn, price);
            }
            TaskServiceImpl.getInstance().enterTaskPush(player, pushID, mode, true);
        } else {
            TaskServiceImpl.getInstance().enterTaskPush(player, pushID, mode, true);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseTaskPushType(mode, 0));
        }
    }
}
