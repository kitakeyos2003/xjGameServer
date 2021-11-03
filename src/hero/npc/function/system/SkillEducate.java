// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.ui.UI_Confirm;
import hero.npc.function.ENpcFunctionType;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_SkillList;
import hero.npc.detail.NpcHandshakeOptionData;
import yoyo.core.queue.ResponseMessageQueue;
import hero.skill.service.SkillServiceImpl;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.skill.dict.SkillDict;
import hero.share.ESystemFeature;
import hero.skill.Skill;
import java.util.ArrayList;
import hero.share.EVocation;
import hero.npc.function.BaseNpcFunction;

public class SkillEducate extends BaseNpcFunction {

    private static final String[] mainMenuList;
    private static final short[] mainMenuMarkImageIDList;
    private static final String[] learnMenuList;
    private static final String forgetSkillTip = "\u4f60\u786e\u8ba4\u8981\u9057\u5fd8\u6240\u6709\u7684\u6280\u80fd\u5417\uff1f";
    private EVocation educateVocation;
    private ArrayList<Skill> skillList;
    private static final String TIP_OF_NONE_SKILL = "\u6ca1\u6709\u6280\u80fd\u53ef\u5b66";

    static {
        mainMenuList = new String[]{"\u5b66\u4e60\u6280\u80fd", "\u9057\u5fd8\u6280\u80fd"};
        mainMenuMarkImageIDList = new short[]{1006, 1007};
        learnMenuList = new String[]{"\u5b66\u3000\u3000\u4e60", "\u67e5\u3000\u3000\u770b"};
    }

    public SkillEducate(final int _hostNpcID, final EVocation _vocation, final ESystemFeature _feature) {
        super(_hostNpcID);
        this.educateVocation = _vocation;
        this.skillList = SkillDict.getInstance().getSkillList(this.educateVocation, _feature);
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        if (Step.TOP.tag == _step) {
            if (selectIndex == 0) {
                ArrayList<Skill> learnableSkillList = SkillServiceImpl.getInstance().getLearnableSkillList(this.skillList, _player);
                if (learnableSkillList != null) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, Step.LEARN_SKILL.tag, UI_SkillList.getBytes(SkillEducate.learnMenuList, learnableSkillList)));
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u6280\u80fd\u53ef\u5b66"));
                }
            }
        } else if (Step.LEARN_SKILL.tag == _step) {
            _content.readByte();
        }
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.SKILL_EDUCATE;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < SkillEducate.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = SkillEducate.mainMenuList[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
        (this.optionList.get(1).followOptionData = new ArrayList<byte[]>(1)).add(UI_Confirm.getBytes("\u4f60\u786e\u8ba4\u8981\u9057\u5fd8\u6240\u6709\u7684\u6280\u80fd\u5417\uff1f"));
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        if (_player.getVocation() == this.educateVocation) {
            return this.optionList;
        }
        return null;
    }

    enum Step {
        TOP("TOP", 0, 1),
        LEARN_SKILL("LEARN_SKILL", 1, 2);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
