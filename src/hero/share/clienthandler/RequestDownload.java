// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.ResponseDownload;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.service.AllPictureDataDict;
import hero.share.service.ShareServiceImpl;
import hero.share.service.ShareConfig;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class RequestDownload extends AbsClientProcess {

    private static Logger log;
    private static final byte FILE_TYPE_PNG = 0;
    private static final byte FILE_TYPE_ANU = 1;
    private static final byte FILE_TYPE_MAP = 2;

    static {
        RequestDownload.log = Logger.getLogger((Class) RequestDownload.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        String path = "";
        String url = this.yis.readUTF();
        byte clientType = this.yis.readByte();
        String mainPath = "";
        if (3 == clientType) {
            mainPath = ShareServiceImpl.getInstance().getConfig().getHighPath();
        }
        path = String.valueOf(mainPath) + url;
        byte[] bytes = AllPictureDataDict.getInstance().getFileBytes(path);
        RequestDownload.log.info((Object) ("\u53d1\u9001\u524d\u7684fileURL--->" + url));
        if (bytes != null) {
            RequestDownload.log.info((Object) ("\u53d1\u9001\u524d\u7684file.length--->" + bytes.length));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseDownload(url, bytes));
        } else {
            RequestDownload.log.warn((Object) ("\u6ca1\u6709\u627e\u5230\u9700\u8981\u4e0b\u8f7d\u7684\u8d44\u6e90:" + url));
        }
    }
}
