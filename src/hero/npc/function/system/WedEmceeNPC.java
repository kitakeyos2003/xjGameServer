// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import java.util.ArrayList;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import java.util.Iterator;
import hero.dungeon.Dungeon;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.InviteAttendWedding;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.social.SocialObjectProxy;
import hero.dungeon.service.DungeonServiceImpl;
import hero.social.ESocialRelationType;
import hero.social.service.SocialServiceImpl;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import org.apache.log4j.Logger;
import hero.npc.function.BaseNpcFunction;

public class WedEmceeNPC extends BaseNpcFunction {

    private static Logger log;
    private static final String[] mainMenuList;
    private static final short[] mainMenuMarkImageIDList;

    static {
        WedEmceeNPC.log = Logger.getLogger((Class) WedEmceeNPC.class);
        mainMenuList = new String[]{"\u9080\u8bf7\u73a9\u5bb6"};
        mainMenuMarkImageIDList = new short[]{1008};
    }

    public WedEmceeNPC(final int npcID) {
        super(npcID);
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _topSelectIndex, final YOYOInputStream _content) throws Exception {
        List<SocialObjectProxy> friends = SocialServiceImpl.getInstance().getSocialRelationList(_player.getUserID(), ESocialRelationType.FRIEND);
        if (friends != null) {
            WedEmceeNPC.log.debug((Object) ("WedEmcee NPC , player[" + _player.getName() + "] friends size = " + friends.size()));
            Dungeon dungeon = DungeonServiceImpl.getInstance().getWhereDungeon(_player.getUserID());
            WedEmceeNPC.log.debug((Object) ("wedemcee player dungeon history id = " + dungeon.getHistoryID()));
            for (final SocialObjectProxy sop : friends) {
                WedEmceeNPC.log.debug((Object) ("friend [" + sop.name + "] isOnline=" + sop.isOnline));
                if (!sop.name.equals(_player.spouse) && sop.isOnline) {
                    HeroPlayer friend = PlayerServiceImpl.getInstance().getPlayerByUserID(sop.userID);
                    ResponseMessageQueue.getInstance().put(friend.getMsgQueueIndex(), new InviteAttendWedding(_player, dungeon.getEntranceMap(), dungeon.getHistoryID()));
                }
            }
        }
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.WEDDING;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < WedEmceeNPC.mainMenuList.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = WedEmceeNPC.mainMenuList[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }
}
