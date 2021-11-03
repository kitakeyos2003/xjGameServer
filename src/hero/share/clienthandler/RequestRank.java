// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.clienthandler;

import hero.share.RankInfo;
import java.util.List;
import hero.player.HeroPlayer;
import hero.share.message.ResponseRankData;
import hero.share.RankMenuField;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.ResponseRankMenu;
import hero.share.service.ShareServiceImpl;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class RequestRank extends AbsClientProcess {

    private static Logger log;
    private static final byte OPERATE_MAIN = 0;
    private static final byte OPERATE_MENU = 1;

    static {
        RequestRank.log = Logger.getLogger((Class) RequestRank.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        byte type = this.yis.readByte();
        if (type == 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRankMenu(ShareServiceImpl.getInstance().getRankTypeMap()));
        } else {
            byte menuLevel = this.yis.readByte();
            byte menuID = this.yis.readByte();
            RequestRank.log.debug((Object) ("operate rank menu level=" + menuLevel + ",menuid=" + menuID));
            RankMenuField rmf = ShareServiceImpl.getInstance().getRankTypeMap().get(menuID);
            if (menuLevel == 1 || menuLevel == 2) {
                List<RankInfo> rankInfoList = ShareServiceImpl.getInstance().getRankInfoList(menuID, 0, 0, false);
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRankData(rankInfoList, rmf.fieldList));
            } else if (menuLevel == 3) {
                byte secondMenuID = this.yis.readByte();
                byte thirdMenuID = this.yis.readByte();
                RequestRank.log.debug((Object) ("operate rank second menuID =" + secondMenuID + ",third menuid=" + thirdMenuID));
                RankMenuField secondMenu = rmf.getChildRankMenuFieldByID(secondMenuID);
                if (secondMenu != null) {
                    RequestRank.log.debug((Object) ("second menu name=" + secondMenu.name));
                    RankMenuField thirdMenu = secondMenu.getChildRankMenuFieldByID(thirdMenuID);
                    if (thirdMenu != null) {
                        RequestRank.log.debug((Object) ("third menu name=" + thirdMenu.name + ",vocation:" + thirdMenu.name));
                        String vocations = thirdMenu.vocation;
                        RequestRank.log.debug((Object) ("rank vocation=" + vocations));
                        int vocation1 = 0;
                        int vocation2 = 0;
                        boolean moreVocations = false;
                        if (vocations.indexOf(",") > 0) {
                            String[] vs = vocations.split(",");
                            vocation1 = Integer.parseInt(vs[0]);
                            vocation2 = Integer.parseInt(vs[1]);
                            moreVocations = true;
                        } else {
                            vocation1 = Integer.parseInt(vocations);
                        }
                        List<RankInfo> rankInfoList2 = ShareServiceImpl.getInstance().getRankInfoList(menuID, vocation1, vocation2, moreVocations);
                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRankData(rankInfoList2, rmf.fieldList));
                    }
                }
            }
        }
    }
}
