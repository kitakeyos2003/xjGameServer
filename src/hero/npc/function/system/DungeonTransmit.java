// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import java.util.ArrayList;
import hero.npc.Npc;
import hero.map.Map;
import hero.dungeon.Dungeon;
import hero.group.Group;
import hero.dungeon.DungeonDataModel;
import hero.task.service.TaskServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import hero.group.service.GroupServiceImpl;
import hero.dungeon.service.DungeonServiceImpl;
import hero.dungeon.service.DungeonConfig;
import hero.dungeon.service.DungeonDataModelDictionary;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.BaseNpcFunction;

public class DungeonTransmit extends BaseNpcFunction {

    private static final String[] mainMenuList;
    private static final short[] mainMenuMarkImageIDList;
    private int dungeonDataID;

    static {
        mainMenuList = new String[]{"\u8fdb\u5165\uff08\u96be\u5ea6\uff0d\u7b80\u5355\uff09", "\u8fdb\u5165\uff08\u96be\u5ea6\uff0d\u56f0\u96be\uff09"};
        mainMenuMarkImageIDList = new short[]{1011, 1011};
    }

    public DungeonTransmit(final int _npcID, final int _dungeonDataID) {
        super(_npcID);
        this.dungeonDataID = _dungeonDataID;
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.DUNGEON_TRANSMIT;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < DungeonTransmit.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = DungeonTransmit.mainMenuList[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        if (_player.getGroupID() <= 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5728\u961f\u4f0d\u4e2d\u624d\u53ef\u4ee5\u8fdb\u5165"));
            return;
        }
        DungeonDataModel dungeonData = DungeonDataModelDictionary.getInsatnce().get(this.dungeonDataID);
        if (dungeonData != null) {
            if (selectIndex == 0) {
                if (_player.getLevel() < dungeonData.level) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u7684\u7b49\u7ea7\u4e0d\u591f"));
                    return;
                }
            } else {
                int heroDungeon = dungeonData.level + DungeonServiceImpl.getInstance().getConfig().difficult_addition_level;
                if (_player.getLevel() < heroDungeon) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u7684\u7b49\u7ea7\u4e0d\u591f"));
                    return;
                }
            }
            Group group = GroupServiceImpl.getInstance().getGroup(_player.getGroupID());
            if (group != null) {
                Dungeon dungeon;
                if (selectIndex == 0) {
                    dungeon = DungeonServiceImpl.getInstance().tryEnterDungeon(_player, dungeonData, (byte) 1, group.getID(), group.getLeader().player.getUserID());
                } else {
                    dungeon = DungeonServiceImpl.getInstance().tryEnterDungeon(_player, dungeonData, (byte) 2, group.getID(), group.getLeader().player.getUserID());
                }
                if (dungeon != null) {
                    if (dungeon.isInFightingBoss()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u9996\u9886\u6218\u4e2d\u65e0\u6cd5\u8fdb\u5165"));
                        return;
                    }
                    Map entranceMap = dungeon.getEntranceMap();
                    _player.setCellX(entranceMap.getBornX());
                    _player.setCellY(entranceMap.getBornY());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, entranceMap, _player.where()));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, entranceMap));
                    _player.gotoMap(entranceMap);
                    EffectServiceImpl.getInstance().sendEffectList(_player, entranceMap);
                    DungeonServiceImpl.getInstance().playerEnterDungeon(_player.getUserID(), dungeon);
                    Npc escortNpc = _player.getEscortTarget();
                    if (escortNpc != null) {
                        TaskServiceImpl.getInstance().endEscortNpcTask(_player, escortNpc);
                    }
                }
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u961f\u4f0d\u4fe1\u606f\u9519\u8bef"));
            }
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u526f\u672c\u6570\u636e\u9519\u8bef"));
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }
}
