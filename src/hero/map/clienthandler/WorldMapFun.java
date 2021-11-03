// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.clienthandler;

import hero.npc.Npc;
import hero.map.Map;
import hero.item.Goods;
import hero.player.HeroPlayer;
import hero.map.message.ResponseWorld;
import hero.map.message.ResponseMapNpcList;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import hero.item.dictionary.GoodsContents;
import hero.share.message.Warning;
import hero.item.special.TaskTransportItem;
import hero.map.message.ResponseMapDesc;
import hero.map.service.WorldMapDict;
import hero.map.service.MapServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.WorldMap;
import java.util.List;
import hero.map.message.ResponseWorldMaps;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class WorldMapFun extends AbsClientProcess {

    private static Logger log;
    private static final byte ENTER = 0;
    private static final byte DESC = 1;
    private static final byte GOTO_MAP_START = 2;
    private static final byte FIND_NPC = 3;
    private static final byte WORLD = 4;
    private static final byte SHOW_SINGLE_WORLD = 5;
    private static final byte GOTO_MAP = 6;
    private static final byte GOTO_NPC_START = 7;
    private static final byte GOTO_NPC = 8;

    static {
        WorldMapFun.log = Logger.getLogger((Class) WorldMapFun.class);
    }

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        if (player != null && player.isSelling()) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseWorldMaps(null, null, (byte) 0, "\u6446\u644a\u72b6\u6001\u4e2d\u4e0d\u80fd\u4f7f\u7528\u6b64\u529f\u80fd"));
            return;
        }
        byte type = this.yis.readByte();
        switch (type) {
            case 0: {
                byte worldType = MapServiceImpl.getInstance().getPlayerMapWorldType(player);
                if (worldType == 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseWorldMaps(null, null, (byte) 0, "\u5f53\u524d\u5730\u56fe\u4e0d\u80fd\u4f7f\u7528\u6b64\u529f\u80fd"));
                    return;
                }
                List<WorldMap> mapList = WorldMapDict.getInstance().getWorldMapListByType(worldType);
                String name = "";
                if (mapList != null) {
                    name = MapServiceImpl.getInstance().getWorldNameByType(worldType);
                }
                WorldMapFun.log.debug((Object) (" name =" + name));
                WorldMap maxWorldMap = WorldMapDict.getInstance().getMaxWorldMapByName(name);
                WorldMapFun.log.debug((Object) ("max world map = " + maxWorldMap));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseWorldMaps(mapList, maxWorldMap, (byte) 1, ""));
                break;
            }
            case 1: {
                short mapID = this.yis.readShort();
                String desc = WorldMapDict.getInstance().getMapDesc(mapID);
                if (desc == null) {
                    desc = "";
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapDesc(desc, mapID));
                break;
            }
            case 2: {
                int transPortNum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
                WorldMapFun.log.debug((Object) ("trans port num=" + transPortNum));
                if (transPortNum <= 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f20\u9001\u5230\u8be5\u5730\u56fe\u9700\u8981\u4f20\u9001\u7b26\uff0c\u4f60\u8981\u8d2d\u4e70\u4e00\u4e2a\u5417\uff1f", (byte) 2, (byte) 1));
                    break;
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f20\u9001\u5230\u8be5\u5730\u56fe\u9700\u8981\u6d88\u8d39\u4e00\u4e2a\u4f20\u9001\u7b26\uff0c\u4f60\u786e\u5b9a\u5417\uff1f", (byte) 2, (byte) 7));
                break;
            }
            case 7: {
                int transPortNum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
                WorldMapFun.log.debug((Object) ("trans port num=" + transPortNum));
                if (transPortNum <= 0) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f20\u9001\u5230NPC\u9700\u8981\u4f20\u9001\u7b26\uff0c\u4f60\u8981\u8d2d\u4e70\u4e00\u4e2a\u5417\uff1f", (byte) 2, (byte) 1));
                    break;
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f20\u9001\u5230NPC\u9700\u8981\u6d88\u8d39\u4e00\u4e2a\u4f20\u9001\u7b26\uff0c\u4f60\u786e\u5b9a\u5417\uff1f", (byte) 2, (byte) 6));
                break;
            }
            case 6: {
                Goods goods = GoodsContents.getGoods(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
                if (!((TaskTransportItem) goods).beUse(player, null, -1)) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f7f\u7528\u4f20\u9001\u7b26\u5931\u8d25\uff0c\u4e0d\u80fd\u4f20\u9001", (byte) 1));
                    break;
                }
                short mapID2 = this.yis.readShort();
                Map entranceMap = MapServiceImpl.getInstance().getNormalMapByID(mapID2);
                if (entranceMap == null) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4e0d\u5b58\u5728\u7684\u5730\u56fe\uff0cID:" + mapID2));
                    return;
                }
                player.setCellX(entranceMap.getBornX());
                player.setCellY(entranceMap.getBornY());
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapBottomData(player, entranceMap, player.where()));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapGameObjectList(player.getLoginInfo().clientType, entranceMap));
                player.gotoMap(entranceMap);
                EffectServiceImpl.getInstance().sendEffectList(player, entranceMap);
                if (((TaskTransportItem) goods).disappearImmediatelyAfterUse()) {
                    ((TaskTransportItem) goods).remove(player, (short) (-1));
                    break;
                }
                break;
            }
            case 8: {
                Goods goods = GoodsContents.getGoods(TaskTransportItem.TASK_TRANSPORT_ITEM_ID);
                if (!((TaskTransportItem) goods).beUse(player, null, -1)) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f7f\u7528\u4f20\u9001\u7b26\u5931\u8d25\uff0c\u4e0d\u80fd\u4f20\u9001", (byte) 1));
                    break;
                }
                short mapID2 = this.yis.readShort();
                int npcID = this.yis.readInt();
                Map entranceMap2 = MapServiceImpl.getInstance().getNormalMapByID(mapID2);
                if (entranceMap2 == null) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4e0d\u5b58\u5728\u7684\u5730\u56fe\uff0cID:" + mapID2));
                    return;
                }
                Npc npc = entranceMap2.getNpc(npcID);
                if (npc == null) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u8fd9\u4e2aNPC:" + npcID));
                    return;
                }
                player.setCellX(npc.getCellX());
                player.setCellY(npc.getCellY());
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapBottomData(player, entranceMap2, player.where()));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapGameObjectList(player.getLoginInfo().clientType, entranceMap2));
                player.gotoMap(entranceMap2);
                EffectServiceImpl.getInstance().sendEffectList(player, entranceMap2);
                if (((TaskTransportItem) goods).disappearImmediatelyAfterUse()) {
                    ((TaskTransportItem) goods).remove(player, (short) (-1));
                    break;
                }
                break;
            }
            case 3: {
                short mapID = this.yis.readShort();
                Map map = MapServiceImpl.getInstance().getNormalMapByID(mapID);
                if (map != null) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapNpcList(map.getNpcList(), mapID));
                    break;
                }
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4e0d\u5b58\u5728\u7684\u5730\u56fe\uff0cID:" + mapID));
                break;
            }
            case 4: {
                byte worldType = MapServiceImpl.getInstance().getPlayerMapWorldType(player);
                WorldMapFun.log.debug((Object) ("\u8fdb\u5165\u4e16\u754c\u5730\u56fe.. world type=" + worldType));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseWorld(worldType));
                break;
            }
            case 5: {
                byte showType = this.yis.readByte();
                WorldMapFun.log.debug((Object) ("show single world type=" + showType));
                String name2 = MapServiceImpl.getInstance().getWorldNameByType(showType);
                WorldMapFun.log.debug((Object) ("show single world name = " + name2));
                WorldMap maxWorldMap2 = WorldMapDict.getInstance().getMaxWorldMapByName(name2);
                WorldMapFun.log.debug((Object) ("max world map = " + maxWorldMap2.name));
                List<WorldMap> mapList2 = WorldMapDict.getInstance().getWorldMapListByType(showType);
                WorldMapFun.log.debug((Object) ("show single world maps = " + mapList2));
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseWorldMaps(mapList2, maxWorldMap2, (byte) 1, ""));
                break;
            }
        }
    }
}
