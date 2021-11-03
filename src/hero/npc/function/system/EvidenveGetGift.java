// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.share.message.Warning;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.share.service.ShareServiceImpl;
import hero.share.service.ShareConfig;
import hero.player.service.PlayerServiceImpl;
import hero.share.service.ShareDAO;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_EvidenveReceive;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.npc.dict.EvidenveGiftDict;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import hero.npc.detail.NpcHandshakeOptionData;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class EvidenveGetGift extends BaseNpcFunction {

    protected ArrayList<NpcHandshakeOptionData> optionList;

    public EvidenveGetGift(final int npcID) {
        super(npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.EVIDENVE_GET_GIFT;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    @Override
    public void initTopLayerOptionList() {
        String[] optionName = EvidenveGiftDict.getInstance().getEvidenveGift();
        this.optionList = new ArrayList<NpcHandshakeOptionData>();
        for (int i = 0; i < optionName.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = optionName[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        EvidenveGiftDict.EvidenveData evidenve = EvidenveGiftDict.getInstance().getEvidenveData(selectIndex);
        if (_step == Step.TOP.tag) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, Step.INPUT.tag, UI_EvidenveReceive.getBytes(evidenve.inputBoxLenghts, evidenve.inputBoxContents, evidenve.name)));
        } else if (_step == Step.INPUT.tag) {
            byte inputCount = _content.readByte();
            if (inputCount == evidenve.inputBoxSum) {
                String[] inputContent = new String[inputCount];
                for (int i = 0; i < inputCount; ++i) {
                    inputContent[i] = _content.readUTF();
                }
                boolean isRightInput = ShareDAO.InputVerify(evidenve.tableName, evidenve.columnNames, inputContent);
                boolean isJoinIt = ShareDAO.isJoinIt(evidenve.tableName, _player.getLoginInfo().accountID);
                boolean isByUse = ShareDAO.isByUse(evidenve.tableName, evidenve.columnNames, inputContent);
                if (!isByUse) {
                    if (!isJoinIt) {
                        if (isRightInput) {
                            boolean update = ShareDAO.updateEvidenveRece(evidenve.tableName, evidenve.columnNames, inputContent, _player.getLoginInfo().accountID, _player.getUserID());
                            if (update) {
                                EvidenveGiftDict.EvidenveAward eAward = evidenve.award;
                                String awards = "";
                                if (eAward.money > 0) {
                                    PlayerServiceImpl.getInstance().addMoney(_player, eAward.money, 1.0f, 2, "\u8f93\u5165\u51ed\u8bc1\u9886\u53d6\u5956\u52b1");
                                    awards = String.valueOf(awards) + ShareServiceImpl.getInstance().getConfig().getSignLineBreak() + eAward.money + ShareServiceImpl.getInstance().getConfig().getMonetaryUnit() + ShareServiceImpl.getInstance().getConfig().getSignLineBreak();
                                }
                                if (eAward.exp > 0) {
                                    PlayerServiceImpl.getInstance().addExperience(_player, eAward.exp, 1.0f, 2);
                                    awards = String.valueOf(awards) + eAward.exp + "\u7ecf\u9a8c\u503c" + ShareServiceImpl.getInstance().getConfig().getSignLineBreak();
                                }
                                for (int j = 0; j < eAward.goodsList.length; ++j) {
                                    if (eAward.goodsList[j] != null) {
                                        GoodsServiceImpl.getInstance().addGoods2Package(_player, eAward.goodsList[j].getID(), 1, CauseLog.EVIDENVEGET);
                                        awards = String.valueOf(awards) + eAward.goodsList[j].getName() + ShareServiceImpl.getInstance().getConfig().getSignLineBreak();
                                    }
                                }
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u606d\u559c\u60a8\u5f97\u5230\uff1a%fa\u611f\u8c22\u60a8\u7684\u652f\u6301\uff0c\u795d\u60a8\u6e38\u620f\u6109\u5feb".replaceAll("%fa", awards), (byte) 1));
                            } else {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25,\u8bf7\u91cd\u8bd5"));
                            }
                        } else {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(evidenve.wrongByInput, (byte) 1));
                        }
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(evidenve.wrongByJoinIt, (byte) 1));
                    }
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(evidenve.wrongByUse, (byte) 1));
                }
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u64cd\u4f5c\u5931\u8d25,\u8bf7\u91cd\u8bd5"));
            }
        }
    }

    enum Step {
        TOP("TOP", 0, 1),
        INPUT("INPUT", 1, 2);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
