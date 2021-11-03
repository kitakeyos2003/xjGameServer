// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.map.Map;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import hero.effect.message.AddEffectNotify;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.effect.Effect;
import hero.effect.detail.StaticEffect;
import hero.group.service.GroupServiceImpl;
import hero.fight.message.MpRefreshNotify;
import hero.fight.message.HpRefreshNotify;
import hero.fight.message.SpecialStatusChangeNotify;
import hero.dungeon.service.DungeonServiceImpl;
import hero.map.EMapType;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.ResponseMapBottomData;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.define.EClan;
import hero.map.service.MapServiceImpl;
import hero.share.EObjectLevel;
import hero.expressions.service.CEService;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class ReleaseSoul extends AbsClientProcess {

    private static Logger log;

    static {
        ReleaseSoul.log = Logger.getLogger((Class) ReleaseSoul.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        PlayerServiceImpl.getInstance().reCalculateRoleProperty(player);
        PlayerServiceImpl.getInstance().refreshRoleProperty(player);
        player.setHp(CEService.hpByStamina(CEService.playerBaseAttribute(player.getLevel(), player.getVocation().getStaminaCalPara()), player.getLevel(), player.getObjectLevel().getHpCalPara()));
        player.setMp(CEService.mpByInte(CEService.playerBaseAttribute(player.getLevel(), player.getVocation().getInteCalcPara()), player.getLevel(), EObjectLevel.NORMAL.getMpCalPara()));
        Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(player.where().getTargetMapIDAfterDie());
        ReleaseSoul.log.debug((Object) ("currmapid = " + player.where().getID() + ",targetmapid=" + player.where().getTargetMapIDAfterDie()));
        if (player.getClan() == EClan.HE_MU_DU) {
            short mapid = player.where().getMozuTargetMapIDAfterDie();
            ReleaseSoul.log.debug((Object) ("\u9b54\u65cf mapid = " + mapid));
            targetMap = MapServiceImpl.getInstance().getNormalMapByID(mapid);
        }
        player.setCellX(targetMap.getBornX());
        player.setCellY(targetMap.getBornY());
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapBottomData(player, targetMap, player.where()));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapGameObjectList(player.getLoginInfo().clientType, targetMap));
        Map currentMap = player.where();
        player.gotoMap(targetMap);
        EffectServiceImpl.getInstance().sendEffectList(player, targetMap);
        if (EMapType.DUNGEON == currentMap.getMapType()) {
            DungeonServiceImpl.getInstance().playerLeftDungeon(player);
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SpecialStatusChangeNotify(player.getObjectType().value(), player.getID(), (byte) 3));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new HpRefreshNotify(player.getObjectType().value(), player.getID(), player.getHp(), player.getHp(), false, false));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MpRefreshNotify(player.getObjectType().value(), player.getID(), player.getMp(), false));
        GroupServiceImpl.getInstance().groupMemberListHpMpNotify(player);
        if (player.getChargeInfo().huntBookTimeTotal > 0L) {
            StaticEffect sef = new StaticEffect(1, "\u53cc\u500d\u7ecf\u9a8c");
            sef.desc = "\u53cc\u500d\u7ecf\u9a8c";
            sef.releaser = player;
            sef.trait = Effect.EffectTrait.BUFF;
            sef.keepTimeType = Effect.EKeepTimeType.LIMITED;
            sef.traceTime = (short) (player.getChargeInfo().huntBookTimeTotal / 1000L);
            sef.iconID = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_icon;
            sef.viewType = 0;
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new AddEffectNotify(player, sef));
        }
        player.revive(null);
    }
}
