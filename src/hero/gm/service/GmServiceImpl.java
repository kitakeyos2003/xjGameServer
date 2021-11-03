// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.service;

import hero.map.Map;
import hero.effect.service.EffectServiceImpl;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.message.ResponseMapBottomData;
import hero.map.service.MapServiceImpl;
import hero.task.service.TaskServiceImpl;
import hero.charge.FeePointInfo;
import hero.charge.ChargeInfo;
import hero.log.service.ServiceType;
import hero.charge.FPType;
import hero.share.service.ShareServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.charge.service.ChargeServiceImpl;
import hero.charge.service.ChargeDAO;
import hero.player.service.PlayerDAO;
import hero.gm.message.GmQuestionSubmitFeedback;
import hero.share.message.MailStatusChanges;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.share.letter.Letter;
import hero.share.letter.LetterService;
import hero.share.service.LogWriter;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Timestamp;
import hero.player.HeroPlayer;
import hero.gm.EResponseType;
import java.text.SimpleDateFormat;
import java.util.Date;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import hero.chat.service.ChatServiceImpl;
import java.util.ArrayList;
import hero.gm.ResponseToGmTool;
import java.util.List;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class GmServiceImpl extends AbsServiceAdaptor<GmServiceConfig> {

    private static Logger log;
    public static final String TIME_FOREVER = "1980-01-01 00:00:00";
    private static GmServiceImpl instance;
    public static int serverID;
    public static int gameID;
    public static String addChatContentURL;
    private static List<ResponseToGmTool> responseGmToolList;

    static {
        GmServiceImpl.log = Logger.getLogger((Class) GmServiceImpl.class);
        GmServiceImpl.instance = null;
        GmServiceImpl.serverID = 1;
        GmServiceImpl.gameID = 1;
        GmServiceImpl.addChatContentURL = "";
        GmServiceImpl.responseGmToolList = null;
    }

    private GmServiceImpl() {
        this.config = new GmServiceConfig();
        GmServiceImpl.responseGmToolList = new ArrayList<ResponseToGmTool>();
    }

    public static GmServiceImpl getInstance() {
        if (GmServiceImpl.instance == null) {
            GmServiceImpl.instance = new GmServiceImpl();
        }
        return GmServiceImpl.instance;
    }

    @Override
    protected void start() {
        try {
            GmServiceImpl.log.info((Object) "### GmServiceImpl start..");
            GmBlackListManager.getInstance().init();
            ChatServiceImpl.getInstance().loadGmNotice();
            GmServiceImpl.serverID = ((GmServiceConfig) this.config).getServerID();
            GmServiceImpl.gameID = ((GmServiceConfig) this.config).getGameID();
            GmServiceImpl.addChatContentURL = ((GmServiceConfig) this.config).getAddChatContentURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createSession(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player != null) {
            Date date = new Date(player.loginTime);
            String loginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            ResponseToGmTool rtgt = new ResponseToGmTool(EResponseType.SEND_ROLE_ONLINE, 0);
            addGmToolMsg(rtgt);
        }
    }

    @Override
    public void sessionFree(final Session _session) {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
        if (player != null) {
            ResponseToGmTool rtgt = new ResponseToGmTool(EResponseType.SEND_ROLE_OUTLINE, 0);
            rtgt.setRoleOutline(player.getName());
            addGmToolMsg(rtgt);
        }
    }

    public static String getBlackEndTimeStr(final Timestamp _endTime) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_endTime);
        if (time.equals("1980-01-01 00:00:00")) {
            time = "\u6c38\u4e45";
        }
        return time;
    }

    public static String getTimestampStr(final Timestamp _ts) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_ts);
    }

    public final Connection getConnection() {
        String dbname = ((GmServiceConfig) this.config).getAccountDBname();
        Connection conn;
        try {
            String dbName = ((GmServiceConfig) this.config).getAccountDBname();
            GmServiceImpl.log.debug((Object) ("GM DB dbName = " + dbName + ", dbUrl = " + ((GmServiceConfig) this.config).getAccountDBurl()));
            String dbUrl = "jdbc:mysql://" + ((GmServiceConfig) this.config).getAccountDBurl() + "/" + dbName + "?connectTimeout=0&autoReconnect=true&failOverReadOnly=false";
            GmServiceImpl.log.debug((Object) ("dbUrl = " + dbUrl));
            String dbUser = ((GmServiceConfig) this.config).getAccountDBusername();
            String dbPassword = ((GmServiceConfig) this.config).getAccountDBpassword();
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (Exception ex) {
            conn = null;
            ex.printStackTrace();
            LogWriter.error("GmServiceImpl.getConnection error!", ex);
        }
        return conn;
    }

    public static boolean addGMLetter(final int userID, final String content, final byte type) {
        GmServiceImpl.log.debug((Object) ("player send to GM letter : " + userID + ",[" + content + "]"));
        return GmDAO.sendGMLetter(userID, content, type);
    }

    public static void GMReplyLetter(final int gmLetterID) {
        int letterID = LetterService.getInstance().getUseableLetterID();
        Letter letter = new Letter();
        letter.letterID = letterID;
        GmDAO.getLetterInfo(gmLetterID, letter);
        GmServiceImpl.log.debug((Object) ("GM \u7ed9\u73a9\u5bb6\u56de\u590d\u7684\u90ae\u4ef6: \u73a9\u5bb6 uerid = " + letter.receiverUserID));
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(letter.receiverUserID);
        if (player == null) {
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(letter.receiverUserID);
        }
        GmServiceImpl.log.debug((Object) ("GM \u7ed9\u73a9\u5bb6\u56de\u590d\u7684\u90ae\u4ef6: \u73a9\u5bb6 player = " + player));
        letter.receiverName = player.getName();
        letter.title = "\u7cfb\u7edf GM \u56de\u590d";
        letter.type = 0;
        LetterService.getInstance().addNewLetter(letter);
        if (player != null && player.isEnable()) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6709\u4e00\u5c01\u65b0\u7684\u90ae\u4ef6\uff0c\u5feb\u53bb\u90ae\u7bb1\u67e5\u6536\u5427"));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_POST_BOX, true));
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new GmQuestionSubmitFeedback((byte) 0));
        }
    }

    public static int szfFeeCallBack(final int userID, final String transID, final byte payresult, final String orderID, final int point) {
        GmServiceImpl.log.info((Object) "\u795e\u5dde\u4ed8\u56de\u8c03.....");
        String result = (payresult == 1) ? "\u5145\u503c\u6210\u529f" : "\u5145\u503c\u5931\u8d25";
        int res = 1;
        HeroPlayer receiver = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
        GmServiceImpl.log.debug((Object) ("receiver=" + receiver));
        if (receiver == null) {
            receiver = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(userID);
            PlayerDAO.loadPlayerAccountInfo(receiver);
        }
        if (receiver != null) {
            GmServiceImpl.log.debug((Object) ("\u795e\u5dde\u4ed8\u56de\u8c03 result=" + payresult + ", orderid=" + orderID + ",transid=" + transID + ",point=" + point));
            Letter letter = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u795e\u5dde\u4ed8\u5145\u503c\u7ed3\u679c\u901a\u77e5", "\u7cfb\u7edf", userID, receiver.getName(), "\u8ba2\u5355\u53f7\uff1a" + orderID + " ,\u5145\u503c\u7ed3\u679c\uff1a" + result + ",\u672c\u6b21\u5145\u503c\u70b9\u6570\uff1a" + point);
            LetterService.getInstance().addNewLetter(letter);
            if (receiver.isEnable()) {
                ResponseMessageQueue.getInstance().put(receiver.getMsgQueueIndex(), new Warning("\u60a8\u6709\u4e00\u5c01\u65b0\u7684\u90ae\u4ef6\uff0c\u5feb\u53bb\u90ae\u7bb1\u67e5\u6536\u5427", (byte) 0));
                ResponseMessageQueue.getInstance().put(receiver.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
            }
            res = ChargeDAO.updateChargeSZF(payresult, new Timestamp(System.currentTimeMillis()), transID, orderID);
            ChargeInfo info = ChargeDAO.getChargeInfo(transID, orderID);
            String memo = "";
            if (info.rechargetype == 2) {
                GmServiceImpl.log.debug((Object) "\u795e\u5dde\u4ed8\u56de\u8c03\uff0c\u7ed9\u5176\u4ed6\u73a9\u5bb6\u5145\u70b9\u6570\uff0c\u53d1\u63d0\u793a\u4fe1\u606f\u3002\u3002\u3002");
                GmServiceImpl.log.debug((Object) ("upd before other ponit=" + receiver.getChargeInfo().pointAmount));
                ChargeServiceImpl.getInstance().updatePointAmount(receiver, point);
                GmServiceImpl.log.debug((Object) ("upd after other point =" + receiver.getChargeInfo().pointAmount));
                HeroPlayer _player = PlayerServiceImpl.getInstance().getPlayerByUserID(info.userID);
                if (_player == null) {
                    _player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(info.userID);
                }
                if (_player != null) {
                    Letter letter_x = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u795e\u5dde\u4ed8\u5145\u503c\u7ed3\u679c\u901a\u77e5", "\u7cfb\u7edf", info.userID, _player.getName(), "\u8ba2\u5355\u53f7\uff1a" + orderID + " ,\u5145\u503c\u7ed3\u679c\uff1a" + result + ",\u672c\u6b21\u7ed9\"" + receiver.getName() + "\"\u5145\u503c\u70b9\u6570\uff1a" + point);
                    LetterService.getInstance().addNewLetter(letter_x);
                    if (_player.isEnable()) {
                        String desc = "\u5145\u503c\u5931\u8d25";
                        if (payresult == 1) {
                            desc = "\u5145\u503c\u6210\u529f\uff0c\u7ed9\"" + receiver.getName() + "\"\u52a0 " + point + " \u70b9";
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(desc, (byte) 1));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u6709\u4e00\u5c01\u65b0\u7684\u90ae\u4ef6\uff0c\u5feb\u53bb\u90ae\u7bb1\u67e5\u6536\u5427", (byte) 0));
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                    }
                }
                memo = "\u7ed9\u5176\u5b83\u4eba\u5145\u503c:\u8d26\u53f7id[" + info.other_account_id + "],\u89d2\u8272id[" + info.other_user_id + "]";
            } else {
                GmServiceImpl.log.debug((Object) "\u795e\u5dde\u4ed8\u56de\u8c03\uff0c\u4fee\u6539\u73a9\u5bb6\u70b9\u6570\uff0c\u53d1\u63d0\u793a\u4fe1\u606f\u3002\u3002\u3002");
                GmServiceImpl.log.debug((Object) ("upd before player ponit=" + receiver.getChargeInfo().pointAmount));
                ChargeServiceImpl.getInstance().updatePointAmount(receiver, point);
                GmServiceImpl.log.debug((Object) ("upd after palyer point =" + receiver.getChargeInfo().pointAmount));
            }
            LogServiceImpl.getInstance().chargeCardCallBackLog("\u5f02\u6b65", result, receiver.getLoginInfo().username, userID, receiver.getLoginInfo().loginMsisdn, receiver.getName(), info.rechargetime, receiver.getLoginInfo().accountID, info.rechargetype, receiver.getLoginInfo().publisher, info.paytype, transID, orderID, memo, info.price);
            if (receiver.isEnable() && receiver.buyHookExp) {
                ShareServiceImpl.getInstance().warnBuyHookExp(receiver);
            }
            if (payresult == 1) {
                FeePointInfo fpi = ChargeServiceImpl.getInstance().getFpInfoByFpcodeAndPrice(info.fpcode, info.price, FPType.CHARGE);
                if (fpi != null && fpi.presentPoint > 0) {
                    ChargeServiceImpl.getInstance().addPoint(receiver, transID, fpi.presentPoint, (byte) 3, receiver.getLoginInfo().publisher, ServiceType.PRESENT);
                }
                int presentPoint = GmDAO.getPresentPoint(info.price);
                if (presentPoint > 0) {
                    ChargeServiceImpl.getInstance().addPoint(receiver, transID, presentPoint, (byte) 3, receiver.getLoginInfo().publisher, ServiceType.ACTIVE_PRESENT);
                }
            }
        } else {
            res = 0;
        }
        GmServiceImpl.log.info((Object) ("\u795e\u5dde\u4ed8\u56de\u8c03...end .. res=" + res));
        return res;
    }

    public static void smsCallBack(final String transID, final String result) {
        boolean res = true;
        TaskServiceImpl.getInstance().asynTaskPushItem(transID, res);
    }

    public static int gmBlinkPlayer(final short mapID, final int userID) {
        int res = -1;
        HeroPlayer _player = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
        if (_player == null || !_player.isEnable()) {
            res = 1;
        }
        Map entranceMap = MapServiceImpl.getInstance().getNormalMapByID(mapID);
        if (entranceMap == null) {
            res = 2;
        }
        _player.setCellX(entranceMap.getBornX());
        _player.setCellY(entranceMap.getBornY());
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, entranceMap, _player.where()));
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, entranceMap));
        _player.gotoMap(entranceMap);
        EffectServiceImpl.getInstance().sendEffectList(_player, entranceMap);
        res = 0;
        return res;
    }

    public static String getAccountInfoByUserName(final String _username) {
        return GmDAO.getAccountInfo(_username);
    }

    public static String getAccountInfoByRoleName(final String _rolename) {
        String username = GmDAO.getAccountUserNameByRolename(_rolename);
        return getAccountInfoByUserName(username);
    }

    public static String[] getRoleInfos(final int _account_id) {
        return GmDAO.getRoleInfos(_account_id);
    }

    public static void addGmToolMsg(final ResponseToGmTool _response) {
        GmServiceImpl.responseGmToolList.add(_response);
    }
}
