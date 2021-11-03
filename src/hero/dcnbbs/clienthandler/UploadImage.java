// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.clienthandler;

import hero.dcnbbs.service.Result;
import hero.player.HeroPlayer;
import hero.dcnbbs.service.ZipUtil;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.dcnbbs.service.DCNService;
import hero.player.service.PlayerDAO;
import hero.player.service.PlayerServiceImpl;
import java.util.Base64;
import yoyo.core.process.AbsClientProcess;

public class UploadImage extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        String imgtype = this.yis.readUTF();
        int bytesize = this.yis.readInt();
        byte[] bytes = new byte[bytesize];
        this.yis.readFully(bytes, 0, bytesize);
        if (player.getLoginInfo().username == null) {
            PlayerDAO.loadPlayerAccountInfo(player);
        }
        String mid = "";
        String djtk = (player.getDcndjtk() != null && player.getDcndjtk().length() > 0) ? player.getDcndjtk() : "";
        if (("".equals(djtk) || "".equals(mid)) && player.getLoginInfo().username.indexOf("1001#") > 0) {
            Result result = DCNService.login(player.getLoginInfo().username.replaceFirst("1001#", ""), "", player.getLoginInfo().password);
            mid = result.getReList();
            djtk = result.getDjtk();
            player.setDcndjtk(djtk);
        } else {
            Result result = DCNService.sys(new StringBuilder(String.valueOf(player.getLoginInfo().accountID)).toString(), player.getName(), player.getLoginInfo().password);
            if (result.isResult()) {
                mid = result.getReList();
                djtk = result.getDjtk();
                player.setDcndjtk(djtk);
            }
        }
        if ("".equals(djtk) || "".equals(mid)) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff0c\u4f60\u65e0\u6743\u9650\u4e0a\u4f20\u56fe\u7247\uff01"));
            return;
        }
        if (imgtype == null || imgtype.length() <= 0 || bytesize <= 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25\uff0c\u670d\u52a1\u5668\u63a5\u6536\u6570\u636e\u9519\u8bef\uff01"));
            return;
        }
        byte[] b = ZipUtil.decompress(bytes);
        Result result2 = DCNService.uploadImg(mid, djtk, "", new StringBuilder(String.valueOf(player.getUserID() + System.currentTimeMillis())).toString(), Base64.getEncoder().encodeToString(b), "", imgtype);
        if (result2.isResult()) {
            Result result3 = DCNService.newTopic("", djtk, "", "\u6211\u7684\u6e38\u620f\u622a\u56fe", "\u6211\u7684\u6e38\u620f\u622a\u56fe", "", player.getName(), result2.getReList(), "\u6211\u7684\u6e38\u620f\u622a\u56fe");
            if (result3.isResult()) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4e0a\u4f20\u56fe\u7247\u6210\u529f\u5df2\u4e3a\u4f60\u751f\u6210\u56fe\u7247\u5c55\u793a\u8d34\uff01"));
            } else {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4e0a\u4f20\u622a\u56fe\u6210\u529f\uff0c\u751f\u6210\u5c55\u793a\u8d34\u5931\u8d25\uff0c\u8bf7\u8054\u7cfbGM\u5904\u7406\uff01"));
            }
            return;
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4e0a\u4f20\u622a\u56fe\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5\uff01"));
        player.setDcndjtk("");
    }
}
