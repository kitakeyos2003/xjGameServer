// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.map.Map;
import hero.item.service.GoodsServiceImpl;
import hero.map.message.ResponseBoxList;
import hero.map.message.ResponseMapElementList;
import hero.map.message.ResponsePetInfoList;
import hero.map.message.ResponseAnimalInfoList;
import hero.effect.service.EffectServiceImpl;
import hero.task.service.TaskServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseSceneElement;
import hero.map.message.ResponseMapBottomData;
import hero.share.service.LogWriter;
import hero.map.service.MapServiceImpl;
import java.io.IOException;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import java.util.Iterator;
import hero.ui.UI_InputString;
import hero.lover.service.LoverDAO;
import java.util.Calendar;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class WeddingNPC extends BaseNpcFunction {

    private static final String[] mainMenuList;
    private static final String input = "\u8bf7\u8f93\u5165\u65e5\u671f";
    private static ArrayList<byte[]>[] weddingMenuOptionData;
    private static final short[] mainMenuMarkImageIDList;
    private static final int CASH = 2000000;
    private static final short targetMapID = 10;

    static {
        mainMenuList = new String[]{"\u9884\u5b9a\u5a5a\u793c(\u6bcf\u67081~28\u53f7) \u683c\u5f0f\u4e3a(YYYY-MM-DD)"};
        WeddingNPC.weddingMenuOptionData = (ArrayList<byte[]>[]) new ArrayList[WeddingNPC.mainMenuList.length];
        mainMenuMarkImageIDList = new short[]{1008};
    }

    public WeddingNPC(final int npcID) {
        super(npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.WEDDING;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        ArrayList<NpcHandshakeOptionData> temp = new ArrayList<NpcHandshakeOptionData>();
        String str = null;
        if (_player.getMoney() >= 2000000) {
            temp.add(this.optionList.get(0));
        }
        Calendar cab = Calendar.getInstance();
        if ((str = LoverDAO.whoWedding(String.valueOf(cab.get(1)) + "-" + (cab.get(2) + 1) + "-" + cab.get(5))) != null) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = 1004;
            data.optionDesc = "\u53c2\u52a0" + str + "\u5a5a\u793c";
            data.functionMark = this.getFunctionType().value() * 100000 + 1;
            temp.add(data);
        }
        return temp;
    }

    @Override
    public void initTopLayerOptionList() {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_InputString.getBytes("\u8bf7\u8f93\u5165\u65e5\u671f", 10, 10));
        WeddingNPC.weddingMenuOptionData[0] = data1;
        for (int i = 0; i < WeddingNPC.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data2 = new NpcHandshakeOptionData();
            data2.miniImageID = this.getMinMarkIconID();
            data2.optionDesc = WeddingNPC.mainMenuList[i];
            data2.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data2);
            if (WeddingNPC.weddingMenuOptionData[i] != null) {
                data2.followOptionData = new ArrayList<byte[]>(WeddingNPC.weddingMenuOptionData[i].size());
                for (final byte[] b : WeddingNPC.weddingMenuOptionData[i]) {
                    data2.followOptionData.add(b);
                }
            }
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        if (_step == 1) {
            switch (selectIndex) {
                case 0: {
                    try {
                        String date = _content.readUTF();
                        int[] userDate = new int[3];
                        int j = 0;
                        for (int i = 0; i < date.length(); ++i) {
                            char v = date.charAt(i);
                            if (v != '-') {
                                int[] array = userDate;
                                int n = j;
                                array[n] *= 10;
                                int[] array2 = userDate;
                                int n2 = j;
                                array2[n2] += v - '0';
                            } else {
                                ++j;
                            }
                        }
                        Calendar cab = Calendar.getInstance();
                        if (userDate[0] >= cab.get(1) && userDate[0] <= 2050 && userDate[1] <= 12 && userDate[2] <= 28 && (userDate[1] > cab.get(2) || (userDate[1] == cab.get(2) && userDate[2] >= cab.get(5)))) {
                            if (userDate[2] == cab.get(5)) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5a5a\u793c\u9884\u7ea6\u65f6\u95f4\u4e0d\u80fd\u662f\u4eca\u5929"));
                            } else if (LoverDAO.registerWedding(date, _player.getName())) {
                                PlayerServiceImpl.getInstance().addMoney(_player, -2000000, 1.0f, 0, "\u7ed3\u5a5a\u82b1\u8d39");
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5a5a\u793c\u9884\u5b9a\u6210\u529f"));
                            } else {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u65e5\u671f\u5df2\u6709\u4eba\u9884\u5b9a"));
                            }
                        } else {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u65e5\u671f\u4e0d\u5408\u6cd5"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 1: {
                    Calendar cab2 = Calendar.getInstance();
                    if (cab2.get(11) < 19 || cab2.get(12) < 30 || cab2.get(11) > 20 || cab2.get(12) > 30) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u53c2\u52a0\u5a5a\u793c\u7684\u65f6\u95f4\u4e3a\u665a\u4e0a7:30\u52308:30"));
                        break;
                    }
                    Map currentMap = _player.where();
                    Map targetMap = MapServiceImpl.getInstance().getNormalMapByID((short) 10);
                    if (targetMap == null) {
                        LogWriter.println("\u4e0d\u5b58\u5728\u7684\u5730\u56fe\uff0cID:10");
                        return;
                    }
                    currentMap.getID();
                    targetMap.getID();
                    _player.setCellX(targetMap.getBornX());
                    _player.setCellY(targetMap.getBornY());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, targetMap, currentMap));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseSceneElement(_player.getLoginInfo().clientType, targetMap));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, targetMap));
                    TaskServiceImpl.getInstance().notifyMapNpcTaskMark(_player, targetMap);
                    EffectServiceImpl.getInstance().sendEffectList(_player, targetMap);
                    if (targetMap.getAnimalList().size() > 0) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseAnimalInfoList(targetMap));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePetInfoList(_player));
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapElementList(_player.getLoginInfo().clientType, targetMap));
                    if (targetMap.getTaskGearList().size() > 0) {
                        TaskServiceImpl.getInstance().notifyMapGearOperateMark(_player, targetMap);
                    }
                    if (targetMap.getGroundTaskGoodsList().size() > 0) {
                        TaskServiceImpl.getInstance().notifyGroundTaskGoodsOperateMark(_player, targetMap);
                    }
                    if (targetMap.getBoxList().size() > 0) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseBoxList(targetMap.getBoxList()));
                    }
                    GoodsServiceImpl.getInstance().sendLegacyBoxList(targetMap, _player);
                    _player.gotoMap(targetMap);
                    break;
                }
            }
        }
    }
}
