// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import java.util.Iterator;
import hero.gather.dict.Refined;
import java.io.IOException;
import hero.ui.message.NotifyListItemMessage;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.share.message.FullScreenTip;
import hero.gather.dict.RefinedDict;
import hero.log.service.CauseLog;
import hero.item.dictionary.GoodsContents;
import hero.item.service.GoodsServiceImpl;
import hero.item.bag.exception.BagException;
import hero.ui.message.CloseUIMessage;
import hero.gather.message.GourdNotify;
import hero.gather.message.GatherSkillNotify;
import hero.share.service.Tip;
import hero.player.service.PlayerServiceImpl;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_AssistSkillList;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.ui.UI_Confirm;
import hero.gather.Gather;
import hero.gather.service.GatherServerImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class GatherNpc extends BaseNpcFunction {

    private static final String[] HAS_GATHER_MAIN_MENU_LIST;
    private static final short[] HAS_GATHER_MAIN_MENU_MARK_IMAGE_ID_LIST;
    private static ArrayList<byte[]>[] hasGatherMainMenuListOptionData;
    private static final String[] NO_GATHER_MAIN_MENU_LIST;
    private static final byte[] NO_GATHER_MAIN_MENU_MARK_IMAGE_ID_LIST;
    private static final String[] MENU_LIST;

    static {
        HAS_GATHER_MAIN_MENU_LIST = new String[]{"\u5b66\u4e60\u6280\u80fd", "\u5347\u7ea7", "\u9057\u5fd8"};
        HAS_GATHER_MAIN_MENU_MARK_IMAGE_ID_LIST = new short[]{1006, 1006, 1007};
        GatherNpc.hasGatherMainMenuListOptionData = (ArrayList<byte[]>[]) new ArrayList[GatherNpc.HAS_GATHER_MAIN_MENU_LIST.length];
        NO_GATHER_MAIN_MENU_LIST = new String[]{"\u8bad\u7ec3\u91c7\u96c6"};
        NO_GATHER_MAIN_MENU_MARK_IMAGE_ID_LIST = new byte[]{1};
        MENU_LIST = new String[]{"\u67e5\u3000\u3000\u770b", "\u5b66\u3000\u3000\u4e60"};
    }

    public GatherNpc(final int _hostNpcID) {
        super(_hostNpcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.GATHER_NPC;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        Gather _gather = GatherServerImpl.getInstance().getGatherByUserID(_player.getUserID());
        if (_gather != null) {
            return this.optionList;
        }
        ArrayList<NpcHandshakeOptionData> handshakeOptionList = new ArrayList<NpcHandshakeOptionData>();
        NpcHandshakeOptionData optionData = null;
        for (int i = 0; i < GatherNpc.NO_GATHER_MAIN_MENU_LIST.length; ++i) {
            optionData = new NpcHandshakeOptionData();
            optionData.miniImageID = this.getMinMarkIconID();
            optionData.optionDesc = GatherNpc.NO_GATHER_MAIN_MENU_LIST[i];
            optionData.functionMark = this.getFunctionType().value() * 100000 + i + GatherNpc.HAS_GATHER_MAIN_MENU_LIST.length;
            handshakeOptionList.add(optionData);
        }
        return handshakeOptionList;
    }

    @Override
    public void initTopLayerOptionList() {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_Confirm.getBytes("\u60a8\u786e\u5b9a\u8981\u9057\u5fd8\u5417\uff1f"));
        GatherNpc.hasGatherMainMenuListOptionData[2] = data1;
        for (int i = 0; i < GatherNpc.HAS_GATHER_MAIN_MENU_LIST.length; ++i) {
            NpcHandshakeOptionData data2 = new NpcHandshakeOptionData();
            data2.miniImageID = this.getMinMarkIconID();
            data2.optionDesc = GatherNpc.HAS_GATHER_MAIN_MENU_LIST[i];
            data2.functionMark = this.getFunctionType().value() * 100000 + i;
            data2.followOptionData = GatherNpc.hasGatherMainMenuListOptionData[i];
            this.optionList.add(data2);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _selectIndex, final YOYOInputStream _content) throws Exception {
        if (_step == Step.TOP.tag) {
            switch (_selectIndex) {
                case 0: {
                    Gather _gather = GatherServerImpl.getInstance().getGatherByUserID(_player.getUserID());
                    if (_gather == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u8fd8\u6ca1\u6709\u5b66\u4e60\u8fc7\u91c7\u96c6\u6280\u80fd"));
                        return;
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SKILL_LIST.tag, UI_AssistSkillList.getRefinedBytes(GatherNpc.MENU_LIST, this.getCanStudys(_gather))));
                    break;
                }
                case 1: {
                    Gather _gather = GatherServerImpl.getInstance().getGatherByUserID(_player.getUserID());
                    if (_gather == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u8fd8\u6ca1\u6709\u5b66\u4e60\u8fc7\u91c7\u96c6\u6280\u80fd"));
                        return;
                    }
                    if (_gather.getLvl() >= 5) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u7ecf\u662f\u9876\u7ea7\u4e86"));
                        return;
                    }
                    if (_gather.getPoint() < GatherServerImpl.POINT_LIMIT[_gather.getLvl() - 1]) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u6280\u80fd\u70b9\u4e0d\u591f\uff0c\u9700\u8981" + GatherServerImpl.POINT_LIMIT[_gather.getLvl() - 1]));
                        return;
                    }
                    if (_player.getMoney() < GatherServerImpl.MONEY_OF_UPGRADE[_gather.getLvl() - 1]) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f\uff0c\u9700\u8981" + GatherServerImpl.MONEY_OF_UPGRADE[_gather.getLvl() - 1]));
                        return;
                    }
                    PlayerServiceImpl.getInstance().addMoney(_player, -GatherServerImpl.MONEY_OF_UPGRADE[_gather.getLvl() - 1], 1.0f, 0, "\u5347\u7ea7\u91c7\u96c6\u6280\u80fd\u82b1\u8d39");
                    GatherServerImpl.getInstance().lvlUp(_player.getUserID(), _gather);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5347\u7ea7\u5230" + Tip.GATHER_LEVEL_TITLE[_gather.getLvl() - 1] + "\u91c7\u96c6\u6280\u80fd"));
                    this.autoStudy(_player, _gather.getLvl());
                    break;
                }
                case 2: {
                    GatherServerImpl.getInstance().forgetGatherByUserID(_player.getUserID());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GatherSkillNotify(false));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GourdNotify(false));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u529f\u9057\u5fd8\u91c7\u96c6\u6280\u80fd"));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                    int gourdID = GatherServerImpl.getInstance().getGourdID(_player);
                    if (gourdID > 0) {
                        try {
                            _player.getInventory().getSpecialGoodsBag().remove(gourdID);
                        } catch (BagException e) {
                            e.printStackTrace();
                        }
                    }
                    PlayerServiceImpl.getInstance().deleteShortcutKey(_player, (byte) 2, -50);
                    break;
                }
                case 3: {
                    if (_player.getMoney() < 400) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f\uff0c\u9700\u8981400"));
                        return;
                    }
                    if (GatherServerImpl.getInstance().getGatherByUserID(_player.getUserID()) == null && GatherServerImpl.getInstance().studyGather(_player.getUserID())) {
                        PlayerServiceImpl.getInstance().addMoney(_player, -400, 1.0f, 0, "\u8bad\u7ec3\u91c7\u96c6\u6280\u80fd\u82b1\u8d39");
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GatherSkillNotify(true));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new GourdNotify(true));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5b66\u4f1a\u4e86\u521d\u7ea7\u91c7\u96c6\u6280\u80fd"));
                        this.autoStudy(_player, (byte) 1);
                        GoodsServiceImpl.getInstance().addGoods2Package(_player, GoodsContents.getGoods(50001), 1, CauseLog.GATHER);
                        break;
                    }
                    break;
                }
            }
        } else if (_step == Step.SKILL_LIST.tag) {
            try {
                byte _index = _content.readByte();
                int _refinedID = _content.readInt();
                Gather _gather2 = GatherServerImpl.getInstance().getGatherByUserID(_player.getUserID());
                Refined refined = RefinedDict.getInstance().getRefinedByID(_refinedID);
                if (_gather2 == null) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u8fd8\u6ca1\u6709\u5b66\u4e60\u8fc7\u91c7\u96c6\u6280\u80fd"));
                    return;
                }
                if (_index == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new FullScreenTip(refined.name, refined.desc));
                } else {
                    if (_gather2.isStudyedRefinedID(_refinedID)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u7ecf\u4e60\u5f97\u4e86\u6b64\u6280\u80fd"));
                        return;
                    }
                    if (_player.getMoney() < refined.money) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f\uff0c\u9700\u8981" + refined.money));
                        return;
                    }
                    PlayerServiceImpl.getInstance().addMoney(_player, -refined.money, 1.0f, 0, "\u5b66\u4e60\u91c7\u96c6\u6280\u80fd\u82b1\u8d39");
                    GatherServerImpl.getInstance().addRefinedItem(_player, refined, GetTypeOfSkillItem.LEARN);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyListItemMessage(_step, false, _refinedID));
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void autoStudy(final HeroPlayer _player, final byte _lvl) {
        Iterator<Refined> iter = RefinedDict.getInstance().getRefineds();
        while (iter.hasNext()) {
            Refined _refined = iter.next();
            if (_refined.needLvl == _lvl && _refined.npcStudy && _refined.money == 0) {
                GatherServerImpl.getInstance().addRefinedItem(_player, _refined, GetTypeOfSkillItem.LEARN);
            }
        }
    }

    private ArrayList<Refined> getCanStudys(final Gather _gather) {
        ArrayList<Refined> list = new ArrayList<Refined>();
        Iterator<Refined> iter = RefinedDict.getInstance().getRefineds();
        while (iter.hasNext()) {
            Refined _refined = iter.next();
            if (!_gather.isStudyedRefinedID(_refined.id) && _gather.getLvl() >= _refined.needLvl) {
                list.add(_refined);
            }
        }
        return list;
    }

    enum Step {
        TOP("TOP", 0, 1),
        SKILL_LIST("SKILL_LIST", 1, 2);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
