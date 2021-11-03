// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_WeaponRecord;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.npc.detail.NpcHandshakeOptionData;
import java.util.ArrayList;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import hero.npc.function.BaseNpcFunction;

public class WeaponRecord extends BaseNpcFunction {

    private static final String[] mainMenuList;
    private static final short[] mainMenuMarkImageIDList;

    static {
        mainMenuList = new String[]{"\u5175\u5668\u8c31\u6392\u884c"};
        mainMenuMarkImageIDList = new short[]{1012};
    }

    public WeaponRecord(final int npcID) {
        super(npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.WEAPON_RECORD;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < WeaponRecord.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = WeaponRecord.mainMenuList[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _topSelectIndex, final YOYOInputStream _content) throws Exception {
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(0).functionMark, (byte) 1, UI_WeaponRecord.getBytes()));
    }
}
