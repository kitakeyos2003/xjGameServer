// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import java.util.Iterator;
import hero.manufacture.dict.ManufSkill;
import java.util.List;
import java.io.IOException;
import hero.ui.message.NotifyListItemMessage;
import hero.manufacture.service.GetTypeOfSkillItem;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.Tip;
import hero.share.message.FullScreenTip;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.ManufactureType;
import hero.ui.message.CloseUIMessage;
import hero.manufacture.message.ManufNotify;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_AssistSkillList;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.ui.UI_Confirm;
import hero.manufacture.Manufacture;
import hero.npc.Npc;
import hero.manufacture.service.ManufactureServerImpl;
import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class ManufNpc extends BaseNpcFunction {

    private static final String[] HAS_MANUF_MAIN_MENU_LIST;
    private static final short[] HAS_MANUF_MAIN_MENU_MARK_IMAGE_ID_LIST;
    private static ArrayList<byte[]>[] hasManufMainMenuListOptionData;
    private static final String[] NO_MANUF_MAIN_MENU_LIST;
    private static final short[] NO_MANUF_MAIN_MENU_MARK_IMAGE_ID_LIST;
    private static final String[] MENU_LIST;

    static {
        HAS_MANUF_MAIN_MENU_LIST = new String[]{"\u5b66\u4e60\u6280\u80fd", "\u5347\u7ea7", "\u9057\u5fd8"};
        HAS_MANUF_MAIN_MENU_MARK_IMAGE_ID_LIST = new short[]{1006, 1006, 1007};
        ManufNpc.hasManufMainMenuListOptionData = (ArrayList<byte[]>[]) new ArrayList[ManufNpc.HAS_MANUF_MAIN_MENU_LIST.length];
        NO_MANUF_MAIN_MENU_LIST = new String[]{"\u8bad\u7ec3"};
        NO_MANUF_MAIN_MENU_MARK_IMAGE_ID_LIST = new short[]{1006};
        MENU_LIST = new String[]{"\u67e5\u3000\u3000\u770b", "\u5b66\u3000\u3000\u4e60"};
    }

    public ManufNpc(final int _hostNpcID) {
        super(_hostNpcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.MANUF_NPC;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        Npc _npc = NotPlayerServiceImpl.getInstance().getNpc(this.getHostNpcID());
        Manufacture _manuf = ManufactureServerImpl.getInstance().getManufactureByUserIDAndNpcName(_player.getUserID(), _npc.getName());
        if (_manuf != null) {
            return this.optionList;
        }
        ArrayList<NpcHandshakeOptionData> handshakeOptionList = new ArrayList<NpcHandshakeOptionData>();
        NpcHandshakeOptionData optionData = null;
        for (int i = 0; i < ManufNpc.NO_MANUF_MAIN_MENU_LIST.length; ++i) {
            optionData = new NpcHandshakeOptionData();
            optionData.miniImageID = this.getMinMarkIconID();
            optionData.optionDesc = ManufNpc.NO_MANUF_MAIN_MENU_LIST[i];
            optionData.functionMark = this.getFunctionType().value() * 100000 + i + ManufNpc.HAS_MANUF_MAIN_MENU_LIST.length;
            handshakeOptionList.add(optionData);
        }
        return handshakeOptionList;
    }

    @Override
    public void initTopLayerOptionList() {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_Confirm.getBytes("\u60a8\u786e\u8ba4\u8981\u9057\u5fd8\u5417\uff1f"));
        ManufNpc.hasManufMainMenuListOptionData[2] = data1;
        for (int i = 0; i < ManufNpc.HAS_MANUF_MAIN_MENU_LIST.length; ++i) {
            NpcHandshakeOptionData data2 = new NpcHandshakeOptionData();
            data2.miniImageID = this.getMinMarkIconID();
            data2.optionDesc = ManufNpc.HAS_MANUF_MAIN_MENU_LIST[i];
            data2.functionMark = this.getFunctionType().value() * 100000 + i;
            data2.followOptionData = ManufNpc.hasManufMainMenuListOptionData[i];
            this.optionList.add(data2);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _selectIndex, final YOYOInputStream _content) throws Exception {
        Npc _npc = NotPlayerServiceImpl.getInstance().getNpc(this.getHostNpcID());
        Manufacture manuf = ManufactureServerImpl.getInstance().getManufactureByUserIDAndNpcName(_player.getUserID(), _npc.getName());
        if (_step == Step.TOP.tag) {
            switch (_selectIndex) {
                case 0: {
                    if (manuf == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u8fd8\u6ca1\u6709\u5b66\u4e60\u8fc7\u5236\u9020\u6280\u80fd"));
                        return;
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SKILL_LIST.tag, UI_AssistSkillList.getManufSkillBytes(ManufNpc.MENU_LIST, this.getCanStudys(manuf, _player), manuf)));
                    break;
                }
                case 1: {
                    if (manuf == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u8fd8\u6ca1\u6709\u5b66\u4e60\u8fc7\u5236\u9020\u6280\u80fd"));
                        return;
                    }
                    break;
                }
                case 2: {
                    List<Manufacture> oldManufList = ManufactureServerImpl.getInstance().forgetManufactureByUserID(_player.getUserID());
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ManufNotify(oldManufList));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u529f\u9057\u5fd8\u4e86\u5236\u9020\u6280\u80fd"));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                    break;
                }
                default: {
                    if (manuf != null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u65e0\u6cd5\u8bad\u7ec3\u4e24\u79cd\u5236\u9020\u6280\u80fd"));
                        return;
                    }
                    ManufactureType _type = ManufactureType.get(_npc.getName());
                    if (ManufactureServerImpl.getInstance().studyManufacture(_player, _type)) {
                        List<Manufacture> manufactureList = ManufactureServerImpl.getInstance().getManufactureListByUserID(_player.getUserID());
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ManufNotify(manufactureList));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                        break;
                    }
                    break;
                }
            }
        } else if (_step == Step.SKILL_LIST.tag) {
            try {
                byte _index = _content.readByte();
                int _manufSkillID = _content.readInt();
                ManufSkill _manufSkill = ManufSkillDict.getInstance().getManufSkillByID(_manufSkillID);
                if (manuf == null) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u8fd8\u6ca1\u6709\u5b66\u4e60\u8fc7\u5236\u9020\u6280\u80fd"));
                    return;
                }
                if (_index == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new FullScreenTip(_manufSkill.name, _manufSkill.desc));
                } else {
                    if (manuf.isStudyedManufSkillID(_manufSkillID)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u7ecf\u5b66\u4f1a\u4e86\u6b64\u6280\u80fd"));
                        return;
                    }
                    if (manuf.getPoint() < _manufSkill.needSkillPoint) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(Tip.TIP_NPC_SKILL_POINT_NOT_ENOUGH[0]) + _manufSkill.needSkillPoint + Tip.TIP_NPC_SKILL_POINT_NOT_ENOUGH[1]));
                        return;
                    }
                    if (_player.getMoney() < _manufSkill.money) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f\uff0c\u9700\u8981" + _manufSkill.money));
                        return;
                    }
                    PlayerServiceImpl.getInstance().addMoney(_player, -_manufSkill.money, 1.0f, 0, "\u5b66\u4e60\u5236\u9020\u6280\u80fd\u82b1\u8d39");
                    ManufactureServerImpl.getInstance().addManufSkillItem(_player, _manufSkill, GetTypeOfSkillItem.LEARN);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NotifyListItemMessage(_step, false, _manufSkillID));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void autoStudy(final HeroPlayer _player, final Manufacture manuf) {
        Iterator<ManufSkill> iter = ManufSkillDict.getInstance().getManufSkills();
        while (iter.hasNext()) {
            ManufSkill _manufSkill = iter.next();
            if (_manufSkill.type == manuf.getManufactureType().getID() && _manufSkill.needLevel <= _player.getLevel() && _manufSkill.needSkillPoint <= manuf.getPoint() && _manufSkill.npcStudy && _manufSkill.money <= _player.getMoney()) {
                ManufactureServerImpl.getInstance().addManufSkillItem(_player, _manufSkill, GetTypeOfSkillItem.LEARN);
            }
        }
    }

    private ArrayList<ManufSkill> getCanStudys(final Manufacture _manuf, final HeroPlayer _player) {
        ArrayList<ManufSkill> list = new ArrayList<ManufSkill>();
        Iterator<ManufSkill> iter = ManufSkillDict.getInstance().getManufSkills();
        while (iter.hasNext()) {
            ManufSkill _manufSkill = iter.next();
            if (_manuf.getManufactureType().getID() == _manufSkill.type && !_manuf.isStudyedManufSkillID(_manufSkill.id) && _manuf.getPoint() >= _manufSkill.needSkillPoint && _manufSkill.npcStudy && _manufSkill.needLevel <= _player.getLevel() && _manufSkill.money <= _player.getMoney()) {
                list.add(_manufSkill);
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
