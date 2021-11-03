// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.player.define.ESex;
import hero.share.service.DateFormatter;
import hero.share.message.MailStatusChanges;
import hero.share.letter.Letter;
import hero.share.letter.LetterService;
import hero.lover.service.LoverServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import java.util.Iterator;
import hero.ui.UI_Confirm;
import hero.ui.UI_InputString;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import hero.share.service.Tip;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class LoverTree extends BaseNpcFunction {

    private static final String SPName = "#FNAME";
    private static final int[] taskID;
    private static final short[] mainMenuMarkImageIDList;
    private static ArrayList<byte[]>[] loverMenuOptionData;

    static {
        taskID = new int[]{0, 60188, 60194};
        mainMenuMarkImageIDList = new short[]{1008};
        LoverTree.loverMenuOptionData = (ArrayList<byte[]>[]) new ArrayList[Tip.FUNCTION_LOVE_MAIN_MENU_LIST.length];
    }

    public LoverTree(final int npcID) {
        super(npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.LOVER_TREE;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        ArrayList<NpcHandshakeOptionData> temp = new ArrayList<NpcHandshakeOptionData>();
        short index = _player.getClan().getID();
        temp.add(this.optionList.get(0));
        return temp;
    }

    @Override
    public void initTopLayerOptionList() {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_InputString.getBytes("\u8bf7\u8f93\u5165\u5bf9\u65b9\u53ef\u7231\u7684\u6635\u79f0"));
        LoverTree.loverMenuOptionData[0] = data1;
        for (int i = 0; i < Tip.FUNCTION_LOVE_MAIN_MENU_LIST.length; ++i) {
            NpcHandshakeOptionData data2 = new NpcHandshakeOptionData();
            data2.miniImageID = this.getMinMarkIconID();
            data2.optionDesc = Tip.FUNCTION_LOVE_MAIN_MENU_LIST[i];
            data2.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data2);
            if (LoverTree.loverMenuOptionData[i] != null) {
                data2.followOptionData = new ArrayList<byte[]>(LoverTree.loverMenuOptionData[i].size());
                for (final byte[] b : LoverTree.loverMenuOptionData[i]) {
                    data2.followOptionData.add(LoverTree.loverMenuOptionData[i].get(0));
                    data2.followOptionData.add(UI_Confirm.getBytes("\u786e\u8ba4\u4e0e#FNAME\u7b7e\u540d\u5417\uff1f\u7b7e\u540d\u540e7\u5929\u5185\u65e0\u6cd5\u4fee\u6539"));
                }
            }
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        if (_step == 1) {
            switch (selectIndex) {
                case 0: {
                    String otherName = _content.readUTF();
                    if (otherName.equals(_player.getName())) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u540d\u79f0\u4e0d\u80fd\u4e3a\u81ea\u5df1"));
                        return;
                    }
                    HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(otherName);
                    if (other == null || other.isEnable()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u627e\u4e0d\u5230\u5728\u7ebf\u73a9\u5bb6\u2018" + otherName + "\u2019"));
                        return;
                    }
                    if (other.getSex() == _player.getSex()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u6027\u522b\u76f8\u540c"));
                        return;
                    }
                    if (other.getClan() != _player.getClan()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u9635\u8425\u4e0d\u540c"));
                        return;
                    }
                    LoverServiceImpl.LoverStatus status = LoverServiceImpl.getInstance().registerLoverTree(_player.getName(), otherName);
                    if (status == LoverServiceImpl.LoverStatus.SUCCESS) {
                        Letter letterOfRegistor = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u7eaf\u771f\u7684\u7231\u60c5", "\u5927\u6995\u6811", _player.getUserID(), _player.getName(), "\u8fd9\u662f\u4e00\u4efd\u6700\u7b80\u5355\uff0c\u6700\u76f4\u63a5\u7684\u7231\u60c5\uff0c\u4f60\u4eec\u51e0\u4e4e\u540c\u65f6\u5411\u6211\u8868\u8fbe\u4e86\u4e0e\u5bf9\u65b9\u7684\u611f\u60c5\uff0c\u8fd9\u611f\u52a8\u4e86\u6211\uff0c\u4f5c\u4e3a\u795e\u57df\u4e16\u754c\u4e2d\u7231\u60c5\u7684\u5b88\u62a4\u8005\uff0c\u6211\u5ba3\u5e03\u4f60\u548c\u2018" + otherName + "\u2019\u5c31\u5728\u8fd9\u4e00\u523b\u8ba2\u5a5a");
                        LetterService.getInstance().addNewLetter(letterOfRegistor);
                        Letter letterOfOther = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u7eaf\u771f\u7684\u7231\u60c5", "\u5927\u6995\u6811", other.getUserID(), otherName, "\u8fd9\u662f\u4e00\u4efd\u6700\u7b80\u5355\uff0c\u6700\u76f4\u63a5\u7684\u7231\u60c5\uff0c\u4f60\u4eec\u51e0\u4e4e\u540c\u65f6\u5411\u6211\u8868\u8fbe\u4e86\u4e0e\u5bf9\u65b9\u7684\u611f\u60c5\uff0c\u8fd9\u611f\u52a8\u4e86\u6211\uff0c\u4f5c\u4e3a\u795e\u57df\u4e16\u754c\u4e2d\u7231\u60c5\u7684\u5b88\u62a4\u8005\uff0c\u6211\u5ba3\u5e03\u4f60\u548c\u2018" + _player.getName() + "\u2019\u5c31\u5728\u8fd9\u4e00\u523b\u8ba2\u5a5a");
                        LetterService.getInstance().addNewLetter(letterOfOther);
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u4e0e\u2018" + otherName + "\u2019\u8ba2\u5a5a"));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u4e0e\u2018" + _player.getName() + "\u2019\u8ba2\u5a5a"));
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                        break;
                    }
                    if (status == LoverServiceImpl.LoverStatus.ME_SUCCESSED) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u8ba2\u5a5a"));
                        break;
                    }
                    if (status == LoverServiceImpl.LoverStatus.THEM_SUCCESSED) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(otherName) + "\u5df2\u8ba2\u5a5a"));
                        break;
                    }
                    if (status == LoverServiceImpl.LoverStatus.REGISTERED) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u767b\u8bb0\u8fc7\u4e86"));
                        break;
                    }
                    if (status == LoverServiceImpl.LoverStatus.REGISTER) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u767b\u8bb0\u6210\u529f"));
                        Letter letter = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u5fc3\u7075\u7684\u4f20\u9012", "\u5927\u6995\u6811", other.getUserID(), otherName, "\u5c31\u5728" + DateFormatter.currentTime() + "\uff0c" + _player.getName() + "\u5728\u6211\u7684\u8eab\u65c1\u9ed8\u5ff5\u7740\u4f60\u7684\u540d\u5b57\uff0c" + ((_player.getSex() == ESex.Male) ? "\u4ed6" : "\u5979") + "\u5411\u6211\u503e\u8ff0\u7740\u5bf9\u4f60\u7684\u7231\u6155\uff0c\u5982\u679c\u4f60\u5bf9\u4ed6\u4e5f\u6709\u540c\u6837 \u7684\u611f\u89c9\u8bf7\u6765\u7eff\u91ce\u5e7f\u573a\u5728\u6211\u7684\u8eab\u65c1\u547c\u5524\u4ed6" + ((_player.getSex() == ESex.Male) ? "\u4ed6" : "\u5979") + "\u7684\u540d\u5b57");
                        LetterService.getInstance().addNewLetter(letter);
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                        break;
                    }
                    break;
                }
            }
        }
    }
}
