// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.npc.Npc;
import hero.map.Map;
import java.util.ArrayList;
import hero.share.message.Warning;
import hero.task.service.TaskServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import hero.player.service.PlayerServiceImpl;
import hero.map.service.MapServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_SelectOperationWithTip;
import yoyo.core.queue.ResponseMessageQueue;
import hero.npc.function.system.transmit.TransmitTargetMapInfo;
import hero.npc.function.system.transmit.MapTransmitInfoDict;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.BaseNpcFunction;

public class Transmit extends BaseNpcFunction {

    private static final String[] mainMenuList;
    private static final short[] mainMenuMarkImageIDList;
    private String npcModelID;

    static {
        mainMenuList = new String[]{"\u4ed9\u955c\u4e4b\u95e8"};
        mainMenuMarkImageIDList = new short[]{1010};
    }

    public Transmit(final int _npcID, final String _npcModelID) {
        super(_npcID);
        this.npcModelID = _npcModelID;
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.TRANSMIT;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < Transmit.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = Transmit.mainMenuList[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _selectIndex, final YOYOInputStream _content) throws Exception {
        ArrayList<TransmitTargetMapInfo> list = MapTransmitInfoDict.getInstance().getTargetMapInfoList(this.npcModelID);
        if (_step == Step.TOP.tag) {
            if (list != null) {
                String[][] menuList = new String[list.size()][2];
                for (int i = 0; i < list.size(); ++i) {
                    menuList[i][0] = list.get(i).getMapName();
                    menuList[i][1] = list.get(i).getDescription();
                }
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.TRANSMIT.tag, UI_SelectOperationWithTip.getBytes("\u563f\uff0c\u60f3\u53bb\u54ea\u91cc\u8bf7\u9009\u62e9\u5427", menuList)));
            }
        } else if (_step == Step.TRANSMIT.tag) {
            if (!_player.isDead()) {
                byte index = _content.readByte();
                if (list != null && index < list.size()) {
                    TransmitTargetMapInfo targetMapInfo = list.get(index);
                    if (_player.getLevel() >= targetMapInfo.getNeedLevel()) {
                        if (_player.getMoney() >= targetMapInfo.getFreight()) {
                            Map currentMap = _player.where();
                            Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(targetMapInfo.getMapID());
                            if (targetMap == null || currentMap.getID() == targetMap.getID()) {
                                return;
                            }
                            PlayerServiceImpl.getInstance().addMoney(_player, -targetMapInfo.getFreight(), 1.0f, 0, "\u4f20\u9001\u82b1\u8d39");
                            _player.setCellX(targetMapInfo.getMapX());
                            _player.setCellY(targetMapInfo.getMapY());
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, targetMap, currentMap));
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, targetMap));
                            _player.gotoMap(targetMap);
                            EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                            Npc escortNpc = _player.getEscortTarget();
                            if (escortNpc != null) {
                                TaskServiceImpl.getInstance().endEscortNpcTask(_player, escortNpc);
                            }
                        } else {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u91d1\u94b1\u4e0d\u591f"));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u9700\u8981\u7b49\u7ea7 " + targetMapInfo.getNeedLevel()));
                    }
                }
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u4e86\u9b3c\u9b42\u5c31\u4e0d\u8981\u4f20\u6765\u4f20\u53bb\u5413\u4eba\u4e86"));
            }
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    private enum Step {
        TOP("TOP", 0, 1),
        TRANSMIT("TRANSMIT", 1, 2);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
