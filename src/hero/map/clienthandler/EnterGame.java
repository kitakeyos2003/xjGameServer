// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.clienthandler;

import hero.player.HeroPlayer;
import hero.log.service.LogServiceImpl;
import hero.charge.message.SendChargeList;
import hero.charge.service.ChargeServiceImpl;
import hero.effect.message.MoveSpeedChangerNotify;
import hero.map.message.ResponseMapGameObjectList;
import hero.pet.message.ResponseWearPetGridNumber;
import hero.item.message.SendBodyWearList;
import hero.player.message.ResponseRoleViewInfo;
import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.NotifyEnterMap;
import yoyo.core.queue.ResponseMessageQueue;
import hero.novice.service.NoviceServiceImpl;
import hero.map.service.MapServiceImpl;
import hero.player.define.EClan;
import hero.map.service.MapRelationDict;
import hero.map.EMapType;
import java.io.IOException;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.SessionServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class EnterGame extends AbsClientProcess {

    private static Logger log;

    static {
        EnterGame.log = Logger.getLogger((Class) EnterGame.class);
    }

    @Override
    public void read() throws Exception {
        SessionServiceImpl.getInstance().initSession(SessionServiceImpl.getInstance().getSession(this.contextData.sessionID));
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        EnterGame.log.info((Object) ("#### player name[" + player.getName() + "] login...."));
        PlayerServiceImpl.getInstance().initProperty(player);
        byte clientType = 0;
        try {
            clientType = this.yis.readByte();
            player.getLoginInfo().clientType = clientType;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (player.where().getMapType() == EMapType.DUNGEON && player.getGroupID() == 0) {
            short[] relations = MapRelationDict.getInstance().getRelationByMapID(player.where().getID());
            short targetmapid = relations[3];
            if (player.getClan() == EClan.HE_MU_DU && relations[9] > 0) {
                targetmapid = relations[9];
            }
            EnterGame.log.debug((Object) ("relation map id = " + targetmapid));
            Map where = MapServiceImpl.getInstance().getNormalMapByID(targetmapid);
            player.live(where);
            player.setCellX(where.getBornX());
            player.setCellY(where.getBornY());
        }
        Map where = player.where();
        EnterGame.log.info((Object) ("player entry game where = " + where.getName()));
        if (PlayerServiceImpl.getInstance().getNovice(player)) {
            NoviceServiceImpl.getInstance().novicePlayerAward(player);
        }
        EnterGame.log.info((Object) ("player entry game mapID = " + where.getID()));
        player.setDirection((byte) 2);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new NotifyEnterMap());
        EnterGame.log.info((Object) "player login send notify enter map message.");
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapBottomData(player, where, null));
        EnterGame.log.info((Object) "player login send response map bottom data ...");
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseRoleViewInfo(player));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SendBodyWearList(player.getBodyWear(), player));
        EnterGame.log.info((Object) "player login send body wear list message...");
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseWearPetGridNumber(player.getBodyWearPetList()));
        EnterGame.log.info((Object) "player login send response wear pet grid number message...");
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapGameObjectList(player.getLoginInfo().clientType, where));
        EnterGame.log.info((Object) "player login send response map game object list ..");
        PlayerServiceImpl.getInstance().refreshRoleProperty(player);
        EnterGame.log.info((Object) "player login refresh role property ...");
        if (where.getID() != NoviceServiceImpl.getInstance().getNoviceMapID()) {
            player.born(where);
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MoveSpeedChangerNotify(player.getObjectType().value(), player.getID(), player.getMoveSpeed()));
        EnterGame.log.info((Object) "player send move speed changer notify message ...");
        player.active();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SendChargeList(ChargeServiceImpl.getInstance().getFpListForRecharge(), ChargeServiceImpl.getInstance().getFeeTypeListForRecharge()));
        EnterGame.log.info((Object) "player login send charge list message ....");
        LogServiceImpl.getInstance().roleLoginLog(player.getLoginInfo().accountID, player.getUserID(), player.getName(), player.getLoginInfo().loginMsisdn, player.getLoginInfo().userAgent, player.getLoginInfo().clientVersion, player.getLoginInfo().clientType, player.getLoginInfo().communicatePipe, System.currentTimeMillis(), player.getLoginInfo().publisher, player.where().getID(), player.where().getName(), this.getIp());
        EnterGame.log.info((Object) "player login save loginlog end ...");
    }
}
