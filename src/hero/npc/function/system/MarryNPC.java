// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.map.message.ResponseBoxList;
import hero.map.message.ResponseMapElementList;
import hero.map.message.ResponsePetInfoList;
import hero.map.message.ResponseAnimalInfoList;
import hero.effect.service.EffectServiceImpl;
import hero.task.service.TaskServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseSceneElement;
import hero.map.message.ResponseMapBottomData;
import hero.share.service.ME2ObjectList;
import hero.share.ME2GameObject;
import hero.map.Map;
import hero.dungeon.service.DungeonServiceImpl;
import hero.map.service.MapServiceImpl;
import hero.player.define.EClan;
import hero.item.bag.exception.BagException;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.player.message.ResponsePlayerMarryStatus;
import hero.lover.message.ResponseMarryRelationShow;
import java.util.Timer;
import hero.chat.service.ChatQueue;
import hero.lover.service.LoverLevel;
import hero.group.Group;
import hero.npc.message.AskPlayerAgreeWedding;
import hero.group.service.GroupServiceImpl;
import hero.lover.service.LoverServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.ui.UI_InputString;
import java.util.Iterator;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import hero.share.service.Tip;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import hero.npc.function.BaseNpcFunction;

public class MarryNPC extends BaseNpcFunction {

    private static Logger log;
    private static ArrayList<byte[]>[] loverMenuOptionData;
    public static final short marryMapId = 406;
    public static final short marryMapId2 = 407;
    public static final short marryNPCMapId = 7;
    public static final short marryNPCMapId2 = 65;
    public static final int[] marryer;
    private static final int CASH = 2000000;
    public static boolean canEntry;

    static {
        MarryNPC.log = Logger.getLogger((Class) MarryNPC.class);
        MarryNPC.loverMenuOptionData = (ArrayList<byte[]>[]) new ArrayList[Tip.FUNCTION_LOVE_MENU_LIST.length];
        marryer = new int[2];
        MarryNPC.canEntry = false;
    }

    public MarryNPC(final int npcID) {
        super(npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.MARRY_NPC;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        ArrayList<NpcHandshakeOptionData> temp = new ArrayList<NpcHandshakeOptionData>();
        for (final NpcHandshakeOptionData data : this.optionList) {
            temp.add(data);
        }
        return temp;
    }

    @Override
    public void initTopLayerOptionList() {
        ArrayList<byte[]> data1 = new ArrayList<byte[]>();
        for (int i = 0; i < Tip.FUNCTION_LOVE_MENU_LIST.length; ++i) {
            if (i == 0) {
                data1.add(UI_InputString.getBytes("Vui lòng nhập tên người kia"));
                MarryNPC.loverMenuOptionData[i] = data1;
            }
            NpcHandshakeOptionData data2 = new NpcHandshakeOptionData();
            data2.miniImageID = this.getMinMarkIconID();
            data2.optionDesc = Tip.FUNCTION_LOVE_MENU_LIST[i];
            data2.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data2);
            if (MarryNPC.loverMenuOptionData[i] != null) {
                data2.followOptionData = new ArrayList<byte[]>(MarryNPC.loverMenuOptionData[i].size());
                for (final byte[] b : MarryNPC.loverMenuOptionData[i]) {
                    data2.followOptionData.add(b);
                }
            }
        }
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        MarryNPC.log.debug((Object) ("MarryNPC _step = " + _step + "  selectIndex = " + selectIndex));
        if (_step == Step.TOP.tag) {
            switch (selectIndex) {
                case 0: {
                    String name = _content.readUTF();
                    MarryNPC.log.debug((Object) ("\u7ed3\u5a5a name = " + name));
                    if (name.equals(_player.getName())) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Không thể thực hiện với bản thân."));
                        return;
                    }
                    HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(name);
                    if (other == null || !other.isEnable()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Hiện tại người này không online."));
                        return;
                    }
                    String myLover = LoverServiceImpl.getInstance().whoMarriedMe(_player.getName());
                    if (myLover != null && myLover.equals(name)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Bạn đã kết hôn, không thể kết hôn thêm nữa."));
                        return;
                    }
                    myLover = LoverServiceImpl.getInstance().whoLoveMe(_player.getName());
                    if (myLover == null || !myLover.equals(name)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Bạn và " + name + "Không phải người yêu nên không thể kết hôn."));
                        return;
                    }
                    if (other.getSex() == _player.getSex()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Không thể kết hôn với người này."));
                        return;
                    }
                    if (other.getClan() != _player.getClan()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Người chơi khác gia tộc không thể kết hôn."));
                        return;
                    }
                    if (_player.getLevel() < 20) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Yêu cầu cấp độ 20 trở lên để có thể kết hôn."));
                        return;
                    }
                    if (other.getLevel() < 20) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Trình độ người kia chưa đạt cấp 20 để có thể kết hôn."));
                        return;
                    }
                    if (_player.getLoverValue() < 3000 || other.getLoverValue() < 3000) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Điểm tình yêu của bạn hoặc người kìa không đủ 3.000 điểm."));
                        return;
                    }
                    MarryNPC.log.debug((Object) "start ......");
                    int goodsID = MarryGoods.RANG.getId();
                    int rangnum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID);
                    if (rangnum == 0) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Bạn cần một chiếc nhẫn cưới để kết hôn. Hãy mua một chiếc và trở lại!"));
                        return;
                    }
                    rangnum = other.getInventory().getSpecialGoodsBag().getGoodsNumber(goodsID);
                    if (rangnum == 0) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Bên kia chưa có nhẫn cưới, để anh (chị) mua một chiếc rồi rinh về nhé!"));
                        return;
                    }
                    if (_player.getGroupID() == 0 || other.getGroupID() == 0) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Kết hôn yêu cầu những người yêu nhau lập đội, và chỉ có hai người trong đội!"));
                        return;
                    }
                    if (_player.getGroupID() != other.getGroupID()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Kết hôn yêu cầu 2 người cùng 1 đội."));
                        return;
                    }
                    Group group = GroupServiceImpl.getInstance().getGroup(_player.getGroupID());
                    if (group.getMemberNumber() > 2) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Chỉ được 2 người trong đội."));
                        return;
                    }
                    if (myLover.equals(name)) {
                        String content = "Người chơi \"" + _player.getName() + "\" muốn kết hôn với bạn ngay bây giờ, bạn có đồng ý không?";
                        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new AskPlayerAgreeWedding(_player, other, content, (byte) 2));
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(name) + " không phải người yêu của bạn, không thể lấy bạn"));
                    return;
                }
                case 1: {
                    MarryNPC.log.debug((Object) "NPC \u79bb\u5a5a.....");
                    if (LoverServiceImpl.getInstance().whoMarriedMe(_player.getName()) == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Bạn chưa có gia đình"));
                        break;
                    }
                    MarryNPC.log.debug((Object) "\u534f\u8bae\u79bb\u5a5a divorce");
                    String name = _player.spouse;
                    HeroPlayer otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(name);
                    if (otherMarryPlayer == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Người kia không online"));
                        break;
                    }
                    String myLover = LoverServiceImpl.getInstance().whoMarriedMe(_player.getName());
                    MarryNPC.log.debug((Object) ("mylover = " + myLover));
                    if (!myLover.equals(name)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(name) + " không phải vợ hoặc chông của bạn."));
                        break;
                    }
                    int num = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                    if (num == 0) {
                        num = otherMarryPlayer.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                        if (num == 0) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Không có thỏa thuận ly hôn, cho dù để đi đến trung tâm mua sắm để mua!", (byte) 2, (byte) 1));
                            return;
                        }
                    }
                    if (_player.getGroupID() <= 0) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Cả hai vợ chồng phải thành lập một đội riêng!"));
                        break;
                    }
                    if (otherMarryPlayer.getGroupID() <= 0 || _player.getGroupID() != otherMarryPlayer.getGroupID()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Cả hai vợ chồng phải thành lập một đội riêng!"));
                        break;
                    }
                    MarryNPC.log.debug((Object) "divorce in same group ...");
                    if (GroupServiceImpl.getInstance().getGroup(_player.getGroupID()).getMemberNumber() == 2) {
                        MarryNPC.log.debug((Object) "divorce group only 2 ");
                        ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(), new AskPlayerAgreeWedding(_player, otherMarryPlayer, String.valueOf(_player.getName()) + "Tôi muốn ly hôn với bạn, \nBạn có đồng ý không?", (byte) 3));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Tin nhắn đã được gửi đi, vui lòng chờ phản hồi từ bên kia!", (byte) 0));
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Chỉ được 2 người trong dội."));
                    break;
                }
                case 2: {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Lời nhắc nhở kết hôn: Xin chào, khi cấp độ của bạn đạt đến cấp 20, sau khi giá trị tình yêu đạt đến 3000, bạn có thể mang theo nhẫn cưới và cùng người yêu của mình tìm đến Nguyệt Lão để đăng ký kết hôn.", (byte) 1));
                    break;
                }
            }
        }
        if (_step == Step.DIVORCE.tag) {
            byte optionIndex = _content.readByte();
            MarryNPC.log.debug((Object) ("optionIndex = " + optionIndex));
            switch (optionIndex) {
                case 0: {
                    MarryNPC.log.debug((Object) "\u534f\u8bae\u79bb\u5a5a divorce");
                    String name2 = _player.spouse;
                    HeroPlayer otherMarryPlayer2 = PlayerServiceImpl.getInstance().getPlayerByName(name2);
                    if (otherMarryPlayer2 == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Người kia không online"));
                        break;
                    }
                    String myLover2 = LoverServiceImpl.getInstance().whoMarriedMe(_player.getName());
                    MarryNPC.log.debug((Object) ("mylover = " + myLover2));
                    if (!myLover2.equals(name2)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(name2) + "Không phải là vợ chồng của bạn!"));
                        break;
                    }
                    int num2 = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                    if (num2 == 0) {
                        num2 = otherMarryPlayer2.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                        if (num2 == 0) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Không có thỏa thuận ly hôn, đi đến trung tâm mua sắm để mua!", (byte) 2, (byte) 1));
                            return;
                        }
                    }
                    if (_player.getGroupID() <= 0) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Cả hai vợ chồng phải thành lập một đội riêng!"));
                        break;
                    }
                    if (otherMarryPlayer2.getGroupID() <= 0 || _player.getGroupID() != otherMarryPlayer2.getGroupID()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Cả hai vợ chồng phải thành lập một đội riêng!"));
                        break;
                    }
                    MarryNPC.log.debug((Object) "divorce in same group ...");
                    if (GroupServiceImpl.getInstance().getGroup(_player.getGroupID()).getMemberNumber() == 2) {
                        MarryNPC.log.debug((Object) "divorce group only 2 ");
                        ResponseMessageQueue.getInstance().put(otherMarryPlayer2.getMsgQueueIndex(), new AskPlayerAgreeWedding(_player, otherMarryPlayer2, String.valueOf(_player.getName()) + "\u8981\u548c\u4f60\u79bb\u5a5a\uff0c\n\u4f60\u540c\u610f\u5417\uff1f", (byte) 3));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Tin nhắn đã được gửi đi, vui lòng chờ phản hồi từ bên kia!", (byte) 0));
                        break;
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("Chỉ được 2 người trong 1 đội."));
                    break;
                }
            }
        }
    }

    public static boolean married(final HeroPlayer _player, final String otherName) {
        MarryNPC.log.debug((Object) "married\u3002\u3002\u3002\u3002");
        MarryNPC.log.debug((Object) "\u5f00\u59cb ");
        LoverServiceImpl.MarryStatus status = LoverServiceImpl.getInstance().registerMarriage(_player.getName(), otherName, _player.getClan().getID());
        if (status == LoverServiceImpl.MarryStatus.SUCCESS) {
            HeroPlayer otherMarryPlayer = PlayerServiceImpl.getInstance().getPlayerByName(otherName);
            if (playerGoTOMarryMap(_player, null) && playerGoTOMarryMap(_player, otherMarryPlayer)) {
                MarryNPC.marryer[0] = _player.getUserID();
                MarryNPC.marryer[1] = otherMarryPlayer.getUserID();
                _player.canRemoveAllFromMarryMap = true;
                otherMarryPlayer.canRemoveAllFromMarryMap = true;
                _player.spouse = otherName;
                otherMarryPlayer.spouse = _player.getName();
                _player.loverLever = LoverLevel.ZHI;
                otherMarryPlayer.loverLever = LoverLevel.ZHI;
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u606d\u559c\uff01\u4f60\u548c\"" + otherName + "\"\u5df2\u6210\u4e3a\u592b\u59bb!"));
                for (int i = 0; i < 3; ++i) {
                    ChatQueue.getInstance().add((byte) 5, null, null, null, "\u606d\u559c" + _player.getName() + "\u4e0e" + otherName + "\u559c\u7ed3\u8fde\u7406\uff0c\u6c38\u4e0d\u5206\u79bb\u3002");
                }
                MarryNPC.canEntry = true;
                _player.marryed = true;
                otherMarryPlayer.marryed = true;
                Timer loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(_player.getUserID());
                if (loverValueTimer == null) {
                    loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(otherMarryPlayer.getUserID());
                    if (loverValueTimer == null) {
                        PlayerServiceImpl.getInstance().startLoverValueTimer(_player);
                    }
                }
                MarryNPC.log.debug((Object) ("can entry marry map = " + MarryNPC.canEntry));
                return true;
            }
            LoverServiceImpl.getInstance().marryFaild(_player.getName(), otherName);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u7ed3\u5a5a\u53cc\u65b9\u8fdb\u5165\u5a5a\u793c\u793c\u5802\u65f6\u51fa\u9519\uff0c\u7ed3\u5a5a\u5931\u8d25\uff01"));
        } else if (status == LoverServiceImpl.MarryStatus.NO_TIME) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u5230\u767b\u8bb0\u7ed3\u5a5a\u7684\u65f6\u95f4"));
        } else if (status == LoverServiceImpl.MarryStatus.MARRIED) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u5a5a"));
        } else if (status == LoverServiceImpl.MarryStatus.NOT_LOVER) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u8fd8\u6ca1\u8ba2\u5a5a"));
        }
        return false;
    }

    public static boolean propose(final HeroPlayer player, final String name2) {
        HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(name2);
        if (otherPlayer == null) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u5728\u7ebf\uff01"));
            return false;
        }
        if (player.getSex() == otherPlayer.getSex()) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u672c\u6e38\u620f\u4e0d\u652f\u6301\u540c\u6027\u604b\uff01"));
            return false;
        }
        String othername = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
        if (othername != null) {
            MarryNPC.log.debug((Object) "\u4f60\u5df2\u7ecf\u6709\u604b\u4eba\u4e86");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u6709\u604b\u4eba\u4e86\uff01"));
            return false;
        }
        othername = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
        if (othername != null) {
            MarryNPC.log.debug((Object) "\u4f60\u5df2\u7ecf\u7ed3\u5a5a\u4e86\uff01");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u7ed3\u5a5a\u4e86\uff01"));
            return false;
        }
        String whoLoveOther = LoverServiceImpl.getInstance().whoLoveMe(name2);
        if (whoLoveOther != null) {
            MarryNPC.log.debug((Object) "\u5bf9\u65b9\u5df2\u7ecf\u6709\u604b\u4eba\u4e86");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u7ecf\u6709\u604b\u4eba\u4e86\uff01"));
            return false;
        }
        whoLoveOther = LoverServiceImpl.getInstance().whoMarriedMe(name2);
        if (whoLoveOther != null) {
            MarryNPC.log.debug((Object) "\u5bf9\u65b9\u5df2\u7ecf\u7ed3\u5a5a\u4e86\uff01");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u7ecf\u7ed3\u5a5a\u4e86\uff01"));
            return false;
        }
        ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(), new AskPlayerAgreeWedding(player, otherPlayer, String.valueOf(player.getName()) + "\u60f3\u8981\u548c\u4f60\u6210\u4e3a\u604b\u4eba\uff0c\n\u4f60\u540c\u610f\u5417\uff1f", (byte) 1));
        return true;
    }

    public static boolean propose(final HeroPlayer player, HeroPlayer otherPlayer) {
        MarryNPC.log.debug((Object) "propose\u3002\u3002\u3002");
        LoverServiceImpl.MarryStatus status = LoverServiceImpl.getInstance().registerLover(player.getName(), otherPlayer.getName());
        MarryNPC.log.debug((Object) ("propose marry status = " + status));
        if (status == LoverServiceImpl.MarryStatus.LOVED_NO_MARRY) {
            MarryNPC.log.debug((Object) "\u4f60\u5df2\u7ecf\u6709\u604b\u4eba\u4e86");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u6709\u604b\u4eba\u4e86\uff01"));
            otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(player.spouse);
            if (otherPlayer == null) {
                otherPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(player.spouse);
                otherPlayer.setLoverValue(player.getLoverValue());
                otherPlayer.loverLever = player.loverLever;
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMarryRelationShow((byte) 1, otherPlayer));
            }
        }
        if (status == LoverServiceImpl.MarryStatus.MARRIED) {
            MarryNPC.log.debug((Object) "\u4f60\u5df2\u7ecf\u7ed3\u5a5a\u4e86\uff01");
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u7ed3\u5a5a\u4e86\uff01"));
            otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(player.spouse);
            if (otherPlayer == null) {
                otherPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(player.spouse);
                otherPlayer.setLoverValue(player.getLoverValue());
                otherPlayer.loverLever = player.loverLever;
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMarryRelationShow((byte) 2, otherPlayer));
            }
        }
        if (status == LoverServiceImpl.MarryStatus.NOT_LOVER) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6210\u4e3a\u604b\u4eba\u5931\u8d25\uff01"));
            return false;
        }
        if (status == LoverServiceImpl.MarryStatus.LOVED_SUCCESS) {
            player.spouse = otherPlayer.getName();
            otherPlayer.spouse = player.getName();
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u606d\u559c\uff0c\u4f60\u4e0e\"" + otherPlayer.getName() + "\"\u6210\u4e3a\u604b\u4eba\uff01"));
            ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(), new Warning("\u606d\u559c\uff0c\u4f60\u4e0e\"" + player.getName() + "\"\u6210\u4e3a\u604b\u4eba\uff01"));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePlayerMarryStatus(player));
            ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(), new ResponsePlayerMarryStatus(otherPlayer));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMarryRelationShow((byte) 1, otherPlayer));
            ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(), new ResponseMarryRelationShow((byte) 1, player));
            Timer loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(player.getUserID());
            if (loverValueTimer == null) {
                loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(otherPlayer.getUserID());
                if (loverValueTimer == null) {
                    PlayerServiceImpl.getInstance().startLoverValueTimer(player);
                }
            }
            MarryNPC.log.debug((Object) "LOVED_SUCCESS .. ResponseMarryRelationShow end ...");
            ChatQueue.getInstance().add((byte) 5, null, null, null, "\u521a\u521a \"" + player.getName() + "\" \u4e0e \"" + otherPlayer.getName() + "\" \u6210\u4e3a\u604b\u4eba\uff0c\u606d\u559c\u4ed6\u4eec\uff01");
        }
        return status == LoverServiceImpl.MarryStatus.LOVED_SUCCESS;
    }

    public static void breakUp(final HeroPlayer player, final String otherName) {
        HeroPlayer otherPlayer = PlayerServiceImpl.getInstance().getPlayerByName(otherName);
        if (otherPlayer == null) {
            otherPlayer = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(otherName);
        }
        String lover = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
        if (lover == null || !lover.equals(otherName)) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u4e0d\u662f\u4f60\u7684\u604b\u4eba\uff01"));
        } else {
            LoverServiceImpl.getInstance().updateMarryStatus(player.getName(), otherName, LoverServiceImpl.MarryStatus.BREAK_UP);
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u6210\u529f\u4e0e " + otherName + " \u5206\u624b\uff01"));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMarryRelationShow((byte) 0, null));
            Timer loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(player.getUserID());
            if (loverValueTimer == null) {
                loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(otherPlayer.getUserID());
                if (loverValueTimer != null) {
                    loverValueTimer.cancel();
                    PlayerServiceImpl.getInstance().removeLoverValueTimer(otherPlayer);
                }
            } else {
                loverValueTimer.cancel();
                PlayerServiceImpl.getInstance().removeLoverValueTimer(player);
            }
            player.clearLoverValue();
            if (otherPlayer.isEnable()) {
                otherPlayer.clearLoverValue();
                ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(), new Warning(String.valueOf(player.getName()) + "\u5df2\u4e0e\u4f60\u5206\u624b\uff01"));
                ResponseMessageQueue.getInstance().put(otherPlayer.getMsgQueueIndex(), new ResponseMarryRelationShow((byte) 0, null));
            } else {
                PlayerServiceImpl.getInstance().updatePlayerLoverValue(otherPlayer.getUserID(), 0);
            }
            ChatQueue.getInstance().add((byte) 5, null, null, null, "\u521a\u521a \"" + player.getName() + "\" \u4e0e \"" + otherPlayer.getName() + "\" \u5206\u624b\u4e86\u3002");
        }
    }

    public static boolean divorce(final HeroPlayer _player, final HeroPlayer otherMarryPlayer, final byte type) {
        MarryNPC.log.debug((Object) ("divorce \u3002\u3002\u3002 player groupid = " + _player.getGroupID() + " other groupid = " + otherMarryPlayer.getGroupID()));
        if (type == 0) {
            MarryNPC.log.debug((Object) "divorce \u534f\u8bae\u79bb\u5a5a");
            int num = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
            if (num == 0) {
                num = otherMarryPlayer.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.DIVORCE.getId());
                if (num == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u79bb\u5a5a\u534f\u8bae\uff0c\u662f\u5426\u53bb\u5546\u57ce\u8d2d\u4e70\uff01", (byte) 2, (byte) 1));
                    return false;
                }
            }
            if (_player.getGroupID() > 0) {
                if (otherMarryPlayer.getGroupID() > 0 && _player.getGroupID() == otherMarryPlayer.getGroupID()) {
                    MarryNPC.log.debug((Object) "divorce in same group ...");
                    if (GroupServiceImpl.getInstance().getGroup(_player.getGroupID()).getMemberNumber() == 2) {
                        MarryNPC.log.debug((Object) "divorce group only 2 ");
                        return divorce(_player, otherMarryPlayer, false);
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u592b\u59bb\u961f\u4f0d\u91cc\u5fc5\u987b\u53ea\u6709\u53cc\u65b9\u4e24\u4e2a\u4eba\uff01"));
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u592b\u59bb\u53cc\u65b9\u5fc5\u987b\u8981\u5355\u72ec\u7ec4\u6210\u4e00\u4e2a\u961f\u4f0d\uff01"));
                }
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u592b\u59bb\u53cc\u65b9\u5fc5\u987b\u8981\u5355\u72ec\u7ec4\u6210\u4e00\u4e2a\u961f\u4f0d\uff01"));
            }
        } else if (type == 1) {
            MarryNPC.log.debug((Object) "divorce \u5f3a\u5236\u79bb\u5a5a");
            int num = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.FORCE_DIVORCE.getId());
            if (num == 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u6ca1\u6709\u5f3a\u5236\u79bb\u5a5a\u8bc1\u660e\uff0c\u662f\u5426\u53bb\u5546\u57ce\u8d2d\u4e70\uff01", (byte) 2, (byte) 1));
                return false;
            }
            return divorce(_player, otherMarryPlayer, true);
        }
        return false;
    }

    private static boolean divorce(final HeroPlayer _player, final HeroPlayer otherMarryPlayer, final boolean force) {
        MarryNPC.log.debug((Object) "\u79bb\u5a5a\u5f00\u59cb\u4e86\u3002\u3002\u3002\u3002");
        LoverServiceImpl.MarryStatus status = LoverServiceImpl.getInstance().divorce(_player.getName());
        MarryNPC.log.debug((Object) ("status = " + status));
        if (status == LoverServiceImpl.MarryStatus.DIVORCE_SUCCESS) {
            MarryNPC.marryer[0] = 0;
            MarryNPC.marryer[1] = 0;
            _player.spouse = "";
            otherMarryPlayer.spouse = "";
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u4e0e\"" + otherMarryPlayer.getName() + "\"\u79bb\u5a5a\uff01"));
            ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u4e0e\"" + _player.getName() + "\"\u79bb\u5a5a\uff01"));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponsePlayerMarryStatus(_player));
            ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(), new ResponsePlayerMarryStatus(otherMarryPlayer));
            for (int i = 0; i < 3; ++i) {
                ChatQueue.getInstance().add((byte) 5, null, null, null, "\u521a\u521a \"" + _player.getName() + "\" \u4e0e \"" + otherMarryPlayer.getName() + "\" \u79bb\u5a5a\u4e86\u3002");
            }
            MarryNPC.canEntry = false;
            if (force) {
                MarryNPC.log.debug((Object) "force delete goods....");
                try {
                    int dnum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.FORCE_DIVORCE.getId());
                    if (dnum > 0) {
                        GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), MarryGoods.FORCE_DIVORCE.getId(), CauseLog.DIVORCE);
                    } else {
                        dnum = otherMarryPlayer.getInventory().getSpecialGoodsBag().getGoodsNumber(MarryGoods.FORCE_DIVORCE.getId());
                        if (dnum > 0) {
                            GoodsServiceImpl.getInstance().deleteSingleGoods(otherMarryPlayer, otherMarryPlayer.getInventory().getSpecialGoodsBag(), MarryGoods.FORCE_DIVORCE.getId(), CauseLog.DIVORCE);
                        }
                    }
                    GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), MarryGoods.RANG.getId(), CauseLog.DIVORCE);
                    GoodsServiceImpl.getInstance().deleteSingleGoods(otherMarryPlayer, otherMarryPlayer.getInventory().getSpecialGoodsBag(), MarryGoods.RANG.getId(), CauseLog.DIVORCE);
                    MarryNPC.log.debug((Object) "force delete goods..end ....");
                } catch (BagException e) {
                    e.printStackTrace();
                }
            } else {
                MarryNPC.log.debug((Object) "not force delete goods....");
                try {
                    GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), MarryGoods.DIVORCE.getId(), CauseLog.DIVORCE);
                    GoodsServiceImpl.getInstance().deleteSingleGoods(otherMarryPlayer, otherMarryPlayer.getInventory().getSpecialGoodsBag(), MarryGoods.DIVORCE.getId(), CauseLog.DIVORCE);
                    GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), MarryGoods.RANG.getId(), CauseLog.DIVORCE);
                    GoodsServiceImpl.getInstance().deleteSingleGoods(otherMarryPlayer, otherMarryPlayer.getInventory().getSpecialGoodsBag(), MarryGoods.RANG.getId(), CauseLog.DIVORCE);
                    MarryNPC.log.debug((Object) "not  force delete goods....");
                } catch (BagException e) {
                    e.printStackTrace();
                }
            }
            _player.marryed = false;
            otherMarryPlayer.marryed = false;
            PlayerServiceImpl.getInstance().dbUpdate(_player);
            PlayerServiceImpl.getInstance().dbUpdate(otherMarryPlayer);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMarryRelationShow((byte) 0, null));
            ResponseMessageQueue.getInstance().put(otherMarryPlayer.getMsgQueueIndex(), new ResponseMarryRelationShow((byte) 0, null));
            Timer loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(_player.getUserID());
            if (loverValueTimer == null) {
                loverValueTimer = PlayerServiceImpl.getInstance().getLoverValueTimerByUserID(otherMarryPlayer.getUserID());
                if (loverValueTimer != null) {
                    loverValueTimer.cancel();
                    PlayerServiceImpl.getInstance().removeLoverValueTimer(otherMarryPlayer);
                }
            } else {
                loverValueTimer.cancel();
                PlayerServiceImpl.getInstance().removeLoverValueTimer(_player);
            }
            _player.clearLoverValue();
            otherMarryPlayer.clearLoverValue();
            MarryNPC.log.debug((Object) "\u79bb\u5a5a\u7ed3\u675f\u4e86\u3002\u3002\u3002\u3002");
            return true;
        }
        if (status == LoverServiceImpl.MarryStatus.LOVED_NO_MARRY) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u8fd8\u6ca1\u6709\u7ed3\u5a5a\uff0c\u73b0\u5728\u4e0d\u80fd\u79bb\u5a5a\uff01"));
        } else if (status == LoverServiceImpl.MarryStatus.DIVORCED) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u8fd8\u6ca1\u6709\u8ba2\u5a5a\u6216\u5df2\u79bb\u5a5a\uff01"));
        }
        return false;
    }

    private static boolean playerGoTOMarryMap(final HeroPlayer _player, final HeroPlayer _other) {
        MarryNPC.log.debug((Object) ("goto MarryMap \u3002\u3002\u3002now player [ " + _player.getName() + " ] mapid= " + _player.where().getID()));
        try {
            short mapid = (short) ((_player.getClan() == EClan.LONG_SHAN) ? 406 : 407);
            Map targetMap = MapServiceImpl.getInstance().getNormalMapByID(mapid);
            if (targetMap == null) {
                MarryNPC.log.error((Object) ("\u4e0d\u5b58\u5728\u5a5a\u793c\u793c\u5802\u5730\u56fe\uff0cID:" + mapid));
                return false;
            }
            DungeonServiceImpl.getInstance().marryerGotoMarryDungeon(_player, _other, mapid);
            MarryNPC.log.debug((Object) ("goto MarryMap success now player [ " + _player.getName() + " ] mapid= " + _player.where().getID()));
            return true;
        } catch (Exception e) {
            MarryNPC.log.error((Object) ("\u73a9\u5bb6" + _player.getName() + "\u8fdb\u5165\u793c\u5802\u5730\u56fe error : "), (Throwable) e);
            return false;
        }
    }

    public static void loverExitMarryMap(final HeroPlayer player) {
        if (player.getUserID() == MarryNPC.marryer[0] || player.getUserID() == MarryNPC.marryer[1]) {
            removeAllPlayer(player.getClan().getID());
        }
    }

    public static void removeAllPlayer(final short clan) {
        MarryNPC.canEntry = false;
        ArrayList<HeroPlayer> playerList = new ArrayList<HeroPlayer>();
        Map map = MapServiceImpl.getInstance().getNormalMapByID((short) 406);
        Map marryNPCMap = MapServiceImpl.getInstance().getNormalMapByID((short) 7);
        if (clan == EClan.LONG_SHAN.getID()) {
            map = MapServiceImpl.getInstance().getNormalMapByID((short) 406);
            marryNPCMap = MapServiceImpl.getInstance().getNormalMapByID((short) 7);
        } else {
            map = MapServiceImpl.getInstance().getNormalMapByID((short) 407);
            marryNPCMap = MapServiceImpl.getInstance().getNormalMapByID((short) 65);
        }
        if (map.getPlayerList().size() > 0) {
            ME2ObjectList list = map.getPlayerList();
            if (list != null) {
                for (final ME2GameObject aList : list) {
                    HeroPlayer player = (HeroPlayer) aList;
                    MarryNPC.log.debug((Object) ("marryMap player : " + player.getName() + " , canRemoveAllFromMarryMap = " + player.canRemoveAllFromMarryMap));
                    playerList.add(player);
                }
            }
        }
        for (final HeroPlayer player2 : playerList) {
            MarryNPC.log.debug((Object) ("player : " + player2.getName() + " out MarryMap"));
            ResponseMessageQueue.getInstance().put(player2.getMsgQueueIndex(), new Warning("\u7ed3\u5a5a\u7684\u73a9\u5bb6\u5df2\u7ecf\u9000\u51fa\u793c\u5802\uff0c\u5a5a\u793c\u7ed3\u675f\uff01"));
            DungeonServiceImpl.getInstance().playerLeftDungeon(player2);
            gotoMap(player2, map, marryNPCMap);
        }
    }

    private static void gotoMap(final HeroPlayer _player, final Map _currMap, final Map targetMap) {
        _player.setCellX(targetMap.getBornX());
        _player.setCellY(targetMap.getBornY());
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, targetMap, _currMap));
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
    }

    enum Step {
        TOP("TOP", 0, 1),
        DIVORCE("DIVORCE", 1, 10);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
