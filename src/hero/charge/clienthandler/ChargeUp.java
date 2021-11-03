// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.clienthandler;

import hero.charge.FeeType;
import hero.charge.FeePointInfo;
import hero.player.HeroPlayer;
import hero.log.service.LogServiceImpl;
import hero.gm.service.GmServiceImpl;
import hero.charge.service.ChargeDAO;
import hero.log.service.ServiceType;
import hero.charge.service.ChargeServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class ChargeUp extends AbsClientProcess {

    private static Logger log;

    static {
        ChargeUp.log = Logger.getLogger((Class) ChargeUp.class);
    }

    @Override
    public void read() throws Exception {
        ChargeUp.log.debug((Object) "\u795e\u5dde\u4ed8\u5145\u503c....");
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte chargeid = this.yis.readByte();
        String transID = this.yis.readUTF();
        String mid = this.yis.readUTF();
        int userID = this.yis.readInt();
        int accountID = this.yis.readInt();
        int publisher = this.yis.readInt();
        String ip = this.yis.readUTF();
        byte rechargetype = (byte) ((player.getLoginInfo().accountID == accountID) ? 1 : 2);
        String othername = player.getName();
        if (rechargetype == 2) {
            HeroPlayer other = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(userID);
            if (other != null) {
                othername = other.getName();
            }
            if (other == null) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u627e\u5230\u73a9\u5bb6", (byte) 2, (byte) 5));
                return;
            }
        }
        FeePointInfo feePointInfo = ChargeServiceImpl.getInstance().getFpcodeByTypeAndPrice(chargeid);
        String msisdn = player.getLoginInfo().loginMsisdn;
        String bindmsisdn = player.getLoginInfo().boundMsisdn;
        String cardnum = this.yis.readUTF();
        String cardpass = this.yis.readUTF();
        int cardsum = feePointInfo.price;
        byte cardtypecombine = feePointInfo.typeID;
        FeeType feeType = ChargeServiceImpl.getInstance().getFeeTypeById(feePointInfo.typeID);
        if (feeType != null) {
            cardtypecombine = (byte) feeType.cardType.getId();
        }
        if (ip == null || ip.trim().length() == 0) {
            ip = this.getIp();
        }
        String result = ChargeServiceImpl.getInstance().chargeUpSZF(transID, accountID, userID, accountID, cardnum, cardpass, cardsum, cardtypecombine, msisdn, feePointInfo.price, ServiceType.CHARGEUP, publisher, ip, bindmsisdn);
        if (result != null && result.trim().length() > 0) {
            ChargeUp.log.debug((Object) ("charge up szf result =" + result));
            if (result.indexOf("#") > 0) {
                String[] res = result.split("#");
                String orderid = res[0];
                String status_code = res[1];
                String status_desc = "\u5df2\u63d0\u4ea4\uff0c\u8bf7\u7b49\u5f85\u7ed3\u679c\uff01";
                if (res.length == 3) {
                    status_desc = res[2];
                }
                ChargeUp.log.info((Object) ("chargeup: transID=" + transID + ",res:[" + orderid + "][" + status_code + "][" + status_desc + "]"));
                int syncres = status_code.equals("200") ? 0 : 1;
                ChargeDAO.insertChargeUpSZF(player.getLoginInfo().accountID, player.getUserID(), (byte) 1, rechargetype, accountID, userID, transID, status_code, feePointInfo.price, orderid, syncres, feePointInfo.fpcode);
                ChargeDAO.insertChargeUpSZFAccount(GmServiceImpl.gameID, GmServiceImpl.serverID, accountID, userID, transID, orderid);
                if (status_code.equals("200")) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5145\u503c\u5904\u7406\u4e2d\uff0c\u8bf7\u8010\u5fc3\u7b49\u5f85..."));
                } else {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning(status_desc, (byte) 1));
                }
                LogServiceImpl.getInstance().chargeGenLog(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), player.getLoginInfo().loginMsisdn, transID, feePointInfo.fpcode, rechargetype, othername, accountID, new StringBuilder(String.valueOf(syncres)).toString(), "\u795e\u5dde\u4ed8", res[1], status_desc);
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5145\u503c\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5...", (byte) 1));
            }
        } else {
            LogServiceImpl.getInstance().chargeGenLog(player.getLoginInfo().accountID, player.getLoginInfo().username, player.getUserID(), player.getName(), player.getLoginInfo().loginMsisdn, transID, feePointInfo.fpcode, rechargetype, othername, accountID, "1", "\u795e\u5dde\u4ed8", "\u5931\u8d25", "\u7ed3\u679c\u4e3a\u7a7a");
        }
    }

    public static void main(final String[] args) {
        String result = ChargeServiceImpl.getInstance().queryBalancePoint(50057);
        System.out.println(result);
    }
}
