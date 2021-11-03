// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.service;

import java.util.Iterator;
import hero.item.service.GoodsServiceImpl;
import hero.item.detail.EGoodsType;
import hero.gm.service.GmDAO;
import java.util.ArrayList;
import hero.chat.message.GetGoodsNofity;
import hero.item.Goods;
import hero.guild.Guild;
import hero.group.Group;
import hero.group.service.GroupServiceImpl;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import hero.gm.service.GmServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.player.HeroPlayer;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Map;
import java.util.Timer;
import java.util.Random;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class ChatServiceImpl extends AbsServiceAdaptor<MsgQConfig> {

    private static Logger log;
    public static final byte PLAYER_SINGLE = 0;
    public static final byte PLAYER_WORLD = 1;
    public static final byte PLAYER_MAP = 2;
    public static final byte PLAYER_GROUP = 3;
    public static final byte PLAYER_GUILD = 4;
    public static final byte TOP_SYSTEM_WORLD = 5;
    public static final byte BOTTOM_SYSTEM_MAP = 7;
    public static final byte BOTTOM_SYSTEM_GROUP = 8;
    public static final byte BOTTOM_SYSTEM_GUILD = 9;
    public static final byte GOODS_SYSTEM_GROUP = 20;
    public static final byte CLAN = 10;
    public static final byte TOP_SYSTEM_SINGLE = 12;
    private String[] tip;
    private Object tipMutex;
    private static final Random random;
    private Timer timer;
    private Timer loadNoticeTimer;
    private Timer sendNoticeTimer;
    private static ChatServiceImpl instance;
    private Map<Integer, GmNotice> gmNoticeMap;
    private Map<Integer, Timer> sendNoticeTimerMap;
    private Map<Integer, TimerTask> sendNoticeMap;

    static {
        ChatServiceImpl.log = Logger.getLogger((Class) ChatServiceImpl.class);
        random = new Random();
        ChatServiceImpl.instance = null;
    }

    public static ChatServiceImpl getInstance() {
        if (ChatServiceImpl.instance == null) {
            ChatServiceImpl.instance = new ChatServiceImpl();
        }
        return ChatServiceImpl.instance;
    }

    private ChatServiceImpl() {
        this.tip = null;
        this.tipMutex = new Object();
        this.timer = new Timer();
        this.loadNoticeTimer = new Timer();
        this.sendNoticeTimer = new Timer();
        this.sendNoticeTimerMap = null;
        this.sendNoticeMap = null;
        try {
            this.config = new MsgQConfig();
            this.gmNoticeMap = new HashMap<Integer, GmNotice>();
            this.sendNoticeTimerMap = new HashMap<Integer, Timer>();
            this.sendNoticeMap = new HashMap<Integer, TimerTask>();
            this.timer.schedule(new SystemTipTask(), 60000L, 30000L);
            this.loadNoticeTimer.schedule(new GmNoticeLoadTask(), 120200L, 301000L);
            this.sendNoticeTimer.schedule(new CheckSendNoticeTask(), 90000L, 300000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isChatBlack(final HeroPlayer _speaker) {
        return PlayerServiceImpl.getInstance().playerChatIsBlank(_speaker.getLoginInfo().accountID, _speaker.getUserID());
    }

    public void toGMaddChatContent(final String speakerName, final String targetName, String content) {
        String addUrl = GmServiceImpl.addChatContentURL;
        content = "[" + speakerName + "]\u5bf9[" + targetName + "]\u8bf4\uff1a" + content;
        try {
            URL url = new URL(addUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write("serverID=" + GmServiceImpl.serverID + "&content=" + content);
            writer.flush();
            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSinglePlayer(final HeroPlayer _speaker, final String _destName, final HeroPlayer _target, final String _content, final boolean _toGm) {
        if (_toGm) {
            ChatQueue.getInstance().add((byte) 0, _speaker, _destName, _target, _content);
        } else if (!this.isChatBlack(_speaker)) {
            ChatQueue.getInstance().add((byte) 0, _speaker, _destName, _target, _content);
        }
    }

    public void sendSinglePlayer(final String _dsetName, final String _content) {
        ChatQueue.getInstance().add((byte) 12, null, _dsetName, null, _content);
    }

    public void sendMapPlayer(final HeroPlayer _sender, final String _content) {
        if (this.isChatBlack(_sender)) {
            return;
        }
        ChatQueue.getInstance().add((byte) 2, _sender, "", null, _content);
        this.toGMaddChatContent(_sender.getName(), "\u540c\u5730\u56fe\u73a9\u5bb6", _content);
    }

    public void sendWorldPlayer(final HeroPlayer _speaker, final String _content) {
        if (!this.isChatBlack(_speaker)) {
            ChatQueue.getInstance().add((byte) 1, _speaker, null, null, _content);
            this.toGMaddChatContent(_speaker.getName(), "\u6240\u6709\u73a9\u5bb6", _content);
        }
    }

    public void sendWorldPlayerUseMassHorn(final HeroPlayer _speaker, final String _content) {
        if (!this.isChatBlack(_speaker)) {
            ChatQueue.getInstance().add((byte) 1, _speaker, null, null, _content, true);
            this.toGMaddChatContent(_speaker.getName(), "\u6240\u6709\u73a9\u5bb6", _content);
        }
    }

    public void sendWorldGM(final String _gmName, final String _content) {
        ChatQueue.getInstance().add((byte) 1, _gmName, _content);
    }

    public void sendNoticeGM(final String _GMName, final String _content) {
        ChatQueue.getInstance().add((byte) 5, _GMName, _content, true);
        this.loadGmNotice();
    }

    public void sendGroupPlayer(final HeroPlayer _speaker, final String _content) {
        if (!this.isChatBlack(_speaker)) {
            ChatQueue.getInstance().add((byte) 3, _speaker, null, null, _content);
            this.toGMaddChatContent(_speaker.getName(), "\u540c\u961f\u4f0d\u73a9\u5bb6", _content);
        }
    }

    public void sendGuildContent(final HeroPlayer _speaker, final String _content) {
        if (!this.isChatBlack(_speaker)) {
            ChatQueue.getInstance().add((byte) 4, _speaker, null, null, _content);
            this.toGMaddChatContent(_speaker.getName(), "\u540c\u5de5\u4f1a\u73a9\u5bb6", _content);
        }
    }

    public void setTopSys(final String[] str) {
        synchronized (this.tipMutex) {
            this.tip = str;
        }
        // monitorexit(this.tipMutex)
    }

    public void clearTopSys() {
        synchronized (this.tipMutex) {
            this.tip = null;
        }
        // monitorexit(this.tipMutex)
    }

    public void sendWorldBottomSys(final String _content) {
        ChatQueue.getInstance().add((byte) 5, null, null, null, _content);
    }

    public void sendGroupBottomSys(final int groupID, final String _content) {
        Group group = GroupServiceImpl.getInstance().getGroup(groupID);
        this.sendGroupBottomSys(group, _content);
    }

    public void sendGroupBottomSys(final Group _group, final String _content) {
        ChatQueue.getInstance().add((byte) 8, null, null, null, _content);
    }

    public void sendGuildBottomSys(final Guild _guild, final String _content) {
        ChatQueue.getInstance().addGuildSys(_guild.getID(), _content);
    }

    public void sendGroupGoods(final int _groupID, final String _content, final Goods _goods, final byte _num, final boolean _needExcludeTrigger, final int _playerObjectID) {
        Group group = GroupServiceImpl.getInstance().getGroup(_groupID);
        if (group != null) {
            GetGoodsNofity msg = new GetGoodsNofity(_content, _goods.getName(), _goods.getTrait().getViewRGB(), _num);
            ArrayList<HeroPlayer> list = group.getPlayerList();
            for (int i = 0; i < list.size(); ++i) {
                HeroPlayer player = list.get(i);
                if (player != null && player.isEnable()) {
                    if (!_needExcludeTrigger || _playerObjectID != player.getID()) {
                        ChatQueue.getInstance().addGoodsMsg(player, msg);
                    }
                }
            }
        }
    }

    public void sendSingleGoods(final HeroPlayer _player, final String _content, final Goods _goods, final byte _num) {
        ChatQueue.getInstance().addGoodsMsg(_player, _content, _goods.getName(), _goods.getTrait().value(), _num);
    }

    public void sendClan(final HeroPlayer _speaker, final short _clan, final String _content) {
        if (!this.isChatBlack(_speaker)) {
            ChatQueue.getInstance().add((byte) 10, _speaker, null, null, _content, _clan);
            this.toGMaddChatContent(_speaker.getName(), "\u540c\u9635\u8425\u73a9\u5bb6", _content);
        }
    }

    @Override
    protected void start() {
        WorldHornService.getInstance().start();
    }

    public void loadGmNotice() {
        synchronized (this.gmNoticeMap) {
            this.gmNoticeMap = GmDAO.getGmNoticeList(GmServiceImpl.serverID);
            ChatServiceImpl.log.info((Object) ("loadGmNotice gmNoticeMap size  = " + this.gmNoticeMap.size()));
        }
        // monitorexit(this.gmNoticeMap)
    }

    public static String parseGoodsInContent(final HeroPlayer _player, String _content) {
        int goodsInfoStartIndex = 0;
        int goodsInfoEndIndex = 0;
        int goodsInfoNumber = 0;
        StringBuffer sb = new StringBuffer();
        try {
            while (-1 != (goodsInfoEndIndex = _content.indexOf("]", 3))) {
                goodsInfoStartIndex = goodsInfoEndIndex - 3;
                String goodsInfo = _content.substring(goodsInfoStartIndex, goodsInfoEndIndex);
                if (!goodsInfo.startsWith("[") && goodsInfoEndIndex >= 4) {
                    goodsInfoStartIndex = goodsInfoEndIndex - 4;
                }
                goodsInfo = _content.substring(goodsInfoStartIndex, goodsInfoEndIndex + 1);
                if (goodsInfo.startsWith("[")) {
                    Goods goods = parseGoods(_player, goodsInfo.toLowerCase());
                    if (goods != null) {
                        sb.append(_content.substring(0, goodsInfoStartIndex));
                        sb.append("#S");
                        sb.append(goods.getTrait().value());
                        sb.append("F");
                        sb.append(goods.getID());
                        sb.append("[");
                        sb.append("<goodsname_" + goodsInfoNumber + ">").append(goods.getName()).append("</goodsname_" + goodsInfoNumber + ">");
                        sb.append("]");
                        ++goodsInfoNumber;
                        if (goodsInfoEndIndex + 1 == _content.length()) {
                            return sb.append("<num>").append(goodsInfoNumber).append("</num>").toString();
                        }
                        if (3 == goodsInfoNumber) {
                            return sb.append(_content.substring(goodsInfoEndIndex + 1)).append("<num>").append(goodsInfoNumber).append("</num>").toString();
                        }
                    } else {
                        sb.append(_content.substring(0, goodsInfoEndIndex + 1));
                    }
                    _content = _content.substring(goodsInfoEndIndex + 1);
                } else {
                    if (goodsInfoEndIndex + 1 == _content.length()) {
                        return sb.append(_content).append("<num>").append(goodsInfoNumber).append("</num>").toString();
                    }
                    sb.append(_content.substring(0, goodsInfoEndIndex + 1)).toString();
                    _content = _content.substring(goodsInfoEndIndex + 1);
                }
            }
            return sb.append(_content).append("<num>").append(goodsInfoNumber).append("</num>").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return _content;
        }
    }

    private static Goods parseGoods(final HeroPlayer _player, String _goodsInfo) {
        int bagGridIndex = -1;
        EGoodsType goodsType = null;
        Object bag = null;
        try {
            _goodsInfo = _goodsInfo.substring(1, _goodsInfo.length() - 1);
            bagGridIndex = Integer.parseInt(_goodsInfo.substring(1));
            if (_goodsInfo.startsWith("w")) {
                goodsType = EGoodsType.EQUIPMENT;
                bag = _player.getBodyWear();
            } else if (_goodsInfo.startsWith("z")) {
                goodsType = EGoodsType.EQUIPMENT;
                bag = _player.getInventory().getEquipmentBag();
            } else if (_goodsInfo.startsWith("c")) {
                goodsType = EGoodsType.MATERIAL;
                bag = _player.getInventory().getMaterialBag();
            } else if (_goodsInfo.startsWith("t")) {
                goodsType = EGoodsType.SPECIAL_GOODS;
                bag = _player.getInventory().getSpecialGoodsBag();
            } else if (_goodsInfo.startsWith("y")) {
                goodsType = EGoodsType.MEDICAMENT;
                bag = _player.getInventory().getMedicamentBag();
            } else if (_goodsInfo.startsWith("r")) {
                goodsType = EGoodsType.TASK_TOOL;
                bag = _player.getInventory().getTaskToolBag();
            }
            return GoodsServiceImpl.getInstance().bagGoodsModel(bag, goodsType, bagGridIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    class CheckSendNoticeTask extends TimerTask {

        Timer sendNoticeTimer;
        SendNoticeTask sntask;

        @Override
        public void run() {
            synchronized (ChatServiceImpl.this.gmNoticeMap) {
                if (ChatServiceImpl.this.gmNoticeMap != null && ChatServiceImpl.this.gmNoticeMap.size() > 0) {
                    Iterator<GmNotice> iterator = ChatServiceImpl.this.gmNoticeMap.values().iterator();
                    while (iterator.hasNext()) {
                        GmNotice notice = iterator.next();
                        if (notice.getStartTime().getTime() <= System.currentTimeMillis() && notice.getEndTime().getTime() >= System.currentTimeMillis()) {
                            ChatServiceImpl.log.debug((Object) "\u516c\u544a\u671f\u5185....");
                            if (ChatServiceImpl.this.sendNoticeTimerMap.get(notice.getId()) != null) {
                                continue;
                            }
                            ChatServiceImpl.log.debug((Object) "\u65b0\u52a0\u516c\u544a\u4efb\u52a1..");
                            this.sendNoticeTimer = new Timer();
                            this.sntask = new SendNoticeTask(notice.getId(), notice.getTimes(), notice.getContent());
                            this.sendNoticeTimer.schedule(this.sntask, 0L, notice.getIntervalTime());
                            ChatServiceImpl.this.sendNoticeTimerMap.put(notice.getId(), this.sendNoticeTimer);
                            ChatServiceImpl.this.sendNoticeMap.put(notice.getId(), this.sntask);
                        } else {
                            ChatServiceImpl.log.info((Object) ("gm notice title=" + notice.getId() + " cancel.."));
                            this.sendNoticeTimer = ChatServiceImpl.this.sendNoticeTimerMap.get(notice.getId());
                            if (this.sendNoticeTimer != null) {
                                this.sendNoticeTimer.cancel();
                                this.sendNoticeTimer = null;
                                ChatServiceImpl.this.sendNoticeTimerMap.remove(notice.getId());
                            }
                            iterator.remove();
                        }
                    }
                }
            }
            // monitorexit(ChatServiceImpl.access$0(this.this$0))
        }
    }

    class SendNoticeTask extends TimerTask {

        int nid;
        int times;
        String content;

        SendNoticeTask(final int nid, final int _times, final String _content) {
            this.nid = nid;
            this.times = _times;
            this.content = _content;
        }

        @Override
        public void run() {
            GmNotice notice = ChatServiceImpl.this.gmNoticeMap.get(this.nid);
            if (notice != null) {
                if (notice.getStartTime().getTime() <= System.currentTimeMillis() && notice.getEndTime().getTime() >= System.currentTimeMillis()) {
                    for (int i = 0; i < this.times; ++i) {
                        ChatServiceImpl.this.sendNoticeGM("", this.content);
                    }
                } else {
                    ChatServiceImpl.log.debug((Object) ("cancel notice id=" + this.nid));
                    this.cancel();
                    ChatServiceImpl.this.sendNoticeMap.remove(this.nid);
                }
            }
        }
    }

    class GmNoticeLoadTask extends TimerTask {

        @Override
        public void run() {
            synchronized (ChatServiceImpl.this.gmNoticeMap) {
                ChatServiceImpl.this.loadGmNotice();
            }
            // monitorexit(ChatServiceImpl.access$0(this.this$0))
        }
    }

    class SystemTipTask extends TimerTask {

        @Override
        public void run() {
            synchronized (ChatServiceImpl.this.tipMutex) {
                if (ChatServiceImpl.this.tip != null) {
                    ChatQueue.getInstance().add((byte) 5, null, null, null, ChatServiceImpl.this.tip[ChatServiceImpl.random.nextInt(ChatServiceImpl.this.tip.length)]);
                }
            }
            // monitorexit(ChatServiceImpl.access$4(this.this$0))
        }
    }
}
