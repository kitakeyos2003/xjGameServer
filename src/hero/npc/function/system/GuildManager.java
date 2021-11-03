// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_GuildMemberList;
import yoyo.core.packet.AbsResponseMessage;
import hero.ui.message.CloseUIMessage;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import java.util.Iterator;
import hero.ui.UI_Confirm;
import hero.ui.UI_InputString;
import hero.guild.Guild;
import hero.guild.service.GuildServiceImpl;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import hero.share.service.Tip;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class GuildManager extends BaseNpcFunction {

    private static final short[] mainMenuMarkImageIDList;
    private static final String[] menuList;
    private static ArrayList<byte[]>[] guildMenuOptionData;
    private static final byte OPERATION_OF_CREATE_GUILD = 0;
    private static final byte OPERATION_OF_DISBAND_GUILD = 1;
    private static final byte OPERATION_OF_TRANSFER_PRESIDENT = 2;

    static {
        mainMenuMarkImageIDList = new short[]{1008, 1008, 1008};
        menuList = new String[]{"\u4e0b\u3000\u3000\u9875", "\u4e0a\u3000\u3000\u9875", "\u786e\u3000\u3000\u5b9a"};
        GuildManager.guildMenuOptionData = (ArrayList<byte[]>[]) new ArrayList[Tip.FUNCTION_MAIN_MENU_LIST.length];
    }

    public GuildManager(final int _hostNpcID) {
        super(_hostNpcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.GUILD_MANAGE;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        ArrayList<NpcHandshakeOptionData> playerOptionList = new ArrayList<NpcHandshakeOptionData>();
        if (_player.getGuildID() == 0) {
            playerOptionList.add(this.optionList.get(0));
        } else {
            Guild guild = GuildServiceImpl.getInstance().getGuild(_player.getGuildID());
            if (guild != null && guild.getPresident().userID == _player.getUserID()) {
                playerOptionList.add(this.optionList.get(1));
            }
        }
        return playerOptionList;
    }

    @Override
    public void initTopLayerOptionList() {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        data1.add(UI_InputString.getBytes("\u5e2e\u6d3e\u540d\u79f0", 2, 6));
        GuildManager.guildMenuOptionData[0] = data1;
        data1 = new ArrayList<byte[]>();
        data1.add(UI_Confirm.getBytes("\u60a8\u786e\u8ba4\u89e3\u6563\u5e2e\u6d3e\u5417\uff1f"));
        GuildManager.guildMenuOptionData[1] = data1;
        for (int i = 0; i < Tip.FUNCTION_MAIN_MENU_LIST.length; ++i) {
            NpcHandshakeOptionData data2 = new NpcHandshakeOptionData();
            data2.miniImageID = this.getMinMarkIconID();
            data2.optionDesc = Tip.FUNCTION_MAIN_MENU_LIST[i];
            data2.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data2);
            if (GuildManager.guildMenuOptionData[i] != null) {
                data2.followOptionData = new ArrayList<byte[]>(GuildManager.guildMenuOptionData[i].size());
                for (final byte[] b : GuildManager.guildMenuOptionData[i]) {
                    data2.followOptionData.add(b);
                }
            }
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) {
        try {
            if (_step == Step.TOP.tag) {
                switch (selectIndex) {
                    case 0: {
                        String name = _content.readUTF();
                        if (GuildServiceImpl.getInstance().createGuild(_player, name)) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                            break;
                        }
                        break;
                    }
                    case 1: {
                        if (GuildServiceImpl.getInstance().disbandGuild(_player)) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                            break;
                        }
                        break;
                    }
                    case 2: {
                        Guild guild = GuildServiceImpl.getInstance().getGuild(_player.getGuildID());
                        if (guild != null && guild.getPresident().userID == _player.getUserID()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, (byte) 2, UI_GuildMemberList.getBytes(GuildManager.menuList, guild.getMemberList(1), guild.getMemberNumber(), guild.GetMaxMemberNumber(), 1, guild.getViewPageNumber())));
                            break;
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                        break;
                    }
                }
            } else if (_step == Step.VIEW_LIST.tag) {
                byte menuIndex = _content.readByte();
                switch (menuIndex) {
                    case 0:
                    case 1: {
                        byte page = _content.readByte();
                        Guild guild2 = GuildServiceImpl.getInstance().getGuild(_player.getGuildID());
                        if (guild2 != null && guild2.getPresident().userID == _player.getUserID()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, (byte) 2, UI_GuildMemberList.getBytes(GuildManager.menuList, guild2.getMemberList(page), guild2.getMemberNumber(), guild2.GetMaxMemberNumber(), page, guild2.getViewPageNumber())));
                            break;
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                        break;
                    }
                    case 2: {
                        int targetMemberUserID = _content.readInt();
                        Guild guild2 = GuildServiceImpl.getInstance().getGuild(_player.getGuildID());
                        if (guild2 != null) {
                            if (guild2.getPresident().userID != _player.getUserID()) {
                                GuildServiceImpl.getInstance().transferPresident(guild2, _player, targetMemberUserID);
                                return;
                            }
                            if (targetMemberUserID == _player.getUserID()) {
                                return;
                            }
                            GuildServiceImpl.getInstance().transferPresident(guild2, _player, targetMemberUserID);
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new CloseUIMessage());
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    enum Step {
        TOP("TOP", 0, 1),
        VIEW_LIST("VIEW_LIST", 1, 2),
        TRANSFER("TRANSFER", 2, 3);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
