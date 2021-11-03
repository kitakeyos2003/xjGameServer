// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.clienthandler;

import hero.dcnbbs.service.Result;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.dcnbbs.service.DCNService;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class AddTopic extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        String topicid = this.yis.readUTF();
        String title = this.yis.readUTF();
        String content = this.yis.readUTF();
        short pageno = this.yis.readShort();
        if (player.getLoginInfo().username == null) {
            PlayerDAO.loadPlayerAccountInfo(player);
        }
        String djtk = (player.getDcndjtk() != null && player.getDcndjtk().length() > 0) ? player.getDcndjtk() : "";
        if ("".equals(djtk) && player.getLoginInfo() != null && player.getLoginInfo().username.indexOf("1001#") > 0) {
            Result result = DCNService.login(player.getLoginInfo().username.replaceFirst("1001#", ""), "", player.getLoginInfo().password);
            if (result.isResult()) {
                djtk = result.getDjtk();
                player.setDcndjtk(djtk);
            }
        }
        if ("".equals(djtk)) {
            Result result = DCNService.sys(new StringBuilder(String.valueOf(player.getLoginInfo().accountID)).toString(), player.getName(), player.getLoginInfo().password);
            if (result.isResult()) {
                djtk = result.getDjtk();
                player.setDcndjtk(djtk);
            }
        }
        if ("".equals(djtk)) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff0c\u4f60\u65e0\u6cd5\u53d1\u5e16\u6216\u56de\u5e16\uff01"));
            return;
        }
        if (topicid == null || content == null) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff0c\u670d\u52a1\u5668\u63a5\u6536\u6570\u636e\u9519\u8bef\uff01"));
            return;
        }
        if (topicid.length() == 0 || content.length() == 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff0c\u670d\u52a1\u5668\u63a5\u6536\u6570\u636e\u9519\u8bef\uff01"));
            return;
        }
        if ("0".equals(topicid)) {
            if (title == null && title.length() <= 0) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff0c\u670d\u52a1\u5668\u63a5\u6536\u6570\u636e\u9519\u8bef\uff01"));
                return;
            }
            Result result = DCNService.newTopic("", djtk, "", title, content, "", player.getName(), "", "");
            if (result.isResult()) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u53d1\u5e16\u6210\u529f\uff01"));
                return;
            }
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u53d1\u5e16\u5931\u8d25\uff0c\u8bf7\u5c1d\u8bd5\u91cd\u65b0\u53d1\u5e16\uff01"));
            player.setDcndjtk("");
        } else {
            Result result = DCNService.replyTopic(topicid, "", djtk, "", title, content, "", player.getName());
            if (result.isResult()) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u56de\u5e16\u6210\u529f\uff01"));
                return;
            }
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u56de\u5e16\u5931\u8d25\uff0c\u8bf7\u91cd\u65b0\u5c1d\u8bd5\uff01"));
            player.setDcndjtk("");
        }
    }
}
