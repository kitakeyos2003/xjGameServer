// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.group.service.GroupServiceImpl;
import hero.task.service.TaskServiceImpl;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.player.message.VocationChangeNotify;
import hero.skill.service.SkillServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.ui.message.CloseUIMessage;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.ui.UI_Confirm;
import hero.npc.detail.NpcHandshakeOptionData;
import java.util.ArrayList;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import hero.item.dictionary.ChangeVocationToolsDict;
import hero.player.define.EClan;
import hero.item.TaskTool;
import hero.share.EVocation;
import hero.npc.function.BaseNpcFunction;

public class ChangeVocation extends BaseNpcFunction {

    private String[] mainMenuList;
    private String noneToolTip;
    private String confirmTip;
    private EVocation vocationThatChange;
    private EVocation[] changes;
    private TaskTool tool;

    public ChangeVocation(final int _npcID, final EVocation _vocation, final EClan _clan) {
        super(_npcID);
        this.vocationThatChange = _vocation;
        this.changes = this.vocationThatChange.getSubVoction(_clan);
        this.tool = ChangeVocationToolsDict.getInstance().getToolByVocation(this.vocationThatChange);
        this.noneToolTip = "\u7f3a\u5c11\u8f6c\u804c\u9053\u5177\u2018" + this.tool.getName() + "\u2019";
        this.confirmTip = "\u786e\u5b9a\u8f6c\u804c\u5417";
        this.mainMenuList = new String[this.changes.length];
        for (int i = 0; i < this.mainMenuList.length; ++i) {
            this.mainMenuList[i] = "\u8f6c\u804c\u4e3a" + this.changes[i].getDesc();
        }
        this.initTopLayerOptionListSecond();
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.CHANGE_VOCATION;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        if (_player.getVocation() == this.vocationThatChange) {
            return this.optionList;
        }
        return null;
    }

    @Override
    public void initTopLayerOptionList() {
    }

    private void initTopLayerOptionListSecond() {
        for (int i = 0; i < this.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = this.mainMenuList[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
        (this.optionList.get(0).followOptionData = new ArrayList<byte[]>(1)).add(UI_Confirm.getBytes(this.confirmTip));
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        if (_player.getInventory().getTaskToolBag().getGoodsNumber(this.tool.getID()) < 1) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(this.noneToolTip));
        } else if (GoodsServiceImpl.getInstance().deleteOne(_player, _player.getInventory().getTaskToolBag(), this.tool.getID(), CauseLog.CHANGEVOCATION)) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
            _player.setVocation(this.changes[selectIndex]);
            PlayerServiceImpl.getInstance().dbUpdate(_player);
            PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
            PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
            SkillServiceImpl.getInstance().changeVocationProcess(_player);
            VocationChangeNotify msg = new VocationChangeNotify(_player.getID(), this.changes[selectIndex].value(), _player.getActualProperty().getHpMax(), _player.getActualProperty().getMpMax());
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, true, _player.getID());
            TaskServiceImpl.getInstance().notifyMapNpcTaskMark(_player, _player.where());
            GroupServiceImpl.getInstance().refreshMemberVocation(_player);
        }
    }
}
